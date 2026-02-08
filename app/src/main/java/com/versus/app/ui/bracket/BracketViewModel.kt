package com.versus.app.ui.bracket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.versus.app.data.repository.MovieRepository
import com.versus.app.domain.model.BracketState
import com.versus.app.domain.model.Matchup
import com.versus.app.domain.model.Movie
import com.versus.app.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Bracket (Tournament) Screen.
 *
 * Handles two modes:
 * - Single Actor: Traditional bracket tournament (32→16→8→4→2→1)
 * - Actor vs Actor: Head-to-head matchups where each actor's top 32 movies
 *   are randomly paired, and the actor with the most wins takes it.
 */
class BracketViewModel : ViewModel() {

    private val repository = MovieRepository()

    // ── Common UI State ──

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _bracketState = MutableStateFlow(BracketState())
    val bracketState: StateFlow<BracketState> = _bracketState.asStateFlow()

    // All movies in the tournament (for display purposes)
    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    // ── Versus Mode State ──

    private val _isVersusMode = MutableStateFlow(false)
    val isVersusMode: StateFlow<Boolean> = _isVersusMode.asStateFlow()

    private val _actor1Name = MutableStateFlow("")
    val actor1Name: StateFlow<String> = _actor1Name.asStateFlow()

    private val _actor2Name = MutableStateFlow("")
    val actor2Name: StateFlow<String> = _actor2Name.asStateFlow()

    private val _actor1Wins = MutableStateFlow(0)
    val actor1Wins: StateFlow<Int> = _actor1Wins.asStateFlow()

    private val _actor2Wins = MutableStateFlow(0)
    val actor2Wins: StateFlow<Int> = _actor2Wins.asStateFlow()

    private val _versusComplete = MutableStateFlow(false)
    val versusComplete: StateFlow<Boolean> = _versusComplete.asStateFlow()

    private val _totalVersusMatchups = MutableStateFlow(0)
    val totalVersusMatchups: StateFlow<Int> = _totalVersusMatchups.asStateFlow()

