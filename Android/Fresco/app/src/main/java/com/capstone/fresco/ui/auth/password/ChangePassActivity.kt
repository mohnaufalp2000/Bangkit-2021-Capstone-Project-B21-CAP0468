package com.capstone.fresco.ui.auth.password

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fresco.databinding.ActivityChangePassBinding
import com.capstone.fresco.ui.auth.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePassActivity : AppCompatActivity() {

    private var user: FirebaseUser? = null

    private val binding by lazy { ActivityChangePassBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance().currentUser

        binding.btnChangePass.setOnClickListener(View.OnClickListener {
            onChangePass()
            return@OnClickListener
        })

        binding.txtSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
    }

    private fun onChangePass() {
        val getNewPassword = binding.edtNewPass.text.toString().trim { it <= ' ' }
        val getConfirmPassword =
            binding.edtConfirmPass.text.toString().trim { it <= ' ' }

        if (getNewPassword.isEmpty() || getConfirmPassword.isEmpty()) {
            binding.apply {
                edtNewPass.error = "Password can't be empty"
                edtNewPass.requestFocus()
                edtConfirmPass.error = "Confirm Password can't be empty"
                edtConfirmPass.requestFocus()
            }
        }

        if (getNewPassword.length < 8) {
            binding.apply {
                edtNewPass.error = "Password lenght minimal 8 char"
                edtNewPass.requestFocus()
            }
        }

        if (getNewPassword != getConfirmPassword) {
            binding.apply {
                edtNewPass.error = "Password and Confirm Password is different"
                edtNewPass.requestFocus()
                edtConfirmPass.error = "Password and Confirm Password is different"
                edtConfirmPass.requestFocus()
            }
        }

        user?.updatePassword(getNewPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Change password successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                } else {
                    Toast.makeText(
                        this,
                        "Error, Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}