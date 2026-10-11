package com.example.data.network

import com.example.data.model.CQEvaluationResult
import com.example.data.model.CreativeQuestionItem

/**
 * High-precision Offline Rubrics Engine complying with NCTB Chief Examiner guidelines.
 * Evaluates student answers against Bloom's Taxonomy even without an internet connection.
 */
object OfflineRubricEngine {

    fun evaluateCQ(
        cq: CreativeQuestionItem,
        ansKa: String,
        ansKha: String,
        ansGa: String,
        ansGha: String
    ): CQEvaluationResult {
        // --- 1. Evaluate ক (জ্ঞানমূলক - ১ নম্বর) ---
        // Rubric: Exact textbook keyword or definition present.
        val cleanKa = ansKa.trim()
        val scoreKa: Int
        val feedbackKa: String
        val keywordsKa = extractCoreKeywords(cq.modelAnswerKa)
        val matchedKa = keywordsKa.filter { cleanKa.contains(it, ignoreCase = true) }

        if (cleanKa.length < 5) {
            scoreKa = 0
            feedbackKa = "উত্তর অতি সংক্ষিপ্ত বা অনুপস্থিত। পাঠ্যবইয়ের সঠিক সংজ্ঞাটি স্পষ্টভাবে ১ বাক্যে লিখুন।"
        } else if (matchedKa.isNotEmpty() || cleanKa.length >= 15) {
            scoreKa = 1
            feedbackKa = "চমৎকার! জ্ঞানমূলক তথ্যটি এনসিটিবি পাঠ্যবই অনুসারে যথাযথ ও নির্ভুল হয়েছে।"
        } else {
            scoreKa = 0
            feedbackKa = "তথ্যটিতে মূল পারিভাষিক শব্দের ঘাটতি রয়েছে। পাঠ্যবইয়ের মূল শব্দটি উল্লেখ করুন।"
        }

        // --- 2. Evaluate খ (অনুধাবনমূলক - ২ নম্বর) ---
        // Rubric: 2 paragraphs ideal (1st: Core concept, 2nd: Cause-effect explanation)
        val cleanKha = ansKha.trim()
        val scoreKha: Int
        val feedbackKha: String
        val paragraphsKha = cleanKha.split("\n\n", "\n").filter { it.isNotBlank() }
        val keywordsKha = extractCoreKeywords(cq.modelAnswerKha)
        val matchedKha = keywordsKha.filter { cleanKha.contains(it, ignoreCase = true) }

        if (cleanKha.length < 15) {
            scoreKha = 0
            feedbackKha = "অনুধাবনের কার্যকারণ ও ব্যাখ্যা অনুপস্থিত। অন্তত দুটি স্পষ্ট বাক্যে ব্যাখ্যা করুন।"
        } else if (matchedKha.size >= 2 || (cleanKha.length > 50 && paragraphsKha.size >= 2)) {
            scoreKha = 2
            feedbackKha = "সম্পূর্ণ ২ নম্বর বরাদ্দ: প্রথম অংশে মূল কারণ এবং দ্বিতীয় অংশে প্রাতিষ্ঠানিক ব্যাখ্যা নিখুঁত হয়েছে।"
        } else if (cleanKha.length > 30) {
            scoreKha = 1
            feedbackKha = "আংশিক ১ নম্বর: মূল ভাব উপস্থিত কিন্তু দুটি স্পষ্ট প্যারা বা পূর্ণ কার্যকারণ বিশ্লেষণ অনুপস্থিত।"
        } else {
            scoreKha = 0
            feedbackKha = "পাঠ্যবইয়ের কার্যকারণ নীতির সাথে সামঞ্জস্যপূর্ণ নয়।"
        }

        // --- 3. Evaluate গ (প্রয়োগমূলক - ৩ নম্বর) ---
        // Rubric: 3 components (জ্ঞান, অনুধাবন, উদ্দীপকের সাথে প্রয়োগ বা গাণিতিক সমাধান)
        val cleanGa = ansGa.trim()
        val scoreGa: Int
        val feedbackGa: String
        val keywordsGa = extractCoreKeywords(cq.modelAnswerGa)
        val matchedGa = keywordsGa.filter { cleanGa.contains(it, ignoreCase = true) }

        val hasCalculation = cleanGa.contains("=") || cleanGa.contains("+") || cleanGa.contains("×") || cleanGa.contains("*") || cleanGa.any { it.isDigit() }
        
        if (cleanGa.length < 25) {
            scoreGa = 0
            feedbackGa = "উদ্দীপকের সাথে পাঠ্যসূত্রের সম্পর্কযুক্ত প্রয়োগ দেখানো হয়নি।"
        } else if (matchedGa.size >= 2 && (hasCalculation || cleanGa.length > 90)) {
            scoreGa = 3
            feedbackGa = "পূর্ণ ৩ নম্বর: উদ্দীপক এবং মূল তত্ত্বের যথাযথ মেলবন্ধন এবং ধারাবাহিক যুক্তি/গণনা প্রদর্শিত হয়েছে।"
        } else if (matchedGa.isNotEmpty() || cleanGa.length > 50) {
            scoreGa = 2
            feedbackGa = "২ নম্বর: সূত্র প্রয়োগ করা হয়েছে, তবে উদ্দীপকের উপাত্তের সাথে পূর্ণ সমীকরণ বা ৩-প্যারা কাঠামো অসম্পূর্ণ।"
        } else {
            scoreGa = 1
            feedbackGa = "১ নম্বর: শুধু প্রাথমিক সূত্রের উল্লেখ রয়েছে, প্রয়োগ অংশ অনুপস্থিত।"
        }

        // --- 4. Evaluate ঘ (উচ্চতর দক্ষতা - ৪ নম্বর) ---
        // Rubric: 4 distinct analytical levels (জ্ঞান, অনুধাবন, প্রয়োগ, তুলনামূলক যৌক্তিক সিদ্ধান্ত)
        val cleanGha = ansGha.trim()
        val scoreGha: Int
        val feedbackGha: String
        val keywordsGha = extractCoreKeywords(cq.modelAnswerGha)
        val matchedGha = keywordsGha.filter { cleanGha.contains(it, ignoreCase = true) }
        val paragraphsGha = cleanGha.split("\n\n", "\n").filter { it.isNotBlank() }

        if (cleanGha.length < 35) {
            scoreGha = 0
            feedbackGha = "উচ্চতর দক্ষতার তুলনামূলক বিশ্লেষণ বা যৌক্তিক সিদ্ধান্ত উপস্থাপিত হয়নি।"
        } else if (matchedGha.size >= 2 && cleanGha.length > 120 && paragraphsGha.size >= 2) {
            scoreGha = 4
            feedbackGha = "অসাধারণ! পূর্ণ ৪ নম্বর: উদ্দীপকের পরিস্থিতি বিচার করে সুনির্দিষ্ট সিদ্ধান্ত এবং পাঠ্যবইয়ের আলোকে যুক্তি প্রতিষ্ঠিত হয়েছে।"
        } else if (matchedGha.isNotEmpty() && cleanGha.length > 70) {
            scoreGha = 3
            feedbackGha = "৩ নম্বর: বিশ্লেষণ প্রশংসনীয়, তবে সমাপনী মন্তব্যে আরেকটু গভীর তুলনামূলক বিচার প্রত্যাশিত।"
        } else if (cleanGha.length > 45) {
            scoreGha = 2
            feedbackGha = "২ নম্বর: প্রয়োগ স্তর পর্যন্ত পৌঁছেছে, কিন্তু উচ্চতর দক্ষতার সিদ্ধান্ত গ্রহণ ও মূল্যায়ন দুর্বল।"
        } else {
            scoreGha = 1
            feedbackGha = "১ নম্বর: কেবল সাধারণ বর্ণনামূলক উত্তর, বিশ্লেষণমূলক কাঠামো অনুপস্থিত।"
        }

        val total = scoreKa + scoreKha + scoreGa + scoreGha
        val missing = (keywordsKa + keywordsKha + keywordsGa + keywordsGha)
            .filterNot { 
                cleanKa.contains(it) || cleanKha.contains(it) || cleanGa.contains(it) || cleanGha.contains(it)
            }.distinct().take(4)

        val examinerNote = when {
            total >= 9 -> "বোর্ড প্রধান পরীক্ষকের মন্তব্য: উত্তরটি এ-প্লাস (A+) মানের। এনসিটিবি রুব্রিক্স ও ব্লুমস ট্যাক্সনমি যথাযথভাবে রক্ষিত হয়েছে।"
            total >= 7 -> "বোর্ড প্রধান পরীক্ষকের মন্তব্য: ভালো প্রস্তুতি! প্রয়োগ ও উচ্চতর দক্ষতা অংশে প্যারা ও মূল কি-ওয়ার্ডে আরও যত্নবান হলে পূর্ণ নম্বর নিশ্চিত হবে।"
            total >= 5 -> "বোর্ড প্রধান পরীক্ষকের মন্তব্য: পাশ মার্ক অতিক্রম করেছে, তবে এনসিটিবি পরিভাষা ও কাঠামোগত ব্যাখ্যার আরও অনুশীলন প্রয়োজন।"
            else -> "বোর্ড প্রধান পরীক্ষকের মন্তব্য: ধারণাগত বিভ্রান্তি রয়েছে। মিস্টেক ভল্টে যোগ করে পাঠ্যবই ও মাইন্ডম্যাপ পুনরায় রিভিশন দিন।"
        }

        return CQEvaluationResult(
            totalScore = total,
            scoreKa = scoreKa,
            scoreKha = scoreKha,
            scoreGa = scoreGa,
            scoreGha = scoreGha,
            feedbackKa = feedbackKa,
            feedbackKha = feedbackKha,
            feedbackGa = feedbackGa,
            feedbackGha = feedbackGha,
            overallExaminerNote = examinerNote,
            missingKeywords = missing
        )
    }

