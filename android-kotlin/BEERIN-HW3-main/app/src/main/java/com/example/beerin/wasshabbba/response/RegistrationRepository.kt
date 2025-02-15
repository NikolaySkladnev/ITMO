package com.example.beerin.wasshabbba.response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationRepository {

    private val api: RegistrationApi = Retrofit.Builder()
        .baseUrl("http://147.45.48.182:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RegistrationApi::class.java)

    suspend fun registerUser(
        login: String,
        password: String,
        phoneNumber: String,
        name: String,
        surname: String
    ): RegistrationResponse {
        return api.registerUser(
            login = login,
            password = password,
            phoneNumber = phoneNumber,
            name = name,
            surname = surname
        )
    }
}
