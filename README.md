# Versus - Movie Tournament App

An Android app where you pick your favorite movies in a tournament bracket! Enter an actor's name, and their top 32 movies battle it out — you decide the winner of each matchup until a champion is crowned.

## Features

- **Single Actor Mode**: Top 32 movies from one actor in a bracket
- **Actor vs Actor Mode**: Top 16 from each actor (32 total) — who has the better filmography?
- **Preset Actor Database**: 160+ popular actors from Tamil, Telugu, Bollywood, and Hollywood cinema
- **Auto-search**: Type a name and get instant suggestions
- **Disambiguation**: When an actor isn't in the preset list, search TMDb and pick the right one
- **Beautiful UI**: Movie poster cards with gradient overlays, animated transitions, and confetti for the winner

## Setup

### 1. Get a TMDb API Key (Free)

1. Go to [themoviedb.org](https://www.themoviedb.org/) and create a free account
2. Go to **Settings** > **API** > **Create** > Choose **Developer**
3. Fill in the application details (you can put anything for the URL)
4. Copy your **API Key (v3 auth)**

### 2. Add API Key to the Project

Create or edit the `local.properties` file in the project root directory:

```properties
# This file should NOT be committed to git (it's in .gitignore)
TMDB_API_KEY=your_api_key_here
```

Replace `your_api_key_here` with your actual TMDb API key.

### 3. Open in Android Studio

1. Open Android Studio
2. Select **File > Open** and choose this project folder
3. Wait for Gradle sync to complete
4. Run the app on an emulator or physical device

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (ViewModel + Repository)
- **Networking**: Retrofit + Gson
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/src/main/
├── assets/
│   └── preset_actors.json          # Preset database of 160+ actors with TMDb IDs
├── java/com/versus/app/
│   ├── MainActivity.kt             # Single activity (hosts all Compose screens)
│   ├── VersusApplication.kt        # App-level initialization
│   ├── data/
│   │   ├── api/
│   │   │   ├── TmdbApiService.kt   # Retrofit API interface
│   │   │   ├── TmdbModels.kt       # API response data classes
│   │   │   └── RetrofitClient.kt   # Retrofit singleton
│   │   ├── local/
│   │   │   └── PresetActorDatabase.kt  # Loads & searches preset actors
│   │   └── repository/
│   │       └── MovieRepository.kt  # Single source of truth for data
│   ├── domain/model/
│   │   ├── Actor.kt                # Actor data class
│   │   ├── Movie.kt                # Movie data class
│   │   └── BracketState.kt         # Tournament state & matchup models
│   ├── ui/
│   │   ├── theme/                  # Material 3 dark theme (cinematic look)
│   │   ├── components/
│   │   │   └── MovieCard.kt        # Reusable movie poster card
│   │   ├── navigation/
│   │   │   └── NavGraph.kt         # Navigation routes & graph
│   │   ├── home/                   # Home screen + ViewModel
│   │   ├── disambiguation/         # Actor search results screen
│   │   ├── bracket/                # Tournament bracket screen
│   │   └── winner/                 # Champion screen with confetti
│   └── utils/
│       └── Constants.kt            # App-wide constants
└── res/                            # Android resources (strings, themes, icons)
```

## How It Works

1. **Home Screen**: Choose Single or Versus mode, type actor name(s)
2. **Actor Matching**: If the name matches a preset actor, go directly to the bracket. Otherwise, search TMDb and show disambiguation.
3. **Movie Fetching**: Fetches the actor's movie credits from TMDb, sorts by `voteCount * voteAverage`, takes the top 32
4. **Tournament**: Movies are paired into matchups. User taps their pick for each matchup. Winners advance to the next round.
5. **Champion**: The last movie standing is crowned with confetti!

## Notes

- The preset actor TMDb IDs may need verification — some IDs are approximate. The app falls back to TMDb search if a preset ID doesn't work correctly.
- Internet connection is required for fetching movie data and poster images.

## License

MIT License — see [LICENSE](LICENSE) for details.
