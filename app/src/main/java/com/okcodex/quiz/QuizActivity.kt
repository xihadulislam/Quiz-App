package com.okcodex.quiz

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.okcodex.quiz.Model.Question
import kotlinx.android.synthetic.main.activity_quiz.*
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity(), View.OnClickListener {


    private var questionlist: ArrayList<Question> = ArrayList()


    private lateinit var txtQuestion: TextView
    private lateinit var txtScore: TextView
    private lateinit var txtQuestionCount: TextView
    private lateinit var txtCounter: TextView
    private lateinit var txtOption1: TextView
    private lateinit var txtOption2: TextView
    private lateinit var txtOption3: TextView
    private lateinit var txtOption4: TextView

    private lateinit var backHomeButton: TextView
    private lateinit var continueButton: Button


    private var colorStateListCountDown: ColorStateList? = null
    private var countDownTimer: CountDownTimer? = null

    private var timeLeft: Long = 0

    private var qCounter: Int = 0
    private lateinit var currQuestion: Question;
    private var qCountTotal: Int = 0

    private var score: Int = 0

    private var onBackPressedTime: Long = 0


    private var mPlayer: MediaPlayer? = null

    private var wrngcnt = 0

    private var wronglist = ArrayList<Int>()

    private var coin = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        txtQuestion = findViewById(R.id.Question)
        txtScore = findViewById(R.id.Score)
        txtQuestionCount = findViewById(R.id.questionCount)
        txtCounter = findViewById(R.id.timeCounter)
        txtOption1 = findViewById(R.id.Option1)
        txtOption2 = findViewById(R.id.Option2)
        txtOption3 = findViewById(R.id.Option3)
        txtOption4 = findViewById(R.id.Option4)

        backHomeButton = findViewById(R.id.backhome)
        continueButton = findViewById(R.id.continuebutton)

        colorStateListCountDown = txtCounter.textColors
        generateQuestion()

        questionlist.shuffle()


        qCountTotal = questionlist.size

        showQuestion()


        txtOption1.setOnClickListener(this)
        txtOption2.setOnClickListener(this)
        txtOption3.setOnClickListener(this)
        txtOption4.setOnClickListener(this)

        backHomeButton.setOnClickListener(this)
        continueButton.setOnClickListener(this)


    }


    override fun onClick(v: View?) {

        when (v) {
            txtOption1 -> check(1)
            txtOption2 -> check(2)
            txtOption3 -> check(3)
            txtOption4 -> check(4)

            backHomeButton -> finishQuizActivity()
            continueButton -> {

                if (coin < 5) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "You don't have sufficient Coin ",
                        Snackbar.LENGTH_SHORT
                    ).show()

                } else {
                    if (wrngcnt == 3) {
                        wrngcnt = 0
                        qCounter++
                        wronglist.clear()
                        showQuestion()
                    } else {
                        showQuestion()
                    }

                }
            }
        }
    }



    private fun showQuestion() {

        txtCounter.text = "0"
        coin -= 5

        startStartingPlayer()

      //  startCountDownPlayer()
        updateColor()
        updateVisibility(true, 5)

        Log.e("xyz ", qCounter.toString())

        if (qCounter < qCountTotal) {
            currQuestion = questionlist[qCounter]
            txtQuestion.text = currQuestion.Question

            txtOption1.text = currQuestion.Option1
            txtOption2.text = currQuestion.Option2
            txtOption3.text = currQuestion.Option3
            txtOption4.text = currQuestion.Option4
            txtQuestionCount.text = "${qCounter + 1} / $qCountTotal"

            label_coin.text = "$$coin"

            timeLeft = COUNTDOWN_TIMER

         //   startCountDown()
        } else {
            finishQuizActivity()
        }

    }


    private fun finishQuizActivity() {
        val rIntent = Intent()
        rIntent.putExtra(FINAL_SCORE, score)
        rIntent.putExtra(FINAL_COIN, coin)
        setResult(Activity.RESULT_OK, rIntent)

        stopPlayer()
        finish()
    }


    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateCountDown()
            }

            override fun onFinish() {
                timeLeft = 0
              //  updateCountDown()
                check(5)

            }
        }.start()
    }


    private fun updateCountDown() {
        val min = (timeLeft / 1000).toInt() / 60
        val sec = (timeLeft / 1000).toInt() % 60

        //  val timeFormat = String.format(Locale.getDefault(), "%01d:%02d", min, sec)
        val timeFormat = String.format(Locale.getDefault(), "%01d", sec)

        txtCounter.text = timeFormat

        val animation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        txtCounter.startAnimation(animation)

      //  txtCounter.animate().setDuration(500).rotationBy(0f).translationY(30f)
        if (timeLeft < 4000) {
            txtCounter.setTextColor(Color.RED)
        } else {
            txtCounter.setTextColor(colorStateListCountDown)
        }

    }

    private fun check(ans: Int) {
        countDownTimer!!.cancel()
        stopPlayer()


        if (ans == currQuestion.RightAns) {
            score++
            txtScore!!.text = "Score $score"

        }

        showRightAns(ans)

    }


    private fun showRightAns(ans: Int) {
        if (ans == currQuestion.RightAns) {
            coin += 15
            qCounter++
            wronglist.clear()
            wrngcnt = 0
            when (ans) {
                1 -> {
                    txtOption1.setTextColor(resources.getColor(R.color.white))
                    txtOption1.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_correct);
                }
                2 -> {
                    txtOption2.setTextColor(resources.getColor(R.color.white))
                    txtOption2.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_correct);
                }
                3 -> {
                    txtOption3.setTextColor(resources.getColor(R.color.white))
                    txtOption3.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_correct);
                }
                4 -> {
                    txtOption4.setTextColor(resources.getColor(R.color.white))
                    txtOption4.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_correct);
                }
            }

            startCorrectPlayer()
        } else {
            startWrongPlayer(ans)

            when (ans) {
                1 -> {
                    txtOption1.setTextColor(resources.getColor(R.color.white))
                    txtOption1.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_wromg);
                }
                2 -> {
                    txtOption2.setTextColor(resources.getColor(R.color.white))
                    txtOption2.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_wromg);
                }
                3 -> {
                    txtOption3.setTextColor(resources.getColor(R.color.white))
                    txtOption3.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_wromg);
                }
                4 -> {
                    txtOption4.setTextColor(resources.getColor(R.color.white))
                    txtOption4.background =
                        ContextCompat.getDrawable(this, R.drawable.option_bg_wromg);
                }
            }


        }

    }




    private  fun startStartingPlayer(){

        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.rstart)
        }
        mPlayer?.start()
        mPlayer?.setOnCompletionListener {
            stopPlayer()
            startCountDownPlayer()
            startCountDown()
        }

    }


    private fun startWrongPlayer(ans: Int) {

        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.rwrong)

        }
        mPlayer?.start()
        mPlayer?.setOnCompletionListener {
            stopPlayer()


            if (ans == 5) {
                updateVisibility(false, ans)
            } else {
                wrngcnt++
                wronglist.add(ans)
                updateVisibility(false, ans)

            }

        }

    }


    private fun startCountDownPlayer() {
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.rcountdown)
        }
        mPlayer?.start()

    }


    private fun startCorrectPlayer() {

        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.rcorrect)
        }
        mPlayer?.start()
        mPlayer?.setOnCompletionListener {
            stopPlayer()
            showQuestion()
        }
    }


    private fun stopPlayer() {

        if (mPlayer != null) {
            mPlayer?.release()
            mPlayer = null
        }

    }


    private fun updateVisibility(check: Boolean, selected: Int) {

        if (check) {

            bottom_const.visibility = View.GONE

            txtOption1.isClickable = true
            txtOption2.isClickable = true
            txtOption3.isClickable = true
            txtOption4.isClickable = true


            txtOption1.visibility = View.VISIBLE
            txtOption2.visibility = View.VISIBLE
            txtOption3.visibility = View.VISIBLE
            txtOption4.visibility = View.VISIBLE


            for (i in wronglist) {

                when (i) {
                    1 -> {
                        txtOption1.visibility = View.GONE

                    }
                    2 -> {
                        txtOption2.visibility = View.GONE
                    }
                    3 -> {

                        txtOption3.visibility = View.GONE

                    }
                    4 -> {
                        txtOption4.visibility = View.GONE
                    }

                }


            }


        } else {


            bottom_const.visibility = View.VISIBLE

            txtOption1.isClickable = false
            txtOption2.isClickable = false
            txtOption3.isClickable = false
            txtOption4.isClickable = false


            when (selected) {
                1 -> {
                    txtOption2.visibility = View.GONE
                    txtOption3.visibility = View.GONE
                    txtOption4.visibility = View.GONE
                }
                2 -> {
                    txtOption1.visibility = View.GONE
                    txtOption3.visibility = View.GONE
                    txtOption4.visibility = View.GONE
                }
                3 -> {
                    txtOption1.visibility = View.GONE
                    txtOption2.visibility = View.GONE
                    txtOption4.visibility = View.GONE
                }
                4 -> {
                    txtOption1.visibility = View.GONE
                    txtOption2.visibility = View.GONE
                    txtOption3.visibility = View.GONE

                }
                5 -> {
                    txtOption1.visibility = View.GONE
                    txtOption2.visibility = View.GONE
                    txtOption3.visibility = View.GONE
                    txtOption4.visibility = View.GONE

                }
            }

        }

    }


    private fun updateColor() {
        txtOption1.setTextColor(resources.getColor(R.color.grey_active))
        txtOption2.setTextColor(resources.getColor(R.color.grey_active))
        txtOption3.setTextColor(resources.getColor(R.color.grey_active))
        txtOption4.setTextColor(resources.getColor(R.color.grey_active))


        txtOption1.background = ContextCompat.getDrawable(this, R.drawable.option_bg);
        txtOption2.background = ContextCompat.getDrawable(this, R.drawable.option_bg);
        txtOption3.background = ContextCompat.getDrawable(this, R.drawable.option_bg);
        txtOption4.background = ContextCompat.getDrawable(this, R.drawable.option_bg);

    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            stopPlayer()
        }
    }


    override fun onBackPressed() {
        if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuizActivity()
            stopPlayer()
        } else {
            Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT).show()
        }
        onBackPressedTime = System.currentTimeMillis()

    }


    companion object {

        val FINAL_COIN = "finalcoin"
        val FINAL_SCORE = "FinalScore"
        private val COUNTDOWN_TIMER: Long = 11000
    }


    private fun generateQuestion() {
        val q1 = Question("2+2", "1", "5", "4", "none", 3)
        questionlist.add(q1)
        val q2 = Question("What is Capital of India", "Mumbai", "New Delhi", "Chennai", "none", 2)
        questionlist.add(q2)
        val q3 = Question(
            "The author of Harry Potter book is",
            "J.K Rowling",
            "Stephen King",
            "Toni Morrison",
            "none",
            1
        )
        questionlist.add(q3)
        val q4 = Question("Which is fourth planet from sun?", "Mars", "Earth", "Venus", "none", 1)
        questionlist.add(q4)
        val q5 =
            Question("How many gram are there in a kilogram", "100", "1000", "10000", "none", 2)
        questionlist.add(q5)
    }


}