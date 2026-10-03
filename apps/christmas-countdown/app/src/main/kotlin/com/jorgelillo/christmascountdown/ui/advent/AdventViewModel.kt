package com.jorgelillo.christmascountdown.ui.advent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jorgelillo.christmascountdown.data.SettingsRepository
import com.jorgelillo.christmascountdown.domain.AdventCalendar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AdventUiState(
    val unlockedDoors: Int,
    val openedDoors: Set<Int> = emptySet(),
) {
    val isSeason: Boolean get() = unlockedDoors > 0
}

class AdventViewModel(
    private val settings: SettingsRepository,
    private val today: LocalDate = LocalDate.now(),
) : ViewModel() {

    val uiState: StateFlow<AdventUiState> = settings.openedDoors(today.year)
        .map { AdventUiState(AdventCalendar.unlockedDoors(today), it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdventUiState(AdventCalendar.unlockedDoors(today)))

    fun canOpen(door: Int): Boolean = AdventCalendar.canOpen(door, today)

    fun open(door: Int) {
        if (!canOpen(door)) return
        viewModelScope.launch { settings.markDoorOpened(today.year, door) }
    }

    companion object {
        fun factory(settings: SettingsRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { AdventViewModel(settings) }
        }
    }
}
