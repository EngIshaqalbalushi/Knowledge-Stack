package com.architect.service;

import com.architect.model.CodeReviewResult;
import com.architect.model.CodeReviewResult.Issue;
import com.architect.model.CodeReviewResult.Metrics;
import com.architect.model.CodeReviewResult.Suggestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeReviewService {

    private static final Map<String, List<LanguageCheck>> LANGUAGE_CHECKS = new ConcurrentHashMap<>();
    private static final List<LanguageCheck> GENERIC_CHECKS = new ArrayList<>();

    private final AiCodeReviewService aiCodeReviewService;

    public CodeReviewService(AiCodeReviewService aiCodeReviewService) {
        this.aiCodeReviewService = aiCodeReviewService;
        initGenericChecks();
        initJavascriptChecks();
        initPythonChecks();
        initJavaChecks();
        initRustChecks();
        initGoChecks();
        initTypeScriptChecks();
        initCppChecks();
        initCSharpChecks();
        initRubyChecks();
        initPhpChecks();
        initSwiftChecks();
        initKotlinChecks();
    }

    public CodeReviewResult review(String code, String language, boolean beginnerMode) {
        // Try AI-powered review first
        CodeReviewResult aiResult = aiCodeReviewService.review(code, language, beginnerMode);
        if (aiResult != null) {
            return aiResult;
        }

        // Fall back to pattern-matching analysis
        String detectedLang = detectLanguage(code, language);
        List<Issue> issues = analyzeIssues(code, detectedLang);
        String fixedCode = generateFixedCode(code, issues, detectedLang);
        List<Suggestion> suggestions = generateSuggestions(detectedLang);
        Metrics metrics = computeMetrics(issues);

        if (beginnerMode) {
            for (Issue issue : issues) {
                issue.setExplanation("In simple terms: " + simplifyExplanation(issue.getExplanation()));
            }
        }

        return new CodeReviewResult(language, detectedLang, fixedCode, issues, suggestions, metrics);
    }

    private String detectLanguage(String code, String hint) {
        if (hint != null && !hint.isBlank()) return hint;
        if (code.contains("function") || code.contains("=>") || code.contains("const ") || code.contains("let ") || code.contains("var ") || code.contains("document.") || code.contains("console.log")) return "JavaScript";
        if (code.contains("def ") || code.contains("class ") || code.contains("import ") || code.contains("print(") || code.contains(":") && (code.contains("    ") || code.contains("\t")) && (code.contains("#") || code.contains("lambda"))) return "Python";
        if (code.contains("public class") || code.contains("System.out") || code.contains("private ") || code.contains("protected ") || code.contains("@Override") || code.contains("void main")) return "Java";
        if (code.contains("fn ") || code.contains("let ") || code.contains("mut ") || code.contains("impl ") || code.contains("->") || code.contains("unwrap()")) return "Rust";
        if (code.contains("func ") || code.contains("package ") || code.contains("defer ") || code.contains("go ") || code.contains("fmt.")) return "Go";
        if (code.contains("interface ") || code.contains("type ") || code.contains(": string") || code.contains(": number") || code.contains(": boolean") || code.contains(": any")) return "TypeScript";
        if (code.contains("#include") || code.contains("std::") || code.contains("int main") || code.contains("cout") || code.contains("cin >>") || code.contains("template<")) return "C++";
        if (code.contains("using System") || code.contains("namespace ") || code.contains("class ") && code.contains("static void Main") || code.contains("Console.WriteLine") || code.contains("public class")) return "C#";
        if (code.contains("def ") || code.contains("end") || code.contains("puts ") || code.contains("attr_accessor") || code.contains("do |") || code.contains("gem ")) return "Ruby";
        if (code.contains("<?php") || code.contains("echo ") || code.contains("function ") || code.contains("$") || code.contains("->") && (code.contains("public") || code.contains("private"))) return "PHP";
        if (code.contains("func ") || code.contains("import Swift") || code.contains("var ") && code.contains("let ") || code.contains("UIKit") || code.contains("Foundation")) return "Swift";
        if (code.contains("fun ") || code.contains("val ") || code.contains("var ") && code.contains("class ") && code.contains(":") || code.contains("import ") && code.contains("kotlin")) return "Kotlin";
        return "Unknown";
    }

    private List<Issue> analyzeIssues(String code, String language) {
        List<Issue> issues = new ArrayList<>();
        for (LanguageCheck check : GENERIC_CHECKS) check.check(code, issues);
        List<LanguageCheck> langChecks = LANGUAGE_CHECKS.getOrDefault(language, List.of());
        for (LanguageCheck check : langChecks) check.check(code, issues);
        if (issues.isEmpty()) issues.add(new Issue(0, "Info", "info", "No critical issues detected", "Your code looks clean. Consider adding error handling and input validation."));
        return issues;
    }

    private String generateFixedCode(String code, List<Issue> issues, String language) {
        String fixed = code;
        for (Issue issue : issues) {
            switch (issue.getType() + ":" + issue.getMessage()) {
                case "Performance:Sequential async calls in loop detected" -> {
                    if ("JavaScript".equals(language) || "TypeScript".equals(language))
                        fixed = fixed.replaceAll("for\\s*\\([^)]+\\)\\s*\\{[^}]*await\\s+(\\w+)\\(([^)]+)\\)[^}]*\\}", "const results = await Promise.all($2.map(item => $1(item)));");
                }
                case "Best Practice:Avoid using 'var' declaration" -> fixed = fixed.replaceAll("\\bvar\\b", "let");
                case "Security:print() used in production code" -> fixed = fixed.replaceAll("(?m)^(\\s*)print\\((.+)\\)", "$1# print($2)  # TODO: replace with logger");
                case "Best Practice:Use f-strings instead of % or .format()" -> {
                    if ("Python".equals(language))
                        fixed = fixed.replaceAll("\"[^\"]*\"\\s*%\\s*\\(([^)]+)\\)", "f\"...\"  # TODO: convert to f-string with $1");
                }
                case "Security:Input validation required" -> {
                    if ("Java".equals(language))
                        fixed = "import java.util.Objects;\n" + fixed.replaceAll("(String\\s+\\w+\\s*=\\s*\\w+;)", "if ($1 == null) throw new IllegalArgumentException(\"Input cannot be null\");\n        $1");
                }
                case "Memory:Possible memory leak" -> {
                    if ("Rust".equals(language))
                        fixed = fixed.replace("unwrap()", "expect(\"safe unwrap failed\")");
                }
                case "Error Handling:Ignored error return" -> {
                    if ("Go".equals(language))
                        fixed = fixed.replaceAll("_(\\s*::=\\s*\\w+\\([^)]+\\))", "err := $1\n    if err != nil {\n        return err\n    }");
                }
            }
        }
        return fixed;
    }

    private List<Suggestion> generateSuggestions(String language) {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Error Handling", "Always handle errors properly instead of ignoring them.", switch (language) {
            case "JavaScript", "TypeScript" -> "try {\n  const result = await riskyOperation();\n} catch (err) {\n  console.error('Operation failed:', err);\n}";
            case "Python" -> "try:\n    result = risky_operation()\nexcept Exception as e:\n    logger.error(f'Operation failed: {e}')";
            case "Java" -> "try {\n    Result result = riskyOperation();\n} catch (Exception e) {\n    log.error(\"Operation failed\", e);\n}";
            case "Rust" -> "match risky_operation() {\n    Ok(result) => result,\n    Err(e) => {\n        eprintln!(\"Operation failed: {}\", e);\n        return Err(e);\n    }\n}";
            case "Go" -> "result, err := riskyOperation()\nif err != nil {\n    return fmt.Errorf(\"operation failed: %w\", err)\n}";
            case "C++" -> "try {\n    auto result = riskyOperation();\n} catch (const std::exception& e) {\n    std::cerr << \"Operation failed: \" << e.what() << std::endl;\n}";
            default -> "Add proper error handling for all fallible operations.";
        }));
        suggestions.add(new Suggestion("Input Validation", "Always validate external inputs.", switch (language) {
            case "JavaScript", "TypeScript" -> "if (typeof input !== 'string' || input.length === 0) throw new Error('Invalid input');";
            case "Python" -> "if not isinstance(input, str) or not input.strip():\n    raise ValueError('Invalid input')";
            case "Java" -> "Objects.requireNonNull(input, \"input must not be null\");\nif (input.isBlank()) throw new IllegalArgumentException(\"input must not be blank\");";
            default -> "Validate all external inputs before use.";
        }));
        suggestions.add(new Suggestion("Performance", "Avoid unnecessary allocations in hot paths.", switch (language) {
            case "JavaScript", "TypeScript" -> "const memoized = useMemo(() => expensive(data), [data]);";
            case "Python" -> "from functools import lru_cache\n\n@lru_cache(maxsize=None)\ndef expensive(data):\n    ...";
            case "Java" -> "private final Map<K, V> cache = new ConcurrentHashMap<>();\n\npublic V compute(K key) {\n    return cache.computeIfAbsent(key, this::expensive);\n}";
            case "Rust" -> "use std::collections::HashMap;\nlet mut cache: HashMap<&str, Result> = HashMap::new();\ncache.entry(key).or_insert_with(|| expensive(key));";
            default -> "Consider caching or memoization for expensive operations.";
        }));
        return suggestions;
    }

    private Metrics computeMetrics(List<Issue> issues) {
        int severityScore = 0;
        boolean hasPerf = false, hasSecurity = false;
        for (Issue i : issues) {
            switch (i.getSeverity()) {
                case "critical" -> { severityScore += 40; hasSecurity = true; }
                case "high" -> { severityScore += 25; if ("Performance".equals(i.getType())) hasPerf = true; }
                case "medium" -> severityScore += 10;
                case "low" -> severityScore += 3;
            }
        }
        return new Metrics(hasPerf ? "O(n)" : "O(1)",
            severityScore > 30 ? "D-" : severityScore > 10 ? "C+" : "A",
            hasPerf ? "High" : "Low",
            issues.size() > 2 ? "High" : issues.size() > 1 ? "Medium" : "Low");
    }

    private String simplifyExplanation(String exp) {
        return exp
            .replace("blocking the event loop", "slowing down your program because tasks run one after another")
            .replace("asynchronous", "background")
            .replace("invocation", "call")
            .replace("memoization", "caching results")
            .replace("mutation", "changing a value")
            .replace("concurrent", "parallel")
            .replace("immutable", "unchangeable")
            .replace("recursion", "a function calling itself");
    }

    private void initGenericChecks() {
        GENERIC_CHECKS.add((code, issues) -> {
            if (code.contains("TODO")) issues.add(new Issue(0, "Code Quality", "low", "Unresolved TODO comment", "There's a TODO left in the code. Complete the implementation or create a tracking issue."));
        });
        GENERIC_CHECKS.add((code, issues) -> {
            if (code.length() > 500) issues.add(new Issue(0, "Maintainability", "medium", "Large code block detected", "This code block exceeds 500 characters. Consider refactoring into smaller, focused functions."));
        });
        GENERIC_CHECKS.add((code, issues) -> {
            if (code.contains("password") || code.contains("secret") || code.contains("api_key") || code.contains("token")) {
                issues.add(new Issue(0, "Security", "critical", "Potential secret exposure", "Hardcoded credentials or secrets found. Use environment variables or a secrets manager."));
            }
        });
        GENERIC_CHECKS.add((code, issues) -> {
            if (code.contains("null") && (code.contains("==") || code.contains("!="))) {
                issues.add(new Issue(0, "Best Practice", "medium", "Null comparison detected", "Direct null comparison can lead to subtle bugs. Consider using Optional or null-safe utilities."));
            }
        });
        GENERIC_CHECKS.add((code, issues) -> {
            if ((code.contains("catch") || code.contains("except")) && code.contains("{}") || code.contains("pass")) {
                issues.add(new Issue(0, "Error Handling", "high", "Empty catch block", "Empty catch blocks silently swallow errors. Always handle or re-throw exceptions."));
            }
        });
    }

    private void initJavascriptChecks() {
        LANGUAGE_CHECKS.put("JavaScript", List.of(
            (code, issues) -> {
                Pattern p = Pattern.compile("for\\s*\\([^)]+\\)\\s*\\{[^}]*await[^}]*\\}", Pattern.DOTALL);
                if (p.matcher(code).find()) issues.add(new Issue(2, "Performance", "high", "Sequential async calls in loop detected", "Each iteration awaits the previous one, blocking the event loop. Use Promise.all() for independent calls."));
            },
            (code, issues) -> {
                if (code.contains("var ")) issues.add(new Issue(1, "Best Practice", "medium", "Avoid using 'var' declaration", "'var' has function scoping which can lead to bugs. Use 'let' or 'const' for block-scoped variables."));
            },
            (code, issues) -> {
                if (code.contains("innerHTML") || code.contains("eval(")) issues.add(new Issue(0, "Security", "critical", "Potential XSS vulnerability", "Direct DOM manipulation with unsanitized input can lead to XSS. Use textContent or DOMPurify."));
            },
            (code, issues) -> {
                if (code.contains("console.log")) issues.add(new Issue(0, "Code Quality", "low", "Debugging statement in production code", "console.log should be removed or wrapped behind a debug flag in production."));
            },
            (code, issues) -> {
                if (code.contains("==") && !code.contains("===")) issues.add(new Issue(0, "Best Practice", "medium", "Use strict equality (===)", "Loose equality performs type coercion. Always use === for predictable comparisons."));
            },
            (code, issues) -> {
                if (code.contains("for (")) issues.add(new Issue(0, "Modern JS", "low", "Consider using for...of instead of traditional for loop", "for...of is more readable and less error-prone for iterating arrays."));
            }
        ));
    }

    private void initTypeScriptChecks() {
        List<LanguageCheck> checks = new ArrayList<>(LANGUAGE_CHECKS.get("JavaScript"));
        checks.add((code, issues) -> {
            if (code.contains(": any")) issues.add(new Issue(0, "Type Safety", "medium", "Avoid using 'any' type", "'any' defeats TypeScript's type checking. Use proper types or 'unknown' with type guards."));
        });
        checks.add((code, issues) -> {
            if (!code.contains("interface") && !code.contains("type ") && code.contains("function") && code.contains(":")) issues.add(new Issue(0, "Type Safety", "low", "Consider defining interfaces for complex objects", "Explicit interfaces improve readability and catch type errors at compile time."));
        });
        LANGUAGE_CHECKS.put("TypeScript", checks);
    }

    private void initPythonChecks() {
        LANGUAGE_CHECKS.put("Python", List.of(
            (code, issues) -> {
                if (code.contains("print(")) issues.add(new Issue(0, "Security", "medium", "print() used in production code", "Use logging module instead of print() for production applications."));
            },
            (code, issues) -> {
                if (code.contains("%") && (code.contains("%s") || code.contains("%d"))) issues.add(new Issue(0, "Best Practice", "low", "Use f-strings instead of % formatting", "f-strings are more readable and faster: f\"Hello {name}\" instead of \"Hello %s\" % name"));
            },
            (code, issues) -> {
                if (code.contains("except:") || code.contains("except :")) issues.add(new Issue(0, "Error Handling", "high", "Bare except clause", "Bare except catches all exceptions including KeyboardInterrupt. Use except SpecificException:"));
            },
            (code, issues) -> {
                if (code.contains("== None") || code.contains("!= None")) issues.add(new Issue(0, "Best Practice", "medium", "Use 'is None' instead of '== None'", "None is a singleton. Use 'is None' for identity comparison, not equality."));
            },
            (code, issues) -> {
                if (code.contains("open(") && !code.contains("with open")) issues.add(new Issue(0, "Resource Management", "high", "File handle may not be closed", "Use 'with' statement for file operations to ensure proper cleanup: with open(...) as f:"));
            },
            (code, issues) -> {
                if (code.contains("range(len(")) issues.add(new Issue(0, "Best Practice", "low", "Use enumerate() instead of range(len())", "for i, item in enumerate(items): is more Pythonic than for i in range(len(items)):"));
            },
            (code, issues) -> {
                Pattern p = Pattern.compile("(\\w+)\\s*=\\s*\\[\\s*\\]\\s*[\\s\\S]*?for\\s+\\w+\\s+in\\s+\\w+\\s*:[^}]*\\1\\.append");
                if (p.matcher(code).find()) issues.add(new Issue(0, "Best Practice", "low", "Use list comprehension instead of loop + append", "Use [expr for item in items] instead of creating an empty list and appending in a loop."));
            }
        ));
    }

    private void initJavaChecks() {
        LANGUAGE_CHECKS.put("Java", List.of(
            (code, issues) -> {
                if (code.contains("null") && !code.contains("Optional")) issues.add(new Issue(0, "Null Safety", "medium", "Possible NullPointerException", "Consider using Optional or @Nullable annotations to handle null cases explicitly."));
            },
            (code, issues) -> {
                if (code.contains("System.out.println") || code.contains("System.err.println")) issues.add(new Issue(0, "Code Quality", "low", "Use logger instead of System.out", "Use SLF4J/Logback logger for production logging instead of System.out.println."));
            },
            (code, issues) -> {
                if (code.contains("public ") && code.contains("void ") && !code.contains("private")) issues.add(new Issue(0, "Encapsulation", "medium", "Consider reducing visibility", "Fields and methods should have the most restrictive visibility possible. Prefer private over public."));
            },
            (code, issues) -> {
                if (code.contains("List<") && code.contains("ArrayList<") && !code.contains("var ")) issues.add(new Issue(0, "Modern Java", "low", "Use var for local variables", "Java 10+ supports var for local variable type inference, reducing boilerplate."));
            },
            (code, issues) -> {
                Pattern p = Pattern.compile("catch\\s*\\(\\w+\\s+\\w+\\)\\s*\\{\\s*\\}");
                if (p.matcher(code).find()) issues.add(new Issue(0, "Error Handling", "high", "Empty catch block", "Empty catch blocks silently swallow exceptions. Log or re-throw the exception."));
            },
            (code, issues) -> {
                if (code.contains("String ") && code.contains("+") && (code.contains("println") || code.contains("concat"))) issues.add(new Issue(0, "Performance", "low", "Use StringBuilder for string concatenation in loops", "String concatenation with + creates many intermediate objects. Use StringBuilder in loops."));
            }
        ));
    }

    private void initRustChecks() {
        LANGUAGE_CHECKS.put("Rust", List.of(
            (code, issues) -> {
                if (code.contains("unwrap()")) issues.add(new Issue(0, "Error Handling", "high", "Use expect() instead of unwrap()", "unwrap() panics without context. Use expect(\"message\") to provide meaningful panic messages."));
            },
            (code, issues) -> {
                if (code.contains("let mut") && !code.contains("&mut")) issues.add(new Issue(0, "Best Practice", "medium", "Prefer immutable bindings", "Use immutable (let) by default. Only use let mut when mutation is necessary."));
            },
            (code, issues) -> {
                if (code.contains("clone()")) issues.add(new Issue(0, "Performance", "medium", "Expensive clone() call", "clone() copies data. Consider borrowing (&) instead of cloning where possible."));
            },
            (code, issues) -> {
                if (code.contains("unsafe {")) issues.add(new Issue(0, "Safety", "critical", "Unsafe block detected", "Unsafe code bypasses Rust's safety guarantees. Minimize unsafe blocks and verify invariants."));
            },
            (code, issues) -> {
                if (!code.contains("impl") && !code.contains("#[derive") && code.contains("struct ")) issues.add(new Issue(0, "Best Practice", "low", "Derive common traits for structs", "Add #[derive(Debug, Clone, PartialEq)] to your structs for useful trait implementations."));
            },
            (code, issues) -> {
                if (code.contains("loop") && !code.contains("break")) issues.add(new Issue(0, "Logic", "high", "Potential infinite loop", "loop without a break condition will run forever. Ensure there's a termination path."));
            }
        ));
    }

    private void initGoChecks() {
        LANGUAGE_CHECKS.put("Go", List.of(
            (code, issues) -> {
                if (code.contains("_,") || code.contains("_ :=")) issues.add(new Issue(0, "Error Handling", "medium", "Ignored error return value", "Assign errors to _ discards them. Always check errors: if err != nil { return err }"));
            },
            (code, issues) -> {
                if (code.contains("if err != nil") && !code.contains("return") && !code.contains("panic")) issues.add(new Issue(0, "Error Handling", "medium", "Error checked but not handled", "After checking err != nil, you should return, log, or handle the error appropriately."));
            },
            (code, issues) -> {
                if (code.contains("defer") && code.contains("Close()")) {} // defer close is good
                else if (code.contains(".Close()")) issues.add(new Issue(0, "Resource Management", "medium", "Use defer to close resources", "Use defer file.Close() right after opening to ensure cleanup even on panic."));
            },
            (code, issues) -> {
                if (!code.contains("go ") && code.contains("func ") && code.contains("http")) issues.add(new Issue(0, "Concurrency", "low", "Consider using goroutines", "Go's goroutines make concurrency simple. Consider parallelizing independent tasks."));
            },
            (code, issues) -> {
                if (code.contains("fmt.Println") || code.contains("fmt.Print")) issues.add(new Issue(0, "Code Quality", "low", "Use structured logging instead of fmt.Print", "Consider using log or slog package for production logging instead of fmt.Print."));
            }
        ));
    }

    private void initCppChecks() {
        LANGUAGE_CHECKS.put("C++", List.of(
            (code, issues) -> {
                if (code.contains("new ") && !code.contains("delete") && !code.contains("unique_ptr") && !code.contains("shared_ptr"))
                    issues.add(new Issue(0, "Memory", "high", "Possible memory leak", "Raw new without delete causes memory leaks. Use smart pointers (unique_ptr, shared_ptr)."));
            },
            (code, issues) -> {
                if (code.contains("#include <iostream>") && code.contains("using namespace std"))
                    issues.add(new Issue(0, "Best Practice", "medium", "Avoid 'using namespace std'", "using namespace std pollutes the global namespace. Use std:: prefix instead."));
            },
            (code, issues) -> {
                if (code.contains("malloc(") || code.contains("calloc(") || code.contains("free("))
                    issues.add(new Issue(0, "Memory", "high", "C-style memory management", "Prefer new/delete or smart pointers over malloc/free in C++ code."));
            },
            (code, issues) -> {
                if (code.contains("char ") && code.contains("[")) issues.add(new Issue(0, "Safety", "critical", "C-style array detected", "C-style arrays have no bounds checking. Use std::array or std::vector instead."));
            },
            (code, issues) -> {
                if (code.contains("const_cast") || code.contains("reinterpret_cast"))
                    issues.add(new Issue(0, "Safety", "high", "Potentially unsafe cast", "const_cast and reinterpret_cast bypass type safety. Use only when absolutely necessary."));
            }
        ));
    }

    private void initCSharpChecks() {
        LANGUAGE_CHECKS.put("C#", List.of(
            (code, issues) -> {
                if (code.contains("var ") && code.contains("null") && !code.contains("?")) issues.add(new Issue(0, "Null Safety", "medium", "Enable nullable reference types", "Use nullable annotation context and ? suffix for nullable types to prevent NullReferenceException."));
            },
            (code, issues) -> {
                if (code.contains("ArrayList")) issues.add(new Issue(0, "Best Practice", "medium", "Use generic collections", "ArrayList is obsolete. Use List<T> for type-safe collections."));
            },
            (code, issues) -> {
                if (code.contains("catch") && code.contains("Exception") && code.contains("throw ex")) issues.add(new Issue(0, "Error Handling", "high", "throw ex resets stack trace", "Use 'throw;' instead of 'throw ex;' to preserve the original stack trace."));
            },
            (code, issues) -> {
                if (code.contains("using ") && !code.contains("await using") && code.contains("Async")) issues.add(new Issue(0, "Resource Management", "medium", "Use await using for async disposables", "Async disposal ensures proper cleanup of async resources with IAsyncDisposable."));
            },
            (code, issues) -> {
                if (code.contains("public ") && code.contains("class ") && !code.contains("sealed") && !code.contains("abstract"))
                    issues.add(new Issue(0, "Design", "low", "Consider sealing non-abstract classes", "Sealing classes that aren't designed for inheritance prevents misuse."));
            }
        ));
    }

    private void initRubyChecks() {
        LANGUAGE_CHECKS.put("Ruby", List.of(
            (code, issues) -> {
                if (code.contains("puts ") || code.contains("p ")) issues.add(new Issue(0, "Code Quality", "low", "Use logger instead of puts/p", "Use Rails.logger or Logging gem for production logging instead of puts."));
            },
            (code, issues) -> {
                if (code.contains("nil?") && !code.contains("&.")) issues.add(new Issue(0, "Best Practice", "medium", "Use safe navigation operator", "Use &. (safe navigation) instead of checking nil? to avoid verbose nil checks."));
            },
            (code, issues) -> {
                if (code.contains("unless ") && code.contains("!")) issues.add(new Issue(0, "Style", "low", "Avoid double negation in unless", "unless !condition is confusing. Use 'if condition' instead."));
            },
            (code, issues) -> {
                if (code.contains("for ") && code.contains(" in ")) issues.add(new Issue(0, "Best Practice", "low", "Use #each instead of for", "Ruby's for loop uses the same scope as its caller. Prefer collection.each do |item|"));
            },
            (code, issues) -> {
                if (code.contains("eval(") || code.contains("instance_eval") || code.contains("send("))
                    issues.add(new Issue(0, "Security", "critical", "Dynamic execution detected", "eval/send with user input can lead to code injection. Avoid or sanitize strictly."));
            }
        ));
    }

    private void initPhpChecks() {
        LANGUAGE_CHECKS.put("PHP", List.of(
            (code, issues) -> {
                if (code.contains("mysql_") || code.contains("mysqli_") && !code.contains("prepare")) issues.add(new Issue(0, "Security", "critical", "SQL injection vulnerability", "Use prepared statements (PDO) instead of raw SQL string concatenation."));
            },
            (code, issues) -> {
                if (code.contains("$_GET") || code.contains("$_POST") || code.contains("$_REQUEST") && !code.contains("filter_input") && !code.contains("htmlspecialchars"))
                    issues.add(new Issue(0, "Security", "high", "Unsanitized user input", "Sanitize all user input with filter_input() or htmlspecialchars() to prevent XSS."));
            },
            (code, issues) -> {
                if (code.contains("echo ") && (code.contains("$_") || code.contains("$"))) issues.add(new Issue(0, "Security", "medium", "XSS vulnerability in echo", "Escape output with htmlspecialchars() when echoing user-controlled data."));
            },
            (code, issues) -> {
                if (!code.contains("<?php") && !code.contains("<?=")) issues.add(new Issue(0, "Syntax", "high", "Missing PHP opening tag", "PHP files must start with <?php or <?="));
            },
            (code, issues) -> {
                if (code.contains("extract($_")) issues.add(new Issue(0, "Security", "critical", "extract() on user input is dangerous", "extract() can overwrite existing variables. Never use it with user input like $_GET or $_POST."));
            }
        ));
    }

    private void initSwiftChecks() {
        LANGUAGE_CHECKS.put("Swift", List.of(
            (code, issues) -> {
                if (code.contains("!") && (code.contains("as!") || code.contains("try!") || code.contains("!"))) issues.add(new Issue(0, "Safety", "high", "Force unwrapping detected", "Force unwrapping (!) will crash on nil. Use optional binding (if let) or guard let instead."));
            },
            (code, issues) -> {
                if (code.contains("var ") && !code.contains("let ") && code.contains("=")) issues.add(new Issue(0, "Best Practice", "medium", "Prefer let over var", "Use let (immutable) by default. Only use var when the value needs to change."));
            },
            (code, issues) -> {
                if (code.contains("class ") && !code.contains("final ") && !code.contains("struct ")) issues.add(new Issue(0, "Best Practice", "low", "Prefer struct over class", "Swift structs are value types with automatic thread safety. Prefer structs unless you need reference semantics."));
            },
            (code, issues) -> {
                if (code.contains("weak var") || code.contains("unowned var") && !code.contains("weak ")) issues.add(new Issue(0, "Memory", "medium", "Check for retain cycles", "Closures capturing self strongly can cause retain cycles. Use [weak self] in closure capture lists."));
            }
        ));
    }

    private void initKotlinChecks() {
        LANGUAGE_CHECKS.put("Kotlin", List.of(
            (code, issues) -> {
                if (code.contains("!!")) issues.add(new Issue(0, "Null Safety", "high", "Avoid !! (double-bang operator)", "!! throws NullPointerException if the value is null. Use safe calls (?.) or Elvis (?:) instead."));
            },
            (code, issues) -> {
                if (code.contains("var ") && !code.contains("val ")) issues.add(new Issue(0, "Best Practice", "medium", "Prefer val over var", "Use val (immutable reference) by default. Only use var when reassignment is necessary."));
            },
            (code, issues) -> {
                if (code.contains("companion object") && !code.contains("const val")) issues.add(new Issue(0, "Best Practice", "low", "Use const for compile-time constants", "Constants known at compile time should use const val instead of val in companion object."));
            },
            (code, issues) -> {
                if (!code.contains("data class") && code.contains("class ") && code.contains("toString") && code.contains("hashCode"))
                    issues.add(new Issue(0, "Design", "low", "Consider using data class", "data class automatically generates equals(), hashCode(), toString(), and copy() methods."));
            }
        ));
    }

    @FunctionalInterface
    private interface LanguageCheck {
        void check(String code, List<Issue> issues);
    }
}
