package com.example.jedicouncilchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jedicouncilchallenge.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    val isDarthVaderMode = repository.observeDarthVaderMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun toggleDarthVaderMode() {
        viewModelScope.launch {
            repository.setDarthVaderMode(!isDarthVaderMode.value)
        }
    }
}
