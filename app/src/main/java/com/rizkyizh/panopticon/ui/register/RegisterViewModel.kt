package com.rizkyizh.panopticon.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rizkyizh.panopticon.data.remote.request.RegisterRequest
import com.rizkyizh.panopticon.data.remote.response.RegisterResponse
import com.rizkyizh.panopticon.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.rizkyizh.panopticon.helper.Result

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {


    fun registerUser(registerRequest: RegisterRequest, callback: (Result<RegisterResponse>) -> Unit) {
        callback(Result.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authRepository.register(registerRequest)
                callback(Result.Success(response))
            } catch (e: HttpException) {

                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                callback(Result.Error(errorBody))
            }
        }
    }



}