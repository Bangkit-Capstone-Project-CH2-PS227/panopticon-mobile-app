package com.rizkyizh.panopticon.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rizkyizh.panopticon.data.pref.UserModel
import com.rizkyizh.panopticon.data.remote.request.LoginRequest
import com.rizkyizh.panopticon.data.remote.response.ErrorResponse
import com.rizkyizh.panopticon.data.remote.response.LoginResponse
import com.rizkyizh.panopticon.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.rizkyizh.panopticon.helper.Result

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private fun saveSession(user: UserModel) {
        authRepository.saveSession(user)
    }

    fun loginUser(loginRequest: LoginRequest, callback: (Result<LoginResponse>) -> Unit, errorCallback: (Result<ErrorResponse>) -> Unit) {
        callback(Result.Loading)
        viewModelScope.launch {
            try {
                val response = authRepository.login(loginRequest)

                val userModel = UserModel(
                    username = response.username,
                    email = loginRequest.email,
                    token = response.accessToken,
                    isLogin = true
                )
                if (response.email == loginRequest.email) {
                    saveSession(userModel)
                }

                callback(Result.Success(response))

            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                errorCallback(Result.Error(errorBody))
            }
        }
    }

}
