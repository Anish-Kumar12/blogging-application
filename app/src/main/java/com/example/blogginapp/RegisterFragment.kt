package com.example.blogginapp

import FirebaseAuthManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogginapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authManager = FirebaseAuthManager(requireContext())
        database =
            FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app")
        storage = FirebaseStorage.getInstance()
        binding.registerbutton2.setOnClickListener {
            val registerName = binding.registername.text.toString()
            val registerEmail = binding.registeremail.text.toString()
            val registerPassword = binding.registerpassword.text.toString()
            if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all the details", Toast.LENGTH_SHORT)
                    .show()
            } else {
                authManager.registerUser(registerEmail, registerPassword,
                    onSuccess = {
                        val user = authManager.getCurrentUser()
                        authManager.signOut()
                        user?.let {
                            //Save user data into Firebase realtime database
                            val userReference = database.getReference("users")
                            val userId = user.uid
                            val userData =
                                com.example.blogapp.model.UserData(registerName, registerEmail)
                            userReference.child(userId).setValue(userData)

                            // Upload image to Firebase storage if imageUri is not null
                            imageUri?.let { uri ->
                                val storageReference =
                                    storage.reference.child("profile_image/$userId.jpg")
                                storageReference.putFile(imageUri!!).addOnCompleteListener { uploadTask ->
                                    if (uploadTask.isSuccessful) {
                                        storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                            val imageUrl = imageUri.result.toString()

                                            // Save the image URL to the realtime database
                                            userReference.child(userId).child("profileImage")
                                                .setValue(imageUrl)

//                                                // Load and display the uploaded image
//                                                Glide.with(this)
//                                                    .load(uri)
//                                                    .apply(RequestOptions.circleCropTransform())
//                                                    .into(binding.registerimage)
                                        }
                                        // Display a toast message for successful image upload
                                        Toast.makeText(
                                            requireContext(),
                                            "Image uploaded successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        // Handle the case when image upload fails
                                        Toast.makeText(
                                            requireContext(),
                                            "Image Upload Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                            requireActivity().finish()

                        }
                    },
                    onFailure = {
                            message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "select image"),
                PICK_IMAGE_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.registerimage)
            }
        }
    }
}