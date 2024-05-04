package com.example.blogginapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.model.BlogItemModel
import com.example.blogginapp.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityReadMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backbutton.setOnClickListener{
            finish()
        }

        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")

        if(blogs != null){
            //Retreive user related data here
            binding.titletext.text = blogs.heading
            binding.userBlog.text = blogs.userName
            binding.blogDate.text = blogs.date
            binding.blogdesc.text = blogs.post
            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .load(binding.profileimage)
        }
        else{
            Toast.makeText(this,"Failed to load blog",Toast.LENGTH_SHORT).show()
        }
    }
}