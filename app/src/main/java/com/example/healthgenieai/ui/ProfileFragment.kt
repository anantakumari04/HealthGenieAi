package com.example.healthgenieai.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthgenieai.LoginActivity
import com.example.healthgenieai.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        val name = view.findViewById<TextView>(R.id.profileName)
        val email = view.findViewById<TextView>(R.id.profileEmail)
        val editBtn = view.findViewById<Button>(R.id.btnEdit)
       val logoutBtn = view.findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser

        name.text = user?.displayName ?: "User"
        email.text = user?.email ?: "user@email.com"

        editBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Clicked", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {

            auth.signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        return view
    }
}
