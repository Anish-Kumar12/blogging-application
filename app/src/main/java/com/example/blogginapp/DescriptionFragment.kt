package com.example.blogginapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blogginapp.databinding.FragmentDescriptionBinding
import com.example.blogginapp.databinding.FragmentReminderBinding
import com.google.firebase.auth.FirebaseAuth

class DescriptionFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()

        binding.Logoutbutton.setOnClickListener {
            mAuth.signOut()
            navigateToWelcomeActivity()
        }

        return view
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(activity, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Clear back stack
        startActivity(intent)
    }
}
