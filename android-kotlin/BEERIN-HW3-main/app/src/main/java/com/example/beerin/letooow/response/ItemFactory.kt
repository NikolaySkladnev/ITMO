package com.example.beerin.letooow.response

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemFactory(private val response: ItemsResponse) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsViewModel::class.java)) {
            return ItemsViewModel(response) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}