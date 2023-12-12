package com.rizkyizh.panopticon

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rizkyizh.panopticon.databinding.ActivityMainBinding
import com.rizkyizh.panopticon.ui.dashboard.DashboardActivity
import com.rizkyizh.panopticon.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    private fun setupView() {
        this.setSupportActionBar(findViewById(R.id.bottom_app_bar))
        binding.apply {
            navView.background = null
            navView.menu.getItem(1).apply {
                isEnabled = false
            }

            btnShowDialogCreateRoom.setOnClickListener {
                val dialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_layout, null)
                val textInput = dialogView.findViewById<EditText>(R.id.et_name_room)
                val alertDialog = MaterialAlertDialogBuilder(this@MainActivity)
                    .setBackground(getDrawable(R.drawable.bg_dialog_layout))
                    .setView(dialogView)
                    .setPositiveButton("Ok"
                    ) { _, _ ->
                        // TODO: membuat room baru ketika success
                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        startActivity(intent)

                    }.create()
                alertDialog.show()
            }

        }

    }
}