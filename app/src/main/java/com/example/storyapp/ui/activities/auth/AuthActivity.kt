package com.example.storyapp.ui.activities.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storyapp.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, LogInFragment())
                .commit()
        }

    }
}