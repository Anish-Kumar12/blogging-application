package com.example.blogginapp

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapp.model.BlogItemModel
import com.example.blogapp.model.UserData
import com.example.blogginapp.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class add_article : AppCompatActivity() {
    private val binding: ActivityAddArticleBinding by lazy{
        ActivityAddArticleBinding.inflate(layoutInflater)
    }
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
    private val userReference : DatabaseReference = FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backbtn.setOnClickListener{
            finish()
        }
        binding.AddBlogbutton.setOnClickListener{
            val title = binding.BlogTitle.editText?.text.toString().trim()
            val description = binding.BlogDescription.editText?.text.toString().trim()

            if(title.isEmpty() || description.isEmpty()){
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }
            //get current user
            val user : FirebaseUser? = auth.currentUser
            if(user != null){
                val userId = user.uid
                val userName = user.displayName?:"Anonymous"
                val userImageUrl = user.photoUrl?:""

                //fetch username and userprofile from database

                userReference.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if(userData !=null){
                            val userNameFromDB = userData.name
                            val userImageURLFromDB = userData.profileImage

                            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                            //create a blofItemModel
                            val blogItem = BlogItemModel(
                                title,
                                userNameFromDB,
                                currentDate,
                                description,
                                likecount = 0,
                                userImageURLFromDB
                            )
                            //generate unique key for blog post
                            val key = databaseReference.push().key
                            if(key!=null){
                                blogItem.postId = key
                                val blogReference = databaseReference.child(key)
                                blogReference.setValue(blogItem).addOnCompleteListener{
                                    if(it.isSuccessful){
                                        Toast.makeText(this@add_article,"Suucessfully added blog",Toast.LENGTH_SHORT).show()

                                        finish()
                                    }
                                    else{
                                        Toast.makeText(this@add_article,"Failed to add blog",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
    }
}