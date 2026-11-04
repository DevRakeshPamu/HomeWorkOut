package com.fit10.app.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.domain.model.BodySignal
import com.fit10.app.domain.model.ClearanceStatus
import com.fit10.app.domain.model.DeliveryType
import com.fit10.app.domain.model.MomGoal
import com.fit10.app.domain.model.PostpartumProfile
import com.fit10.app.domain.model.PriorActivity
import com.fit10.app.domain.model.StartingPoint
import kotlin.math.roundToInt

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = rememberFit10Haptics()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) onOnboardingFinished()
    }
    BackHandler(enabled = state.step != OnboardingStep.WELCOME) { viewModel.back() }

    val progress by animateFloatAsState(state.progress, spring(stiffness = 300f), label = "progress")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(48.dp)) {
                if (state.step != OnboardingStep.WELCOME) {
                    IconButton(onClick = { haptics.perform(HapticKind.Tick); viewModel.back() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            }
            AnimatedVisibility(
                visible = state.progress > 0f,
                modifier = Modifier.weight(1f).padding(end = 20.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                    drawStopIndicator = {}
                )
            }
        }

        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                val dir = if (state.forward) 1 else -1
                (slideInHorizontally(tween(320)) { it / 4 * dir } + fadeIn(tween(320)))
                    .togetherWith(slideOutHorizontally(tween(220)) { -it / 4 * dir } + fadeOut(tween(220)))
            },
            modifier = Modifier.weight(1f),
            label = "step"
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                StepContent(step, state, viewModel, onSelect = { haptics.perform(HapticKind.Tick) })
            }
        }

        if (state.step != OnboardingStep.NOT_PREGNANCY_READY) {
            PrimaryButton(
                text = when (state.step) {
                    OnboardingStep.WELCOME -> "Let's begin"
                    OnboardingStep.PLAN -> "Start 2-minute breathing"
                    else -> "Continue"
                },
                enabled = state.canContinue && !state.isSaving,
                onClick = { haptics.perform(HapticKind.Confirm); viewModel.next() }
            )
        }
    }
}

@Composable
private fun StepContent(
    step: OnboardingStep,
    state: OnboardingUiState,
    vm: OnboardingViewModel,
    onSelect: () -> Unit
) {
    val p = state.profile
    when (step) {
        OnboardingStep.WELCOME -> WelcomeStep(p.name, vm::setName, vm::next)

        OnboardingStep.PREGNANCY -> Question("First, are you pregnant right now?", "This app is built for after birth.") {
            Option("No, my baby is here", null, Icons.Rounded.ChildCare, state.isPregnant == false) { onSelect(); vm.setPregnant(false) }
            Option("Yes, I'm pregnant", null, Icons.Rounded.Favorite, state.isPregnant == true) { onSelect(); vm.setPregnant(true) }
        }

        OnboardingStep.BABY_AGE -> BabyAgeStep(p.babyAgeWeeks, onSelect, vm::setBabyAgeWeeks)

        OnboardingStep.DELIVERY -> Question("How was your baby born?", "C-section recovery needs extra care for your core.") {
            Option("Vaginal birth", null, null, p.delivery == DeliveryType.VAGINAL) { onSelect(); vm.setDelivery(DeliveryType.VAGINAL) }
            Option("C-section", null, null, p.delivery == DeliveryType.C_SECTION) { onSelect(); vm.setDelivery(DeliveryType.C_SECTION) }
            Option("Prefer not to say", "We'll use the gentler C-section plan", null, p.delivery == DeliveryType.PREFER_NOT_TO_SAY) { onSelect(); vm.setDelivery(DeliveryType.PREFER_NOT_TO_SAY) }
        }

        OnboardingStep.CLEARANCE -> Question(
            "Has your doctor said it's okay to exercise?",
            "Usually around ${p.minWeeksBeforeJourney} weeks after birth, but your doctor's advice comes first."
        ) {
            Option("Yes, I'm cleared", null, null, p.clearance == ClearanceStatus.YES) { onSelect(); vm.setClearance(ClearanceStatus.YES) }
            Option("Not yet", "We'll start with gentle breathing and posture", null, p.clearance == ClearanceStatus.NOT_YET) { onSelect(); vm.setClearance(ClearanceStatus.NOT_YET) }
            Option("I'm not sure", "Ask at your next check-up. Gentle basics until then", null, p.clearance == ClearanceStatus.NOT_SURE) { onSelect(); vm.setClearance(ClearanceStatus.NOT_SURE) }
        }

        OnboardingStep.BODY_CHECK -> Question("Do you notice any of these?", "Choose all that apply. Your answers stay on this phone.") {
            val signals = listOf(
                BodySignal.PAIN to "Pain in your belly, scar or pelvis",
                BodySignal.BLEEDING to "Bleeding that's new or getting heavier",
                BodySignal.LEAKING to "Leaking when you cough, laugh or jump",
                BodySignal.HEAVINESS to "Heaviness or pressure in your pelvis",
                BodySignal.DOMING to "A bulge or gap down the middle of your belly",
            )
            signals.forEach { (signal, label) ->
                Option(label, null, null, signal in p.signals, multi = true) { onSelect(); vm.toggleSignal(signal) }
            }
            Option("None of these", null, null, state.signalsAnswered && p.signals.isEmpty(), multi = true) { onSelect(); vm.toggleSignal(null) }
        }

        OnboardingStep.ACTIVITY -> Question("How active were you before pregnancy?", "This sets how fast we build up.") {
            Option("Not very active", null, Icons.Rounded.SelfImprovement, p.priorActivity == PriorActivity.INACTIVE) { onSelect(); vm.setActivity(PriorActivity.INACTIVE) }
            Option("Somewhat active", "Walks, yoga, the odd workout", Icons.Rounded.DirectionsWalk, p.priorActivity == PriorActivity.SOMEWHAT_ACTIVE) { onSelect(); vm.setActivity(PriorActivity.SOMEWHAT_ACTIVE) }
            Option("Very active", "Regular workouts or sport", Icons.Rounded.FitnessCenter, p.priorActivity == PriorActivity.VERY_ACTIVE) { onSelect(); vm.setActivity(PriorActivity.VERY_ACTIVE) }
        }

        OnboardingStep.GOAL -> Question("What matters most to you right now?", null) {
            Option("More energy", "Feel less drained through the day", Icons.Rounded.Bolt, p.goal == MomGoal.ENERGY) { onSelect(); vm.setGoal(MomGoal.ENERGY) }
            Option("A stronger core", "Rebuild from the inside out", Icons.Rounded.Spa, p.goal == MomGoal.CORE_STRENGTH) { onSelect(); vm.setGoal(MomGoal.CORE_STRENGTH) }
            Option("Less back and neck pain", "From feeding and carrying", Icons.Rounded.SelfImprovement, p.goal == MomGoal.BACK_RELIEF) { onSelect(); vm.setGoal(MomGoal.BACK_RELIEF) }
            Option("Overall fitness", "Feel like yourself again", Icons.Rounded.FitnessCenter, p.goal == MomGoal.OVERALL_FITNESS) { onSelect(); vm.setGoal(MomGoal.OVERALL_FITNESS) }
        }

        OnboardingStep.SCHEDULE -> ScheduleStep(p.daysPerWeek, onSelect, vm::setDaysPerWeek)

        OnboardingStep.PLAN -> PlanStep(p)

        OnboardingStep.NOT_PREGNANCY_READY -> NotForPregnancyStep { vm.back() }
    }
}