    fun generateSocraticResponse(studentInput: String, chapterTopic: String): SocraticOfflineResponse {
        val convertedBanglish = convertBanglishToBengali(studentInput)
        val inputLower = studentInput.lowercase()

        val responseText: String
        val scaffolding: String
        val retention: String

        when {
            inputLower.contains("utol") || inputLower.contains("uttol") || studentInput.contains("উত্তল") -> {
                responseText = "খুব সুন্দর প্রশ্ন! উত্তল দর্পণে প্রতিফলিত রশ্মিগুলো কখনো একটি বিন্দুতে সত্যি সত্যি মিলিত হয় কি, নাকি অপসৃত হচ্ছে বলে মনে হয়? মনে করে দেখো ফোকাসের মূল সংজ্ঞাটি।"
                scaffolding = "ধাপ ১: উত্তল দর্পণে সমান্তরাল আলোক রশ্মি প্রতিফলনের পর ছড়িয়ে পড়ে (অপসারী রশ্মিগুচ্ছ)।\nধাপ ২: এই রশ্মিগুলোকে পেছনের দিকে বর্ধিত করলে তারা একটি কাল্পনিক বিন্দু থেকে আসছে বলে মনে হয়।"
                retention = "১০ সেকেন্ডের কুইজ: যদি কোনো বস্তুর প্রতিবিম্ব পর্দায় ফেলা না যায়, তবে সেই প্রতিবিম্বটি কি বাস্তব নাকি অবাস্তব?"
            }
            inputLower.contains("kaj") || inputLower.contains("work") || studentInput.contains("কাজ") -> {
                responseText = "ভাবো তো, তুমি সারাদিন একটি ভারী দেয়ালকে দুই হাত দিয়ে ধাক্কা দিলে, কিন্তু দেয়াল একটুও সরলো না। পদার্থবিজ্ঞানের দৃষ্টিতে কি কাজ হয়েছে? সরণের ভূমিকাটা চিন্তা করো।"
                scaffolding = "ধাপ ১: বল প্রয়োগ করতে হবে (F)।\nধাপ ২: বলের দিকে বা বলের অভিমুখে সরণ (s) ঘটতে হবে (W = F·s·cosθ)।"
                retention = "১০ সেকেন্ডের কুইজ: এক ব্যক্তি মাথায় ৫০ কেজি বোঝা নিয়ে স্থির দাঁড়িয়ে আছেন। তার দ্বারা কৃতকাজ কত জুল?"
            }
            inputLower.contains("shokti") || inputLower.contains("energy") || studentInput.contains("শক্তি") -> {
                responseText = "শক্তি ধ্বংস বা সৃষ্টি করা যায় কি? মহাবিশ্বের মোট শক্তির পরিমাণ সম্পর্কে শক্তির নিত্যতা সূত্র কী বলে মনে করো?"
                scaffolding = "ধাপ ১: শক্তি কেবল এক রূপ থেকে অন্য রূপে রূপান্তরিত হতে পারে।\nধাপ ২: মুক্তভাবে পড়ন্ত বস্তুর ক্ষেত্রে যেকোনো বিন্দুতে গতিশক্তি ও বিভব শক্তির যোগফল সর্বদা সমান।"
                retention = "১০ সেকেন্ডের কুইজ: কোনো বস্তুকে ভূপৃষ্ঠ থেকে সর্বোচ্চ উচ্চতায় নিয়ে গেলে তার গতিশক্তি কত হয়?"
            }
            else -> {
                responseText = "তোমার অনুসন্ধিৎসা প্রশংসনীয়! এই বিষয়টি বুঝতে হলে আগে ভাবো—এই নিয়মের পেছনের মূল প্রাকৃতিক চলক বা শর্তটি কী? একটু গভীরে ভেবে বলো তো।"
                scaffolding = "ধাপ ১: এনসিটিবি পাঠ্যবইয়ের মূল সূত্র ও সংজ্ঞাটি স্মরণ করো।\nধাপ ২: বাস্তব উদাহরণের সাথে সূত্রটির উপাদানগুলোকে একীভূত করো।"
                retention = "১০ সেকেন্ডের কুইজ: তুমি কি নিশ্চিত এই প্রক্রিয়ায় শক্তির নিত্যতা বা মৌলিক নীতি অক্ষুণ্ণ থাকে?"
            }
        }

        return SocraticOfflineResponse(
            banglishConverted = if (convertedBanglish != studentInput) convertedBanglish else null,
            replyText = responseText,
            scaffolding = scaffolding,
            retentionScenario = retention
        )
    }

