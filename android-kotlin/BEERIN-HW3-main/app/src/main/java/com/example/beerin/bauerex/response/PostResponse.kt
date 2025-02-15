package com.example.beerin.bauerex.response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName

class PostRepository {
    private val api: PostsApi = Retrofit.Builder()
        .baseUrl("http://147.45.48.182:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PostsApi::class.java)

    suspend fun getPosts(): List<Post> {
        return api.getPosts()
    }

    suspend fun likePost(login: String, password: String, postId: Int): LikeResponse {
        return api.likePost(login, password, postId)
    }

    suspend fun getComments(postId: Int): List<Comment> {
        return api.getComments(postId)
    }
}

interface PostsApi {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @POST("like_post")
    suspend fun likePost(
        @Header("login") login: String,
        @Header("password") password: String,
        @Header("post_id") postId: Int
    ): LikeResponse

    @GET("post/get_comments")
    suspend fun getComments(
        @Header("post_id") postId: Int
    ): List<Comment>
}

data class Post(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("company_name")
    val companyName: String?,
    @SerializedName("company_logo_image")
    val companyLogoImage: String?,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("like_count")
    val likeCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("created_at")
    val createdAt: String?
)

data class LikeResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("time")
    val time: String
)

data class Comment(
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("user_login")
    val userLogin: String,
    @SerializedName("user_avatar_image")
    val userAvatarImage: String
)