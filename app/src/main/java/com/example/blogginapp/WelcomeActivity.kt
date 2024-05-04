package com.example.blogginapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blogginapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadfragment(LoginFragment())

        binding.loginButton.setOnClickListener{
            loadfragment(LoginFragment())

        }
        binding.RegisterButton.setOnClickListener{
            loadfragment(RegisterFragment())
        }
    }
    private fun loadfragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmet_containers,fragment)
            .commit()
    }
}