package com.versus.app.data.api

import com.google.gson.annotations.SerializedName

// ── TMDb API Response Models ──
// These classes map directly to the JSON responses from TMDb.
// Gson automatically converts JSON keys to these fields using @SerializedName.

/**
 * Response from /search/person endpoint.
 * Contains a list of actors matching the search query.
 */
data class PersonSearchResponse(
    val results: List<PersonResult>
)

/**
 * A single person result from TMDb search.
 */
data class PersonResult(
    val id: Int,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("known_for")
    val knownFor: List<KnownForItem>?
)

/**
 * A movie/show that an actor is known for (returned in search results).
 */
data class KnownForItem(
    val id: Int,
    val title: String?,          // Movie title (null for TV shows)
    val name: String?,           // TV show name (null for movies)
    @SerializedName("media_type")
    val mediaType: String?       // "movie" or "tv"
)

/**
 * Response from /person/{id}/movie_credits endpoint.
 * Contains all movies an actor has appeared in.
 */
data class MovieCreditsResponse(
    val cast: List<CastItem>
)

/**
 * Response from /person/{id} endpoint.
 * Contains basic person info.
 */
data class PersonDetailsResponse(
    val id: Int,
    val name: String
)

/**
 * A single movie credit for an actor.
 */
data class CastItem(
    val id: Int,
    val title: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("release_date")
    val releaseDate: String?     // Format: "2023-07-15" (can be null or empty)
)
