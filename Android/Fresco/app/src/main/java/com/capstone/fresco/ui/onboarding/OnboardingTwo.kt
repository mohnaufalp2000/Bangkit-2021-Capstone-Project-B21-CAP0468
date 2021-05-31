package com.capstone.fresco.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityOnboardingTwoBinding

class OnboardingTwo : AppCompatActivity() {

    private val binding by lazy { ActivityOnboardingTwoBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnOnboardTwo.setOnClickListener {
            startActivity(Intent(this, OnboardingThree::class.java))
        }

    }
}