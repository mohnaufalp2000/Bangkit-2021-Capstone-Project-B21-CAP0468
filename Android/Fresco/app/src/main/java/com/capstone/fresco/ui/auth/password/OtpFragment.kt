@file:Suppress("DEPRECATION")

package com.capstone.fresco.ui.auth.password

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.capstone.fresco.databinding.FragmentOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpFragment : Fragment() {

    private lateinit var fragmentOtpBinding: FragmentOtpBinding
    private lateinit var phoneAuthProvider: PhoneAuthProvider.ForceResendingToken
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var verificationId: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOtpBinding.bind(view)
        fragmentOtpBinding = binding

        firebaseAuth = FirebaseAuth.getInstance()

        /*progressDialog = ProgressDialog(this@OtpFragment)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)*/

        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                TODO("Not yet implemented")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

            }
        }

        binding.txtRequestOtp.setOnClickListener {

        }

        binding.btnVerify.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    ChangePassActivity::class.java
                )
            )
            return@setOnClickListener
        }
    }
}