package com.versus.app.ui.winner

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.versus.app.ui.theme.VsPrimary
import com.versus.app.utils.Constants
import kotlin.random.Random

/**
 * Winner Screen — shows the champion movie with confetti animation!
 *
 * Displayed after the final matchup is decided.
 * Shows the winning movie's poster, title, and a "Play Again" button.
 */
@Composable
fun WinnerScreen(
    movieTitle: String,
    posterPath: String?,
    onPlayAgain: () -> Unit
) {
    // ── Scale animation for the poster (bouncy entrance) ──
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
            // ── Trophy / Crown ──
            Text(
                text = "CHAMPION",
                style = MaterialTheme.typography.displayLarge,
                color = VsPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Winner Poster ──
            val posterUrl = posterPath?.let {
                "${Constants.TMDB_IMAGE_BASE_URL}${Constants.POSTER_SIZE}$it"
            }

            Box(
                modifier = Modifier
                    .width(200.dp * scale.value)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = movieTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Movie Title ──
            Text(
                text = movieTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "is the ultimate winner!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

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
 * Simple confetti animation using Canvas.
 *
 * Draws colorful circles/rectangles falling from the top of the screen.
 * Uses Compose's infinite transition for continuous animation.
 */
@Composable
private fun ConfettiAnimation() {
    // Create confetti particles with random properties
    val confettiPieces = remember {
        List(60) {
            ConfettiPiece(
                x = Random.nextFloat(),             // Random horizontal position (0-1)
                startY = Random.nextFloat() * -1f,  // Start above screen
                speed = 0.3f + Random.nextFloat() * 0.7f,  // Random fall speed
                size = 4f + Random.nextFloat() * 8f,        // Random size
                color = confettiColors[Random.nextInt(confettiColors.size)]
            )
        }
    }

    // Animate a progress value from 0 to 1 repeatedly
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
            // Calculate Y position: move down over time, wrap around
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

/**
 * Data for a single confetti particle.
 */
private data class ConfettiPiece(
    val x: Float,
    val startY: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

// Playful confetti colors matching the party-game palette
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
