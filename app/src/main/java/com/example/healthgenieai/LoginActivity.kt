package com.example.healthgenieai

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgot: TextView

    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && user.isEmailVerified) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgot = findViewById(R.id.tvForgot)

        val tvSignup = findViewById<TextView>(R.id.tvSignup)

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        tvForgot.setOnClickListener {
            showResetDialog()
        }

        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Enter email"
            return
        }

        if (pass.length < 6) {
            etPassword.error = "Minimum 6 characters required"
            return
        }

        btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {

                btnLogin.isEnabled = true

                if (it.isSuccessful) {

                    val user = auth.currentUser

                    if (user != null && user.isEmailVerified) {

                        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Please verify your email before login.",
                            Toast.LENGTH_LONG
                        ).show()

                        auth.signOut()
                    }

                } else {

                    Toast.makeText(
                        this,
                        it.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showResetDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Password")

        val input = EditText(this)
        input.hint = "Enter your email"
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        builder.setView(input)

        builder.setPositiveButton("Send") { _, _ ->

            val email = input.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Reset link sent to email",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}