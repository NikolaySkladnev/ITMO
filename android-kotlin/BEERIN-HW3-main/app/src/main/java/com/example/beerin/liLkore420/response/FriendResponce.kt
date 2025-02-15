package com.example.beerin.liLkore420.response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.google.gson.annotations.SerializedName
import retrofit2.http.Header

interface FriendsApi {
    @GET("user/friends")
    suspend fun getFriends(
        @Header("login") login: String,
        @Header("password") password: String
    ): List<Friend>

    @GET("user/friend_info")
    suspend fun getFriendInfo(
        @Header("login") login: String,
        @Header("password") password: String,
        @Header("friend_id") friendId: Int
    ): Friend
}


class FriendsRepository {
    private val api: FriendsApi = Retrofit.Builder()
        .baseUrl("http://147.45.48.182:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FriendsApi::class.java)

    suspend fun getFriends(login: String, password: String): List<Friend> {
        return api.getFriends(login, password)
    }

    suspend fun getFriendInfo(login: String, password: String, friendId: Int): Friend {
        return api.getFriendInfo(login, password, friendId)
    }
}

data class Friend(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("surname")
    val surname: String,
    @SerializedName("status")
    val status: String?,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("image")
    val image: String?
)
