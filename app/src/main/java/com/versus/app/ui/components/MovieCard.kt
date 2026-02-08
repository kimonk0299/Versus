package com.versus.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.versus.app.domain.model.Movie
import com.versus.app.ui.theme.VsCardBorder
import com.versus.app.ui.theme.VsGradientEnd
import com.versus.app.ui.theme.VsGradientStart
import com.versus.app.ui.theme.VsPrimary
import com.versus.app.utils.Constants

/**
 * A movie card showing the poster as background with title overlaid at the bottom.
 *
 * Used in the bracket screen for matchups. When tapped, the user picks this movie as the winner.
 *
 * @param movie     The movie data to display
 * @param onClick   Called when the user taps this card (picks this movie)
 * @param modifier  Optional Compose modifier
 * @param isWinner  Whether this movie was selected as winner (adds highlight border)
 */
@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isWinner: Boolean = false
) {
    // Animate the border color when the card is selected
    val borderColor by animateColorAsState(
        targetValue = if (isWinner) VsPrimary else VsCardBorder,
        label = "borderColor"
    )

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isWinner) 3.dp else 1.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        // Stack: poster image at the back, gradient + text on top
        Box(modifier = Modifier.fillMaxSize()) {
            // ── Poster Image ──
            // Coil's AsyncImage loads the poster from TMDb's image server
            val posterUrl = movie.posterPath?.let {
                "${Constants.TMDB_IMAGE_BASE_URL}${Constants.POSTER_SIZE}$it"
            }

            if (posterUrl != null) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,   // Fill the card, cropping edges
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder when no poster is available
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ── Gradient Overlay ──
            // Dark gradient at the bottom so the title text is readable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .gradientBackground()
            )

            // ── Title + Year ──
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (movie.year.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = movie.year,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Extension function that adds a vertical gradient from transparent to dark.
 * This makes text readable on top of the poster image.
 */
private fun Modifier.gradientBackground(): Modifier {
    return this.then(
        Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(VsGradientStart, VsGradientEnd),
                startY = 100f  // Start the gradient partway down
            )
        )
    )
}
