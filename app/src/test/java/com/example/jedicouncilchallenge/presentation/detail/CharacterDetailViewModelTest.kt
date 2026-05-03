package com.example.jedicouncilchallenge.presentation.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.Planet
import com.example.jedicouncilchallenge.domain.usecase.GetCharacterDetailUseCase
import com.example.jedicouncilchallenge.domain.usecase.GetFavouritesUseCase
import com.example.jedicouncilchallenge.domain.usecase.ToggleFavouriteUseCase
import com.example.jedicouncilchallenge.fake.FakeCharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCharacter sets character and homeworld in state`() = runTest {
        val repo = FakeCharacterRepository(
            characters = listOf(sampleCharacter),
            planets = listOf(samplePlanet)
        )
        val viewModel = createViewModel(repo)

        viewModel.loadCharacter(sampleCharacter.id)

        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.character).isEqualTo(sampleCharacter)
        assertThat(state.homeworld).isEqualTo(samplePlanet)
    }

    @Test
    fun `loadCharacter with same id twice does not reload`() = runTest {
        var fetchCount = 0
        val repo = object : FakeCharacterRepository(characters = listOf(sampleCharacter)) {
            override suspend fun getCharacters() =
                super.getCharacters().also { fetchCount++ }
        }
        val viewModel = createViewModel(repo)

        viewModel.loadCharacter(sampleCharacter.id)
        val countAfterFirst = fetchCount

        viewModel.loadCharacter(sampleCharacter.id) // same id → guarded
        assertThat(fetchCount).isEqualTo(countAfterFirst)
    }

    @Test
    fun `network error on loadCharacter sets error state`() = runTest {
        val repo = FakeCharacterRepository(
            networkError = DataError.Network.NO_INTERNET
        )
        val viewModel = createViewModel(repo)

        viewModel.loadCharacter(1)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `toggle favourite reflects isFavourite in state`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(sampleCharacter))
        val viewModel = createViewModel(repo)

        viewModel.loadCharacter(sampleCharacter.id)
        assertThat(viewModel.state.value.isFavourite).isFalse()

        viewModel.toggleFavourite()
        assertThat(viewModel.state.value.isFavourite).isTrue()

        viewModel.toggleFavourite()
        assertThat(viewModel.state.value.isFavourite).isFalse()
    }

    private fun createViewModel(repo: FakeCharacterRepository) =
        CharacterDetailViewModel(
            getCharacterDetail = GetCharacterDetailUseCase(repo),
            toggleFavourite = ToggleFavouriteUseCase(repo),
            getFavourites = GetFavouritesUseCase(repo)
        )
}

private val sampleCharacter = Character(
    id = 1,
    name = "Luke Skywalker",
    gender = "male",
    birthYear = "19BBY",
    height = "172",
    mass = "77",
    hairColor = "blond",
    skinColor = "fair",
    eyeColor = "blue",
    speciesIds = emptyList(),
    filmIds = emptyList(),
    starshipIds = emptyList(),
    homeworldId = 1
)

private val samplePlanet = Planet(
    id = 1,
    name = "Tatooine",
    climate = "arid",
    terrain = "desert",
    population = "200000"
)
