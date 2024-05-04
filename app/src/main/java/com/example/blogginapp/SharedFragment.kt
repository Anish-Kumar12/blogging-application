package com.example.blogginapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blogginapp.databinding.FragmentDescriptionBinding
import com.example.blogginapp.databinding.FragmentSharedBinding


class SharedFragment : Fragment() {
    private lateinit var binding: FragmentSharedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSharedBinding.inflate(inflater, container, false)
        val view = binding.root
        val editor = requireActivity().getSharedPreferences("My Settings",Context.MODE_PRIVATE)
        binding.sharedEmail.setText(editor.getString("email",null))
        binding.SharedPassword.setText(editor.getString("password",null))

        return view
    }


}