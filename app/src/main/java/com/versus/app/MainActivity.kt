package com.versus.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.versus.app.ui.navigation.VersusNavGraph
import com.versus.app.ui.theme.VersusTheme

/**
 * Main Activity — the single activity in the app.
 *
 * We use "single activity" architecture: this one Activity hosts
 * all screens via Jetpack Compose Navigation. Each screen is a
 * Composable function, not a separate Activity.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (content extends behind status/nav bars)
        enableEdgeToEdge()

        // Set the Compose UI as the content of this Activity
        setContent {
            VersusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create a NavController to handle navigation between screens
                    val navController = rememberNavController()

                    // The navigation graph defines all screens and their routes
                    VersusNavGraph(navController = navController)
                }
            }
        }
    }
}
