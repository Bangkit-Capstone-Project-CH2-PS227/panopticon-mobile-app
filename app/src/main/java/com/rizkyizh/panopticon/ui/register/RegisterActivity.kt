package com.rizkyizh.panopticon.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.rizkyizh.panopticon.R
import com.rizkyizh.panopticon.databinding.ActivityRegisterBinding
import com.rizkyizh.panopticon.helper.isValidEmail
import com.rizkyizh.panopticon.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var isEmailInputValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()


        binding.btnRegisterToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            finish()
        }



    }

    private fun setupView(){
        val emailInputLayout = binding.etLayoutEmail
        val emailEditText = binding.etEmail
        val passwordInputLayout = binding.etLayoutPassword
        val passwordEditText = binding.etPassword
        val nameEditText = binding.etFullName

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonLoginEnable()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        emailInputLayout.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (emailEditText.text?.length!! >= 0) {
                        emailInputLayout.startIconDrawable = null
                    }
                } else {
                    if (emailEditText.text?.length!! == 0) {
                        emailInputLayout.startIconDrawable = getDrawable(R.drawable.ic_baseline_email_24)
                    }
                }
            }

        passwordInputLayout.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                if (passwordEditText.text?.length!! >= 0){
                    passwordInputLayout.startIconDrawable = null
                }
            }else{
                if (passwordEditText.text?.length!! == 0){
                    passwordInputLayout.startIconDrawable = getDrawable(R.drawable.ic_baseline_lock_24)
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
    }

    private fun setButtonLoginEnable() {
        val name = binding.etFullName.text
        val email = isEmailInputValid
        val password = binding.etPassword.text?.length
        if (password != null && name.toString().isNotEmpty()) {
            binding.btnRegister.apply {
                if(password >= 8 && email){
                    isEnabled = true
                    alpha = 1f
                }else{
                    isEnabled = false
                    alpha = 0f
                }
            }

        }
    }
}