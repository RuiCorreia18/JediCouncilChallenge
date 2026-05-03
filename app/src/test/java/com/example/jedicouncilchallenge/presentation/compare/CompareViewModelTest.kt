package com.example.jedicouncilchallenge.presentation.compare

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.domain.model.Character
import com.example.jedicouncilchallenge.domain.usecase.GetCharactersUseCase
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
class CompareViewModelTest {

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
    fun `characters load on init`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = CompareViewModel(GetCharactersUseCase(repo))

        viewModel.openLeftPicker()
        assertThat(viewModel.state.value.leftSuggestions).isNotNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `selected left character is excluded from right suggestions`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = CompareViewModel(GetCharactersUseCase(repo))

        viewModel.openLeftPicker()
        val lukeUi = viewModel.state.value.leftSuggestions.first { it.id == luke.id }
        viewModel.selectLeft(lukeUi)

        viewModel.openRightPicker()
        assertThat(viewModel.state.value.rightSuggestions.none { it.id == luke.id }).isTrue()
    }

    @Test
    fun `selected right character is excluded from left suggestions`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = CompareViewModel(GetCharactersUseCase(repo))

        viewModel.openRightPicker()
        val leiaUi = viewModel.state.value.rightSuggestions.first { it.id == leia.id }
        viewModel.selectRight(leiaUi)

        viewModel.openLeftPicker()
        assertThat(viewModel.state.value.leftSuggestions.none { it.id == leia.id }).isTrue()
    }

    @Test
    fun `compare sets isComparing and clearLeft resets it`() = runTest {
        val repo = FakeCharacterRepository(characters = listOf(luke, leia))
        val viewModel = CompareViewModel(GetCharactersUseCase(repo))

        viewModel.openLeftPicker()
        viewModel.selectLeft(viewModel.state.value.leftSuggestions.first { it.id == luke.id })
        viewModel.openRightPicker()
        viewModel.selectRight(viewModel.state.value.rightSuggestions.first { it.id == leia.id })

        viewModel.compare()
        assertThat(viewModel.state.value.isComparing).isTrue()

        viewModel.clearLeft()
        assertThat(viewModel.state.value.isComparing).isFalse()
        assertThat(viewModel.state.value.leftSelected).isNull()
    }

    @Test
    fun `network error sets error state`() = runTest {
        val repo = FakeCharacterRepository(networkError = DataError.Network.NO_INTERNET)
        val viewModel = CompareViewModel(GetCharactersUseCase(repo))

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }
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
