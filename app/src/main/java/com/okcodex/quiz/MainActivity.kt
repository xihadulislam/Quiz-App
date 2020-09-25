package com.okcodex.quiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bartoszlipinski.flippablestackview.StackPageTransformer
import com.okcodex.quiz.Model.Question
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    open   var scoreval:Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener {
            startActivityForResult(Intent(applicationContext, QuizActivity::class.java), REQUEST_CODE)
        }

    }





    override fun onResume() {
        super.onResume()
        Log.d("ffffffffffff","onresume")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("ffffffffffff","dfsdfsdfsdfsdfsd")


        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val score = data!!.getIntExtra(QuizActivity.FINAL_SCORE, 1)
                val coin = data!!.getIntExtra(QuizActivity.FINAL_COIN, 15)
                highscore.text = "My HighScore  $score"
                yourcoin.text = "My Coin $coin"
            }
        }

    }


    companion object {

        private val REQUEST_CODE = 1

    }

}