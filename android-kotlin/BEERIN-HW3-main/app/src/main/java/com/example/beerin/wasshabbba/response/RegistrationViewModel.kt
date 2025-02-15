package com.example.beerin.wasshabbba.response

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val repository: RegistrationRepository) : ViewModel() {

    private val _registrationResult = MutableStateFlow<RegistrationResponse?>(null)
    val registrationResult: StateFlow<RegistrationResponse?> = _registrationResult

    val login = "Alexey"
    val password = "321"

    fun registerUser(
        login: String,
        password: String,
        phoneNumber: String,
        name: String,
        surname: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(login, password, phoneNumber, name, surname)
                _registrationResult.value = response
//                Log.d("REGISTRATION", "Ответ сервера: $response")
            } catch (e: Exception) {
                Log.e("REGISTRATION", "Ошибка запроса: $e")
            }
        }
    }
}
