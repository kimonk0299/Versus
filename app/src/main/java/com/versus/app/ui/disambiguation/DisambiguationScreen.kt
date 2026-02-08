package com.versus.app.ui.disambiguation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.versus.app.domain.model.Actor
import com.versus.app.ui.theme.VsPrimary
import com.versus.app.utils.Constants

/**
 * Disambiguation Screen — shown when the actor name isn't in the preset database.
 *
 * Displays a list of matching actors from TMDb with:
 * - Profile photo
 * - Full name
 * - Top 3 known movies
 *
 * The user taps the correct actor to continue to the bracket.
 */
@Composable
fun DisambiguationScreen(
    query: String,
    mode: String,
    otherActorId: Int,
    onActorSelected: (actorId: Int) -> Unit,
    onBack: () -> Unit,
    viewModel: DisambiguationViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val error by viewModel.error.collectAsState()

    // Search for actors when the screen loads
    LaunchedEffect(query) {
        viewModel.searchActors(query)
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
                text = "Which \"$query\"?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Multiple actors found. Tap the correct one:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Content ──
        when {
            isLoading -> {
                // Show loading spinner
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = VsPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Searching TMDb...",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            error != null -> {
                // Show error message
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
                // Show list of matching actors
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = searchResults,
                        key = { it.id }
                    ) { actor ->
                        ActorResultCard(
                            actor = actor,
                            onClick = { onActorSelected(actor.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * A card showing one actor search result.
 * Shows profile photo, name, and known-for movies.
 */
@Composable
private fun ActorResultCard(
    actor: Actor,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Profile Photo ──
            val profileUrl = actor.profilePath?.let {
                "${Constants.TMDB_IMAGE_BASE_URL}${Constants.PROFILE_SIZE}$it"
            }

            if (profileUrl != null) {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = actor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                // Placeholder when no photo is available
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ── Name + Known For ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actor.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (actor.knownFor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Known for: ${actor.knownFor.take(3).joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