@Composable
private fun WelcomeStep(name: String, onNameChange: (String) -> Unit, onDone: () -> Unit) {
    Spacer(Modifier.height(32.dp))
    Box(
        modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Rounded.Spa, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
    }
    Spacer(Modifier.height(28.dp))
    Text("Feel like yourself again", fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))
    Text(
        "Safe 10-minute workouts to rebuild your strength and energy after baby. Built around your recovery, your sleep and your day.",
        fontSize = 16.sp, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(32.dp))
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("What should we call you?") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onDone() }),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(20.dp))
    Note(Icons.Rounded.Lock, "Private by design. No account, no ads, and your answers never leave this phone.")
}

@Composable
private fun BabyAgeStep(weeks: Int, onSelect: () -> Unit, onChange: (Int) -> Unit) {
    Question("How old is your baby?", "We'll match your plan to where you are in recovery.") {
        Text(
            formatBabyAge(weeks),
            fontSize = 40.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), textAlign = TextAlign.Center
        )
        Slider(
            value = weeks.toFloat(),
            onValueChange = {
                val w = it.roundToInt()
                if (w != weeks) { onSelect(); onChange(w) }
            },
            valueRange = 0f..104f
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(6 to "6 weeks", 13 to "3 months", 26 to "6 months", 52 to "1 year").forEach { (w, label) ->
                QuickChip(label, weeks == w, Modifier.weight(1f)) { onSelect(); onChange(w) }
            }
        }
    }
}

