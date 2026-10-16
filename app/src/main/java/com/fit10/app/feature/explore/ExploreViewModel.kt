package com.fit10.app.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.data.seed.WorkoutCatalog
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ExploreUiState(
    val selectedFilter: FocusArea? = null,
    val allWorkouts: List<Workout> = emptyList(),
    val filteredWorkouts: List<Workout> = emptyList(),
    val isLoading: Boolean = false
)

class ExploreViewModel(
    private val workoutRepository: WorkoutRepository,
    initialFilter: FocusArea? = null
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<FocusArea?>(initialFilter)

    val uiState: StateFlow<ExploreUiState> = combine(
        workoutRepository.getAllWorkouts(),
        selectedFilter
    ) { workouts, filter ->
        val list = workouts.ifEmpty { WorkoutCatalog.ALL }
        val filtered = if (filter == null) list else list.filter { it.focusArea == filter }
        ExploreUiState(
            selectedFilter = filter,
            allWorkouts = list,
            filteredWorkouts = filtered,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExploreUiState(selectedFilter = initialFilter, isLoading = true)
    )

    fun setFilter(filter: FocusArea?) {
        selectedFilter.value = filter
    }

    companion object {
        fun provideFactory(
            workoutRepository: WorkoutRepository,
            initialFilter: FocusArea? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExploreViewModel(workoutRepository, initialFilter) as T
            }
        }
    }
}
