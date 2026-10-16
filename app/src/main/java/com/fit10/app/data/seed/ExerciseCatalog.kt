package com.fit10.app.data.seed

import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.Exercise
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.ImpactLevel

object ExerciseCatalog {

    val JUMPING_JACKS = Exercise(
        id = "ex_jumping_jacks",
        name = "Jumping Jacks",
        description = "Classic full-body cardiovascular warm-up movement that raises heart rate and increases blood circulation.",
        type = ExerciseType.CARDIO,
        focusArea = FocusArea.FULL_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.MODERATE,
        defaultDurationSeconds = 40,
        instructions = listOf(
            "Stand upright with feet together and arms at your sides.",
            "In one fluid motion, jump feet out to the sides while raising arms overhead.",
            "Immediately jump back to the starting position and repeat with steady rhythm."
        ),
        tips = listOf("Land softly on the balls of your feet", "Keep core gently engaged"),
        targetMuscles = listOf("Calves", "Shoulders", "Quadriceps")
    )

    val BODYWEIGHT_SQUATS = Exercise(
        id = "ex_squats",
        name = "Bodyweight Squats",
        description = "Foundational lower-body compound exercise strengthening the quadriceps, hamstrings, and glutes.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.LOWER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 15,
        instructions = listOf(
            "Stand with feet shoulder-width apart, toes pointed slightly outward.",
            "Hinge at hips and bend knees, lowering hips as if sitting into a chair.",
            "Lower until thighs are parallel to the ground, keeping chest proud.",
            "Push through your heels to return to the standing position."
        ),
        tips = listOf("Keep knees tracking over toes", "Avoid letting knees cave inward"),
        targetMuscles = listOf("Quadriceps", "Glutes", "Hamstrings")
    )

    val PUSH_UPS = Exercise(
        id = "ex_push_ups",
        name = "Push-ups",
        description = "Effective upper-body pushing exercise developing chest, shoulder, and triceps strength.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.UPPER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 12,
        instructions = listOf(
            "Begin in a high plank position with hands slightly wider than shoulder-width.",
            "Lower body until chest is a few inches off the floor, keeping elbows at a 45-degree angle to the torso.",
            "Press firmly through palms back up to full arm extension."
        ),
        tips = listOf("Maintain a straight line from head to heels", "Do not let hips sag"),
        targetMuscles = listOf("Chest", "Triceps", "Anterior Deltoids")
    )

    // FIX W-03: Added wide push-up variation to address push-plane monotony and add chest variety.
    val WIDE_PUSH_UPS = Exercise(
        id = "ex_wide_push_ups",
        name = "Wide-Grip Push-ups",
        description = "Push-up variation with a wider hand placement emphasising the outer chest and anterior deltoids.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.UPPER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 10,
        instructions = listOf(
            "Start in a high plank position with hands placed 1.5 to 2 times shoulder-width apart.",
            "Lower your chest toward the mat, keeping elbows flared to roughly 60 degrees from your torso.",
            "Press back up to the starting position with control, fully extending the elbows."
        ),
        tips = listOf("Do not let your hips sag or rise", "Keep your neck neutral throughout"),
        targetMuscles = listOf("Outer Chest", "Anterior Deltoids", "Triceps")
    )

    // FIX W-03: Diamond push-ups replace the verbatim push-up duplicate and hit triceps directly.
    val DIAMOND_PUSH_UPS = Exercise(
        id = "ex_diamond_push_ups",
        name = "Diamond Push-ups",
        description = "Close-grip push-up variation that intensely targets the triceps and inner chest.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.UPPER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 8,
        instructions = listOf(
            "Form a diamond shape with your thumbs and index fingers touching directly beneath your chest.",
            "Lower your chest toward your hands while keeping elbows close to the body.",
            "Press back up fully and repeat. Modify to knees if needed to maintain form."
        ),
        tips = listOf("Keep elbows tracking directly backward, not outward", "Move slowly on the way down for more stimulus"),
        targetMuscles = listOf("Triceps", "Inner Chest", "Anterior Deltoids")
    )

