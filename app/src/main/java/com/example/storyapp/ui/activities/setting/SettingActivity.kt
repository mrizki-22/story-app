package com.example.storyapp.ui.activities.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySettingBinding
import com.example.storyapp.ui.activities.auth.AuthActivity
import com.example.storyapp.ui.activities.auth.AuthViewModel
import com.example.storyapp.ui.activities.auth.AuthViewModelFactory

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.show()
        supportActionBar?.title = getString(R.string.menu_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val authViewModelFactory = AuthViewModelFactory.getInstance(this)
        val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

        binding.apply {
            buttonLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            buttonLogout.setOnClickListener {
                authViewModel.logout()
                startActivity(Intent(this@SettingActivity, AuthActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }

    }
}