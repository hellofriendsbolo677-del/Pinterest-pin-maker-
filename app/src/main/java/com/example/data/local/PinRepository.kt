package com.example.data.local

import com.example.data.model.PinItem
import com.example.data.model.PinProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PinRepository(private val pinProjectDao: PinProjectDao) {

    val allProjects: Flow<List<PinProject>> = pinProjectDao.getAllProjects()
    val recentProjects: Flow<List<PinProject>> = pinProjectDao.getRecentProjects()
    val projectCount: Flow<Int> = pinProjectDao.getProjectCount()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedSampleIfEmpty()
        }
    }

    private suspend fun seedSampleIfEmpty() {
        val existing = allProjects.firstOrNull()
        if (existing.isNullOrEmpty()) {
            val samplePins = listOf(
                PinItem(
                    title = "She Found a Hidden Room Behind the Bookshelf — What Was Inside Changed Everything",
                    headline = "The Secret Room Nobody Knew Existed",
                    cta = "READ THE FULL STORY →",
                    description = "When renovating an old 1920s Victorian estate, she moved a heavy mahogany bookcase and discovered an unmarked passage leading to a preserved archive room. Click through to discover the captivating full mystery story and unseen photographs!",
                    primaryKeyword = "mystery stories",
                    secondaryKeywords = listOf(
                        "hidden room", "secret room", "mystery story", "victorian house mystery",
                        "old bookshelf secret", "creepy mystery", "true mystery", "hidden doors",
                        "secret passages", "house secrets", "short story fiction"
                    ),
                    boardName = "Mystery Story Ideas & Hidden Spaces",
                    imagePrompt = "Cinematic vertical 2:3 photograph of an antique dark wood bookshelf slightly swung open like a door, revealing a dimly lit secret vintage room with dust motes illuminated by soft golden lantern light, mysterious atmosphere, deep shadows, authentic editorial composition with clean upper negative space for text",
                    altText = "An antique wooden bookshelf swung open to reveal a hidden secret room lit by warm lantern glow",
                    hook = "She thought it was just a regular renovation until the wall opened up...",
                    targetAudience = "Mystery readers, vintage home lovers, and creative writers",
                    websiteUrl = "https://example.com/hidden-room-story",
                    isSaved = true
                ),
                PinItem(
                    title = "10 Chilling Clues Discovered in an Abandoned Bookshelf Chamber",
                    headline = "What Was Hidden Behind the Wall?",
                    cta = "DISCOVER MORE →",
                    description = "From handwritten letters to forgotten keys, here are the most intriguing artifacts recovered from a secret room hidden behind a library shelf. Read the complete story and explore the architectural mystery.",
                    primaryKeyword = "secret room discoveries",
                    secondaryKeywords = listOf(
                        "hidden passages", "abandoned room", "secret chambers", "old house mysteries",
                        "vintage library secrets", "mystery archives", "hidden historical artifacts"
                    ),
                    boardName = "Architecture Secrets & Mystery",
                    imagePrompt = "Editorial photography, vertical 2:3 Pinterest format, close-up of a weathered vintage brass key and leather-bound journal resting on a wooden threshold leading into an atmospheric secret passage, soft moody rim lighting, sharp focus",
                    altText = "Vintage brass key and antique diary sitting near a secret bookcase opening",
                    hook = "The journal found inside had entries that stopped abruptly in 1934.",
                    targetAudience = "Curious readers, history and mystery enthusiasts",
                    websiteUrl = "https://example.com/hidden-room-story",
                    isSaved = true
                )
            )

            val sampleProject = PinProject(
                topic = "She Found a Hidden Room Behind the Bookshelf",
                websiteUrl = "https://example.com/hidden-room-story",
                pinType = "Story",
                tone = "Curious",
                ctaStyle = "Read More",
                targetAudience = "Mystery fiction lovers & curious readers",
                primaryKeyword = "mystery stories",
                variationsJson = PinProject.createVariationsJson(samplePins),
                createdAt = System.currentTimeMillis() - 86400000L, // 1 day ago
                isFavorite = true
            )
            pinProjectDao.insertProject(sampleProject)
        }
    }

    suspend fun insertProject(project: PinProject): Long =
        pinProjectDao.insertProject(project)

    suspend fun updateProject(project: PinProject) =
        pinProjectDao.updateProject(project)

    suspend fun deleteProjectById(id: Long) =
        pinProjectDao.deleteProjectById(id)

    suspend fun deleteAllProjects() =
        pinProjectDao.deleteAllProjects()

    suspend fun getProjectById(id: Long): PinProject? =
        pinProjectDao.getProjectById(id)

    suspend fun duplicateProject(project: PinProject): Long {
        val copy = project.copy(
            id = 0,
            topic = "${project.topic} (Copy)",
            createdAt = System.currentTimeMillis()
        )
        return pinProjectDao.insertProject(copy)
    }
}
