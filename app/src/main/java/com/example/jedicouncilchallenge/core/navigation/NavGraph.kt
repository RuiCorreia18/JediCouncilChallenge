package com.example.jedicouncilchallenge.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.jedicouncilchallenge.presentation.characters.CharacterListRoot
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
            ModalDrawerSheet {
                Text(
                    text = "Star Wars",
                    modifier = Modifier.padding(16.dp),
                    color = StarWarsColors.Yellow
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Favourites") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(FavouritesRoute)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null) },
                    label = { Text("Compare") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(CompareRoute)
                    }
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Darth Vader Mode") },
                    selected = false,
                    onClick = { onToggleDarthVaderMode() },
                    badge = {
                        Switch(
                            checked = isDarthVaderMode,
                            onCheckedChange = { onToggleDarthVaderMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StarWarsColors.Black,
                                checkedTrackColor = StarWarsColors.Yellow
                            )
                        )
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Star Wars") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                    // TODO: CharacterDetailRoot(characterId = route.characterId, onNavigateBack = { navController.popBackStack() })
                }
                composable<FavouritesRoute> {
                    // TODO: FavouritesRoot(onNavigateToDetail = { navController.navigate(CharacterDetailRoute(it)) })
                }
                composable<CompareRoute> {
                    // TODO: CompareRoot()
                }
            }
        }
    }
}
