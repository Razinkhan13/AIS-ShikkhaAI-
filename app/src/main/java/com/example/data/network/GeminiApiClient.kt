package com.example.data.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CQEvaluationResult
import com.example.data.model.CreativeQuestionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun isKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun solveDoubtSocratically(
        studentInput: String,
        chapterTitle: String
    ): SocraticOfflineResponse = withContext(Dispatchers.IO) {
        val convertedBanglish = OfflineRubricEngine.convertBanglishToBengali(studentInput)
        if (!isKeyConfigured()) {
            return@withContext OfflineRubricEngine.generateSocraticResponse(studentInput, chapterTitle)
        }

        try {
            val systemInstruction = """
                You are an empathetic, highly skilled AI personal tutor built on Google's LearnLM pedagogical principles for Bangladeshi secondary/higher secondary students.
                NCTB Curriculum Context: $chapterTitle.
                Directives:
                1. DO NOT immediately give the full final answer.
                2. Active Learning: Acknowledge effort in warm Bengali, ask 1 targeted, step-by-step guiding question prompting memory of core concepts.
                3. Cognitive Scaffolding: Provide 2 digestible intuitive steps.
                4. Retention Check: Conclude with a 10-second micro-scenario testing understanding.
                5. If input is in Banglish (Latin Bengali), note the formal Bengali academic term.
                Respond with clean JSON:
                {
                   "replyText": "...",
                   "scaffolding": "...",
                   "retentionScenario": "..."
                }
            """.trimIndent()

            val prompt = "Student Doubt: \"$studentInput\" (Interpreted: \"$convertedBanglish\") in chapter \"$chapterTitle\"."

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val sysContent = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", parts)
                }
                put("systemInstruction", sysContent)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=${BuildConfig.GEMINI_API_KEY}")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyStr)
                val textCandidate = responseJson.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!textCandidate.isNullOrBlank()) {
                    val parsed = JSONObject(textCandidate)
                    return@withContext SocraticOfflineResponse(
                        banglishConverted = if (convertedBanglish != studentInput) convertedBanglish else null,
                        replyText = parsed.optString("replyText", "চমৎকার প্রশ্ন! চল ধাপে ধাপে বিষয়টি বুঝি।"),
                        scaffolding = parsed.optString("scaffolding", "মূল সূত্রটির প্রতিটি চলকের তাৎপর্য বিবেচনা করো।"),
                        retentionScenario = parsed.optString("retentionScenario", "১০ সেকেন্ডের কুইজ: চলকটির মান দ্বিগুণ করলে ফলাফলে কী পরিবর্তন ঘটবে?")
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Gemini API call failed or timed out, falling back to Offline Rubrics: ${e.message}")
        }

        OfflineRubricEngine.generateSocraticResponse(studentInput, chapterTitle)
    }

    suspend fun evaluateCQWithChiefExaminer(
        cq: CreativeQuestionItem,
        ansKa: String,
        ansKha: String,
        ansGa: String,
        ansGha: String
    ): CQEvaluationResult = withContext(Dispatchers.IO) {
        if (!isKeyConfigured()) {
            return@withContext OfflineRubricEngine.evaluateCQ(cq, ansKa, ansKha, ansGa, ansGha)
        }

        try {
            val systemInstruction = """
                Act as an elite Senior Question Setter and Chief Examiner for the Bangladesh National Curriculum and Textbook Board (NCTB).
                Marking rubric rules (Bloom's Taxonomy, total 10 marks):
                - ক (জ্ঞানমূলক, ১ নম্বর): Fact/definition.
                - খ (অনুধাবনমূলক, ২ নম্বর): 2-paragraph conceptual explanation.
                - গ (প্রয়োগমূলক, ৩ নম্বর): 3-paragraph stimulus-to-theory synthesis or calculation.
                - ঘ (উচ্চতর দক্ষতা, ৪ নম্বর): 4-paragraph critical judgment and comparative analysis.
                Output strictly JSON:
                {
                   "scoreKa": 0 to 1,
                   "scoreKha": 0 to 2,
                   "scoreGa": 0 to 3,
                   "scoreGha": 0 to 4,
                   "feedbackKa": "...",
                   "feedbackKha": "...",
                   "feedbackGa": "...",
                   "feedbackGha": "...",
                   "overallExaminerNote": "...",
                   "missingKeywords": ["keyword1", "keyword2"]
                }
            """.trimIndent()

            val studentAnswersPrompt = """
                উদ্দীপক: ${cq.stimulus}
                [প্রশ্ন ক] ${cq.questionKa}
                শিক্ষার্থীর উত্তর (ক): $ansKa
                [প্রশ্ন খ] ${cq.questionKha}
                শিক্ষার্থীর উত্তর (খ): $ansKha
                [প্রশ্ন গ] ${cq.questionGa}
                শিক্ষার্থীর উত্তর (গ): $ansGa
                [প্রশ্ন ঘ] ${cq.questionGha}
                শিক্ষার্থীর উত্তর (ঘ): $ansGha
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", studentAnswersPrompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val sysContent = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", parts)
                }
                put("systemInstruction", sysContent)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=${BuildConfig.GEMINI_API_KEY}")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyStr)
                val textCandidate = responseJson.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!textCandidate.isNullOrBlank()) {
                    val parsed = JSONObject(textCandidate)
                    val sKa = parsed.optInt("scoreKa", 1).coerceIn(0, 1)
                    val sKha = parsed.optInt("scoreKha", 1).coerceIn(0, 2)
                    val sGa = parsed.optInt("scoreGa", 2).coerceIn(0, 3)
                    val sGha = parsed.optInt("scoreGha", 3).coerceIn(0, 4)
                    val missingKeywordsList = mutableListOf<String>()
                    val kwArray = parsed.optJSONArray("missingKeywords")
                    if (kwArray != null) {
                        for (i in 0 until kwArray.length()) {
                            missingKeywordsList.add(kwArray.getString(i))
                        }
                    }

                    return@withContext CQEvaluationResult(
                        totalScore = sKa + sKha + sGa + sGha,
                        scoreKa = sKa,
                        scoreKha = sKha,
                        scoreGa = sGa,
                        scoreGha = sGha,
                        feedbackKa = parsed.optString("feedbackKa", "জ্ঞানমূলক অংশ মূল্যায়িত হয়েছে।"),
                        feedbackKha = parsed.optString("feedbackKha", "অনুধাবনমূলক অংশ মূল্যায়িত হয়েছে।"),
                        feedbackGa = parsed.optString("feedbackGa", "প্রয়োগমূলক অংশ মূল্যায়িত হয়েছে।"),
                        feedbackGha = parsed.optString("feedbackGha", "উচ্চতর দক্ষতা মূল্যায়িত হয়েছে।"),
                        overallExaminerNote = parsed.optString("overallExaminerNote", "বোর্ড প্রধান পরীক্ষকের মূল্যায়ন সম্পন্ন হয়েছে।"),
                        missingKeywords = missingKeywordsList
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Gemini Chief Examiner call failed, falling back to Offline Rubrics: ${e.message}")
        }

        OfflineRubricEngine.evaluateCQ(cq, ansKa, ansKha, ansGa, ansGha)
    }
}
