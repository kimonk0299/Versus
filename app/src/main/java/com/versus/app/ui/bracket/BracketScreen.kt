package com.versus.app.ui.bracket

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.versus.app.domain.model.Movie
import com.versus.app.ui.components.MovieCard
import com.versus.app.ui.theme.VsPrimary
import com.versus.app.utils.Constants

/**
 * Bracket (Tournament) Screen — the core of the app!
 *
 * Two modes:
 * - Single Actor: Shows two movie cards side by side. User taps their pick.
 *   Advances through rounds until a champion is crowned.
 * - Actor vs Actor: Head-to-head matchups. Each matchup pits one actor's movie
 *   against the other's. The actor with the most wins at the end wins.
 */
@Composable
fun BracketScreen(
    actorId: Int,
    actor2Id: Int?,
    onWinner: (Movie) -> Unit,
    onVersusResult: (winnerName: String, loserName: String, winnerWins: Int, loserWins: Int) -> Unit,
    onBack: () -> Unit,
    viewModel: BracketViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val bracketState by viewModel.bracketState.collectAsState()
    val isVersusMode by viewModel.isVersusMode.collectAsState()
    val actor1Name by viewModel.actor1Name.collectAsState()
    val actor2Name by viewModel.actor2Name.collectAsState()
    val actor1Wins by viewModel.actor1Wins.collectAsState()
    val actor2Wins by viewModel.actor2Wins.collectAsState()
    val versusComplete by viewModel.versusComplete.collectAsState()
    val totalVersusMatchups by viewModel.totalVersusMatchups.collectAsState()

    // Load movies when the screen opens
    LaunchedEffect(actorId, actor2Id) {
        if (actor2Id != null) {
            viewModel.loadVersusActors(actorId, actor2Id)
        } else {
            viewModel.loadSingleActor(actorId)
        }
    }

    // Navigate to winner screen when champion is determined (single actor mode)
    LaunchedEffect(bracketState.champion) {
        bracketState.champion?.let { champion ->
            onWinner(champion)
        }
    }

    // Navigate to versus result screen when all matchups are done
    LaunchedEffect(versusComplete) {
        if (versusComplete) {
            if (actor1Wins >= actor2Wins) {
                onVersusResult(actor1Name, actor2Name, actor1Wins, actor2Wins)
            } else {
                onVersusResult(actor2Name, actor1Name, actor2Wins, actor1Wins)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // ── Top Bar ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isVersusMode) "Head to Head" else "Tournament",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        when {
            isLoading -> {
                // ── Loading State ──
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = VsPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading movies...",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            error != null -> {
                // ── Error State ──
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                // ── Tournament / Head-to-Head UI ──
                val currentMatch = bracketState.currentMatch
                val currentRound = bracketState.currentRound
                val rounds = bracketState.rounds
                val currentMatchups = rounds.getOrNull(if (isVersusMode) 0 else currentRound)
                val matchup = currentMatchups?.getOrNull(currentMatch)

                if (matchup != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isVersusMode) {
                        // ── Versus Mode: Score Bar ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Actor 1 score
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = actor1Name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1
                                )
                                Text(
                                    text = "$actor1Wins",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = VsPrimary,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )

                            // Actor 2 score
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = actor2Name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1
                                )
                                Text(
                                    text = "$actor2Wins",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = VsPrimary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── Progress ──
                        val totalMatches = totalVersusMatchups
                        Text(
                            text = "Match ${currentMatch + 1} of $totalMatches",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { currentMatch.toFloat() / totalMatches },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = VsPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    } else {
                        // ── Single Actor Mode: Round Info ──
                        val totalMatches = currentMatchups?.size ?: 1
                        val roundName = Constants.getRoundName(totalMatches)

                        Text(
                            text = roundName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = VsPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Match ${currentMatch + 1} of $totalMatches",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { currentMatch.toFloat() / totalMatches },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = VsPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── "Pick your winner" ──
                    Text(
                        text = "Tap your pick!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Matchup Cards (stacked vertically) ──
                    AnimatedContent(
                        targetState = currentMatch,
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn())
                                .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                        },
                        label = "matchupTransition"
                    ) { _ ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Movie 1 (top) — portrait rectangle
                            MovieCard(
                                movie = matchup.movie1,
                                onClick = { viewModel.pickWinner(matchup.movie1) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(2f / 3f)
                            )

                            // "VS" text between cards
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.headlineMedium,
                                color = VsPrimary,
                                fontWeight = FontWeight.Black
                            )

                            // Movie 2 (bottom) — portrait rectangle
                            MovieCard(
                                movie = matchup.movie2,
                                onClick = { viewModel.pickWinner(matchup.movie2) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(2f / 3f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
