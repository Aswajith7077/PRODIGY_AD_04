package com.example.tictactoe2

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class HomeAudioService : Service() {
    private var mediaPlayer: MediaPlayer? = null;

    override fun onBind(intent: Intent?): IBinder? = null;

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.home_audio).apply {
            isLooping = true;
            start()
        }
        runner = true;
        return START_NOT_STICKY;
    }

    override fun onDestroy() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }
        runner = false;
        super.onDestroy();
    }

    companion object {
        var runner: Boolean = false;
    }
}