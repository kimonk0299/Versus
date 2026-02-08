package com.versus.app.domain.model

/**
 * Represents a movie in the bracket tournament.
 *
 * @param id          TMDb movie ID
 * @param title       Movie title
 * @param posterPath  Partial URL for the poster image (e.g., "/poster123.jpg")
 * @param popularity  Calculated score (voteCount * voteAverage) used for seeding
 * @param year        Release year (for display)
 */
data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String? = null,
    val popularity: Double = 0.0,
    val year: String = ""
)
