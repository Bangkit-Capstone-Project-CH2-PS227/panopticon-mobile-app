package com.rizkyizh.panopticon

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rizkyizh.panopticon.data.pref.UserModel
import com.rizkyizh.panopticon.data.repository.AuthRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AuthRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}