package com.example.tictactoe2

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe2.databinding.ActivityMainBinding
import com.example.tictactoe2.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var mainBinding: ActivitySplashScreenBinding;
    private lateinit var serviceIntent: Intent;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        serviceIntent = Intent(applicationContext,HomeAudioService::class.java);

        perform();

        Handler().postDelayed({
            startActivity(Intent(this,HomePage::class.java))
            finish();
        },3000)
    }
    @SuppressLint("SetTextI18n")
    private fun perform(){

        val handler = Handler(Looper.getMainLooper());
        var counter = 0;
        Thread{
            while(counter < 100){
                counter++;
                if(counter == 50)
                    startService(serviceIntent);
                try{
                    Thread.sleep(30)
                }catch(e:InterruptedException){
                    e.printStackTrace()
                }
                handler.post{
                    val progressBar = findViewById<ProgressBar>(R.id.progress_bar);
                    val percentView = findViewById<TextView>(R.id.percentage);

                    progressBar.progress = counter;
                    percentView.text = "$counter%";
                }
            }
        }.start();

        val currentProgress = 100
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        ObjectAnimator.ofInt(progressBar,"progress",currentProgress).setDuration(3000).start();

    }
}