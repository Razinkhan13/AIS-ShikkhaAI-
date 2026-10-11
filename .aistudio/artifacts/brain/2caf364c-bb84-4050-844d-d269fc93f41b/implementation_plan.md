# AIS ShikkhaAI (এআইএস শিক্ষা এআই) — NCTB & LearnLM Academic Transformation Platform

A comprehensive, production-ready AI-driven mobile learning platform designed for Bangladeshi secondary (SSC) and higher secondary (HSC) students. Grounded strictly in the National Curriculum and Textbook Board (NCTB) syllabus, Bloom's Taxonomy, and Google's LearnLM pedagogical principles, it eliminates commercial coaching dependency through zero-friction, point-to-point learning.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following core product and architectural choices were confirmed:

- **Target Grades & Starter Curriculum**: **Both SSC (Class 9–10) and HSC (Class 11–12) Science**, with curated starter cognitive units across Physics, Chemistry, Biology, and Bangla 1st Paper (e.g., Physics *কাজ, ক্ষমতা ও শক্তি*, *গতি*, *আলোর প্রতিফলন*; Chemistry *পদার্থের গঠন*; Biology *কোষ ও এর গঠন*; Bangla *শুভা* ও *বই পড়া*).
- **Audio Priming Delivery**: **Compact audio summary card with instant chapter overview**, featuring dual-speaker conversational Bengali podcast audio player, synced text snippet viewer, and speed controls (0.75x, 1x, 1.25x, 1.5x) respecting the 0.3-second dead-space trimming rule.
- **AI Evaluation & Socratic Engine**: **Dual-Engine Architecture** integrating live **Gemini 3.5 Flash** with LearnLM pedagogical system prompts alongside an **Offline Rule-Based Rubrics Validator** ensuring 100% availability even on spotty 2G/3G connections.

---

### 1. Overview & Core Concept

- **What It Does**: AIS ShikkhaAI implements the **4-Step Academic Mastery Funnel** from the strategic blueprint:
  1. **Audio Priming (১০ মিনিট)**: Grounded two-host conversational Bengali audio overview (NotebookLM style) to mentally map the chapter prior to textbook reading.
  2. **Concept Drill & Visual Mindmap (২০ মিনিট)**: NCTB textbook chunk reading accompanied by the **Squint Test (3-Sentence Essence)** and an interactive hierarchical mindmap (Core $\rightarrow$ Branches $\rightarrow$ Leaves/Formulas/Analogy).
  3. **LearnLM Socratic Doubt Solver (১৫ মিনিট)**: Pedagogical AI tutor that refuses blind spoon-feeding, accepts conversational Bengali and Banglish inputs, translates them into formal Bengali concepts, and guides the student step-by-step through cognitive scaffolding and a 10-second retention check.
  4. **Diagnostic Self-Test & Mistake Vault (১৫ মিনিট)**: 10 targeted MCQs and 1 official Creative Question (CQ) evaluated against NCTB Chief Examiner rubrics across all 4 Bloom cognitive levels:
     - **ক (জ্ঞানমূলক, ১ নম্বর)**: Exact textbook fact/definition.
     - **খ (অনুধাবনমূলক, ২ নম্বর)**: 2-paragraph causal explanation.
     - **গ (প্রয়োগমূলক, ৩ নম্বর)**: 3-paragraph stimulus-to-theory synthesis or calculation.
     - **ঘ (উচ্চতর দক্ষতা, ৪ নম্বর)**: 4-paragraph critical judgment and comparative analysis.
  5. **100:6:1 Funnel Analytics & Mistake Vault**: Automatically captures weak points into a persistent Mistake Vault until full chapter mastery is achieved.
- **Target Audience / Persona**: Class 9–12 students in Bangladesh preparing for SSC and HSC public board examinations, especially students outside metropolitan areas seeking equal-quality private academic tutoring.
- **Key Value**: 100% free, zero-friction access to elite private tutoring, eliminating the ৳৪,০০০–৳৮,০০০/month coaching barrier while strictly adhering to official NCTB marking schemes.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Home & Subject Selector**: Student selects Class (SSC Class 9–10 or HSC Class 11–12) and subject (Physics, Chemistry, Biology, Bangla). A visual progress card displays their **100:6:1 Funnel** status and Mistake Vault count.
2. **Chapter Mastery Hub**: Student enters a chapter (e.g., *কাজ, ক্ষমতা ও শক্তি* / *Work, Power & Energy*) and sees the 4-step progress tabs:
   - **Tab 1: Audio Priming**: Dual-host Bengali audio podcast player with play/pause, seek bar, playback speed (0.75x, 1x, 1.25x, 1.5x), and live interactive transcript highlighting.
   - **Tab 2: Concept Drill & Mindmap**:
     - *Squint Test Card*: 3 bold, high-retention Bengali sentences summarizing the core principle.
     - *Bangladeshi Real-Life Analogy*: Intuitive real-world relatable breakdown (cricket ball catch, moving bus, lifting bucket from well).
     - *Interactive Mindmap*: Multi-level expandable nodes (Core $\rightarrow$ Main Concepts $\rightarrow$ Mathematical Laws/Units $\rightarrow$ Common Pitfalls).
   - **Tab 3: Socratic AI Tutor**:
     - Natural text input supporting standard Bengali and Banglish (e.g., *"uttol dorpon e keno obastob protibimbo hoy?"*).
     - Live translation indicator & LearnLM Socratic conversation showing cognitive scaffolding.
   - **Tab 4: Board Self-Test & Mistake Vault**:
     - 10-question MCQ quiz with instant reasoning breakdown.
     - 1 full Creative Question (CQ) with real board stimulus and ক, খ, গ, ঘ input fields.
     - Instant AI Marking Breakdown providing marks out of 10 with specific rubric feedback.
     - "Send to Mistake Vault" button for flagged errors.
