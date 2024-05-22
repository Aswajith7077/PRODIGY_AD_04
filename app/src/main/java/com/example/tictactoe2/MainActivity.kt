package com.example.tictactoe2


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe2.R
import com.example.tictactoe2.databinding.ActivityMainBinding
import com.google.android.material.transition.SlideDistanceProvider.GravityFlag

//import com.android.tools.render.compose.main


class Chance(c: Char) {
    private var initialValue: Boolean = true
    private var value: Boolean = true;

    init {
        if (c.toLowerCase() == 'x')
            value = true;
        else
            value = false
        initialValue = value;
    }

    fun current(): Boolean {
        return value;
    }

    fun toggle() {
        val temp = value
        value = !(temp);
    }

    fun reset() {
        value = initialValue
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var chance: Chance;
    private var shown: Boolean = false;

    private lateinit var serviceIntent: Intent;

    private lateinit var map: Map<Int, Char>;
    private lateinit var viewArray: Array<ImageView>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val animatedO = mainBinding.turnO.drawable as AnimatedVectorDrawable
        val animatedX = mainBinding.turnX.drawable as AnimatedVectorDrawable
        animatedO.start()
        animatedX.start()
        if(AudioClass.music) {
            MediaPlayer.create(this, R.raw.start).apply {
                start()
            }
        }

        serviceIntent = Intent(this, GameAudioService::class.java);

        if(AudioClass.music) {
            startService(serviceIntent);
        }
        map = mapOf()
        chance = Chance('X');

        viewArray = arrayOf(
            mainBinding.firstView,
            mainBinding.secondView,
            mainBinding.thirdView,
            mainBinding.fourthView,
            mainBinding.fifthView,
            mainBinding.sixthView,
            mainBinding.seventhView,
            mainBinding.eightthView,
            mainBinding.ninethView
        );

        for (i in viewArray.indices) {
            handleAnimation(viewArray[i], i)
        }

        mainBinding.restartButton.setOnClickListener {
            reset();
        }
        mainBinding.exitButton.setOnClickListener {
            if(AudioClass.sound) {
                MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                    start();
                }
            }
            handleExit()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(AudioClass.sound) {
                    MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                        start();
                    }
                }
                handleExit()
            }
        })
    }

    private fun handleExit() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popUpView = inflater.inflate(R.layout.exit_popup, null);

        val dimension = ViewGroup.LayoutParams.MATCH_PARENT

        val popUpWindow = PopupWindow(popUpView, dimension, dimension, false);
        popUpWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);

        val yesButton = popUpView.findViewById<AppCompatButton>(R.id.yes);
        val noButton = popUpView.findViewById<AppCompatButton>(R.id.no);
        val closeButton = popUpView.findViewById<ImageView>(R.id.close_button);

        yesButton.setOnClickListener {
            if(AudioClass.sound) {
                MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                    start();
                }
            }
            popUpWindow.dismiss();
            val intent = Intent(this, HomePage::class.java);
            if(AudioClass.music) {
                stopService(serviceIntent);
            }
            startActivity(intent);
            finish();
        }
        noButton.setOnClickListener {
            if(AudioClass.sound) {
                MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                    start();
                }
            }
            popUpWindow.dismiss();
        }
        closeButton.setOnClickListener {
            if(AudioClass.sound) {
                MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                    start();
                }
            }
            popUpWindow.dismiss();
        }
    }

    private fun reset() {
        if (AudioClass.music) {
            MediaPlayer.create(this, R.raw.start).apply {
                start()
            }
        }
        chance.reset()
        map = mapOf()
        shown = false
        val animatedO = mainBinding.turnO.drawable as AnimatedVectorDrawable
        val animatedX = mainBinding.turnX.drawable as AnimatedVectorDrawable
        animatedO.start()
        animatedX.start()
        for (i in viewArray.indices)
            viewArray[i].setImageDrawable(null);
    }

    private var string: String = "";

    @SuppressLint("SetTextI18n")
    private fun handleAnimation(value: ImageView, index: Int) {
        value.setOnClickListener {
            if (map[index] == null && chance.current()) {
                if(AudioClass.sound) {
                    MediaPlayer.create(this, R.raw.player_x_audio).apply {
                        start()
                    }
                }
                value.setImageResource(R.drawable.anim_x);
                map += mapOf(index to 'X');
                val animatedVectorDrawable = value.drawable as AnimatedVectorDrawable
                animatedVectorDrawable.start();
                chance.toggle();
                mainBinding.turnXLayout.setBackgroundResource(0)
                mainBinding.turnOLayout.setBackgroundResource(R.drawable.button_bg)
            } else if (map[index] == null && !chance.current()) {
                if(AudioClass.sound) {
                    MediaPlayer.create(this, R.raw.player_o_audio).apply {
                        start()
                    }
                }
                value.setImageResource(R.drawable.anim_o);
                map += mapOf(index to 'O');
                val animatedVectorDrawable = value.drawable as AnimatedVectorDrawable
                animatedVectorDrawable.start();
                chance.toggle();
                mainBinding.turnOLayout.setBackgroundResource(0)
                mainBinding.turnXLayout.setBackgroundResource(R.drawable.button_bg)
            }
            val result = checkResult();

            if (result != 'N' && !shown) {
                displayResult(result);
                shown = true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun displayResult(result: Char) {

        for (i in 0..9) {
            if (map[i] == null) {
                map += mapOf(i to '-');
            }
        }

        if(AudioClass.music) {
            MediaPlayer.create(this, R.raw.game_ending_audio).apply {
                start();
            }
        }

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popUpView = inflater.inflate(R.layout.popup_dialog, null);

        val dimension = ViewGroup.LayoutParams.MATCH_PARENT;
        val popUpWindow = PopupWindow(popUpView, dimension, dimension, true);
        popUpWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0)

        val animXView = popUpView.findViewById<ImageView>(R.id.winner_svg)
        val playerText = popUpView.findViewById<TextView>(R.id.player_text);
        val winnerText = popUpView.findViewById<TextView>(R.id.winning_text);

        if (result == 'O') {
            playerText.text = "PLAYER"
            winnerText.text = "won the match"
            animXView.setImageResource(R.drawable.anim_o_dark)
            val animVectorX = animXView.drawable as AnimatedVectorDrawable
            animVectorX.start();
        } else if (result == 'X') {
            playerText.text = "PLAYER"
            winnerText.text = "won the match"
            animXView.setImageResource(R.drawable.anim_x_dark)
            val animVectorX = animXView.drawable as AnimatedVectorDrawable
            animVectorX.start();
        } else {
            playerText.text = "DRAW"
            winnerText.text = "The Match is a draw."
            animXView.setImageResource(R.drawable.handshake);
        }


        val closeButton = popUpView.findViewById<ImageView>(R.id.close_button)
        val rematchButton = popUpView.findViewById<AppCompatButton>(R.id.rematch)
        val backToHome = popUpView.findViewById<AppCompatButton>(R.id.back_to_home)

        closeButton.setOnClickListener {
            if(AudioClass.sound) {
                MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                    start();
                }
            }
            popUpWindow.dismiss();
        }
        rematchButton.setOnClickListener {
            popUpWindow.dismiss();
            reset();
        }
        backToHome.setOnClickListener {
            if(AudioClass.sound) {
                MediaPlayer.create(applicationContext, R.raw.button_click_audio).apply {
                    start();
                }
            }
            popUpWindow.dismiss();
            val intent = Intent(this, HomePage::class.java);
            stopService(serviceIntent);
            startActivity(intent)
        }
        popUpView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                popUpWindow.dismiss();
                true
            } else {
                false
            }
        }

    }

    private fun checkResult(): Char {

        var value = 'X'
        if (map[0] == value && map[1] == value && map[2] == value)
            return value;
        if (map[0] == value && map[4] == value && map[8] == value)
            return value;
        if (map[0] == value && map[3] == value && map[6] == value)
            return value;


        if (map[1] == value && map[4] == value && map[7] == value)
            return value;
        if (map[3] == value && map[4] == value && map[5] == value)
            return value;


        if (map[2] == value && map[5] == value && map[8] == value)
            return value;
        if (map[6] == value && map[7] == value && map[8] == value)
            return value;
        if (map[2] == value && map[4] == value && map[6] == value)
            return value;





        value = 'O'
        if (map[0] == value && map[1] == value && map[2] == value)
            return value;
        if (map[0] == value && map[4] == value && map[8] == value)
            return value;
        if (map[0] == value && map[3] == value && map[6] == value)
            return value;


        if (map[1] == value && map[4] == value && map[7] == value)
            return value;
        if (map[3] == value && map[4] == value && map[5] == value)
            return value;


        if (map[2] == value && map[5] == value && map[8] == value)
            return value;
        if (map[6] == value && map[7] == value && map[8] == value)
            return value;
        if (map[2] == value && map[4] == value && map[6] == value)
            return value;

        if (map.size == 9)
            return 'D';

        return 'N';
    }
}