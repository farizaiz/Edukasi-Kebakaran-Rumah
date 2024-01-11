package com.farizskripsi.projek.ui.lesson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farizskripsi.projek.data.model.Lesson

class LessonViewModel: ViewModel() {

    val course = MutableLiveData<Lesson>()
}