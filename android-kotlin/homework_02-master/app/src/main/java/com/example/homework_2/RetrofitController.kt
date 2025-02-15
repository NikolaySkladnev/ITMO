package com.example.homework_2

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitController {
    private val httpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    private val catApi = retrofit.create(CatApi::class.java)

    suspend fun fetchRandomCat(): String {
        try {
            val response = catApi.getRandomCat()
            return response.first().url
        } catch (e: Exception) {
            throw e
        }
    }
}
