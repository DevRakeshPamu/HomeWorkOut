package com.fit10.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    historyRepository: HistoryRepository
) : ViewModel() {

    val history: StateFlow<WorkoutHistory> = historyRepository.getWorkoutHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkoutHistory()
        )

    companion object {
        fun provideFactory(historyRepository: HistoryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(historyRepository) as T
                }
            }
    }
}
