package com.versus.app.data.repository

import com.versus.app.BuildConfig
import com.versus.app.data.api.RetrofitClient
import com.versus.app.data.api.TmdbApiService
import com.versus.app.domain.model.Actor
import com.versus.app.domain.model.Movie

/**
 * Repository: the single source of truth for all data in the app.
 *
 * It talks to the TMDb API and converts raw API responses into our domain models
 * (Actor, Movie). ViewModels call this instead of talking to the API directly.
 *
 * This follows the "Repository Pattern" from MVVM architecture.
 */
class MovieRepository(
    private val apiService: TmdbApiService = RetrofitClient.apiService
) {
    // The TMDb API key, read from BuildConfig (set in local.properties)
    private val apiKey = BuildConfig.TMDB_API_KEY

    /**
     * Search for actors by name using the TMDb search API.
     *
     * @param query The actor name to search for (e.g., "Vijay")
     * @return List of matching Actor objects
     */
    suspend fun searchActors(query: String): List<Actor> {
        val response = apiService.searchPerson(query, apiKey)
        return response.results.map { person ->
            Actor(
                id = person.id,
                name = person.name,
                profilePath = person.profilePath,
                // Extract movie titles from "known for" list (filter out TV shows)
                knownFor = person.knownFor
                    ?.filter { it.mediaType == "movie" }
                    ?.mapNotNull { it.title }
                    ?: emptyList()
            )
        }
    }

    /**
     * Get an actor's name from their TMDb ID.
     */
    suspend fun getActorName(actorId: Int): String {
        return try {
            apiService.getPersonDetails(actorId, apiKey).name
        } catch (e: Exception) {
            "Actor $actorId"
        }
    }

    /**
     * Fetch the top movies for an actor, sorted by popularity score.
     *
     * @param actorId TMDb person ID
     * @param count   How many movies to return (default: 32 for single actor mode)
     * @return Sorted list of top movies
     */
    suspend fun getTopMovies(actorId: Int, count: Int = 32): List<Movie> {
        val response = apiService.getMovieCredits(actorId, apiKey)
        return response.cast
            // Filter out entries with no title (poster is optional)
            .filter { it.title != null }
            // Calculate popularity score: voteCount * voteAverage
            // This gives better results than TMDb's built-in popularity
            // because it considers both how MANY people rated it AND how HIGH they rated it
            .map { castItem ->
                Movie(
                    id = castItem.id,
                    title = castItem.title!!,
                    posterPath = castItem.posterPath,
                    popularity = castItem.voteCount.toDouble() * castItem.voteAverage,
                    // Extract just the year from "2023-07-15" → "2023"
                    year = castItem.releaseDate?.take(4) ?: ""
                )
            }
            // Sort by popularity (highest first)
            .sortedByDescending { it.popularity }
            // Take only the top N movies
            .take(count)
    }
}
