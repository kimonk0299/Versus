package com.versus.app.domain.model

/**
 * Holds the entire state of the tournament bracket.
 *
 * @param rounds       List of rounds. Each round is a list of matchups (pairs of movies).
 * @param currentRound Index of the current round (0 = Round of 32, etc.)
 * @param currentMatch Index of the current matchup within the round
 * @param winners      Movies that won in the current round so far
 * @param champion     The final winner (null until the tournament ends)
 */
data class BracketState(
    val rounds: List<List<Matchup>> = emptyList(),
    val currentRound: Int = 0,
    val currentMatch: Int = 0,
    val winners: List<Movie> = emptyList(),
    val champion: Movie? = null
)

/**
 * A single matchup between two movies.
 * The user picks one as the winner.
 */
data class Matchup(
    val movie1: Movie,
    val movie2: Movie,
    val winner: Movie? = null   // null until the user picks
)
