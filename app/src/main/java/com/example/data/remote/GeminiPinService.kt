package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.PinItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiPinService(
    private var customApiKey: String? = null
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun setCustomApiKey(key: String?) {
        customApiKey = key?.trim()
    }

    private fun getEffectiveApiKey(): String {
        if (!customApiKey.isNullOrBlank()) {
            return customApiKey!!
        }
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
        return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
    }

    suspend fun generatePins(
        topic: String,
        websiteUrl: String,
        pinType: String,
        tone: String,
        variationCount: Int,
        targetAudience: String,
        primaryKeyword: String,
        ctaStyle: String,
        templateInstructions: String = "",
        onStatusUpdate: ((String) -> Unit)? = null
    ): List<PinItem> = withContext(Dispatchers.IO) {
        onStatusUpdate?.invoke("Analyzing your topic…")
        delay(350)
        onStatusUpdate?.invoke("Creating attention-grabbing titles…")
        delay(350)
        onStatusUpdate?.invoke("Optimizing Pinterest keywords…")

        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            Log.w("GeminiPinService", "No active Gemini API key found, generating dynamic smart pins.")
            onStatusUpdate?.invoke("Writing pin descriptions…")
            delay(350)
            onStatusUpdate?.invoke("Creating image prompts…")
            delay(350)
            onStatusUpdate?.invoke("Finalizing your pins…")
            delay(200)
            return@withContext generateSmartFallbackPins(
                topic = topic,
                websiteUrl = websiteUrl,
                pinType = pinType,
                tone = tone,
                variationCount = variationCount,
                targetAudience = targetAudience,
                primaryKeyword = primaryKeyword,
                ctaStyle = ctaStyle
            )
        }

        val prompt = buildPinPrompt(
            topic = topic,
            websiteUrl = websiteUrl,
            pinType = pinType,
            tone = tone,
            variationCount = variationCount,
            targetAudience = targetAudience,
            primaryKeyword = primaryKeyword,
            ctaStyle = ctaStyle,
            templateInstructions = templateInstructions
        )

        val systemInstruction = """
            You are an expert Pinterest content strategist, SEO copywriter, visual content planner, and conversion-focused content creator. Your job is to create useful, relevant, honest, and highly readable Pinterest content based only on the user's provided topic and context.
            Follow strict Pinterest content rules:
            - No keyword stuffing.
            - Write natural Pinterest SEO for humans first.
            - Provide 1 primary keyword and 8 to 15 secondary keywords for each pin.
            - Write image prompts specifically formatted for vertical 2:3 Pinterest visuals (approx 1000x1500) specifying subject, composition, lighting, mood, camera realism, and clean negative space for text overlays. Do not include text rendering inside the image prompt.
            - Output MUST be valid JSON conforming strictly to the requested schema.
        """.trimIndent()

        try {
            onStatusUpdate?.invoke("Writing pin descriptions…")
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                }
                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    })
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("responseMimeType", "application/json")
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            onStatusUpdate?.invoke("Creating image prompts…")
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiPinService", "API call failed (${response.code}): $responseBody")
                onStatusUpdate?.invoke("Finalizing your pins…")
                return@withContext generateSmartFallbackPins(
                    topic = topic,
                    websiteUrl = websiteUrl,
                    pinType = pinType,
                    tone = tone,
                    variationCount = variationCount,
                    targetAudience = targetAudience,
                    primaryKeyword = primaryKeyword,
                    ctaStyle = ctaStyle
                )
            }

            onStatusUpdate?.invoke("Finalizing your pins…")
            val parsedPins = parseGeminiResponse(responseBody, websiteUrl)
            if (parsedPins.isNotEmpty()) {
                return@withContext parsedPins
            } else {
                Log.w("GeminiPinService", "Parsed pins was empty, using fallback.")
                return@withContext generateSmartFallbackPins(
                    topic = topic,
                    websiteUrl = websiteUrl,
                    pinType = pinType,
                    tone = tone,
                    variationCount = variationCount,
                    targetAudience = targetAudience,
                    primaryKeyword = primaryKeyword,
                    ctaStyle = ctaStyle
                )
            }
        } catch (e: Exception) {
            Log.e("GeminiPinService", "Exception calling Gemini API", e)
            onStatusUpdate?.invoke("Finalizing your pins…")
            return@withContext generateSmartFallbackPins(
                topic = topic,
                websiteUrl = websiteUrl,
                pinType = pinType,
                tone = tone,
                variationCount = variationCount,
                targetAudience = targetAudience,
                primaryKeyword = primaryKeyword,
                ctaStyle = ctaStyle
            )
        }
    }

    suspend fun improveImagePrompt(
        originalPrompt: String,
        style: String,
        lighting: String,
        camera: String,
        composition: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        val requestedDirectives = "Style: $style, Lighting: $lighting, Camera: $camera, Composition: $composition, Aspect Ratio: 2:3 vertical Pinterest standard"
        if (apiKey.isBlank()) {
            return@withContext "High-end $style vertical 2:3 Pinterest photograph. Composition: $composition. $originalPrompt. Shot with $camera with $lighting illumination, rich textures, fine details, and generous upper negative space ideal for typography overlays."
        }

        val promptText = """
            Rewrite and elevate this Pinterest pin image prompt into an elite, visually stunning prompt for an AI image generator:
            Original prompt: "$originalPrompt"
            
            Enforce these parameters:
            - Style: $style
            - Lighting: $lighting
            - Camera: $camera
            - Composition: $composition
            - Orientation: Vertical 2:3 aspect ratio (approx 1000x1500)
            - Clean negative space for text where appropriate
            - Do NOT put text or words inside the image prompt.
            
            Return ONLY the improved image prompt text in plain text, nothing else.
        """.trimIndent()

        try {
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", promptText) })
                        })
                    })
                })
            }
            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder().url(url).post(requestBody).build()
            val response = client.newCall(request).execute()
            val respStr = response.body?.string() ?: ""
            val json = JSONObject(respStr)
            val candidate = json.optJSONArray("candidates")?.optJSONObject(0)
            val part = candidate?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)
            val result = part?.optString("text")?.trim() ?: ""
            if (result.isNotBlank()) result else "$originalPrompt ($requestedDirectives)"
        } catch (_: Exception) {
            "Professional $style 2:3 vertical Pinterest visual. $originalPrompt with $lighting, shot using $camera in a $composition layout with balanced negative space."
        }
    }

    private fun buildPinPrompt(
        topic: String,
        websiteUrl: String,
        pinType: String,
        tone: String,
        variationCount: Int,
        targetAudience: String,
        primaryKeyword: String,
        ctaStyle: String,
        templateInstructions: String
    ): String {
        return buildString {
            appendLine("Generate exactly $variationCount distinct, high-performing Pinterest pin variations for:")
            appendLine("- Topic: $topic")
            appendLine("- Pin Type: $pinType")
            appendLine("- Tone: $tone")
            appendLine("- Desired CTA Style: $ctaStyle")
            if (websiteUrl.isNotBlank()) appendLine("- Target Website URL: $websiteUrl")
            if (targetAudience.isNotBlank()) appendLine("- Target Audience: $targetAudience")
            if (primaryKeyword.isNotBlank()) appendLine("- Focus Primary Keyword: $primaryKeyword")
            if (templateInstructions.isNotBlank()) {
                appendLine("- Specific Template Instructions: $templateInstructions")
            }
            appendLine()
            appendLine("JSON Output Schema:")
            appendLine("""
            {
              "pins": [
                {
                  "title": "Clear, intriguing, Pinterest-friendly title (max 100 chars)",
                  "headline": "Short visual overlay text for graphic (3-7 punchy words)",
                  "cta": "Short action-oriented CTA button text",
                  "description": "Natural SEO description for humans first, 150-300 chars, embedding keywords naturally without stuffing",
                  "primaryKeyword": "Main search intent keyword",
                  "secondaryKeywords": ["8 to 15 relevant secondary Pinterest search terms"],
                  "boardName": "Suggested relevant Pinterest board title",
                  "imagePrompt": "Detailed 2:3 vertical composition prompt specifying subject, composition, lighting, mood, camera realism, and clean negative space. No text in prompt.",
                  "altText": "Concise accessibility alt text describing visual scene",
                  "hook": "Compelling curiosity or emotional hook",
                  "targetAudience": "Intended audience profile",
                  "websiteUrl": "$websiteUrl"
                }
              ]
            }
            """.trimIndent())
        }
    }

    private fun parseGeminiResponse(rawResponse: String, fallbackUrl: String): List<PinItem> {
        val pins = mutableListOf<PinItem>()
        try {
            val root = JSONObject(rawResponse)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val parts = firstCandidate?.optJSONObject("content")?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleaned = cleanJsonString(text)
            val jsonObject = JSONObject(cleaned)
            val pinArray = jsonObject.optJSONArray("pins") ?: JSONArray()
            for (i in 0 until pinArray.length()) {
                val item = pinArray.getJSONObject(i)
                val pin = PinItem.fromJson(item)
                if (pin.websiteUrl.isBlank() && fallbackUrl.isNotBlank()) {
                    pins.add(pin.copy(websiteUrl = fallbackUrl))
                } else {
                    pins.add(pin)
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiPinService", "Failed to parse JSON response: $rawResponse", e)
        }
        return pins
    }

    private fun cleanJsonString(raw: String): String {
        var s = raw.trim()
        if (s.startsWith("```json")) {
            s = s.removePrefix("```json").trim()
        } else if (s.startsWith("```")) {
            s = s.removePrefix("```").trim()
        }
        if (s.endsWith("```")) {
            s = s.removeSuffix("```").trim()
        }
        val firstBrace = s.indexOf('{')
        val lastBrace = s.lastIndexOf('}')
        return if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            s.substring(firstBrace, lastBrace + 1)
        } else {
            s
        }
    }

    private fun generateSmartFallbackPins(
        topic: String,
        websiteUrl: String,
        pinType: String,
        tone: String,
        variationCount: Int,
        targetAudience: String,
        primaryKeyword: String,
        ctaStyle: String
    ): List<PinItem> {
        val cleanTopic = topic.trim().ifBlank { "Smart Ideas & Inspiration" }
        val mainKw = primaryKeyword.ifBlank {
            cleanTopic.lowercase().take(40)
        }
        val effectiveAudience = targetAudience.ifBlank { "Passionate creators and everyday Pinterest seekers" }
        val effectiveCta = when (ctaStyle) {
            "Read More" -> "READ FULL GUIDE →"
            "Learn More" -> "LEARN MORE NOW →"
            "Get the Recipe" -> "GET THE FULL RECIPE →"
            "Shop Now" -> "SHOP THE LOOK →"
            "Discover More" -> "DISCOVER THE DETAILS →"
            "See How" -> "SEE STEP-BY-STEP →"
            else -> if (ctaStyle.isNotBlank()) "$ctaStyle →" else "CLICK TO EXPLORE →"
        }

        val variations = mutableListOf<PinItem>()
        val headlineHooks = listOf(
            "The Secret To $cleanTopic",
            "Why Everyone Is Obsessed With This",
            "Stop Doing It The Hard Way",
            "Everything Changed When I Tried This",
            "The 5-Minute Trick That Works",
            "What Nobody Tells You About $cleanTopic",
            "The Simple Step-By-Step Method",
            "The Ultimate Beginner Breakdown",
            "Before You Try It, Read This",
            "The Game-Changing Guide"
        )

        val boardIdeas = listOf(
            "$cleanTopic Ideas & Inspiration",
            "Best of $cleanTopic Guides",
            "Daily $pinType Inspiration",
            "Life Hacks & $cleanTopic Tips",
            "Aesthetic $pinType Board"
        )

        for (i in 0 until variationCount) {
            val headline = headlineHooks.getOrElse(i % headlineHooks.size) { "Essential $cleanTopic" }
            val title = when (tone) {
                "Curious" -> "$cleanTopic — The Untold Secret You Need To See"
                "Emotional" -> "How $cleanTopic Completely Transformed My Daily Life"
                "Viral-style" -> "10 Genius $cleanTopic Hacks That Actually Work"
                "Luxury" -> "The Elevated Guide To $cleanTopic (Aesthetic & Refined)"
                "Inspirational" -> "Believe In The Process: $cleanTopic Daily Motivation"
                "Professional" -> "The Complete Expert Strategy For $cleanTopic"
                else -> "The Ultimate Guide To $cleanTopic: Tips & Breakdown"
            } + if (variationCount > 1) " (Variation ${i + 1})" else ""

            val desc = "Looking for high-impact insights on $cleanTopic? Discover our curated $pinType covering everything you need to know. Packed with actionable tips, honest recommendations, and $tone inspiration for $effectiveAudience. Save this pin to your board and tap through to explore the full story!"

            val secondary = listOf(
                mainKw,
                "$mainKw tips",
                "$mainKw ideas",
                "how to $mainKw",
                "best $mainKw",
                "easy $mainKw guide",
                "$pinType inspiration",
                "diy $mainKw",
                "aesthetic $mainKw",
                "pinterest trends",
                "viral $mainKw",
                "step by step $mainKw"
            )

            val imagePrompt = "High-end aesthetic vertical 2:3 Pinterest photograph featuring $cleanTopic. Soft natural golden hour illumination, clean minimalist setting with ample negative space in the upper third for graphic overlay text, editorial depth of field, authentic realistic textures, 8k quality, centered framing."

            val altText = "Aesthetic vertical Pinterest pin image showcasing $cleanTopic with natural warm lighting and clean composition."
            val hook = "You won't believe how easy $cleanTopic can be once you know this simple shift."

            variations.add(
                PinItem(
                    title = title,
                    headline = headline,
                    cta = effectiveCta,
                    description = desc,
                    primaryKeyword = mainKw,
                    secondaryKeywords = secondary,
                    boardName = boardIdeas.getOrElse(i % boardIdeas.size) { "Creative Pinterest Inspiration" },
                    imagePrompt = imagePrompt,
                    altText = altText,
                    hook = hook,
                    targetAudience = effectiveAudience,
                    websiteUrl = websiteUrl,
                    isSaved = false
                )
            )
        }
        return variations
    }
}
