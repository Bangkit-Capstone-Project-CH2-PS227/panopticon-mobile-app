package com.rizkyizh.panopticon

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rizkyizh.panopticon.databinding.ActivityMainBinding
import com.rizkyizh.panopticon.helper.ViewModelFactory
import com.rizkyizh.panopticon.ui.dashboard.DashboardActivity
import com.rizkyizh.panopticon.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

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

        if (!allPermissionsGranted()) {
            //request permission
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
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

    companion object {
        const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}