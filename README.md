# KnowledgeStack Platform

AI-assisted developer and research platform with four integrated tools for modern engineering workflows. Built with Spring Boot + Thymeleaf, styled with a dark industrial design system.

## Website

**KnowledgeStack** presents a sophisticated research and development dashboard with a dark industrial aesthetic. The interface is designed around a **"Sophisticated Industrial"** visual language — blending the tactile quality of premium craftsmanship with the precision of modern software engineering.

### Design System

**Philosophy:** Tactile Minimalism — deep, textured backgrounds evoke dark wood or parchment, with "modal-style" content cards featuring high-contrast typography. Every interface element feels like a curated artifact within a frame.

**Colors:**
- **Surface (#131313):** Deep ebony background with subtle noise texture — the canvas for the entire experience
- **Primary (#ffffff):** High-contrast white for essential typography and borders
- **Secondary (#a7c8ff):** Cool blue accent for interactive states, links, and progress indicators
- **Tertiary (#ffe088 / #D4AF37):** Muted brass/gold for premium call-to-action moments
- **Error (#ffb4ab):** Soft red for vulnerability and error states

**Typography:**
- **Playfair Display** — serif for high-impact headlines and hero statements; literary and premium
- **Manrope** — balanced sans-serif for body copy and documentation
- **IBM Plex Sans** — technical sans-serif for labels, nav items, code metadata

**Layout:**
- Fixed-width 1200px centered content grid
- 72px sticky navbar with backdrop blur
- 8px spacing scale with 48px card padding on desktop
- Mobile-responsive: single column, 16px margins

**Components:**
- **Glass cards** — semi-transparent surfaces with gradient border highlights
- **Brass navigation dots** — active link indicator in #D4AF37
- **Noise overlay** — subtle texture prevents flatness on dark surfaces
- **Glow orbs** — ambient background light effects (blue/gold)

### Home Page (`/`)

The landing page features:
- Hero section with "Engineering Intelligence" headline and global search bar
- Three tool cards: **Start Research** (featured), **Review Code**, **Tech Ideas**
- Recent Research Nodes table with status badges (Processing, Archived, Flagged)
- Navigation links across the top with active brass-dot indicator
- Footer with documentation, privacy, API status, and support links

### Research Page (`/research`)

Technical research aggregator with:
- Query input for topic-based search
- Filter tabs: All, Articles, Videos, Papers
- Results grid with AI-generated summary
- Related topics sidebar
- Results cards with source badges, descriptions, and external links

### Code Review Page (`/code-review`)

Engineer's workbench for static analysis:
- Dual-pane layout: source editor (left) and AI insights panel (right)
- Language selector with 12-language dropdown plus auto-detect
- Beginner Mode toggle for simplified explanations
- Issue cards with color-coded severity (critical/high/medium/low)
- Metrics row: Code Complexity, Security Rating, Engine Overhead, Refactor Index
- Fixed-code section with one-click apply to replace source
- Suggestions panel with code examples

### Tech Ideas Page (`/tech-ideas`)

AI-powered idea generation engine (via DeepSeek):
- **Sidebar parameters** — Industry (AI/Web Dev/Fintech/Health), Skill Level, Goal, Tech Stack preferences
- **Idea cards** — title, description, tech stack, and scale (Startup/Enterprise/Infrastructure/Research/Learning)
- **Market Trends** panel — 5 curated trends for the selected industry
- **Recommended Tools** panel — suggested frameworks, APIs, and platforms
- Falls back to a canned idea database if AI is unreachable

## AI-Powered Features

Both **Code Review** and **Tech Ideas** use DeepSeek via OpenRouter for AI generation, with automatic fallback to built-in logic when the API is unreachable.

### Code Review — 12 Supported Languages

JavaScript, TypeScript, Python, Java, Rust, Go, C++, C#, Ruby, PHP, Swift, Kotlin

- **AI analysis** — context-aware issue detection, explanations, and auto-fixed code via DeepSeek
- **Pattern-matching fallback** — 60+ language-specific rules when AI is unreachable
- **Beginner Mode** — simplified explanations for junior developers
- **Metrics** — complexity, security rating, engine overhead, refactor index

### Tech Ideas — Any Industry

- **AI generation** — unique ideas, trends, and tool recommendations customized to industry, skill level, goal, and tech stack preferences
- **Canned fallback** — curated database of 12 pre-built ideas across AI, Web Dev, Fintech, and Health

## Quick Start

```bash
npm install
npm run build:css
docker compose up -d
```

Open http://localhost:8080

### Deployed Site

Open https://architect-platform.containers.snapdeploy.dev

### Build from source

```bash
npm install
npm run build:css
docker compose build --no-cache
docker compose up -d
```

## API Endpoints

All POST endpoints accept JSON and return JSON.

### `POST /api/code-review/analyze`

```json
{
  "code": "fn main() { println!(\"hello\"); }",
  "language": "",
  "beginnerMode": false
}
```

Fields:
- `code` (required) — source code to review
- `language` (optional) — one of the 12 supported languages; leave blank for auto-detect
- `beginnerMode` (optional) — simplifies explanations for junior developers

### `POST /api/research/search`

```json
{
  "topic": "quantum computing",
  "filterType": "all"
}
```

`filterType` values: `all`, `articles`, `videos`, `papers`

### `POST /api/tech-ideas/generate`

```json
{
  "industry": "AI",
  "skillLevel": "Advanced",
  "goal": "startup",
  "techStack": ["Python", "TypeScript"]
}
```

Fields:
- `industry` (optional) — one of `AI`, `Web Dev`, `Fintech`, `Health`, or any text
- `skillLevel` (optional) — `Advanced`, `Intermediate`, `Beginner`
- `goal` (optional) — e.g. `startup`, `project`, `roadmap`
- `techStack` (optional) — array of preferred technologies to incorporate

## AI Configuration

Both **Code Review** and **Tech Ideas** use **DeepSeek** via **OpenRouter**. Configure via environment variables:

| Variable | Default |
|---|---|
| `OPENROUTER_API_KEY` | `sk-or-v1-...` (embedded fallback) |
| `ai.openrouter.model` | `deepseek/deepseek-chat` |
| `ai.openrouter.base-url` | `https://openrouter.ai/api/v1` |

Set via `docker-compose.yml` environment section:

```yaml
environment:
  - OPENROUTER_API_KEY=sk-or-v1-your-key-here
```

## Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Thymeleaf** server-side templates
- **Tailwind CSS** (CDN, dark industrial design system with custom color palette and typography)
- **Google Material Symbols** for iconography
- **WebClient** (Spring WebFlux) for AI API calls
- **Docker** multi-stage build (Maven build → JRE runtime)

## Project Structure

```
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/main/java/com/architect/
│   ├── ArchitectApplication.java
│   ├── controller/
│   │   ├── PageController.java          # Route definitions for all 4 pages
│   │   ├── CodeReviewController.java
│   │   ├── ResearchController.java
│   │   └── TechIdeasController.java
│   ├── model/
│   │   ├── CodeReviewRequest.java
│   │   ├── CodeReviewResult.java
│   │   ├── ResearchRequest.java
│   │   ├── ResearchResult.java
│   │   ├── TechIdeasRequest.java
│   │   └── TechIdeasResult.java
│   └── service/
│       ├── AiCodeReviewService.java     # DeepSeek code review (OpenRouter)
│       ├── AiTechIdeasService.java      # DeepSeek idea generation (OpenRouter)
│       ├── CodeReviewService.java       # Pattern engine + AI fallback
│       ├── ResearchService.java         # Canned research DB
│       └── TechIdeasService.java        # Idea generator + AI fallback
├── src/main/resources/
│   ├── application.properties
│   ├── static/favicon.svg
│   └── templates/
│       ├── index.html
│       ├── research.html
│       ├── code-review.html
│       └── tech-ideas.html
└── artisan_logic/
    └── DESIGN.md                        # Full design system specification
```