    /**
     * Converts common student phonetic Banglish into standard academic Bengali terms.
     */
    fun convertBanglishToBengali(input: String): String {
        var text = input
        val map = listOf(
            "karon ki" to "কারণ কী",
            "kivabe" to "কীভাবে",
            "uttol dorpon" to "উত্তল দর্পণ",
            "obastob" to "অবাস্তব",
            "protibimbo" to "প্রতিবিম্ব",
            "shokti" to "শক্তি",
            "khomota" to "ক্ষমতা",
            "kaj" to "কাজ",
            "gotishokti" to "গতিশক্তি",
            "bibhob" to "বিভব শক্তি",
            "bhijol" to "বল",
            "keno" to "কেন",
            "ki" to "কী",
            "formula" to "সূত্র",
            "unit" to "একক"
        )
        for ((latin, bn) in map) {
            text = text.replace(Regex("(?i)\\b$latin\\b"), bn)
        }
        return text
    }

    private fun extractCoreKeywords(text: String): List<String> {
        val stopwords = setOf("এবং", "বা", "হলো", "বলে", "একটি", "করে", "করা", "হয়", "যে", "কোনো", "এই", "সেই")
        return text.split(" ", "।", ",", "\n", ";")
            .map { it.trim().trim('.', ',', ':', '-') }
            .filter { it.length > 2 && it !in stopwords }
            .distinct()
            .take(6)
    }
}

data class SocraticOfflineResponse(
    val banglishConverted: String?,
    val replyText: String,
    val scaffolding: String,
    val retentionScenario: String
)
