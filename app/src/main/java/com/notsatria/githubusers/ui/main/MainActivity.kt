package com.notsatria.githubusers.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.notsatria.githubusers.R
import com.notsatria.githubusers.databinding.ActivityMainBinding
import com.notsatria.githubusers.ui.setting.SettingViewModel
import com.notsatria.githubusers.ui.setting.SettingViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavbar: BottomNavigationView = binding.bottomNavbar

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_favorite,
            R.id.navigation_setting
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavbar.setupWithNavController(navController)

        supportActionBar?.hide()

//        Akses night mode preference dari setting fragment
        val factory: SettingViewModelFactory = SettingViewModelFactory.getInstance(applicationContext)
        val settingViewModel: SettingViewModel by viewModels{ factory }

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }
}