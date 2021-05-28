package com.capstone.fresco.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.fresco.ui.main.MainActivity
import com.capstone.fresco.core.util.Preferences
import com.capstone.fresco.databinding.ActivityOnboardingThreeBinding

class OnboardingThree : AppCompatActivity() {

    private val binding by lazy { ActivityOnboardingThreeBinding.inflate(layoutInflater) }
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        preferences = Preferences(this)

        binding.btnOnboardThree.setOnClickListener {
            preferences.setState("onboarding", "1")
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

    }
}