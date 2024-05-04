package com.example.blogginapp

import FirebaseAuthManager
import android.content.SharedPreferences
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.blogginapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import com.google.firebase.storage.FirebaseStorage


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: FirebaseAuthManager
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authManager = FirebaseAuthManager(requireContext())
        database =
            FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app")
        storage = FirebaseStorage.getInstance()
        binding.loginbutton2.setOnClickListener {
            val loginEmail = binding.loginemail.text.toString()
            val loginPassword = binding.loginpassword.text.toString()
            if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Plese fill all the Details", Toast.LENGTH_SHORT)
                    .show()
            } else {
                authManager.loginUser(loginEmail, loginPassword,
                    onSuccess = {
                        val editor = requireActivity().getSharedPreferences(
                            "My Settings",
                            Context.MODE_PRIVATE
                        ).edit()
                        editor.putString("email", loginEmail)
                        editor.putString("password", loginPassword)
                        editor.apply()
                        Toast.makeText(requireContext(), "Login Successful 😁😁😁", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    },
                    onFailure = { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

