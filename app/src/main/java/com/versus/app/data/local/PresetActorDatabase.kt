package com.versus.app.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.versus.app.domain.model.Actor

/**
 * Loads the preset actor database from the JSON file in assets/.
 *
 * This provides instant auto-complete for popular actors without
 * needing an API call. If a typed name matches a preset actor,
 * we skip the disambiguation step entirely.
 */
class PresetActorDatabase(private val context: Context) {

    // All preset actors loaded from JSON, stored in memory for fast lookup
    private var allActors: List<PresetActor> = emptyList()

    /**
     * Load the preset actors from assets/preset_actors.json.
     * Call this once when the app starts (e.g., in VersusApplication or HomeViewModel).
     */
    fun load() {
        val json = context.assets.open("preset_actors.json")
            .bufferedReader()
            .use { it.readText() }

        val database = Gson().fromJson(json, PresetActorJson::class.java)

        // Combine all categories into one flat list
        allActors = listOfNotNull(
            database.tamilActors,
            database.tamilActresses,
            database.teluguActors,
            database.teluguActresses,
            database.bollywoodActors,
            database.bollywoodActresses,
            database.hollywoodActors,
            database.hollywoodActresses
        ).flatten()
    }

    /**
     * Search for actors whose name or alias matches the query.
     *
     * @param query The text the user typed (e.g., "Vijay", "SRK", "Tom")
     * @return List of matching Actor objects, or empty if no match
     */
    fun search(query: String): List<Actor> {
        if (query.isBlank()) return emptyList()

        val lowerQuery = query.lowercase().trim()

        return allActors.filter { preset ->
            // Check if query matches the name or any alias (case-insensitive)
            preset.name.lowercase().contains(lowerQuery) ||
                preset.aliases.any { alias -> alias.lowercase().contains(lowerQuery) }
        }.map { preset ->
            // Convert PresetActor (JSON format) → Actor (domain model)
            Actor(
                id = preset.id,
                name = preset.name,
                profilePath = null,     // Presets don't have profile photos stored
                knownFor = emptyList()
            )
        }
    }

    /**
     * Find an exact match by TMDb ID.
     */
    fun findById(id: Int): Actor? {
        return allActors.find { it.id == id }?.let {
            Actor(id = it.id, name = it.name)
        }
    }
}

// ── JSON model classes (match the structure of preset_actors.json) ──

/**
 * Root structure of the JSON file.
 */
private data class PresetActorJson(
    @SerializedName("tamil_actors") val tamilActors: List<PresetActor>?,
    @SerializedName("tamil_actresses") val tamilActresses: List<PresetActor>?,
    @SerializedName("telugu_actors") val teluguActors: List<PresetActor>?,
    @SerializedName("telugu_actresses") val teluguActresses: List<PresetActor>?,
    @SerializedName("bollywood_actors") val bollywoodActors: List<PresetActor>?,
    @SerializedName("bollywood_actresses") val bollywoodActresses: List<PresetActor>?,
    @SerializedName("hollywood_actors") val hollywoodActors: List<PresetActor>?,
    @SerializedName("hollywood_actresses") val hollywoodActresses: List<PresetActor>?
)

/**
 * A single actor entry in the JSON.
 */
private data class PresetActor(
    val id: Int,
    val name: String,
    val aliases: List<String> = emptyList()
)
