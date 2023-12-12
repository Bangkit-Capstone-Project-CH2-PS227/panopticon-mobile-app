package com.rizkyizh.panopticon.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "fitur ini belum tersedia"
    }
    val text: LiveData<String> = _text
}