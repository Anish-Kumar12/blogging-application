package com.example.blogginapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.model.BlogItemModel
import com.example.blogginapp.Adapter.BlogAdapter
import com.example.blogginapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth :FirebaseAuth
    private val blogItems = mutableListOf<BlogItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("blogs")
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        //setUser Profile
        if(userId !=null){
            loadUserProfileImage(userId)
        }

        //setting on click listerner to profile image
        binding.cardView2.setOnClickListener {
            startActivity(Intent(this,ProfieActivity::class.java))
        }
        //Inititlaize RecyclerView
        val recyclerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //fetch data fro firebase database
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                blogItems.clear()
                for (snapshot in snapshot.children){
                    val blogItem = snapshot.getValue(BlogItemModel::class.java)
                    if(blogItem !=null){
                        blogItems.add(blogItem)
                    }
                }
                blogItems.reverse()
                //Notify the adapter that the data has changed
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Blog Loading Failed",Toast.LENGTH_SHORT).show()
            }
        })
        binding.floatingAddArticleButton.imageTintList = ColorStateList.valueOf(Color.WHITE)

        binding.floatingAddArticleButton.setOnClickListener{
            startActivity(Intent(this,add_article::class.java))
        }
    }
    private fun loadUserProfileImage(userId: String) {
        val userReference = FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userId)
        userReference.child("profile_image").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String
                ::class.java)
                if(profileImageUrl !=null){
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.profileImages)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Error loading Profile Image",Toast.LENGTH_SHORT).show()
            }

        })
    }
}