    /**
     * Load movies for a single actor and create the bracket.
     *
     * @param actorId TMDb person ID
     */
    fun loadSingleActor(actorId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val movies = repository.getTopMovies(actorId, Constants.TOTAL_MOVIES)

                if (movies.size < 2) {
                    _error.value = "Not enough movies found (need at least 2, got ${movies.size})."
                    return@launch
                }

                // Pad to nearest power of 2 if needed, or just use what we have
                val bracketMovies = padToPowerOfTwo(movies)
                _allMovies.value = bracketMovies
                initializeBracket(bracketMovies)
            } catch (e: Exception) {
                _error.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load movies for two actors (Versus mode) — head-to-head matchups.
     * Fetches top 32 from each actor, randomly pairs them, and tracks wins per actor.
     *
     * @param actor1Id First actor's TMDb person ID
     * @param actor2Id Second actor's TMDb person ID
     */
    fun loadVersusActors(actor1Id: Int, actor2Id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isVersusMode.value = true

            try {
                // Fetch actor names from TMDb
                _actor1Name.value = repository.getActorName(actor1Id)
                _actor2Name.value = repository.getActorName(actor2Id)

                // Fetch top 32 movies from each actor
                val movies1 = repository.getTopMovies(actor1Id, Constants.MOVIES_PER_ACTOR_VS)
                val movies2 = repository.getTopMovies(actor2Id, Constants.MOVIES_PER_ACTOR_VS)

                if (movies1.size < 2 || movies2.size < 2) {
                    _error.value = "Not enough movies found for one of the actors."
                    return@launch
                }

                // Shuffle and pair: each matchup is actor1's movie vs actor2's movie
                val shuffled1 = movies1.shuffled()
                val shuffled2 = movies2.shuffled()
                val pairCount = minOf(shuffled1.size, shuffled2.size)

                val matchups = (0 until pairCount).map { i ->
                    Matchup(movie1 = shuffled1[i], movie2 = shuffled2[i])
                }

                _totalVersusMatchups.value = pairCount
                _actor1Wins.value = 0
                _actor2Wins.value = 0

                // Store all matchups in a single "round"
                _bracketState.value = BracketState(
                    rounds = listOf(matchups),
                    currentRound = 0,
                    currentMatch = 0,
                    winners = emptyList(),
                    champion = null
                )
            } catch (e: Exception) {
                _error.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * User picked a movie as the winner of the current matchup.
     * Routes to bracket logic or versus logic depending on mode.
     */
    fun pickWinner(winner: Movie) {
        if (_isVersusMode.value) {
            pickVersusWinner(winner)
            return
        }

        // ── Single Actor Bracket Logic ──
        val state = _bracketState.value
        val currentRoundMatchups = state.rounds.getOrNull(state.currentRound) ?: return
        val currentMatchup = currentRoundMatchups.getOrNull(state.currentMatch) ?: return

        // Record the winner for this matchup
        val updatedMatchups = currentRoundMatchups.toMutableList()
        updatedMatchups[state.currentMatch] = currentMatchup.copy(winner = winner)

        val updatedRounds = state.rounds.toMutableList()
        updatedRounds[state.currentRound] = updatedMatchups

        // Add winner to the list of round winners
        val updatedWinners = state.winners + winner

        if (state.currentMatch + 1 < currentRoundMatchups.size) {
            // More matchups in this round → advance to next matchup
            _bracketState.value = state.copy(
                rounds = updatedRounds,
                currentMatch = state.currentMatch + 1,
                winners = updatedWinners
            )
        } else {
            // Round complete → check if tournament is over
            if (updatedWinners.size == 1) {
                // Only one winner left → CHAMPION!
                _bracketState.value = state.copy(
                    rounds = updatedRounds,
                    champion = winner
                )
            } else {
                // Create next round with the winners
                val nextRoundMatchups = createMatchups(updatedWinners)
                val newRounds = updatedRounds.toMutableList()
                newRounds.add(nextRoundMatchups)

                _bracketState.value = state.copy(
                    rounds = newRounds,
                    currentRound = state.currentRound + 1,
                    currentMatch = 0,
                    winners = emptyList()  // Reset winners for the new round
                )
            }
        }
    }

    /**
     * Handle a pick in versus (head-to-head) mode.
     * Tracks which actor the winning movie belongs to and advances.
     */
    private fun pickVersusWinner(winner: Movie) {
        val state = _bracketState.value
        val matchups = state.rounds.getOrNull(0) ?: return
        val currentMatchup = matchups.getOrNull(state.currentMatch) ?: return

        // movie1 always belongs to actor 1, movie2 to actor 2
        if (winner.id == currentMatchup.movie1.id) {
            _actor1Wins.value++
        } else {
            _actor2Wins.value++
        }

        if (state.currentMatch + 1 < matchups.size) {
            // Advance to next matchup
            _bracketState.value = state.copy(currentMatch = state.currentMatch + 1)
        } else {
            // All matchups done
            _versusComplete.value = true
        }
    }

    /**
     * Initialize the bracket with the first round of matchups.
     */
    private fun initializeBracket(movies: List<Movie>) {
        val matchups = createMatchups(movies)
        _bracketState.value = BracketState(
            rounds = listOf(matchups),
            currentRound = 0,
            currentMatch = 0,
            winners = emptyList(),
            champion = null
        )
    }

    /**
     * Create matchups by pairing adjacent movies.
     * Movie 0 vs Movie 1, Movie 2 vs Movie 3, etc.
     */
    private fun createMatchups(movies: List<Movie>): List<Matchup> {
        val matchups = mutableListOf<Matchup>()
        for (i in movies.indices step 2) {
            if (i + 1 < movies.size) {
                matchups.add(Matchup(movie1 = movies[i], movie2 = movies[i + 1]))
            }
        }
        return matchups
    }

    /**
     * Pad the movie list to the nearest power of 2 (4, 8, 16, 32).
     * If we have fewer than 32, we just use what we have.
     * The bracket works with any even number.
     */
    private fun padToPowerOfTwo(movies: List<Movie>): List<Movie> {
        // Find the largest power of 2 that's <= movies.size
        val targetSize = when {
            movies.size >= 32 -> 32
            movies.size >= 16 -> 16
            movies.size >= 8 -> 8
            movies.size >= 4 -> 4
            else -> 2
        }
        return movies.take(targetSize)
    }
}
