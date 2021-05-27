package com.capstone.fresco.ui.onboarding

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.fresco.MainActivity
import com.capstone.fresco.core.util.Preferences
import com.capstone.fresco.databinding.ActivityOnboardingOneBinding

class OnboardingOne : AppCompatActivity() {

    private val binding by lazy { ActivityOnboardingOneBinding.inflate(layoutInflater) }
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        preferences = Preferences(this)

        if (preferences.getState("onboarding").equals("1")){
            finishAffinity()
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnOnboardOne.setOnClickListener {
            startActivity(Intent(this, OnboardingTwo::class.java))
        }

    }
}