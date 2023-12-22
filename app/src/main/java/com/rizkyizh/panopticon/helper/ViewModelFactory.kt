package com.rizkyizh.panopticon.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rizkyizh.panopticon.MainViewModel
import com.rizkyizh.panopticon.data.repository.AuthRepository
import com.rizkyizh.panopticon.di.Injection
import com.rizkyizh.panopticon.ui.login.LoginViewModel
import com.rizkyizh.panopticon.ui.profile.ProfileViewModel
import com.rizkyizh.panopticon.ui.register.RegisterViewModel



class ViewModelFactory(
    private val authRepository: AuthRepository,
    ): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authRepository) as T
            }
             modelClass.isAssignableFrom(RegisterViewModel::class.java)->{
               RegisterViewModel(authRepository) as T
             }
            modelClass.isAssignableFrom(ProfileViewModel::class.java)->{
                ProfileViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory{
            if (INSTANCE == null){
                synchronized(ViewModelFactory::class.java){
                    INSTANCE = ViewModelFactory(
                        authRepository = Injection.provideAuthRepository(context)

                    )
                }
            }
                return  INSTANCE as ViewModelFactory
        }
    }

}