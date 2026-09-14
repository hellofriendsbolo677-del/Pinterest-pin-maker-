package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

data class PinItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    var title: String,
    var headline: String,
    var cta: String,
    var description: String,
    var primaryKeyword: String,
    var secondaryKeywords: List<String>,
    var boardName: String,
    var imagePrompt: String,
    var altText: String,
    var hook: String,
    var targetAudience: String = "",
    var websiteUrl: String = "",
    var isSaved: Boolean = false
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("title", title)
        obj.put("headline", headline)
        obj.put("cta", cta)
        obj.put("description", description)
        obj.put("primaryKeyword", primaryKeyword)
        val kwArray = JSONArray()
        secondaryKeywords.forEach { kwArray.put(it) }
        obj.put("secondaryKeywords", kwArray)
        obj.put("boardName", boardName)
        obj.put("imagePrompt", imagePrompt)
        obj.put("altText", altText)
        obj.put("hook", hook)
        obj.put("targetAudience", targetAudience)
        obj.put("websiteUrl", websiteUrl)
        obj.put("isSaved", isSaved)
        return obj
    }

    fun formattedForClipboard(): String {
        val kwString = if (secondaryKeywords.isNotEmpty()) {
            secondaryKeywords.joinToString(", ")
        } else {
            "None"
        }
        return buildString {
            appendLine("=== PINTEREST PIN ===")
            appendLine("📌 TITLE:")
            appendLine(title)
            appendLine()
            appendLine("🎨 GRAPHIC HEADLINE / OVERLAY:")
            appendLine(headline)
            appendLine()
            appendLine("👉 CALL TO ACTION (CTA):")
            appendLine(cta)
            appendLine()
            appendLine("📝 PIN DESCRIPTION:")
            appendLine(description)
            appendLine()
            appendLine("🎯 PRIMARY KEYWORD:")
            appendLine(primaryKeyword)
            appendLine()
            appendLine("🏷️ SECONDARY KEYWORDS / TAGS:")
            appendLine(kwString)
            appendLine()
            appendLine("📂 SUGGESTED BOARD:")
            appendLine(boardName)
            appendLine()
            appendLine("🎣 CONTENT HOOK:")
            appendLine(hook)
            appendLine()
            appendLine("🖼️ IMAGE GENERATION PROMPT (2:3 Vertical):")
            appendLine(imagePrompt)
            appendLine()
            appendLine("♿ ALT TEXT:")
            appendLine(altText)
            if (websiteUrl.isNotBlank()) {
                appendLine()
                appendLine("🔗 DESTINATION URL:")
                appendLine(websiteUrl)
            }
        }
    }

    companion object {
        fun fromJson(obj: JSONObject): PinItem {
            val kwList = mutableListOf<String>()
            val kwArray = obj.optJSONArray("secondaryKeywords")
            if (kwArray != null) {
                for (i in 0 until kwArray.length()) {
                    kwList.add(kwArray.optString(i))
                }
            }
            return PinItem(
                id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                title = obj.optString("title"),
                headline = obj.optString("headline"),
                cta = obj.optString("cta"),
                description = obj.optString("description"),
                primaryKeyword = obj.optString("primaryKeyword"),
                secondaryKeywords = kwList,
                boardName = obj.optString("boardName"),
                imagePrompt = obj.optString("imagePrompt"),
                altText = obj.optString("altText"),
                hook = obj.optString("hook"),
                targetAudience = obj.optString("targetAudience"),
                websiteUrl = obj.optString("websiteUrl"),
                isSaved = obj.optBoolean("isSaved", false)
            )
        }
    }
}

@Entity(tableName = "pin_projects")
data class PinProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val websiteUrl: String = "",
    val pinType: String,
    val tone: String,
    val ctaStyle: String,
    val targetAudience: String = "",
    val primaryKeyword: String = "",
    val variationsJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    fun parsePins(): List<PinItem> {
        val list = mutableListOf<PinItem>()
        try {
            val array = JSONArray(variationsJson)
            for (i in 0 until array.length()) {
                list.add(PinItem.fromJson(array.getJSONObject(i)))
            }
        } catch (_: Exception) {
            // Safe fallback if JSON parsing fails
        }
        return list
    }

    companion object {
        fun createVariationsJson(pins: List<PinItem>): String {
            val array = JSONArray()
            pins.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

data class PinTemplate(
    val id: String,
    val name: String,
    val description: String,
    val recommendedTone: String,
    val ctaSuggestion: String,
    val exampleStructure: String,
    val defaultPinType: String,
    val promptIdea: String,
    val iconCategory: String
)

enum class AppScreen(val route: String, val title: String) {
    DASHBOARD("dashboard", "Dashboard"),
    CREATE_PIN("create", "Create Pin"),
    RESULTS("results", "Results"),
    MY_PINS("pins", "My Pins"),
    TEMPLATES("templates", "Templates"),
    SETTINGS("settings", "Settings")
}
