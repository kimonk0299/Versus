package com.versus.app.utils

/**
 * App-wide constants.
 * TMDb base URLs, image sizes, and bracket configuration.
 */
object Constants {
    // ── TMDb API ──
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

    // Image sizes available from TMDb (w92, w154, w185, w342, w500, w780, original)
    const val POSTER_SIZE = "w500"
    const val PROFILE_SIZE = "w185"

    // ── Bracket Configuration ──
    const val TOTAL_MOVIES = 32           // Total movies in a bracket
    const val MOVIES_PER_ACTOR_VS = 32    // Movies per actor in Versus mode

    // ── Round Names ──
    // Returns the round name based on how many matchups are in that round
    fun getRoundName(matchupCount: Int): String {
        return when (matchupCount) {
            16 -> "Round of 32"
            8 -> "Round of 16"
            4 -> "Quarterfinals"
            2 -> "Semifinals"
            1 -> "Final"
            else -> "Round of ${matchupCount * 2}"
        }
    }
}
