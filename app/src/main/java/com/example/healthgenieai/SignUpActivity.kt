package com.example.healthgenieai

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val name = findViewById<EditText>(R.id.etName)
        val age = findViewById<EditText>(R.id.etAge)
        val height = findViewById<EditText>(R.id.etHeight)
        val weight = findViewById<EditText>(R.id.etWeight)
        val email = findViewById<EditText>(R.id.etEmail)
        val pass = findViewById<EditText>(R.id.etPassword)
        val btn = findViewById<Button>(R.id.btnSignup)

        btn.setOnClickListener {

            val n = name.text.toString().trim()
            val a = age.text.toString().trim()
            val h = height.text.toString().trim()
            val w = weight.text.toString().trim()
            val e = email.text.toString().trim()
            val p = pass.text.toString().trim()

            if (n.isEmpty() || a.isEmpty() || h.isEmpty() || w.isEmpty() || e.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (p.length < 6) {
                pass.error = "Minimum 6 characters required"
                return@setOnClickListener
            }

            registerUser(n, a, h, w, e, p)
        }
    }

    private fun registerUser(
        name: String,
        age: String,
        height: String,
        weight: String,
        email: String,
        pass: String
    ) {

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {

                val user = auth.currentUser

                user?.sendEmailVerification()
                    ?.addOnSuccessListener {

                        val userId = user.uid

                        val userMap = hashMapOf(
                            "name" to name,
                            "age" to age,
                            "height" to height,
                            "weight" to weight,
                            "email" to email
                        )

                        db.collection("users")
                            .document(userId)
                            .set(userMap)

                        Toast.makeText(
                            this,
                            "Verification email sent. Please verify your email. Kindly check spam folder too",
                            Toast.LENGTH_LONG
                        ).show()

                        auth.signOut()

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }

            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }
}