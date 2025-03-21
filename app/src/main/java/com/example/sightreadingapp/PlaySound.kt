package com.example.sightreadingapp

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

/**
 * Plays a short audio file from raw resources using MediaPlayer.
 * Automatically releases the MediaPlayer instance after playback completes.
 *
 * @param context The application context used to access resources.
 * @param soundResId The raw resource ID of the sound file to be played.
 */
fun playSound(context: Context, @RawRes soundResId: Int) {
    // Create and initialize a MediaPlayer instance with the given sound resource.
    val mediaPlayer = MediaPlayer.create(context, soundResId)

    // Release the MediaPlayer once the sound has finished playing.
    mediaPlayer.setOnCompletionListener {
        it.release() // Free up resources after playback is complete.
    }

    // Start playing the audio.
    mediaPlayer.start()
}
