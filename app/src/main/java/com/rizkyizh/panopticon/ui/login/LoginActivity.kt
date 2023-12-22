package com.rizkyizh.panopticon.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import com.rizkyizh.panopticon.MainActivity
import com.rizkyizh.panopticon.R
import com.rizkyizh.panopticon.data.remote.request.LoginRequest
import com.rizkyizh.panopticon.databinding.ActivityLoginBinding
import com.rizkyizh.panopticon.helper.ViewModelFactory
import com.rizkyizh.panopticon.helper.isValidEmail
import com.rizkyizh.panopticon.helper.simpleAlertDialog
import com.rizkyizh.panopticon.ui.register.RegisterActivity
import com.rizkyizh.panopticon.helper.Result
import java.util.Objects

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var isEmailInputValid = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        binding.btnLoginToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupView() {
        val emailInputLayout = binding.etLayoutEmail
        val emailEditText = binding.etEmail
        val passwordInputLayout = binding.etLayoutPassword
        val passwordEditText = binding.etPassword

        emailInputLayout.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (emailEditText.text?.length!! >= 0) {
                        emailInputLayout.startIconDrawable = null
                    }
                } else {
                    if (emailEditText.text?.length!! == 0) {
                        emailInputLayout.startIconDrawable =
                            getDrawable(R.drawable.ic_baseline_email_24)
                    }
                }
            }

        passwordInputLayout.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (passwordEditText.text?.length!! >= 0) {
                        passwordInputLayout.startIconDrawable = null
                    }
                } else {
                    if (passwordEditText.text?.length!! == 0) {
                        passwordInputLayout.startIconDrawable =
                            getDrawable(R.drawable.ic_baseline_lock_24)
                    }
                }
            }


        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                isEmailInputValid = text.toString().isValidEmail()
                if (!isEmailInputValid) emailInputLayout.error =
                    "incorrect format!" else emailInputLayout.error = null
                setButtonLoginEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonLoginEnable()
                if (text.toString().length < 8) {
                    passwordInputLayout.errorIconDrawable = null
                    passwordInputLayout.error = "password must have at least 8 characters"
                } else {
                    passwordInputLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.btnLogin.setOnClickListener {
            val loginRequest = LoginRequest(
                email = emailEditText.text.toString(), password = passwordEditText.text.toString()
            )


            viewModel.loginUser(loginRequest, { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        if (result.data.email == emailEditText.text.toString()) {

                            runOnUiThread {

                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        }

                    }

                    is Result.ErrorMessage -> {}
                    else -> {}
                }

            }, { error ->

                when (error) {
                    is Result.Error -> {
                        showLoading(false)

                        var message = "ada masalah dengan server"
                        if (error.error != null) {
                            message = error.error.msg.toString()
                        }

                        runOnUiThread {
                            simpleAlertDialog(
                                context = this,
                                toPreviousActivity = false,
                                title = "Oops!",
                                message = message
                            )
                        }
                    }


                    else -> {}
                }

            })
        }
    }


    private fun setButtonLoginEnable() {
        val email = isEmailInputValid
        val password = binding.etPassword.text?.length
        if (password != null) {
            binding.btnLogin.apply {
                if (password >= 8 && email) {
                    isEnabled = true
                    alpha = 1f
                } else {
                    isEnabled = false
                    alpha = 0f
                }
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        runOnUiThread {
            binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}