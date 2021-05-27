package com.capstone.fresco.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import com.capstone.fresco.MainActivity
import com.capstone.fresco.R
import com.capstone.fresco.databinding.ActivitySplashBinding
import com.capstone.fresco.ui.onboarding.OnboardingOne

class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageAnim = AnimationUtils.loadAnimation(this, R.anim.stb)
        binding.imgSplashScreen.startAnimation(imageAnim)

        Handler(mainLooper).postDelayed({
            startActivity(Intent(this@SplashActivity, OnboardingOne::class.java))
            finishAffinity()
        }, 2500)

    }
}