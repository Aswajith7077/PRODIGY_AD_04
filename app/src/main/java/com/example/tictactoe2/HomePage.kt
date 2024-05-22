package com.example.tictactoe2

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Audio
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.internal.ContextUtils.getActivity
import kotlin.system.exitProcess

class HomePage : AppCompatActivity() {
    private lateinit var serviceIntent: Intent;
    private val onBackPressedCallback = object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            stopService(serviceIntent);
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        serviceIntent = Intent(applicationContext,HomeAudioService::class.java);

        if(!HomeAudioService.runner && AudioClass.music) {
            startService(serviceIntent);
        }


        val musicButton = findViewById<ImageButton>(R.id.music_button)
        if(AudioClass.music){
            musicButton.setImageResource(R.drawable.music);
        }
        else{
            musicButton.setImageResource(R.drawable.music_striked);
        }



        musicButton.setOnClickListener {
            AudioClass.music = !AudioClass.music;
            if(!AudioClass.music && HomeAudioService.runner){
                stopService(serviceIntent);
                musicButton.setImageResource(R.drawable.music_striked)
                HomeAudioService.runner = false;
            }
            else if(AudioClass.music && !HomeAudioService.runner){
                startService(serviceIntent);
                musicButton.setImageResource(R.drawable.music)
                HomeAudioService.runner = true;
            }
        }

        val soundButton = findViewById<ImageButton>(R.id.sound_button);
        if(AudioClass.sound)
            soundButton.setImageResource(R.drawable.volume);
        else
            soundButton.setImageResource(R.drawable.volume_striked)
        soundButton.setOnClickListener {
            AudioClass.sound = !AudioClass.sound;
            if(AudioClass.sound){
                soundButton.setImageResource(R.drawable.volume)
            }
            else if(!AudioClass.sound){
                soundButton.setImageResource(R.drawable.volume_striked)
            }
        }


        val playButton = findViewById<AppCompatButton>(R.id.play_button);
        playButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            stopService(serviceIntent);
            startActivity(intent);
            finish();
        }
    }
}