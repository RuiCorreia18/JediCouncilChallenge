package com.example.jedicouncilchallenge.presentation.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.jedicouncilchallenge.R

object StarWarsColors {
    val Yellow = Color(0xFFFFE300)
    val Black = Color(0xFF000000)
    val SurfaceOverlay = Color(0x14FFFFFF)
    val TextSecondary = Color(0xB3FFFFFF)
    val Error = Color(0xFFFF4444)
}

private val StarWarsColorScheme = darkColorScheme(
    primary = StarWarsColors.Yellow,
    onPrimary = StarWarsColors.Black,
    background = StarWarsColors.Black,
    onBackground = Color.White,
    surface = Color.Transparent,
    onSurface = Color.White,
    error = StarWarsColors.Error,
    onError = StarWarsColors.Black
)

@Composable
fun StarWarsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StarWarsColorScheme,
        content = content
    )
}

@Composable
fun StarWarsBackground(
    isDarthVaderMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(
                if (isDarthVaderMode) R.drawable.bg_darkmode else R.drawable.bg_lightmode
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        content()
    }
}
