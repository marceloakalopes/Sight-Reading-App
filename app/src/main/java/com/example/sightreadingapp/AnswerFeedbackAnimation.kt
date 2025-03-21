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
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier // New modifier parameter
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LaunchedEffect(key1 = animationRes) {
        delay(durationMillis)
        onAnimationEnd()
    }

    // Use the passed modifier instead of fillMaxSize()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animationState.progress }
        )
    }
}
