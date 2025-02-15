package com.example.beerin.letooow.response

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beerin.liLkore420.response.FriendsRepository
import com.example.beerin.liLkore420.response.FriendsViewModel

class FriendsFactory(private val response: FriendsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            return FriendsViewModel(response) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}