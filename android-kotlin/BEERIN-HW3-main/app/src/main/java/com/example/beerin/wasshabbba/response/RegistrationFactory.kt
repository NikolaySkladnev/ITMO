package com.example.beerin.wasshabbba.response

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegistrationFactory(private val repository: RegistrationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
