package com.example.jedicouncilchallenge.presentation.characters

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.usecase.GetCharactersUseCase
import com.example.jedicouncilchallenge.domain.usecase.ToggleFavouriteUseCase
import com.example.jedicouncilchallenge.fake.FakeCharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CharacterListViewModelTest {

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
    fun `loadInitial emits loading then data`() = runTest {
        val repo = FakeCharacterRepository(
            characters = listOf(sampleCharacter),
            blockCharacters = true
        )
        val viewModel = createViewModel(repo)

        // init is suspended at the gate — state must be isLoading = true.
        assertThat(viewModel.state.value.isLoading).isTrue()
        assertThat(viewModel.state.value.displayedCharacters).isEmpty()

        // Release the gate → loadInitialData() runs to completion inline.
        repo.releaseCharacters()

        // State is now fully loaded.
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.displayedCharacters).isNotEmpty()
    }

    @Test
    fun `filter change resets visibleCount`() = runTest {
        // 25 characters so we can page past the default PAGE_SIZE of 20
        val repo = FakeCharacterRepository(
            characters = List(25) { i ->
                sampleCharacter.copy(id = i + 1, name = "Character $i")
            }
        )
        val viewModel = createViewModel(repo)

        // Initial load shows 20 (PAGE_SIZE)
        assertThat(viewModel.state.value.displayedCharacters.size).isEqualTo(20)

        // Load more bumps visibleCount to 40 → all 25 are now shown
        viewModel.loadMore()
        assertThat(viewModel.state.value.displayedCharacters.size).isEqualTo(25)

        // Any filter change must reset visibleCount back to PAGE_SIZE
        viewModel.onSearchQueryChange("Character")
        assertThat(viewModel.state.value.displayedCharacters.size).isEqualTo(20)
    }

    @Test
    fun `toggle favourite updates state`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(sampleCharacter))
        val viewModel = createViewModel(repo)

        viewModel.state.test {
            skipItems(1) // skip init-loaded state

            // Toggle ON — character 1 becomes a favourite
            viewModel.onToggleFavourite(1)
            assertThat(awaitItem().favouriteCharacterIds).isEqualTo(setOf(1))

            // Toggle OFF — favourite removed
            viewModel.onToggleFavourite(1)
            assertThat(awaitItem().favouriteCharacterIds).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `birth year sort orders characters by timeline year`() = runTest {
        val repo = FakeCharacterRepository(
            characters = listOf(
                sampleCharacter.copy(id = 1, name = "Luke Skywalker", birthYear = "19BBY"),
                sampleCharacter.copy(id = 2, name = "Yoda", birthYear = "896BBY"),
                sampleCharacter.copy(id = 3, name = "Rey", birthYear = "15ABY"),
                sampleCharacter.copy(id = 4, name = "Mystery", birthYear = "unknown")
            )
        )
        val viewModel = createViewModel(repo)

        viewModel.onSortOptionChange(CharacterSortOption.BirthYearAscending)

        assertThat(viewModel.state.value.displayedCharacters.map { it.name })
            .containsExactly("Yoda", "Luke Skywalker", "Rey", "Mystery")
    }

    private fun createViewModel(repo: FakeCharacterRepository) =
        CharacterListViewModel(
            getCharacters = GetCharactersUseCase(repo),
            toggleFavourite = ToggleFavouriteUseCase(repo),
            repository = repo
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
    homeworldId = null
)
