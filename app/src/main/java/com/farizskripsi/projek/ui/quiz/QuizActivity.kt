package com.farizskripsi.projek.ui.quiz

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.farizskripsi.projek.R
import com.farizskripsi.projek.data.model.Quiz
import com.farizskripsi.projek.data.repository.DataRepository
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.layout_popup_quiz.*
import kotlinx.coroutines.delay
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColorResource
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    private lateinit var viewModel: QuizViewModel
    private lateinit var dataRepository: DataRepository
    private lateinit var filteredQuiz: List<Quiz>
    private var answeredQuestions = mutableListOf<Quiz>()
    private lateinit var dialog: Dialog
    private var score = 0
    private var i = 0

    private var lcgSeed: Long = 1
    private val lcgMultiplier: Long = 1103515245
    private val lcgIncrement: Long = 12345
    private val lcgModulus: Long = (1L shl 31) - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setSupportActionBar(toolbarQuiz)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        // Inisialisasi seed LCG dengan waktu saat ini
        lcgSeed = System.currentTimeMillis()

        val courseName = intent?.getStringExtra("course")
        toolbarQuiz.title = "Quiz $courseName"

        dataRepository = DataRepository()
        viewModel = ViewModelProviders.of(this).get(QuizViewModel::class.java)
        filteredQuiz = dataRepository.getQuiz(this, courseName)

        viewModel.quiz.postValue(filteredQuiz[i])
        viewModel.quiz.observe(this, Observer {
            with(filteredQuiz[i]) {
                if (imgLink != "") {
                    imgQuiz.visibility = View.VISIBLE
                    Glide.with(applicationContext).load(imgLink).into(imgQuiz)
                } else imgQuiz.visibility = View.GONE

                tvQuestion.text = question
                btnA.text = optionA
                btnB.text = optionB
                btnC.text = optionC
                btnD.text = optionD

                choice(answer)
            }
        })

        initDialog()
        initTransition()

        showNewData()
    }

    private fun choice(answer: Char?) {
        when (answer) {
            'A' -> setAnswer(btnA, btnB, btnC, btnD)
            'B' -> setAnswer(btnB, btnA, btnC, btnD)
            'C' -> setAnswer(btnC, btnA, btnB, btnD)
            'D' -> setAnswer(btnD, btnC, btnB, btnA)
        }
    }

    private fun getNextRandom(): Int {
        lcgSeed = (lcgMultiplier * lcgSeed + lcgIncrement) % lcgModulus
        return lcgSeed.toInt()
    }

    private fun shuffleQuiz() {
        val random = Random(getNextRandom().toLong())
        filteredQuiz = filteredQuiz.shuffled(random)
    }


    private fun setAnswer(vararg btn: Button) {
        btn.forEachIndexed { index, button ->
            if (index == 0) button.onClick {
                showTrue()
            }
            else button.onClick { showWrong() }
        }
    }

    private suspend fun showTrue() {
        score += 20
        showPopup("+20", R.color.colorWhite, R.drawable.ic_benar)

    }

    private suspend fun showWrong() {
        showPopup("+0", android.R.color.darker_gray, R.drawable.ic_salah)
    }

    private fun showNewData() {
        shuffleQuiz() // Lakukan pengacakan terlebih dahulu
        var newQuiz: Quiz? = null
        // Cek jika ada soal yang belum ditampilkan
        for (quiz in filteredQuiz) {
            if (!answeredQuestions.contains(quiz)) {
                newQuiz = quiz
                break
            }
        }
        if (newQuiz != null) {
            answeredQuestions.add(newQuiz)
            viewModel.quiz.postValue(newQuiz)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, QuizScoreFragment.newInstance(score)).commit()
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun showPopup(score: String, color: Int, icon: Int) {
        Glide.with(this).asGif().load(icon).into(dialog.imgTrue)
        dialog.tvAddMin.text = score
        dialog.tvAddMin.textColorResource = color
        dialog.tvScore.text = "Skor Kamu : ${this.score}"
        dialog.show()
        delay(1000)
        dialog.dismiss()
        showNewData()
    }

    private fun initDialog() {
        dialog = Dialog(this).apply {
            setContentView(R.layout.layout_popup_quiz)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
        }
    }

    private fun initTransition() {
        val animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        content.startAnimation(animation)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}