@Composable
private fun ScheduleStep(days: Int, onSelect: () -> Unit, onChange: (Int) -> Unit) {
    Question("How many days a week feels doable?", "10 minutes each. Missed days are okay; 5 a week counts as a full week.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            (3..6).forEach { d ->
                val selected = d == days
                val scale by animateFloatAsState(if (selected) 1.08f else 1f, spring(dampingRatio = 0.5f), label = "day")
                Surface(
                    onClick = { onSelect(); onChange(d) },
                    shape = CircleShape,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.weight(1f).height(64.dp).scale(scale)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "$d", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("days a week", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun PlanStep(p: PostpartumProfile) {
    val greeting = p.name.trim().ifBlank { null }?.let { "$it, your plan is ready" } ?: "Your plan is ready"
    Spacer(Modifier.height(16.dp))
    Text(greeting, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(20.dp))

    val (title, body) = when (p.startingPoint) {
        StartingPoint.SEE_DOCTOR -> "Please check with your doctor first" to
            "Pain or new bleeding should be looked at before exercise. Until then, gentle breathing is safe to try and can help you relax."
        StartingPoint.GENTLE_BASICS -> "Gentle recovery basics" to
            "Breathing, pelvic floor and posture, 10 minutes a day. Once your doctor clears you, we'll move you into Phase 1."
        StartingPoint.PHASE_ONE -> "Phase 1 · Reconnect" to
            "3 weeks of breathing, deep core and pelvic floor work to rebuild your foundation."
    }
    val warning = p.startingPoint == StartingPoint.SEE_DOCTOR
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (warning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Icon(
                if (warning) Icons.Rounded.HealthAndSafety else Icons.Rounded.Spa, null,
                tint = if (warning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(10.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = if (warning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(6.dp))
            Text(body, fontSize = 15.sp, lineHeight = 22.sp,
                color = if (warning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
    Spacer(Modifier.height(20.dp))
    PlanPoint("10 minutes, ${p.daysPerWeek} days a week")
    if (p.avoidImpact) PlanPoint("No jumping or impact until your pelvic floor is ready")
    if (p.protectCore) PlanPoint("No crunches. Deep core first, to protect your belly")
    PlanPoint("Every session adapts to how you slept")
    PlanPoint("Pause anytime. Your progress waits for you")
    Spacer(Modifier.height(20.dp))
    Note(Icons.Rounded.SelfImprovement, "Let's start with a 2-minute breathing session. It's the first step of every recovery plan.")
}

@Composable
private fun NotForPregnancyStep(onBack: () -> Unit) {
    Spacer(Modifier.height(40.dp))
    Icon(Icons.Rounded.Favorite, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
    Spacer(Modifier.height(20.dp))
    Text("Congratulations", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))
    Text(
        "Fit10 Mom is designed for recovery after birth, so it isn't safe to use during pregnancy. Ask your doctor about exercise that suits you now, and come back after your baby arrives. We'll be here.",
        fontSize = 16.sp, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(28.dp))
    Button(onClick = onBack, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
        Text("Go back", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Question(title: String, subtitle: String?, content: @Composable () -> Unit) {
    Spacer(Modifier.height(16.dp))
    Text(title, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    if (subtitle != null) {
        Spacer(Modifier.height(8.dp))
        Text(subtitle, fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Spacer(Modifier.height(24.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { content() }
}

@Composable
private fun Option(
    title: String,
    subtitle: String?,
    icon: ImageVector?,
    selected: Boolean,
    multi: Boolean = false,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val border by animateColorAsState(if (selected) scheme.primary else scheme.outlineVariant, label = "border")
    val bg by animateColorAsState(if (selected) scheme.primaryContainer else scheme.surface, label = "bg")
    val width by animateDpAsState(if (selected) 2.dp else 1.dp, label = "width")
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, spring(dampingRatio = 0.6f), label = "press")

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bg,
        border = BorderStroke(width, border),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    ) {
        Row(Modifier.padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = if (selected) scheme.onPrimaryContainer else scheme.primary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(14.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = if (selected) scheme.onPrimaryContainer else scheme.onSurface)
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, fontSize = 13.sp, color = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(if (multi) RoundedCornerShape(7.dp) else CircleShape)
                    .background(if (selected) scheme.primary else scheme.surface)
                    .then(if (selected) Modifier else Modifier.background(scheme.surface)),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Rounded.Check, null, tint = scheme.onPrimary, modifier = Modifier.size(16.dp))
                else Surface(
                    shape = if (multi) RoundedCornerShape(7.dp) else CircleShape,
                    color = scheme.surface,
                    border = BorderStroke(1.5.dp, scheme.outline),
                    modifier = Modifier.size(24.dp)
                ) {}
            }
        }
    }
}

@Composable
private fun QuickChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun PlanPoint(text: String) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(22.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun Note(icon: ImageVector, text: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 13.sp, lineHeight = 19.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, spring(dampingRatio = 0.6f), label = "cta")
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).height(56.dp).scale(scale)
    ) {
        Text(text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatBabyAge(weeks: Int): String = when {
    weeks == 0 -> "Newborn"
    weeks < 13 -> if (weeks == 1) "1 week" else "$weeks weeks"
    weeks < 52 -> "${(weeks / 4.345).roundToInt()} months"
    weeks < 104 -> {
        val months = ((weeks - 52) / 4.345).roundToInt()
        if (months == 0) "1 year" else "1 year $months mo"
    }
    else -> "2 years+"
}