3. **Mistake Vault & 100:6:1 Tracker**: Dedicated tab reviewing saved weak points, explaining textbook keyword alignment, and tracking progress toward 100 questions practiced, 6 weaknesses diagnosed, and 1 complete chapter mastered.

#### Visual Identity & Theme
- **Aesthetic Direction**: *Academic Emerald & Solar Amber* — inspired by Bangladesh's national educational identity, combined with a crisp modern Material 3 design system.
- **Color Palette**:
  - Primary: Deep Emerald (`#0A5C36`)
  - Primary Container: Soft Mint (`#D1F2DF`)
  - Secondary / Accent: Solar Gold (`#F59E0B`)
  - Surface / Background: Crisp Off-White (`#F8FAFC`) with Charcoal text (`#0F172A`)
  - Dark Mode: Obsidian Slate (`#0F172A`) with Luminous Emerald (`#10B981`)
  - Mistake Vault Alert: Soft Coral (`#FEE2E2`) and Crimson (`#DC2626`)
- **Typography & Hierarchy**:
  - Display: Bold, clear Bengali-optimized typography with proper line-height for diacritics (হসন্ত, কার).
  - Body: 14–16sp high-legibility clean typography.
  - Math & Formulas: Clear monospace badges with units ($\text{Joule}$, $\text{Watt}$, $\text{kg}\cdot\text{m/s}$).
- **Accessibility & Safe Areas**:
  - Minimum touch targets of 48dp on all interactive audio controls, quiz options, and collapsible nodes.
  - Edge-to-edge support with `WindowInsets.safeDrawing` padding.

---

### 3. Key Product Decisions & Trade-Offs

- **Dual-Engine Architecture (Gemini 3.5 Flash + Offline Rubrics Engine)**:
  - *Chosen Approach*: Query the Gemini API using `BuildConfig.GEMINI_API_KEY` for adaptive Socratic responses and natural language grading. If offline or if the API key is not configured, automatically switch to an in-memory NCTB Rubric Engine with pre-computed Bloom's taxonomy heuristics and keyword matching.
  - *Why*: Ensures students in rural areas or with limited mobile data can study seamlessly without errors or blank screens.
- **Local Persistence via Room Database**:
  - *Chosen Approach*: Persist chapter completion, quiz history, Mistake Vault items, and Funnel metrics locally via Room.
  - *Why*: In line with the blueprint's zero-friction policy, students do not need to create accounts or authenticate just to start studying.
- **Banglish-to-Bengali Pre-processor**:
  - *Chosen Approach*: Built-in phonetics dictionary and prompt instructions that accept Latin-script phonetic Bengali (*"karon ki"*, *"kivabe"*), convert it to academic terminology, and respond in encouraging, formal Bengali.
  - *Why*: Matches actual student phone-typing behavior while guiding them toward official board language.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                      AIS ShikkhaAI Presentation Layer                  │
│                                                                        │
│   ┌─────────────────────┐   ┌──────────────────────────────────────┐   │
│   │   Home & Selector   │   │     4-Step Academic Mastery Hub      │   │
│   │  (SSC/HSC Streams)  │   │  ┌────────────────────────────────┐  │   │
│   └──────────┬──────────┘   │  │ Tab 1: Audio Priming (Podcast) │  │   │
│              │              │  ├────────────────────────────────┤  │   │
│   ┌──────────▼──────────┐   │  │ Tab 2: Concept & Mindmap       │  │   │
│   │ 100:6:1 Funnel View │   │  ├────────────────────────────────┤  │   │
│   │  & Mistake Vault    │   │  │ Tab 3: LearnLM Socratic Chat   │  │   │
│   └─────────────────────┘   │  ├────────────────────────────────┤  │   │
│                             │  │ Tab 4: MCQ & CQ Board Exam     │  │   │
│                             │  └────────────────────────────────┘  │   │
│                             └───────────────────▲──────────────────┘   │
└─────────────────────────────────────────────────┼──────────────────────┘
                                                  │
                                     ViewModel & StateFlow
                                                  │
