package com.versus.app.ui.versus

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.versus.app.ui.theme.VsPrimary
import kotlin.random.Random

/**
 * Versus Result Screen — shows which actor won the head-to-head!
 *
 * Displayed after all matchups in Actor vs Actor mode are completed.
 * Shows the winning actor's name, the final score, and a "Play Again" button.
 */
@Composable
fun VersusResultScreen(
    winnerName: String,
    loserName: String,
    winnerWins: Int,
    loserWins: Int,
    onPlayAgain: () -> Unit
) {
    // ── Scale animation (bouncy entrance) ──
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val isTie = winnerWins == loserWins

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Confetti Animation ──
        ConfettiAnimation()

        // ── Main Content ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Result Header ──
            Text(
                text = if (isTie) "IT'S A TIE!" else "WINNER",
                style = MaterialTheme.typography.displayLarge,
                color = VsPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isTie) {
                // ── Winning Actor Name ──
                Text(
                    text = winnerName,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "takes the crown!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Score Card ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Winner / Actor 1
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = winnerName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isTie) MaterialTheme.colorScheme.onBackground else VsPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$winnerWins",
                        style = MaterialTheme.typography.displayMedium,
                        color = VsPrimary,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Text(
                    text = "-",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.width(32.dp))

                // Loser / Actor 2
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = loserName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$loserWins",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Play Again Button ──
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VsPrimary)
            ) {
                Text(
                    text = "PLAY AGAIN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Confetti animation (same style as WinnerScreen).
 */
@Composable
private fun ConfettiAnimation() {
    val confettiPieces = remember {
        List(60) {
            ConfettiPiece(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * -1f,
                speed = 0.3f + Random.nextFloat() * 0.7f,
                size = 4f + Random.nextFloat() * 8f,
                color = confettiColors[Random.nextInt(confettiColors.size)]
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        confettiPieces.forEach { piece ->
            val y = ((piece.startY + progress * piece.speed * 3f) % 1.3f) * size.height
            val x = piece.x * size.width
            drawCircle(
                color = piece.color,
                radius = piece.size,
                center = Offset(x, y)
            )
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val startY: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

private val confettiColors = listOf(
    Color(0xFF6C63FF),  // Purple
    Color(0xFFFF6584),  // Coral
    Color(0xFFFFB800),  // Gold
    Color(0xFF00D4AA),  // Teal
    Color(0xFFFF9F43),  // Orange
    Color(0xFF4ECDC4),  // Mint
    Color(0xFFFF6B6B),  // Pink
    Color(0xFF45B7D1),  // Sky Blue
)
