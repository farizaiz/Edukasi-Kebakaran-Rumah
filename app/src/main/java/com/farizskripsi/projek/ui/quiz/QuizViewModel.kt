package com.farizskripsi.projek.ui.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farizskripsi.projek.data.model.Quiz

class QuizViewModel : ViewModel() {

    val quiz = MutableLiveData<Quiz>()
}