┌─────────────────────────────────────────────────▼──────────────────────┐
│                            Domain & Engine Layer                       │
│                                                                        │
│   ┌───────────────────────────┐      ┌─────────────────────────────┐   │
│   │   Gemini 3.5 Flash API    │      │  Offline NCTB Rubric Engine │   │
│   │   - LearnLM Prompt 3      │◄────►│  - Bloom ক, খ, গ, ঘ matcher │   │
│   │   - CQ Chief Examiner P1  │      │  - Keyword verification     │   │
│   │   - Banglish Preprocessor │      │  - Fallback feedback        │   │
│   └───────────────────────────┘      └─────────────────────────────┘   │
│                                                                        │
│   ┌───────────────────────────┐      ┌─────────────────────────────┐   │
│   │  Audio Engine & Player    │      │  Hierarchical Mindmap Graph │   │
│   │  - Synthesized / Cached   │      │  - Squint 3-Sentence Essence│   │
│   │  - Synced Text Transcript │      │  - Bangladeshi Analogies    │   │
│   └───────────────────────────┘      └─────────────────────────────┘   │
└─────────────────────────────────────────────────▲──────────────────────┘
                                                  │
┌─────────────────────────────────────────────────▼──────────────────────┐
│                        Data & Storage Layer (Room)                     │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │  - Chapters & Cognitive Chunks (NCTB syllabus curriculum)       │   │
│   │  - Mistake Vault Items (flagged questions, weakness notes)     │   │
│   │  - Funnel Progress (MCQs completed, weak points diagnosed)     │   │
│   └────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

#### Key Entities & State Models
- `Chapter`: ID, Subject, Grade (SSC/HSC), Chapter Name (e.g. *কাজ, ক্ষমতা ও শক্তি*), Squint Essence (3 bold sentences), Real-world Analogy, Formula & Pitfalls list, Audio Duration, Audio Transcript snippets.
- `MindmapNode`: ID, Title, Bengali Description, Sub-branches, Leaves (Formulas / Units / Examples).
- `MCQQuestion`: ID, ChapterID, Question Bengali, Options (4), CorrectOptionIndex, Textbook Explanation.
- `CreativeQuestion`: ID, ChapterID, Stimulus (উদ্দীপক), QuestionKa (জ্ঞানমূলক), QuestionKha (অনুধাবনমূলক), QuestionGa (প্রয়োগমূলক), QuestionGha (উচ্চতর দক্ষতা), ModelAnswers & KeywordRubrics.
- `MistakeVaultEntry`: ID, ChapterID, QuestionType (MCQ/CQ), QuestionTitle, StudentAnswer, RubricErrorNote, Timestamp, ResolvedState.
- `FunnelProgress`: QuestionsAttempted (target: 100), WeaknessesIdentified (target: 6), ChaptersMastered (target: 1).

---

### Step-by-Step Implementation Sequence

1. **Platform Identity & Gradle Setup**:
   - Update `metadata.json` with app name `"AIS ShikkhaAI"` and description.
   - Update `res/values/strings.xml` with `"AIS ShikkhaAI"` (`এআইএস শিক্ষা এআই`).
   - Configure `.env.example` with `GEMINI_API_KEY`.
   - Update `app/build.gradle.kts` with required Kotlin Serialization / Retrofit plugins and Room configurations.
2. **Data & Curriculum Foundation**:
   - Create Room entities, DAO, and Database for Curriculum, Mistakes, and Funnel Metrics.
   - Populate authentic NCTB syllabus datasets for Class 9–10 (SSC) and Class 11–12 (HSC) in Science (Physics, Chemistry, Biology) and Bangla.
3. **AI & Evaluation Engines**:
   - Build `GeminiClient` with `gemini-3.5-flash` using Retrofit and 60-second timeouts.
   - Implement the 3 master prompts from the blueprint:
     - Prompt 1: NCTB CQ Chief Examiner marking & generation.
     - Prompt 2: Concept Simplifier & Mindmap breakdown.
     - Prompt 3: LearnLM Socratic Doubt Solver with Banglish pre-processing.
   - Build the Offline Heuristic Fallback engine for zero-network resilience.
4. **Interactive UI Screens & Components**:
   - **Audio Player Component**: Progress bar, speed selector, play/pause, dual-speaker transcript follower.
   - **Concept & Visual Mindmap Component**: Expandable tree nodes, 3-sentence Squint Test badge, formula highlight cards.
   - **LearnLM Socratic Chat Component**: Interactive chat bubble interface with scaffolding indicators and retention quizzes.
   - **Diagnostic Exam & CQ Evaluation Screen**: Split tabs for 10 MCQs and 4-tier CQ answering (ক, খ, গ, ঘ) with instant AI feedback.
   - **Mistake Vault & 100:6:1 Funnel Dashboard**: Visual circular and bar progress tracking 100 $\rightarrow$ 6 $\rightarrow$ 1 mastery funnel.
5. **Compilation, Verification & Edge Cases**:
   - Run `compile_applet` to verify clean build without warnings.
   - Ensure edge-to-edge window insets, smooth navigation backstack handling, and 48dp touch targets.
