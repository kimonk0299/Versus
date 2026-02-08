package com.versus.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.versus.app.data.local.PresetActorDatabase
import com.versus.app.domain.model.Actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Home Screen.
 *
 * Handles:
 * - Loading the preset actor database
 * - Searching for actors as the user types
 * - Managing the selected mode (Single / Versus)
 *
 * AndroidViewModel gives us access to the Application context,
 * which we need to read the preset_actors.json from assets.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // The preset actor database (loaded from JSON)
    private val presetDb = PresetActorDatabase(application)

    // ── UI State ──

    // Current search results from the preset database
    private val _suggestions = MutableStateFlow<List<Actor>>(emptyList())
    val suggestions: StateFlow<List<Actor>> = _suggestions.asStateFlow()

    // Selected mode: "single" or "versus"
    private val _mode = MutableStateFlow("single")
    val mode: StateFlow<String> = _mode.asStateFlow()

    init {
        // Load the preset actors when the ViewModel is created
        presetDb.load()
    }

    /**
     * Called every time the user types in the actor search field.
     * Updates the suggestions dropdown.
     */
    fun onSearchQueryChanged(query: String) {
        _suggestions.value = if (query.length >= 2) {
            presetDb.search(query)
        } else {
            emptyList()
        }
    }

    /**
     * Switch between "single" and "versus" mode.
     */
    fun onModeChanged(newMode: String) {
        _mode.value = newMode
    }

    /**
     * Check if a query matches exactly one preset actor.
     * If so, return their TMDb ID. Otherwise return null.
     */
    fun findExactPresetMatch(query: String): Actor? {
        val results = presetDb.search(query)
        // Only auto-match if there's exactly one result and it's a close match
        return results.firstOrNull { it.name.equals(query.trim(), ignoreCase = true) }
    }
}
