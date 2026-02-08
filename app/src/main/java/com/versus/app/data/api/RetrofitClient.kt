package com.versus.app.data.api

import com.versus.app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton that creates and provides the Retrofit API service.
 *
 * "object" in Kotlin means there's only ONE instance of this class (singleton pattern).
 * This is important because we don't want multiple Retrofit instances wasting memory.
 */
object RetrofitClient {

    // Logging interceptor: prints API requests/responses in Logcat (debug builds only)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC  // Logs URL + response code
    }

    // OkHttp client with the logging interceptor attached
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // The Retrofit instance configured for TMDb API
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.TMDB_BASE_URL)              // Base URL for all API calls
        .client(httpClient)                             // Use our OkHttp client
        .addConverterFactory(GsonConverterFactory.create()) // Auto-convert JSON ↔ Kotlin
        .build()

    // The actual API service we'll use throughout the app
    val apiService: TmdbApiService = retrofit.create(TmdbApiService::class.java)
}
