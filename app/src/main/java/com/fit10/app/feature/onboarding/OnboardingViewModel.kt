package com.fit10.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class OnboardingUiState(
    val step: Int = 1, // 1..6
    val selectedLevel: FitnessLevel = FitnessLevel.INTERMEDIATE,
    val selectedGoal: FitnessGoal = FitnessGoal.BUILD_STRENGTH,
    val targetDurationMinutes: Int = 10,
    val selectedEquipment: Set<Equipment> = setOf(Equipment.NONE),
    val exercisesToAvoid: Set<String> = setOf("None"),
    val intensityPreference: IntensityPreference = IntensityPreference.MODERATE,
    val preferredDays: Set<Int> = setOf(1, 2, 3, 4, 5), // Mon-Fri
    val recoveryPreference: String = "ACTIVE_RECOVERY", // ACTIVE_RECOVERY, COMPLETE_REST
    val preferredFocusAreas: Set<FocusArea> = setOf(FocusArea.FULL_BODY, FocusArea.CORE),
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
)

class OnboardingViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectLevel(level: FitnessLevel) {
        _uiState.update { it.copy(selectedLevel = level) }
    }

    fun selectGoal(goal: FitnessGoal) {
        _uiState.update { it.copy(selectedGoal = goal) }
    }

    fun selectDuration(minutes: Int) {
        _uiState.update { it.copy(targetDurationMinutes = minutes) }
    }

    fun toggleEquipment(equipment: Equipment) {
        _uiState.update { state ->
            val current = state.selectedEquipment.toMutableSet()
            if (equipment == Equipment.NONE) {
                current.clear()
                current.add(Equipment.NONE)
            } else {
                current.remove(Equipment.NONE)
                if (current.contains(equipment)) {
                    current.remove(equipment)
                    if (current.isEmpty()) current.add(Equipment.NONE)
                } else {
                    current.add(equipment)
                }
            }
            state.copy(selectedEquipment = current)
        }
    }

    fun toggleExerciseToAvoid(tag: String) {
        _uiState.update { state ->
            val current = state.exercisesToAvoid.toMutableSet()
            if (tag == "None") {
                current.clear()
                current.add("None")
            } else {
                current.remove("None")
                if (current.contains(tag)) {
                    current.remove(tag)
                    if (current.isEmpty()) current.add("None")
                } else {
                    current.add(tag)
                }
            }
            state.copy(exercisesToAvoid = current)
        }
    }

    fun selectIntensity(intensity: IntensityPreference) {
        _uiState.update { it.copy(intensityPreference = intensity) }
    }

    fun toggleDay(day: Int) {
        _uiState.update { state ->
            val current = state.preferredDays.toMutableSet()
            if (current.contains(day)) {
                if (current.size > 1) { // keep at least 1 day
                    current.remove(day)
                }
            } else {
                current.add(day)
            }
            state.copy(preferredDays = current)
        }
    }

    fun selectRecovery(recovery: String) {
        _uiState.update { it.copy(recoveryPreference = recovery) }
    }

    fun toggleFocusArea(area: FocusArea) {
        _uiState.update { state ->
            val current = state.preferredFocusAreas.toMutableSet()
            if (current.contains(area)) {
                if (current.size > 1) current.remove(area)
            } else {
                current.add(area)
            }
            state.copy(preferredFocusAreas = current)
        }
    }

    fun nextStep() {
        if (_uiState.value.step < 6) {
            _uiState.update { it.copy(step = it.step + 1) }
        } else {
            completeOnboarding()
        }
    }

    fun previousStep() {
        if (_uiState.value.step > 1) {
            _uiState.update { it.copy(step = it.step - 1) }
        }
    }

    fun skipOnboarding() {
        completeOnboarding()
    }

    fun completeOnboarding() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val userProfile = UserProfile(
                id = UUID.randomUUID().toString(),
                name = "Athlete",
                fitnessGoal = state.selectedGoal,
                fitnessLevel = state.selectedLevel,
                availableEquipment = state.selectedEquipment,
                targetWorkoutDurationMinutes = state.targetDurationMinutes,
                workoutsPerWeek = state.preferredDays.size,
                preferredFocusAreas = state.preferredFocusAreas,
                intensityPreference = state.intensityPreference,
                isReminderEnabled = false
            )

            userRepository.saveUserProfile(userProfile)
            userRepository.setOnboardingCompleted(true)
            _uiState.update { it.copy(isLoading = false, isCompleted = true) }
        }
    }

    companion object {
        fun provideFactory(userRepository: UserRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnboardingViewModel(userRepository) as T
                }
            }
    }
}
