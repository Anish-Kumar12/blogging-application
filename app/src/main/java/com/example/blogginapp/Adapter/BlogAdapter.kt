package com.example.blogginapp.Adapter

import FirebaseAuthManager
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.blogapp.model.BlogItemModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogginapp.R
import com.example.blogginapp.ReadMoreActivity
import com.example.blogginapp.databinding.BlogItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogAdapter(private val items: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-c4654-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
            val postIds = blogItemModel.postId
            val context = binding.root.context
            binding.Heading.text = blogItemModel.heading
            Glide.with(binding.profile.context)
                .load(blogItemModel.profileImage)
                .into(binding.profile)
            binding.UserName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likecount.text = blogItemModel.likecount.toString()

            //set on click listerner
            binding.readMoreButton.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }
            // check if the current user has liked the post and update the likecount
            val postlikeReference: DatabaseReference =
                databaseReference.child("blogs").child(postIds).child("likes")
            val currentuserliked = currentUser?.uid?.let { uid ->
                postlikeReference.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.likebutton.setImageResource(R.drawable.redheart)
                            } else {
                                binding.likebutton.setImageResource(R.drawable.heart)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
            }
            //handle likebutton clicks
            binding.likebutton.setOnClickListener {
                if (currentUser != null) {
                    handlelikedbuttonClicked(postIds, blogItemModel, binding)
                } else {
                    Toast.makeText(context, "You have to login first", Toast.LENGTH_SHORT)
                }

            }

            //Set the initial icon bsed on saved status
            val userReference = databaseReference.child("users").child(currentUser?.uid ?: "")
            val postSaveReference = userReference.child("saveBlogPost").child(postIds)
            postSaveReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // if blog already saved
                        binding.postSaveButton.setImageResource(R.drawable.unsaved)
                    } else {
                        // if blog not saved yet
                        binding.postSaveButton.setImageResource(R.drawable.floatingsaved)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            //handle Save Button clicks
            binding.postSaveButton.setOnClickListener {
                if (currentUser != null) {
                    handleSaveButtonClicked(postIds, blogItemModel, binding)
                } else {
                    Toast.makeText(context, "You have to login first", Toast.LENGTH_SHORT)
                }
            }
        }

    }

    private fun handleSaveButtonClicked(
        postIds: String,
        blogItemModel: BlogItemModel,
        binding: BlogItemBinding
    ) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        userReference.child("saveBlogPost").child(postIds)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // the blog is currently saved , So unsaved it
                        userReference.child("saveBlogPost").child(postIds).removeValue()
                            .addOnSuccessListener {
                                //update the ui
                                val clickedBlogItem = items.find { it.postId == postIds }
                                clickedBlogItem?.isSaved = false
                                notifyDataSetChanged()
                                val context = binding.root.context
                                Toast.makeText(context, "Blog Unsaved", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                val context = binding.root.context
                                Toast.makeText(
                                    context,
                                    "falied to unsave The Blog",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }
                        binding.postSaveButton.setImageResource(R.drawable.unsaved)
                    } else {
                        // the blog is not saved
                        userReference.child("saveBlogPost").child(postIds).setValue(true)
                            .addOnSuccessListener {
                                val clickedBlogItem = items.find { it.postId == postIds }
                                clickedBlogItem?.isSaved = true

                                val context = binding.root.context
                                Toast.makeText(context, "Blog Saved", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                val context = binding.root.context
                                Toast.makeText(context, "failed to save blog ", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        //channge the save button
                        binding.postSaveButton.setImageResource(R.drawable.floatingsaved)

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun handlelikedbuttonClicked(
        postIds: String,
        blogItemModel: BlogItemModel,
        binding: BlogItemBinding
    ) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        val postLikeReference = databaseReference.child("blogs").child(postIds).child("likes")

        //check user has already like the post , so unlike it
        postLikeReference.child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userReference.child("likes").child(postIds).removeValue()
                            .addOnSuccessListener {
                                postLikeReference.child(currentUser.uid).removeValue()
                                blogItemModel.likedBy?.remove(currentUser.uid)
                                updateLikebuttonImage(binding, false)

                                //decrement the like in the database
                                val newLikeCount = blogItemModel.likecount - 1
                                blogItemModel.likecount = newLikeCount
                                databaseReference.child("blogs").child(postIds).child("likecount")
                                    .setValue(newLikeCount)

                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "LikedClicked",
                                    "onDataChange : failed to unlike the blog $e",
                                )
                            }
                    } else {
                        // user has not liked the post , so liked it
                        userReference.child("likes").child(postIds).setValue(true)
                            .addOnSuccessListener {
                                postLikeReference.child(currentUser.uid).setValue(true)
                                blogItemModel.likedBy?.add(currentUser.uid)
                                updateLikebuttonImage(binding, true)


                                //Increment Like count in the database
                                val newLikeCount = blogItemModel.likecount + 1
                                blogItemModel.likecount = newLikeCount
                                databaseReference.child("blogs").child(postIds).child("likecount")
                                    .setValue(newLikeCount)

                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e("LikedClicked", "onDataChange : failed to like the blog $e")
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun updateLikebuttonImage(binding: BlogItemBinding, liked: Boolean) {
        if (liked) {
            binding.likebutton.setImageResource(R.drawable.heart)
        } else {
            binding.likebutton.setImageResource(R.drawable.redheart)
        }
    }
}