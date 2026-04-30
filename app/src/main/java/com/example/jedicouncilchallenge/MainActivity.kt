package com.example.jedicouncilchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jedicouncilchallenge.core.navigation.StarWarsNavGraph
import com.example.jedicouncilchallenge.presentation.splash.SplashScreen
import com.example.jedicouncilchallenge.presentation.theme.StarWarsBackground
import com.example.jedicouncilchallenge.presentation.theme.StarWarsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val isDarthVaderMode by viewModel.isDarthVaderMode.collectAsStateWithLifecycle()
            var showSplash by remember { mutableStateOf(true) }

            StarWarsTheme {
                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(durationMillis = 500)
                ) { isSplash ->
                    if (isSplash) {
                        SplashScreen(
                            isDarthVaderMode = isDarthVaderMode,
                            onSplashComplete = { showSplash = false }
                        )
                    } else {
                        StarWarsBackground(isDarthVaderMode = isDarthVaderMode) {
                            StarWarsNavGraph(
                                isDarthVaderMode = isDarthVaderMode,
                                onToggleDarthVaderMode = viewModel::toggleDarthVaderMode
                            )
                        }
                    }
                }
            }
        }
    }
}
