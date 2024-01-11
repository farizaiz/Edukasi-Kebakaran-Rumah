package com.farizskripsi.projek.data.repository

import android.content.Context
import com.farizskripsi.projek.data.model.Lesson
import com.farizskripsi.projek.data.model.LessonResponse
import com.farizskripsi.projek.data.model.Quiz
import com.farizskripsi.projek.data.model.QuizResponse
import com.google.gson.Gson

class DataRepository {

    fun getCourse(context: Context, id: String?): List<Lesson> {
        val jsonFile = context.assets.open("materiKebakaran.json").bufferedReader().use { it.readText() }
        val lesson = Gson().fromJson(jsonFile, LessonResponse::class.java)
        return lesson.course.filter { it.courseId == id }
    }

    fun getQuiz(context: Context, id: String?): List<Quiz> {
        val jsonFile = context.assets.open("kuisKebakaran.json").bufferedReader().use { it.readText() }
        val quizResponse = Gson().fromJson(jsonFile, QuizResponse::class.java)
        return quizResponse.quiz.filter { it.courseId == id}
    }
}