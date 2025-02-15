package com.example.beerin.bauerex.response

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerin.viewModelRegGlobal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostsViewModel(private val repository: PostRepository) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> get() = _comments

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                _posts.value = repository.getPosts()
            } catch (e: Exception) {
                Log.e("PostsViewModel", "Error fetching posts", e)
            }
        }
    }

    fun fetchComments(postId: Int) {
        viewModelScope.launch {
            try {
                _comments.value = repository.getComments(postId)
            } catch (e: Exception) {
                Log.e("PostsViewModel", "Error fetching comments", e)
            }
        }
    }

    fun likePost(postId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.likePost(
                    login = viewModelRegGlobal.login,
                    password = viewModelRegGlobal.password,
                    postId = postId
                )
                if (response.success) {
                    fetchPosts()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
