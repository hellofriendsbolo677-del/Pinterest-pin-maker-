package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PinDatabase
import com.example.data.local.PinRepository
import com.example.data.model.AppScreen
import com.example.data.model.PinItem
import com.example.data.model.PinProject
import com.example.data.model.PinTemplate
import com.example.data.remote.GeminiPinService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PinCraftViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PinRepository
    private val geminiService: GeminiPinService

    init {
        val db = PinDatabase.getDatabase(application)
        repository = PinRepository(db.pinProjectDao())
        geminiService = GeminiPinService()
    }

    // Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // App Preferences
    private val _themeMode = MutableStateFlow("SYSTEM") // "SYSTEM", "LIGHT", "DARK"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    private val _credits = MutableStateFlow(25)
    val credits: StateFlow<Int> = _credits.asStateFlow()

    fun refillCredits() {
        _credits.value = 25
        showToast("Credits refilled! 25 credits available.")
    }

    // Create Pin Form States
    val topic = MutableStateFlow("")
    val websiteUrl = MutableStateFlow("")
    val pinType = MutableStateFlow("Blog Post")
    val tone = MutableStateFlow("Click-worthy")
    val variationCount = MutableStateFlow(3)
    val targetAudience = MutableStateFlow("")
    val primaryKeyword = MutableStateFlow("")
    val ctaStyle = MutableStateFlow("Read More")
    val customCta = MutableStateFlow("")

    // Bulk Mode
    val isBulkMode = MutableStateFlow(false)
    val bulkTopics = MutableStateFlow("")
    val bulkProgress = MutableStateFlow("")

    // Generation Status
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStatus = MutableStateFlow("Analyzing your topic…")
    val generationStatus: StateFlow<String> = _generationStatus.asStateFlow()

    private var activeTemplateInstructions: String = ""

    // Current Active Results
    private val _currentPins = MutableStateFlow<List<PinItem>>(emptyList())
    val currentPins: StateFlow<List<PinItem>> = _currentPins.asStateFlow()

    private val _currentProject = MutableStateFlow<PinProject?>(null)
    val currentProject: StateFlow<PinProject?> = _currentProject.asStateFlow()

    // History & Search/Filters
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("All")
    val selectedSort = MutableStateFlow("Newest") // "Newest", "Oldest", "A–Z"

    val allProjects: StateFlow<List<PinProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentProjects: StateFlow<List<PinProject>> = repository.recentProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalProjectsCount: StateFlow<Int> = repository.projectCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredProjects: StateFlow<List<PinProject>> = combine(
        allProjects,
        searchQuery,
        selectedFilter,
        selectedSort
    ) { projects, query, filter, sort ->
        projects.filter { proj ->
            val matchesFilter = when (filter) {
                "All" -> true
                "Blog" -> proj.pinType.contains("Blog", ignoreCase = true)
                "Story" -> proj.pinType.contains("Story", ignoreCase = true)
                "Recipe" -> proj.pinType.contains("Recipe", ignoreCase = true)
                "Product" -> proj.pinType.contains("Product", ignoreCase = true)
                "Tutorial" -> proj.pinType.contains("Tutorial", ignoreCase = true)
                else -> !listOf("Blog", "Story", "Recipe", "Product", "Tutorial")
                    .any { proj.pinType.contains(it, ignoreCase = true) }
            }
            val matchesQuery = query.isBlank() ||
                proj.topic.contains(query, ignoreCase = true) ||
                proj.primaryKeyword.contains(query, ignoreCase = true) ||
                proj.variationsJson.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }.sortedWith { a, b ->
            when (sort) {
                "Oldest" -> a.createdAt.compareTo(b.createdAt)
                "A–Z" -> a.topic.compareTo(b.topic, ignoreCase = true)
                else -> b.createdAt.compareTo(a.createdAt) // Newest
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast / Feedback State
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Editing & Refiner Dialogs
    val editingPinIndex = MutableStateFlow<Int?>(null)
    val improvingImagePromptIndex = MutableStateFlow<Int?>(null)
    val isImprovingPrompt = MutableStateFlow(false)

    // Form submission
    fun generatePins() {
        val topicValue = topic.value.trim()
        if (topicValue.isBlank()) {
            showToast("Please enter a topic before generating your pin.")
            return
        }
        if (topicValue.length < 3) {
            showToast("Please enter a slightly more descriptive topic.")
            return
        }
        if (ctaStyle.value == "Custom" && customCta.value.trim().isBlank()) {
            showToast("Please enter your custom CTA text.")
            return
        }
        if (_credits.value <= 0) {
            showToast("Out of credits! Please refill in Settings or top bar.")
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatus.value = "Analyzing your topic…"

            val resolvedCta = if (ctaStyle.value == "Custom") customCta.value.trim() else ctaStyle.value

            val generated = geminiService.generatePins(
                topic = topicValue,
                websiteUrl = websiteUrl.value.trim(),
                pinType = pinType.value,
                tone = tone.value,
                variationCount = variationCount.value,
                targetAudience = targetAudience.value.trim(),
                primaryKeyword = primaryKeyword.value.trim(),
                ctaStyle = resolvedCta,
                templateInstructions = activeTemplateInstructions,
                onStatusUpdate = { status ->
                    _generationStatus.value = status
                }
            )

            _isGenerating.value = false
            if (generated.isNotEmpty()) {
                _credits.value = (_credits.value - 1).coerceAtLeast(0)
                _currentPins.value = generated

                // Automatically save to database as requested in Section 2: "generated projects are saved to History"
                val newProject = PinProject(
                    topic = topicValue,
                    websiteUrl = websiteUrl.value.trim(),
                    pinType = pinType.value,
                    tone = tone.value,
                    ctaStyle = resolvedCta,
                    targetAudience = targetAudience.value.trim(),
                    primaryKeyword = primaryKeyword.value.trim(),
                    variationsJson = PinProject.createVariationsJson(generated)
                )
                val id = repository.insertProject(newProject)
                _currentProject.value = newProject.copy(id = id)

                showToast("Generation completed! Created ${generated.size} pins.")
                _currentScreen.value = AppScreen.RESULTS
            } else {
                showToast("Something went wrong while generating your pin. Please try again.")
            }
        }
    }

    fun generateBulkPins() {
        val rawLines = bulkTopics.value.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (rawLines.isEmpty()) {
            showToast("Please enter at least one topic line.")
            return
        }
        if (_credits.value < rawLines.size) {
            showToast("Need ${rawLines.size} credits, you have ${_credits.value}. Refill in Settings.")
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            val allGenerated = mutableListOf<PinItem>()
            val resolvedCta = if (ctaStyle.value == "Custom") customCta.value.trim() else ctaStyle.value

            for ((index, singleTopic) in rawLines.withIndex()) {
                bulkProgress.value = "Generating ${index + 1} / ${rawLines.size}: $singleTopic"
                _generationStatus.value = bulkProgress.value

                val pins = geminiService.generatePins(
                    topic = singleTopic,
                    websiteUrl = websiteUrl.value.trim(),
                    pinType = pinType.value,
                    tone = tone.value,
                    variationCount = 1,
                    targetAudience = targetAudience.value.trim(),
                    primaryKeyword = primaryKeyword.value.trim(),
                    ctaStyle = resolvedCta
                )
                allGenerated.addAll(pins)
                _credits.value = (_credits.value - 1).coerceAtLeast(0)
            }

            _isGenerating.value = false
            if (allGenerated.isNotEmpty()) {
                _currentPins.value = allGenerated
                val combinedProject = PinProject(
                    topic = "Bulk: ${rawLines.first()} (+${rawLines.size - 1} more)",
                    websiteUrl = websiteUrl.value.trim(),
                    pinType = pinType.value,
                    tone = tone.value,
                    ctaStyle = resolvedCta,
                    variationsJson = PinProject.createVariationsJson(allGenerated)
                )
                val id = repository.insertProject(combinedProject)
                _currentProject.value = combinedProject.copy(id = id)
                showToast("Bulk generation completed! ${allGenerated.size} pins ready.")
                _currentScreen.value = AppScreen.RESULTS
            } else {
                showToast("Failed to generate bulk pins.")
            }
        }
    }

    fun regenerateSinglePin(index: Int) {
        val current = _currentPins.value.getOrNull(index) ?: return
        val currentProj = _currentProject.value

        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatus.value = "Regenerating pin variation…"

            val newVariations = geminiService.generatePins(
                topic = currentProj?.topic ?: topic.value.ifBlank { current.title },
                websiteUrl = currentProj?.websiteUrl ?: current.websiteUrl,
                pinType = currentProj?.pinType ?: pinType.value,
                tone = currentProj?.tone ?: tone.value,
                variationCount = 1,
                targetAudience = current.targetAudience,
                primaryKeyword = current.primaryKeyword,
                ctaStyle = current.cta
            )

            _isGenerating.value = false
            if (newVariations.isNotEmpty()) {
                val updatedList = _currentPins.value.toMutableList()
                updatedList[index] = newVariations.first()
                _currentPins.value = updatedList
                syncCurrentProjectVariations(updatedList)
                showToast("Regenerated Pin #${index + 1}!")
            } else {
                showToast("Could not regenerate this pin.")
            }
        }
    }

    fun updatePin(index: Int, updated: PinItem) {
        val list = _currentPins.value.toMutableList()
        if (index in list.indices) {
            list[index] = updated
            _currentPins.value = list
            syncCurrentProjectVariations(list)
            showToast("Updated Pin #${index + 1} ✓")
        }
    }

    fun deletePinVariation(index: Int) {
        val list = _currentPins.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentPins.value = list
            syncCurrentProjectVariations(list)
            showToast("Pin variation removed.")
        }
    }

    private fun syncCurrentProjectVariations(pins: List<PinItem>) {
        val proj = _currentProject.value ?: return
        val updated = proj.copy(variationsJson = PinProject.createVariationsJson(pins))
        _currentProject.value = updated
        viewModelScope.launch {
            repository.updateProject(updated)
        }
    }

    fun improveImagePrompt(
        pinIndex: Int,
        style: String,
        lighting: String,
        camera: String,
        composition: String
    ) {
        val currentPin = _currentPins.value.getOrNull(pinIndex) ?: return
        viewModelScope.launch {
            isImprovingPrompt.value = true
            val improved = geminiService.improveImagePrompt(
                originalPrompt = currentPin.imagePrompt,
                style = style,
                lighting = lighting,
                camera = camera,
                composition = composition
            )
            isImprovingPrompt.value = false
            updatePin(pinIndex, currentPin.copy(imagePrompt = improved))
            improvingImagePromptIndex.value = null
            showToast("Image prompt improved and updated!")
        }
    }

    fun applyTemplate(template: PinTemplate) {
        topic.value = template.promptIdea
        pinType.value = template.defaultPinType
        tone.value = template.recommendedTone
        ctaStyle.value = template.ctaSuggestion
        activeTemplateInstructions = "${template.name} format: ${template.exampleStructure}. Recommended tone: ${template.recommendedTone}."
        _currentScreen.value = AppScreen.CREATE_PIN
        showToast("Loaded '${template.name}' template!")
    }

    fun openProjectInResults(project: PinProject) {
        _currentProject.value = project
        _currentPins.value = project.parsePins()
        _currentScreen.value = AppScreen.RESULTS
    }

    fun duplicateProject(project: PinProject) {
        viewModelScope.launch {
            repository.duplicateProject(project)
            showToast("Duplicated project '${project.topic}'")
        }
    }

    fun deleteProject(project: PinProject) {
        viewModelScope.launch {
            repository.deleteProjectById(project.id)
            if (_currentProject.value?.id == project.id) {
                _currentProject.value = null
                _currentPins.value = emptyList()
            }
            showToast("Deleted project.")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.deleteAllProjects()
            _currentProject.value = null
            _currentPins.value = emptyList()
            showToast("History cleared.")
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val context = getApplication<Application>().applicationContext
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        showToast("Copied $label ✓")
    }

    fun copyEntirePin(pin: PinItem) {
        copyToClipboard("Entire Pin", pin.formattedForClipboard())
    }

    fun copyAllVariations() {
        val allText = buildString {
            appendLine("===============================")
            appendLine("PINCRAFT AI - GENERATED PIN PACKAGE")
            appendLine("===============================")
            appendLine()
            _currentPins.value.forEachIndexed { i, pin ->
                appendLine(">>> VARIATION #${i + 1} <<<")
                appendLine(pin.formattedForClipboard())
                appendLine()
            }
        }
        copyToClipboard("All Pins", allText)
    }
}
