package com.versus.app.ui.disambiguation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.versus.app.data.repository.MovieRepository
import com.versus.app.domain.model.Actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Disambiguation Screen.
 *
 * When the typed actor name isn't in our preset database,
 * we search TMDb's API and show matching persons so the user
 * can pick the right one (e.g., "Vijay" could be multiple people).
 */
class DisambiguationViewModel : ViewModel() {

    private val repository = MovieRepository()

    // ── UI State ──

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // List of actors from TMDb search
    private val _searchResults = MutableStateFlow<List<Actor>>(emptyList())
    val searchResults: StateFlow<List<Actor>> = _searchResults.asStateFlow()

    // Error message (null = no error)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Search TMDb for actors matching the query.
     * Called when the disambiguation screen loads.
     */
    fun searchActors(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val results = repository.searchActors(query)
                _searchResults.value = results
                if (results.isEmpty()) {
                    _error.value = "No actors found for \"$query\". Try a different name."
                }
            } catch (e: Exception) {
                _error.value = "Failed to search: ${e.message}\nCheck your internet connection and API key."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
