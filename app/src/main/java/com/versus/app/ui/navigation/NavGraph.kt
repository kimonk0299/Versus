package com.versus.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.versus.app.ui.bracket.BracketScreen
import com.versus.app.ui.disambiguation.DisambiguationScreen
import com.versus.app.ui.home.HomeScreen
import com.versus.app.ui.versus.VersusResultScreen
import com.versus.app.ui.winner.WinnerScreen

/**
 * All the screen routes in the app.
 * Using an object with constants prevents typo bugs in navigation.
 */
object Routes {
    const val HOME = "home"
    const val DISAMBIGUATION = "disambiguation/{query}/{mode}/{otherActorId}"
    const val BRACKET_SINGLE = "bracket/single/{actorId}"
    const val BRACKET_VS = "bracket/vs/{actor1Id}/{actor2Id}"
    const val WINNER = "winner/{movieTitle}/{posterPath}"
    const val VERSUS_RESULT = "versus_result/{winnerName}/{loserName}/{winnerWins}/{loserWins}"

    // Helper functions to build route strings with actual values
    fun disambiguation(query: String, mode: String, otherActorId: Int = -1) =
        "disambiguation/$query/$mode/$otherActorId"

    fun bracketSingle(actorId: Int) = "bracket/single/$actorId"
    fun bracketVs(actor1Id: Int, actor2Id: Int) = "bracket/vs/$actor1Id/$actor2Id"

    fun winner(movieTitle: String, posterPath: String) =
        "winner/$movieTitle/${posterPath.replace("/", "___")}"

    fun versusResult(winnerName: String, loserName: String, winnerWins: Int, loserWins: Int) =
        "versus_result/${Uri.encode(winnerName)}/${Uri.encode(loserName)}/$winnerWins/$loserWins"
}

/**
 * The navigation graph defines all screens and how to move between them.
 *
 * Flow:
 *   Home → (preset match) → Bracket → Winner
 *   Home → (no match) → Disambiguation → Bracket → Winner
 *   Home → (versus) → Bracket (Head-to-Head) → VersusResult
 */
@Composable
fun VersusNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // ── Home Screen ──
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToBracketSingle = { actorId ->
                    navController.navigate(Routes.bracketSingle(actorId))
                },
                onNavigateToBracketVs = { actor1Id, actor2Id ->
                    navController.navigate(Routes.bracketVs(actor1Id, actor2Id))
                },
                onNavigateToDisambiguation = { query, mode, otherActorId ->
                    navController.navigate(Routes.disambiguation(query, mode, otherActorId))
                }
            )
        }

        // ── Disambiguation Screen ──
        // Shown when the typed actor name isn't in our preset database
        composable(
            route = Routes.DISAMBIGUATION,
            arguments = listOf(
                navArgument("query") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType },
                navArgument("otherActorId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val mode = backStackEntry.arguments?.getString("mode") ?: "single"
            val otherActorId = backStackEntry.arguments?.getInt("otherActorId") ?: -1

            DisambiguationScreen(
                query = query,
                mode = mode,
                otherActorId = otherActorId,
                onActorSelected = { selectedActorId ->
                    if (mode == "single") {
                        navController.navigate(Routes.bracketSingle(selectedActorId)) {
                            // Remove disambiguation from back stack
                            popUpTo(Routes.HOME)
                        }
                    } else {
                        // In versus mode, figure out which actor slot this fills
                        if (otherActorId == -1) {
                            navController.navigate(Routes.bracketVs(selectedActorId, selectedActorId))
                        } else {
                            navController.navigate(Routes.bracketVs(otherActorId, selectedActorId)) {
                                popUpTo(Routes.HOME)
                            }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Bracket Screen (Single Actor) ──
        composable(
            route = Routes.BRACKET_SINGLE,
            arguments = listOf(
                navArgument("actorId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val actorId = backStackEntry.arguments?.getInt("actorId") ?: 0

            BracketScreen(
                actorId = actorId,
                actor2Id = null,
                onWinner = { movie ->
                    val safePoster = (movie.posterPath ?: "none").replace("/", "___")
                    navController.navigate(Routes.winner(movie.title, safePoster)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onVersusResult = { _, _, _, _ -> },  // Not used in single mode
                onBack = { navController.popBackStack() }
            )
        }

        // ── Bracket Screen (Actor vs Actor — Head-to-Head) ──
        composable(
            route = Routes.BRACKET_VS,
            arguments = listOf(
                navArgument("actor1Id") { type = NavType.IntType },
                navArgument("actor2Id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val actor1Id = backStackEntry.arguments?.getInt("actor1Id") ?: 0
            val actor2Id = backStackEntry.arguments?.getInt("actor2Id") ?: 0

            BracketScreen(
                actorId = actor1Id,
                actor2Id = actor2Id,
                onWinner = { },  // Not used in versus mode
                onVersusResult = { winnerName, loserName, winnerWins, loserWins ->
                    navController.navigate(
                        Routes.versusResult(winnerName, loserName, winnerWins, loserWins)
                    ) {
                        popUpTo(Routes.HOME)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Winner Screen (Single Actor mode champion) ──
        composable(
            route = Routes.WINNER,
            arguments = listOf(
                navArgument("movieTitle") { type = NavType.StringType },
                navArgument("posterPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""
            val posterPath = backStackEntry.arguments?.getString("posterPath")
                ?.replace("___", "/")   // Restore the "/" we replaced
                ?.takeIf { it != "none" }

            WinnerScreen(
                movieTitle = movieTitle,
                posterPath = posterPath,
                onPlayAgain = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Versus Result Screen (Actor vs Actor mode winner) ──
        composable(
            route = Routes.VERSUS_RESULT,
            arguments = listOf(
                navArgument("winnerName") { type = NavType.StringType },
                navArgument("loserName") { type = NavType.StringType },
                navArgument("winnerWins") { type = NavType.IntType },
                navArgument("loserWins") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val winnerName = backStackEntry.arguments?.getString("winnerName") ?: ""
            val loserName = backStackEntry.arguments?.getString("loserName") ?: ""
            val winnerWins = backStackEntry.arguments?.getInt("winnerWins") ?: 0
            val loserWins = backStackEntry.arguments?.getInt("loserWins") ?: 0

            VersusResultScreen(
                winnerName = winnerName,
                loserName = loserName,
                winnerWins = winnerWins,
                loserWins = loserWins,
                onPlayAgain = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
