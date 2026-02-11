package com.example.healthgenieai





import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.healthgenieai.ui.ProfileFragment
import com.example.healthgenieai.ui.chat.ChatFragment
import com.example.healthgenieai.ui.diet.DietFragment
import com.example.healthgenieai.ui.fitness.FitnessFragment
import com.example.healthgenieai.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Default fragment
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_chat -> loadFragment(ChatFragment())
                R.id.nav_fitness -> loadFragment(FitnessFragment())
                R.id.nav_diet -> loadFragment(DietFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}