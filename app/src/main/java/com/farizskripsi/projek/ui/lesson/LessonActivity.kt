package com.farizskripsi.projek.ui.lesson

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.farizskripsi.projek.R
import com.farizskripsi.projek.data.model.Lesson
import com.farizskripsi.projek.data.repository.DataRepository
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.android.synthetic.main.activity_lesson.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class LessonActivity : AppCompatActivity() {

    private lateinit var viewModel: LessonViewModel
    private lateinit var dataRepository: DataRepository
    private lateinit var filteredLesson: List<Lesson>
    private var i = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        setSupportActionBar(toolbarLesson)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        val courseName = intent?.getStringExtra("course")
        toolbarLesson.title = courseName

        dataRepository = DataRepository()
        viewModel = ViewModelProviders.of(this).get(LessonViewModel::class.java)
        filteredLesson = dataRepository.getCourse(this, courseName)

        viewModel.course.postValue(filteredLesson[i])
        viewModel.course.observe(this, Observer {
            with(filteredLesson[i]) {
                tvTitle.text = title
                tvContent.text = content
                tvPage.text = "${i + 1}/${filteredLesson.size}"

                if (imgLink != "") {
                    imgContent.visibility = View.VISIBLE
                    Glide.with(applicationContext).load(imgLink)
                        .placeholder(android.R.color.darker_gray)
                        .into(imgContent)
                } else imgContent.visibility = View.GONE

                if ((videoLink?.contains("v=")!!) && (videoLink != "")) {
                    val link = videoLink?.split("v=")
                    videoLesson.visibility = View.VISIBLE
                    videoLesson.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                        override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(link!![1], 0f)
                        }
                    })
                } else videoLesson.visibility = View.GONE

                btnPrev.isEnabled = i >= 1
                btnNext.isEnabled = i != filteredLesson.size - 1
            }
        })

        initTransition()
        onAction()
    }

    private fun onAction() {
        btnPrev.onClick {
            if (i > 0) {
                i--
                showNewData()
                setTransition(R.anim.slide_in_left)
            }
        }

        btnNext.onClick {
            if (i < filteredLesson.size - 1) {
                i++
                showNewData()
                setTransition(R.anim.slide_out_left)
            }
        }
    }

    private fun showNewData() {
        viewModel.course.postValue(filteredLesson[i])
        scrollView2.smoothScrollTo(0, 0)
    }

    private fun initTransition() {
        val animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        content.startAnimation(animation)
    }

    private fun setTransition(anim: Int) {
        val animation =
            AnimationUtils.loadAnimation(applicationContext, anim)
        imgContent.startAnimation(animation)
        tvTitle.startAnimation(animation)
        tvContent.startAnimation(animation)
        videoLesson.startAnimation(animation)

        val imgAnim =
            AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        imgContent.startAnimation(imgAnim)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoLesson.release()
    }
}