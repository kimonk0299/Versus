package com.versus.app.domain.model

/**
 * Represents an actor/actress.
 *
 * @param id          TMDb person ID (used to fetch their movies)
 * @param name        Full name of the actor
 * @param profilePath Partial URL for their profile photo (e.g., "/abc123.jpg")
 * @param knownFor    List of movie titles they're known for (used in disambiguation)
 */
data class Actor(
    val id: Int,
    val name: String,
    val profilePath: String? = null,
    val knownFor: List<String> = emptyList()
)
