package com.rizkyizh.panopticon.ui.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rizkyizh.panopticon.R
import com.rizkyizh.panopticon.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}