package com.example.jedicouncilchallenge.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.example.jedicouncilchallenge.R
import com.example.jedicouncilchallenge.presentation.characters.CharacterListRoot
import com.example.jedicouncilchallenge.presentation.compare.CompareRoot
import com.example.jedicouncilchallenge.presentation.detail.CharacterDetailRoot
import com.example.jedicouncilchallenge.presentation.favourites.FavouritesRoot
import com.example.jedicouncilchallenge.presentation.images.characterImageUrl
import com.example.jedicouncilchallenge.presentation.theme.StarWarsColors
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CharacterListRoute

@Serializable
data class CharacterDetailRoute(val characterId: Int)

@Serializable
object FavouritesRoute

@Serializable
object CompareRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarWarsNavGraph(
    isDarthVaderMode: Boolean,
    onToggleDarthVaderMode: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(StarWarsColors.Black)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { scope.launch { drawerState.close() } }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_close_menu),
                            tint = StarWarsColors.Yellow
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Compare
                Text(
                    text = stringResource(R.string.nav_compare),
                    color = StarWarsColors.Yellow,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 3.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { drawerState.close() }
                            navController.navigate(CompareRoute)
                        }
                        .padding(vertical = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Favourites
                Text(
                    text = stringResource(R.string.nav_favorites),
                    color = StarWarsColors.Yellow,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 3.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { drawerState.close() }
                            navController.navigate(FavouritesRoute)
                        }
                        .padding(vertical = 20.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Yoda / Vader theme toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = characterImageUrl(20), // Yoda
                        contentDescription = stringResource(R.string.cd_yoda),
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                    Switch(
                        checked = isDarthVaderMode,
                        onCheckedChange = { onToggleDarthVaderMode() },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StarWarsColors.Black,
                            checkedTrackColor = StarWarsColors.Yellow,
                            uncheckedThumbColor = StarWarsColors.Yellow,
                            uncheckedTrackColor = Color(0xFF444444)
                        )
                    )
                    AsyncImage(
                        model = characterImageUrl(4), // Darth Vader
                        contentDescription = stringResource(R.string.cd_darth_vader),
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Star Wars") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.cd_open_menu)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = StarWarsColors.Yellow,
                        navigationIconContentColor = StarWarsColors.Yellow
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = CharacterListRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<CharacterListRoute> {
                    CharacterListRoot(
                        onNavigateToDetail = { navController.navigate(CharacterDetailRoute(it)) }
                    )
                }
                composable<CharacterDetailRoute> { backStackEntry ->
                    val route: CharacterDetailRoute = backStackEntry.toRoute()
                    CharacterDetailRoot(
                        characterId = route.characterId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<FavouritesRoute> {
                    FavouritesRoot(
                        onNavigateToDetail = { navController.navigate(CharacterDetailRoute(it)) }
                    )
                }
                composable<CompareRoute> {
                    CompareRoot()
                }
            }
        }
    }
}
