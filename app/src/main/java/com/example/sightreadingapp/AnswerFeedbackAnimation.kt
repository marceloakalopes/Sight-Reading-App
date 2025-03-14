package com.example.sightreadingapp

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

/**
 * A composable function that plays a Lottie animation for a specified duration
 * and executes a callback function when the animation ends.
 *
 * @param animationRes The raw resource ID of the Lottie animation file.
 * @param durationMillis The duration (in milliseconds) for which the animation should play.
 *                       Defaults to 1000ms (1 second).
 * @param onAnimationEnd A callback function invoked after the animation completes.
 */
@Composable
fun AnswerFeedbackAnimation(
    @RawRes animationRes: Int,
    durationMillis: Long = 2000,
    onAnimationEnd: () -> Unit
) {
    // Load the Lottie animation composition from the given raw resource.
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))

    // Create an animation state that plays the Lottie animation once.
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        iterations = 1 // Play once
    )

    // Effect to handle animation end logic after the specified duration.
    LaunchedEffect(key1 = animationRes) {
        delay(durationMillis) // Wait for the animation to finish.
        onAnimationEnd() // Trigger the callback function.
    }

    // Center the animation within a full-screen box.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animationState.progress } // Control progress via animation state.
        )
    }
}
