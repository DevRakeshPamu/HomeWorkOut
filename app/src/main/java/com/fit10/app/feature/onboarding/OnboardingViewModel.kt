package com.fit10.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.data.preferences.UserPreferencesDataSource
import com.fit10.app.domain.model.BodySignal
import com.fit10.app.domain.model.ClearanceStatus
import com.fit10.app.domain.model.DeliveryType
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.MomGoal
import com.fit10.app.domain.model.PostpartumProfile
import com.fit10.app.domain.model.PriorActivity
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class OnboardingStep {
    WELCOME, PREGNANCY, BABY_AGE, DELIVERY, CLEARANCE, BODY_CHECK, ACTIVITY, GOAL, SCHEDULE, PLAN, NOT_PREGNANCY_READY;

    companion object {
        val questionSteps = listOf(PREGNANCY, BABY_AGE, DELIVERY, CLEARANCE, BODY_CHECK, ACTIVITY, GOAL, SCHEDULE)
    }
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val profile: PostpartumProfile = PostpartumProfile(),
    val isPregnant: Boolean? = null,
    val signalsAnswered: Boolean = false,
    val forward: Boolean = true,
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false,
) {
    val progress: Float
        get() {
            val index = OnboardingStep.questionSteps.indexOf(step)
            return when {
                step == OnboardingStep.PLAN -> 1f
                index < 0 -> 0f
                else -> (index + 1f) / (OnboardingStep.questionSteps.size + 1)
            }
        }

    val canContinue: Boolean
        get() = when (step) {
            OnboardingStep.PREGNANCY -> isPregnant != null
            OnboardingStep.BODY_CHECK -> signalsAnswered
            else -> true
        }
}

class OnboardingViewModel(
    private val userRepository: UserRepository,
    private val preferences: UserPreferencesDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private fun updateProfile(block: (PostpartumProfile) -> PostpartumProfile) =
        _uiState.update { it.copy(profile = block(it.profile)) }

    fun setName(name: String) = updateProfile { it.copy(name = name.take(30)) }
    fun setPregnant(pregnant: Boolean) = _uiState.update { it.copy(isPregnant = pregnant) }
    fun setBabyAgeWeeks(weeks: Int) = updateProfile { it.copy(babyAgeWeeks = weeks.coerceIn(0, 104)) }
    fun setDelivery(type: DeliveryType) = updateProfile { it.copy(delivery = type) }
    fun setClearance(status: ClearanceStatus) = updateProfile { it.copy(clearance = status) }
    fun setActivity(activity: PriorActivity) = updateProfile { it.copy(priorActivity = activity) }
    fun setGoal(goal: MomGoal) = updateProfile { it.copy(goal = goal) }
    fun setDaysPerWeek(days: Int) = updateProfile { it.copy(daysPerWeek = days) }

    fun toggleSignal(signal: BodySignal?) = _uiState.update { state ->
        val signals = when {
            signal == null -> emptySet()
            signal in state.profile.signals -> state.profile.signals - signal
            else -> state.profile.signals + signal
        }
        state.copy(profile = state.profile.copy(signals = signals), signalsAnswered = signal == null || signals.isNotEmpty())
    }

    fun next() {
        val state = _uiState.value
        val nextStep = when (state.step) {
            OnboardingStep.WELCOME -> OnboardingStep.PREGNANCY
            OnboardingStep.PREGNANCY -> if (state.isPregnant == true) OnboardingStep.NOT_PREGNANCY_READY else OnboardingStep.BABY_AGE
            OnboardingStep.BABY_AGE -> OnboardingStep.DELIVERY
            OnboardingStep.DELIVERY -> OnboardingStep.CLEARANCE
            OnboardingStep.CLEARANCE -> OnboardingStep.BODY_CHECK
            OnboardingStep.BODY_CHECK -> OnboardingStep.ACTIVITY
            OnboardingStep.ACTIVITY -> OnboardingStep.GOAL
            OnboardingStep.GOAL -> OnboardingStep.SCHEDULE
            OnboardingStep.SCHEDULE -> OnboardingStep.PLAN
            OnboardingStep.PLAN -> return complete()
            OnboardingStep.NOT_PREGNANCY_READY -> return
        }
        _uiState.update { it.copy(step = nextStep, forward = true) }
    }

    fun back(): Boolean {
        val state = _uiState.value
        val previous = when (state.step) {
            OnboardingStep.WELCOME -> return false
            OnboardingStep.NOT_PREGNANCY_READY -> OnboardingStep.PREGNANCY
            OnboardingStep.PLAN -> OnboardingStep.SCHEDULE
            OnboardingStep.PREGNANCY -> OnboardingStep.WELCOME
            else -> OnboardingStep.questionSteps[OnboardingStep.questionSteps.indexOf(state.step) - 1]
        }
        _uiState.update { it.copy(step = previous, forward = false) }
        return true
    }

    private fun complete() {
        val profile = _uiState.value.profile
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            preferences.setPostpartumProfile(profile)
            userRepository.saveUserProfile(
                UserProfile(
                    id = UUID.randomUUID().toString(),
                    name = profile.name.trim(),
                    fitnessGoal = when (profile.goal) {
                        MomGoal.CORE_STRENGTH -> FitnessGoal.BUILD_STRENGTH
                        else -> FitnessGoal.IMPROVE_FITNESS
                    },
                    fitnessLevel = FitnessLevel.BEGINNER,
                    targetWorkoutDurationMinutes = 10,
                    workoutsPerWeek = profile.daysPerWeek,
                    preferredFocusAreas = setOf(FocusArea.CORE, FocusArea.MOBILITY),
                    intensityPreference = IntensityPreference.EASY,
                )
            )
            userRepository.setOnboardingCompleted(true)
            _uiState.update { it.copy(isSaving = false, isCompleted = true) }
        }
    }

    companion object {
        fun provideFactory(userRepository: UserRepository, preferences: UserPreferencesDataSource): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OnboardingViewModel(userRepository, preferences) as T
            }
    }
}