    // FIX F-03 (Critical): First pulling-plane exercise added to the catalog.
    // Targets rear deltoids and rhomboids — previously receiving 0 direct sets per week.
    val PRONE_YTW_HOLD = Exercise(
        id = "ex_prone_ytw",
        name = "Prone Y-T-W Hold",
        description = "Bodyweight posterior shoulder and upper back exercise performed prone, targeting rear deltoids and rhomboids without any equipment.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.UPPER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Lie face-down on the mat with arms extended fully overhead — this is the Y position.",
            "Lift arms, chest, and legs slightly off the mat, then sweep arms out to form a T position (arms straight out to the sides).",
            "Bend elbows to pull arms back toward your hips to form a W position, squeezing shoulder blades together hard.",
            "Reverse back through T to Y. That is one rep. Perform slowly and with intent."
        ),
        tips = listOf(
            "Keep neck neutral — eyes look at the mat",
            "Squeeze shoulder blades together at every position",
            "Quality over speed — this is not a cardio drill"
        ),
        targetMuscles = listOf("Rear Deltoids", "Rhomboids", "Middle Trapezius", "Lower Trapezius")
    )

    val PLANK = Exercise(
        id = "ex_plank",
        name = "Forearm Plank",
        description = "Isometric core hold building deep core stability and endurance across the abdominal wall.",
        type = ExerciseType.CORE,
        focusArea = FocusArea.CORE,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Place forearms on the mat with elbows stacked directly beneath shoulders.",
            "Extend legs back with toes tucked, lifting body off the mat.",
            "Hold steady, squeezing glutes and engaging abdominal muscles throughout."
        ),
        tips = listOf("Keep neck neutral by looking at the mat", "Breathe steadily without holding breath"),
        targetMuscles = listOf("Transverse Abdominis", "Rectus Abdominis", "Lower Back")
    )

    val MOUNTAIN_CLIMBERS = Exercise(
        id = "ex_mountain_climbers",
        name = "Mountain Climbers",
        description = "Dynamic high-plank movement combining cardiovascular conditioning with core stabilization.",
        type = ExerciseType.CARDIO,
        focusArea = FocusArea.FULL_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.MODERATE,
        defaultDurationSeconds = 40,
        instructions = listOf(
            "Start in a high plank position with hands under shoulders.",
            "Drive one knee toward the opposite shoulder, keeping hips level.",
            "Quickly switch legs, extending back while driving opposite knee forward in a running motion."
        ),
        tips = listOf("Keep hips low and level", "Maintain steady cadence"),
        targetMuscles = listOf("Hip Flexors", "Abs", "Shoulders")
    )

    val GLUTE_BRIDGES = Exercise(
        id = "ex_glute_bridges",
        name = "Glute Bridges",
        description = "Posterior chain activation targeting glutes and hamstrings while relieving lower back pressure.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.LOWER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 15,
        instructions = listOf(
            "Lie on back with knees bent and feet flat on the floor, hip-width apart.",
            "Engage glutes and press through heels to lift hips toward ceiling.",
            "Pause at the top in a straight line from knees to shoulders, then lower slowly."
        ),
        tips = listOf("Squeeze glutes tightly at the top", "Do not hyperextend lower back"),
        targetMuscles = listOf("Glutes", "Hamstrings", "Erector Spinae")
    )

    // NEW: Glute Bridge March adds unilateral demand to the bridge pattern.
    val GLUTE_BRIDGE_MARCH = Exercise(
        id = "ex_glute_bridge_march",
        name = "Glute Bridge March",
        description = "Single-leg glute bridge variation that alternately lifts each foot, challenging glute stability and core anti-rotation.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.LOWER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 12,
        instructions = listOf(
            "Set up in a standard glute bridge position and drive hips up to hold the bridge.",
            "Keeping hips level, slowly lift your right knee to 90 degrees.",
            "Lower right foot back to the mat, then lift the left knee. That is one rep.",
            "Maintain hip height throughout — do not let the pelvis drop."
        ),
        tips = listOf("Move slowly — this is a stability exercise, not a race", "Press through the grounded heel to prevent hip drop"),
        targetMuscles = listOf("Glutes", "Hamstrings", "Core", "Hip Flexors")
    )

    val HIGH_KNEES = Exercise(
        id = "ex_high_knees",
        name = "High Knees",
        description = "Intense cardio drill improving agility, hip flexor power, and cardiovascular endurance.",
        type = ExerciseType.CARDIO,
        focusArea = FocusArea.FULL_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.HIGH,
        defaultDurationSeconds = 40,
        instructions = listOf(
            "Stand tall with feet hip-width apart.",
            "Run in place, driving knees up toward hip height with each stride.",
            "Pump arms in rhythm with opposite leg action."
        ),
        tips = listOf("Land lightly on balls of feet", "Keep torso upright"),
        targetMuscles = listOf("Hip Flexors", "Calves", "Quadriceps")
    )

    val BICYCLE_CRUNCHES = Exercise(
        id = "ex_bicycle_crunches",
        name = "Bicycle Crunches",
        description = "Rotational abdominal exercise targeting the internal and external obliques alongside rectus abdominis.",
        type = ExerciseType.CORE,
        focusArea = FocusArea.CORE,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 40,
        defaultReps = 20,
        instructions = listOf(
            "Lie on back with hands lightly behind head and legs lifted at 90 degrees.",
            "Bring right elbow toward left knee while extending right leg out.",
            "Alternate sides in a controlled, pedaling motion."
        ),
        tips = listOf("Rotate from ribcage, not pulling neck", "Control tempo smoothly"),
        targetMuscles = listOf("Obliques", "Rectus Abdominis")
    )

    val LUNGES = Exercise(
        id = "ex_lunges",
        name = "Alternating Forward Lunges",
        description = "Unilateral lower-body movement improving balance, single-leg stability, and leg strength.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.LOWER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 14,
        instructions = listOf(
            "Stand tall with feet together.",
            "Step forward with right leg, lowering hips until both knees are bent at 90 degrees.",
            "Push through right heel back to standing, then repeat with left leg."
        ),
        tips = listOf("Keep front knee over ankle", "Keep torso upright throughout"),
        targetMuscles = listOf("Quadriceps", "Glutes", "Hamstrings")
    )

    // NEW: Reverse Lunges add a knee-friendly posterior loading pattern to balance forward lunge stimulus.
    val REVERSE_LUNGES = Exercise(
        id = "ex_reverse_lunges",
        name = "Alternating Reverse Lunges",
        description = "Reverse stepping lunge that loads the glutes and hamstrings more than the forward lunge while reducing knee-joint shear force.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.LOWER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        defaultReps = 14,
        instructions = listOf(
            "Stand tall with feet together and hands on hips or arms at sides.",
            "Step directly backward with your right foot, lowering the right knee toward the mat.",
            "Stop when both knees are at approximately 90 degrees — front shin should be vertical.",
            "Drive through the left heel to step feet back together, then alternate legs."
        ),
        tips = listOf("Keep your torso upright — do not lean forward", "Control the descent slowly for more glute and hamstring activation"),
        targetMuscles = listOf("Glutes", "Hamstrings", "Quadriceps")
    )

    // FIX F-01: focusArea corrected from UPPER_BODY to CORE.
    // Superman Hold targets the erector spinae and glutes — it is a posterior chain / core exercise.
    val SUPERMAN_HOLD = Exercise(
        id = "ex_superman",
        name = "Superman Hold",
        description = "Prone isometric extension strengthening the entire posterior chain and improving posture.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.CORE, // FIXED: was UPPER_BODY — posterior chain is a core/lower-back movement
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Lie face down on the mat with arms extended forward and legs straight.",
            "Simultaneously lift arms, chest, and legs a few inches off the floor.",
            "Hold position, feeling activation through lower back and glutes, then lower with control."
        ),
        tips = listOf("Look straight down to keep neck neutral", "Squeeze upper back gently"),
        targetMuscles = listOf("Erector Spinae", "Upper Back", "Glutes")
    )

    val BIRD_DOG = Exercise(
        id = "ex_bird_dog",
        name = "Bird Dog",
        description = "Core stability exercise improving rotary stability and spinal alignment without loading spine.",
        type = ExerciseType.CORE,
        focusArea = FocusArea.CORE,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Start on hands and knees with wrists under shoulders and knees under hips.",
            "Reach right arm forward and kick left leg straight back simultaneously.",
            "Hold for 1-2 seconds, return to starting position, and alternate sides."
        ),
        tips = listOf("Avoid tilting hips to either side", "Reach through fingertips and heel"),
        targetMuscles = listOf("Core", "Lower Back", "Glutes")
    )

    val CAT_COW_STRETCH = Exercise(
        id = "ex_cat_cow",
        name = "Cat-Cow Stretch",
        description = "Gentle spinal mobility flow syncing movement with breathing to relieve back stiffness.",
        type = ExerciseType.MOBILITY,
        focusArea = FocusArea.MOBILITY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Begin on all fours in tabletop position.",
            "Inhale: arch back, drop belly, and lift chest (Cow Pose).",
            "Exhale: round spine toward ceiling, tuck chin, and draw navel in (Cat Pose)."
        ),
        tips = listOf("Move slowly with deep breaths", "Keep movements pain-free"),
        targetMuscles = listOf("Spine", "Neck", "Abdomen")
    )

    val CHILDS_POSE = Exercise(
        id = "ex_childs_pose",
        name = "Child's Pose",
        description = "Restorative cooldown stretch opening hips, thighs, and ankles while relaxing the spine.",
        type = ExerciseType.COOLDOWN,
        focusArea = FocusArea.MOBILITY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Kneel on mat with big toes touching and knees spread wide.",
            "Sit hips back onto heels and walk hands forward, resting forehead on the mat.",
            "Breathe deeply, letting chest sink toward the floor."
        ),
        tips = listOf("Relax shoulders completely", "Take slow diaphragmatic breaths"),
        targetMuscles = listOf("Lats", "Hips", "Spine")
    )

    // FIX F-02: focusArea corrected from UPPER_BODY to CORE.
    // Shoulder Taps is fundamentally an anti-rotational CORE drill, not an upper-body strength exercise.
    val SHOULDER_TAPS = Exercise(
        id = "ex_shoulder_taps",
        name = "Plank Shoulder Taps",
        description = "Anti-rotational core and shoulder stability drill performed from a high plank position.",
        type = ExerciseType.CORE,
        focusArea = FocusArea.CORE, // FIXED: was UPPER_BODY — this is a core anti-rotation exercise
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Set up in a high plank position with feet slightly wider than hips.",
            "Tap right hand to left shoulder without twisting or shifting hips.",
            "Place hand down and tap left hand to right shoulder. Alternate with control."
        ),
        tips = listOf("Wider feet create more stability", "Minimize hip rotation"),
        targetMuscles = listOf("Shoulders", "Core", "Obliques")
    )

    val ARM_CIRCLES = Exercise(
        id = "ex_arm_circles",
        name = "Arm Circles Warm-up",
        description = "Dynamic shoulder mobility exercise warming up rotator cuff muscles and deltoids.",
        type = ExerciseType.WARMUP,
        focusArea = FocusArea.UPPER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 30,
        instructions = listOf(
            "Stand tall with feet shoulder-width apart and extend arms straight out to sides at shoulder height.",
            "Make small forward circular motions with arms for half the duration.",
            "Reverse directions and make backward circles for the remaining time."
        ),
        tips = listOf("Keep shoulders relaxed away from ears", "Engage core lightly"),
        targetMuscles = listOf("Deltoids", "Rotator Cuff")
    )

    // NEW: Wall Sit provides isometric quad stimulus with zero impact — ideal for lower-body volume
    // without adding more hip-flexor fatigue from lunges or squats.
    val WALL_SIT = Exercise(
        id = "ex_wall_sit",
        name = "Wall Sit",
        description = "Isometric quad-dominant hold using a wall for support, building endurance in the quadriceps and glutes.",
        type = ExerciseType.STRENGTH,
        focusArea = FocusArea.LOWER_BODY,
        equipmentRequired = Equipment.NONE,
        impactLevel = ImpactLevel.LOW,
        defaultDurationSeconds = 45,
        instructions = listOf(
            "Stand with your back flat against a wall, feet hip-width apart and about 60cm from the wall.",
            "Slide your back down the wall until your thighs are parallel to the floor and knees are at 90 degrees.",
            "Hold the position with arms relaxed at sides or resting on thighs. Keep back flat against the wall."
        ),
        tips = listOf(
            "Do not let knees cave inward — press them slightly outward",
            "Breathe steadily — do not hold your breath",
            "If 90 degrees is too hard, start at a shallower angle"
        ),
        targetMuscles = listOf("Quadriceps", "Glutes", "Calves")
    )

    val ALL: List<Exercise> = listOf(
        JUMPING_JACKS,
        BODYWEIGHT_SQUATS,
        PUSH_UPS,
        WIDE_PUSH_UPS,
        DIAMOND_PUSH_UPS,
        PRONE_YTW_HOLD,
        PLANK,
        MOUNTAIN_CLIMBERS,
        GLUTE_BRIDGES,
        GLUTE_BRIDGE_MARCH,
        HIGH_KNEES,
        BICYCLE_CRUNCHES,
        LUNGES,
        REVERSE_LUNGES,
        SUPERMAN_HOLD,
        BIRD_DOG,
        CAT_COW_STRETCH,
        CHILDS_POSE,
        SHOULDER_TAPS,
        ARM_CIRCLES,
        WALL_SIT
    )
}
