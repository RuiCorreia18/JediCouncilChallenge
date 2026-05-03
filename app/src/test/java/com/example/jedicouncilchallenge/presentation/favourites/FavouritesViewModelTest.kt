package com.example.jedicouncilchallenge.presentation.favourites

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.model.FavouriteRef
import com.example.jedicouncilchallenge.domain.model.FavouriteType
import com.example.jedicouncilchallenge.domain.usecase.GetCharactersUseCase
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
class FavouritesViewModelTest {

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
    fun `init with no favourites shows empty list`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = createViewModel(repo)

        assertThat(viewModel.state.value.characters).isEmpty()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `favourited characters appear in state sorted by name`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = createViewModel(repo)

        repo.toggleFavourite(FavouriteRef(luke.id, FavouriteType.CHARACTER))
        repo.toggleFavourite(FavouriteRef(leia.id, FavouriteType.CHARACTER))

        val names = viewModel.state.value.characters.map { it.name }
        assertThat(names).isEqualTo(listOf("Leia Organa", "Luke Skywalker"))
    }

    @Test
    fun `removing favourite removes character from list`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = createViewModel(repo)

        repo.toggleFavourite(FavouriteRef(luke.id, FavouriteType.CHARACTER))
        repo.toggleFavourite(FavouriteRef(leia.id, FavouriteType.CHARACTER))
        assertThat(viewModel.state.value.characters).isNotEmpty()

        viewModel.onRemoveFavourite(luke.id)
        assertThat(viewModel.state.value.characters.map { it.id }).isEqualTo(listOf(leia.id))
    }

    @Test
    fun `network error sets error state`() = runTest {
        val repo = FakeCharacterRepository(networkError = DataError.Network.NO_INTERNET)
        val viewModel = createViewModel(repo)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    private fun createViewModel(repo: FakeCharacterRepository) =
        FavouritesViewModel(
            getCharacters = GetCharactersUseCase(repo),
            getFavourites = GetFavouritesUseCase(repo),
            toggleFavourite = ToggleFavouriteUseCase(repo)
        )
}

private val luke = Character(
    id = 1, name = "Luke Skywalker", gender = "male", birthYear = "19BBY",
    height = "172", mass = "77", hairColor = "blond", skinColor = "fair",
    eyeColor = "blue", speciesIds = emptyList(), filmIds = emptyList(),
    starshipIds = emptyList(), homeworldId = null
)

private val leia = Character(
    id = 5, name = "Leia Organa", gender = "female", birthYear = "19BBY",
    height = "150", mass = "49", hairColor = "brown", skinColor = "light",
    eyeColor = "brown", speciesIds = emptyList(), filmIds = emptyList(),
    starshipIds = emptyList(), homeworldId = null
)
