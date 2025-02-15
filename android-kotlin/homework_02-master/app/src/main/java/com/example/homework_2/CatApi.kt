package com.example.homework_2

import retrofit2.http.GET

interface CatApi {
    @GET("v1/images/search")
    suspend fun getRandomCat(): List<CatResponse>
}
