package com.architect.service;

import com.architect.model.ResearchResult;
import com.architect.model.ResearchResult.ResourceItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResearchService {

    private final Map<String, List<ResourceItem>> articleDb = new ConcurrentHashMap<>();
    private final Map<String, List<ResourceItem>> videoDb = new ConcurrentHashMap<>();
    private final Map<String, List<ResourceItem>> paperDb = new ConcurrentHashMap<>();

    public ResearchService() {
        articleDb.put("machine learning", List.of(
            new ResourceItem("ML Engineering Best Practices", "https://ml-engineering.org", "Comprehensive guide to production ML pipelines and MLOps.", "Google ML Blog"),
            new ResourceItem("Attention Is All You Need - Visual Guide", "https://jalammar.github.io/illustrated-transformer/", "A visual walkthrough of the Transformer architecture.", "Jay Alammar")
        ));
        articleDb.put("react hooks", List.of(
            new ResourceItem("React Hooks API Reference", "https://react.dev/reference/react/hooks", "Official React documentation for all built-in hooks.", "React Docs"),
            new ResourceItem("Custom Hooks Patterns", "https://usehooks.com", "Collection of reusable custom React Hooks.", "useHooks")
        ));
        articleDb.put("blockchain", List.of(
            new ResourceItem("Ethereum Developer Docs", "https://ethereum.org/developers/", "Official Ethereum development documentation and tutorials.", "Ethereum Foundation"),
            new ResourceItem("Blockchain Fundamentals", "https://developer.ibm.com/articles/blockchain-basics/", "IBM's introduction to blockchain technology and architecture.", "IBM Developer")
        ));

        videoDb.put("machine learning", List.of(
            new ResourceItem("Stanford CS229: Machine Learning", "https://www.youtube.com/playlist?list=PLoROMvodv4rMiGQp3WXShtMGgzqpfVfbU", "Andrew Ng's full Stanford ML course.", "Stanford Online"),
            new ResourceItem("3Blue1Brown - Neural Networks", "https://www.youtube.com/playlist?list=PLZHQObOWTQDNU6R1_67000Dx_ZCJB-3pi", "Visual, intuitive explanation of neural networks.", "3Blue1Brown")
        ));
        videoDb.put("react hooks", List.of(
            new ResourceItem("React Hooks Deep Dive", "https://www.youtube.com/watch?v=TNhaISOUy6Q", "Dan Abramov deep dive into React Hooks internals.", "React Conf")
        ));
        videoDb.put("blockchain", List.of(
            new ResourceItem("Blockchain Explained", "https://www.youtube.com/watch?v=SSo_EIwHSd4", "Simple explanation of blockchain technology.", "Simply Explained"),
            new ResourceItem("Solidity Smart Contracts", "https://www.youtube.com/playlist?list=PLO5VPQH6OWdX-Rh7Ron8GmE3lC9pMZmjE", "Complete Solidity smart contract development course.", "Dapp University")
        ));

        paperDb.put("machine learning", List.of(
            new ResourceItem("Attention Is All You Need", "https://arxiv.org/abs/1706.03762", "The Transformer architecture paper that revolutionized NLP.", "arXiv"),
            new ResourceItem("Deep Residual Learning", "https://arxiv.org/abs/1512.03385", "Deep residual networks for image recognition (ResNet).", "arXiv")
        ));
        paperDb.put("blockchain", List.of(
            new ResourceItem("Bitcoin: A Peer-to-Peer Electronic Cash System", "https://bitcoin.org/bitcoin.pdf", "The original Bitcoin whitepaper by Satoshi Nakamoto.", "Bitcoin.org"),
            new ResourceItem("Ethereum Whitepaper", "https://ethereum.org/en/whitepaper/", "The original Ethereum whitepaper by Vitalik Buterin.", "Ethereum Foundation")
        ));
    }

    public ResearchResult search(String topic, String filterType) {
        String key = topic.toLowerCase().trim();
        String summary = generateSummary(key);

        List<ResourceItem> articles = articleDb.getOrDefault(key, List.of(
            new ResourceItem(topic + " - Getting Started", "https://en.wikipedia.org/wiki/" + topic.replace(" ", "_"), "General overview and documentation.", "Wikipedia"),
            new ResourceItem(topic + " Developer Guide", "https://github.com/topics/" + topic.replace(" ", "-"), "Community resources and tools.", "GitHub")
        ));

        List<ResourceItem> videos = videoDb.getOrDefault(key, List.of(
            new ResourceItem("Introduction to " + topic, "https://www.youtube.com/results?search_query=" + topic.replace(" ", "+"), "Curated educational content.", "YouTube")
        ));

        List<ResourceItem> papers = paperDb.getOrDefault(key, List.of(
            new ResourceItem(topic + " - Academic Review", "https://scholar.google.com/scholar?q=" + topic.replace(" ", "+"), "Related academic papers and citations.", "Google Scholar")
        ));

        List<String> relatedTopics = switch (key) {
            case "machine learning" -> List.of("Deep Learning", "NLP", "Computer Vision", "MLOps", "Reinforcement Learning");
            case "react hooks" -> List.of("React Server Components", "State Management", "useEffect Patterns", "Custom Hooks", "Concurrent React");
            case "blockchain" -> List.of("Smart Contracts", "DeFi", "Layer 2 Scaling", "Solidity", "Zero-Knowledge Proofs");
            default -> List.of(topic + " Architecture", topic + " Best Practices", topic + " Tools", topic + " Performance", topic + " Security");
        };

        return new ResearchResult(topic, summary, articles, videos, papers, relatedTopics);
    }

    private String generateSummary(String topic) {
        return switch (topic) {
            case "machine learning" ->
                "Machine Learning is a subset of artificial intelligence that enables systems to learn and improve from experience. "
                + "Modern ML encompasses deep learning, supervised/unsupervised learning, and reinforcement learning. "
                + "Key frameworks include TensorFlow, PyTorch, and scikit-learn. The field is rapidly evolving with advances in LLMs, "
                + "computer vision, and MLOps practices for production deployments.";
            case "react hooks" ->
                "React Hooks, introduced in React 16.8, allow functional components to use state and lifecycle features. "
                + "Core hooks include useState, useEffect, useContext, and useReducer. Custom hooks enable reusable logic. "
                + "Best practices include proper dependency arrays, avoiding stale closures, and following the Rules of Hooks.";
            case "blockchain" ->
                "Blockchain is a distributed ledger technology that ensures data immutability through cryptographic hashing "
                + "and consensus mechanisms. Major platforms include Ethereum, Bitcoin, and Solana. Smart contracts enable "
                + "programmable transactions. Current trends include DeFi, NFTs, Layer 2 scaling solutions, and enterprise blockchain.";
            default ->
                topic + " encompasses a broad ecosystem of tools, practices, and technologies. "
                + "Modern approaches emphasize modularity, scalability, and developer experience. "
                + "Stay current with official documentation, community best practices, and emerging standards in the field.";
        };
    }
}
