package com.example.beerin.bauerex.response

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PostFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            return PostsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}