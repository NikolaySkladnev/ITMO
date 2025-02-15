package com.example.beerin.wasshabbba.response

import com.example.beerin.wasshabbba.response.*
import retrofit2.http.Header
import retrofit2.http.POST

interface RegistrationApi {
    @POST("registration")
    suspend fun registerUser(
        @Header("login") login: String,
        @Header("password") password: String,
        @Header("phone_number") phoneNumber: String,
        @Header("name") name: String,
        @Header("surname") surname: String
    ): RegistrationResponse
}
