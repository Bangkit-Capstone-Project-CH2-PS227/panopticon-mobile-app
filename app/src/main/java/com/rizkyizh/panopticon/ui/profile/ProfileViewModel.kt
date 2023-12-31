package com.rizkyizh.panopticon.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rizkyizh.panopticon.data.pref.UserModel
import com.rizkyizh.panopticon.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "fitur ini belum tersedia"
    }
    val text: LiveData<String> = _text

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}