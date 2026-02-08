package com.versus.app

import android.app.Application

/**
 * Custom Application class for the Versus app.
 *
 * This is the first thing that runs when the app starts.
 * Right now it's simple, but we declare it so we can add
 * app-wide initialization later if needed (e.g., crash reporting).
 *
 * Registered in AndroidManifest.xml via android:name=".VersusApplication"
 */
class VersusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // App-wide initialization goes here
    }
}
