package com.versus.app.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for TMDb API endpoints.
 *
 * Each function maps to one TMDb API call.
 * Retrofit handles the HTTP request/response automatically.
 * The "suspend" keyword means these run on a coroutine (background thread).
 */
interface TmdbApiService {

    /**
     * Search for actors/actresses by name.
     * Example: /search/person?query=Vijay&api_key=xxx
     *
     * @param query  The actor name to search for
     * @param apiKey Your TMDb API key
     * @return       List of matching persons
     */
    @GET("search/person")
    suspend fun searchPerson(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): PersonSearchResponse

    /**
     * Get all movie credits for a specific actor.
     * Example: /person/12345/movie_credits?api_key=xxx
     *
     * @param personId TMDb person ID
     * @param apiKey   Your TMDb API key
     * @return         All movies the actor appeared in
     */
    @GET("person/{person_id}/movie_credits")
    suspend fun getMovieCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): MovieCreditsResponse

    /**
     * Get basic details for a person (name, etc.).
     * Example: /person/12345?api_key=xxx
     */
    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): PersonDetailsResponse
}
