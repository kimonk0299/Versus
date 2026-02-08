package com.versus.app.ui.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.versus.app.domain.model.Actor
import com.versus.app.ui.theme.VsPrimary

/**
 * Home Screen — the first screen the user sees.
 *
 * User can:
 * 1. Choose mode (Single Actor or Actor vs Actor)
 * 2. Type actor name(s)
 * 3. See auto-complete suggestions from preset database
 * 4. Start the tournament
 */
@Composable
fun HomeScreen(
    onNavigateToBracketSingle: (actorId: Int) -> Unit,
    onNavigateToBracketVs: (actor1Id: Int, actor2Id: Int) -> Unit,
    onNavigateToDisambiguation: (query: String, mode: String, otherActorId: Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // Collect state from ViewModel
    val suggestions by viewModel.suggestions.collectAsState()
    val mode by viewModel.mode.collectAsState()

    // Local text field state
    var actor1Query by remember { mutableStateOf("") }
    var actor2Query by remember { mutableStateOf("") }
    var selectedActor1 by remember { mutableStateOf<Actor?>(null) }
    var selectedActor2 by remember { mutableStateOf<Actor?>(null) }

    // Track which field is currently active (for showing suggestions)
    var activeField by remember { mutableStateOf(1) } // 1 = actor1, 2 = actor2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── App Title ──
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = VsPrimary,
                modifier = Modifier.height(48.dp).width(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "VERSUS",
                style = MaterialTheme.typography.displayLarge,
                color = VsPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Movie Tournament",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Mode Selector ──
            Text(
                text = "Choose Mode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = mode == "single",
                    onClick = {
                        viewModel.onModeChanged("single")
                        selectedActor2 = null
                        actor2Query = ""
                    },
                    label = { Text("Single Actor") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VsPrimary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                FilterChip(
                    selected = mode == "versus",
                    onClick = { viewModel.onModeChanged("versus") },
                    label = { Text("Actor vs Actor") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VsPrimary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Actor 1 Input ──
            ActorSearchField(
                label = if (mode == "versus") "Actor 1" else "Actor Name",
                query = actor1Query,
                selectedActor = selectedActor1,
                onQueryChange = { newQuery ->
                    actor1Query = newQuery
                    selectedActor1 = null
                    activeField = 1
                    viewModel.onSearchQueryChanged(newQuery)
                },
                onClear = {
                    actor1Query = ""
                    selectedActor1 = null
                }
            )

            // ── Suggestions for Actor 1 ──
            AnimatedVisibility(visible = activeField == 1 && suggestions.isNotEmpty() && selectedActor1 == null) {
                SuggestionsList(
                    suggestions = suggestions,
                    onSelect = { actor ->
                        selectedActor1 = actor
                        actor1Query = actor.name
                        viewModel.onSearchQueryChanged("") // Clear suggestions
                    }
                )
            }

            // ── Actor 2 Input (only in Versus mode) ──
            if (mode == "versus") {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VsPrimary,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                ActorSearchField(
                    label = "Actor 2",
                    query = actor2Query,
                    selectedActor = selectedActor2,
                    onQueryChange = { newQuery ->
                        actor2Query = newQuery
                        selectedActor2 = null
                        activeField = 2
                        viewModel.onSearchQueryChanged(newQuery)
                    },
                    onClear = {
                        actor2Query = ""
                        selectedActor2 = null
                    }
                )

                // ── Suggestions for Actor 2 ──
                AnimatedVisibility(visible = activeField == 2 && suggestions.isNotEmpty() && selectedActor2 == null) {
                    SuggestionsList(
                        suggestions = suggestions,
                        onSelect = { actor ->
                            selectedActor2 = actor
                            actor2Query = actor.name
                            viewModel.onSearchQueryChanged("")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Start Button ──
            Button(
                onClick = {
                    handleStart(
                        mode = mode,
                        actor1Query = actor1Query,
                        actor2Query = actor2Query,
                        selectedActor1 = selectedActor1,
                        selectedActor2 = selectedActor2,
                        viewModel = viewModel,
                        onNavigateToBracketSingle = onNavigateToBracketSingle,
                        onNavigateToBracketVs = onNavigateToBracketVs,
                        onNavigateToDisambiguation = onNavigateToDisambiguation
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = actor1Query.isNotBlank() && (mode == "single" || actor2Query.isNotBlank()),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VsPrimary)
            ) {
                Text(
                    text = "START TOURNAMENT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Hint text ──
            Text(
                text = if (mode == "single")
                    "Pick an actor and their top 32 movies\nbattle it out in a tournament bracket!"
                else
                    "Pick two actors and their top movies\ngo head to head — the actor with more wins takes it!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Reusable actor search text field with search icon.
 */
@Composable
private fun ActorSearchField(
    label: String,
    query: String,
    selectedActor: Actor?,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = if (selectedActor != null) Icons.Default.Person else Icons.Default.Search,
                contentDescription = null,
                tint = if (selectedActor != null) VsPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VsPrimary,
            cursorColor = VsPrimary
        )
    )
}

/**
 * Dropdown-like list of actor suggestions.
 */
@Composable
private fun SuggestionsList(
    suggestions: List<Actor>,
    onSelect: (Actor) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        LazyColumn(
            modifier = Modifier.padding(4.dp),
            // Limit height so it doesn't take over the screen
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(
                items = suggestions.take(6),    // Show max 6 suggestions
                key = { it.id }
            ) { actor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(actor) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = actor.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Handles the Start button click.
 * Decides whether to go directly to bracket or through disambiguation.
 */
private fun handleStart(
    mode: String,
    actor1Query: String,
    actor2Query: String,
    selectedActor1: Actor?,
    selectedActor2: Actor?,
    viewModel: HomeViewModel,
    onNavigateToBracketSingle: (Int) -> Unit,
    onNavigateToBracketVs: (Int, Int) -> Unit,
    onNavigateToDisambiguation: (String, String, Int) -> Unit
) {
    if (mode == "single") {
        // Single actor mode
        val actor = selectedActor1 ?: viewModel.findExactPresetMatch(actor1Query)
        if (actor != null) {
            // Found a preset match → go straight to bracket
            onNavigateToBracketSingle(actor.id)
        } else {
            // No preset match → search TMDb and show disambiguation
            onNavigateToDisambiguation(actor1Query, "single", -1)
        }
    } else {
        // Versus mode — need both actors resolved
        val actor1 = selectedActor1 ?: viewModel.findExactPresetMatch(actor1Query)
        val actor2 = selectedActor2 ?: viewModel.findExactPresetMatch(actor2Query)

        when {
            actor1 != null && actor2 != null -> {
                // Both actors found → go to bracket
                onNavigateToBracketVs(actor1.id, actor2.id)
            }
            actor1 != null && actor2 == null -> {
                // Actor 1 found, actor 2 needs disambiguation
                onNavigateToDisambiguation(actor2Query, "versus", actor1.id)
            }
            actor1 == null -> {
                // Actor 1 needs disambiguation (actor 2 will be resolved after)
                onNavigateToDisambiguation(actor1Query, "single", -1)
            }
        }
    }
}
