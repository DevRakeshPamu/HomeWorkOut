package com.fit10.app.feature.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.data.seed.WorkoutCatalog
import com.fit10.app.domain.adaptation.PersonalizationEngine
import com.fit10.app.domain.model.AdaptationResult
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus
import com.fit10.app.domain.repository.HistoryRepository
import com.fit10.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutSummaryUiState(
    val result: WorkoutResult? = null,
    val selectedFeedback: WorkoutFeedback? = null,
    val adaptationMessage: String? = null,
    val isSaved: Boolean = false
)

class WorkoutSummaryViewModel(
    private val sessionId: String,
    private val workoutId: String,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutSummaryUiState())
    val uiState: StateFlow<WorkoutSummaryUiState> = _uiState.asStateFlow()

    init {
        loadResult()
    }

    private fun loadResult() {
        viewModelScope.launch {
            val pastResults = historyRepository.getRecentResults(5).first()
            val existing = pastResults.find { it.sessionId == sessionId }
                ?: WorkoutResult(
                    sessionId = sessionId,
                    workoutId = workoutId,
                    workoutTitle = WorkoutCatalog.ALL.find { it.id == workoutId }?.title ?: "10-Min Home Workout",
                    status = WorkoutStatus.COMPLETED,
                    durationSeconds = 600,
                    exercisesCompletedCount = 10,
                    totalExercisesCount = 10,
                    caloriesBurned = 80,
                    feedback = null,
                    completedAtEpochMs = System.currentTimeMillis()
                )

            _uiState.update { it.copy(result = existing, selectedFeedback = existing.feedback) }
        }
    }

    fun selectFeedback(feedback: WorkoutFeedback) {
        val currentResult = _uiState.value.result ?: return
        _uiState.update { it.copy(selectedFeedback = feedback) }

        viewModelScope.launch {
            val updated = currentResult.copy(feedback = feedback)
            historyRepository.saveWorkoutResult(updated)

            // Run adaptation
            val userProfile = userRepository.getUserProfile().first()
            val recent = historyRepository.getRecentResults(5).first()
            if (userProfile != null) {
                val adaptation = PersonalizationEngine.evaluateAdaptation(userProfile, recent)
                _uiState.update {
                    it.copy(
                        result = updated,
                        adaptationMessage = adaptation.adaptationReason,
                        isSaved = true
                    )
                }
            } else {
                _uiState.update { it.copy(result = updated, isSaved = true) }
            }
        }
    }

    companion object {
        fun provideFactory(
            sessionId: String,
            workoutId: String,
            historyRepository: HistoryRepository,
            userRepository: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkoutSummaryViewModel(
                    sessionId,
                    workoutId,
                    historyRepository,
                    userRepository
                ) as T
            }
        }
    }
}
