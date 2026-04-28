package com.example.jedicouncilchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jedicouncilchallenge.core.navigation.StarWarsNavGraph
import com.example.jedicouncilchallenge.presentation.theme.StarWarsBackground
import com.example.jedicouncilchallenge.presentation.theme.StarWarsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // TODO: replace with MainViewModel once created
            val isDarthVaderMode = false

            StarWarsTheme {
                StarWarsBackground(isDarthVaderMode = isDarthVaderMode) {
                    StarWarsNavGraph(
                        isDarthVaderMode = isDarthVaderMode,
                        onToggleDarthVaderMode = { /* TODO: viewModel.toggleDarthVaderMode() */ }
                    )
                }
            }
        }
    }
}
