package com.example.beerin.letooow.response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

class ItemsResponse {
    private val api: ItemApi = Retrofit.Builder()
        .baseUrl("http://147.45.48.182:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ItemApi::class.java)

    suspend fun getItems(): List<Item> {
        return api.getItems()
    }

    suspend fun getItemsWithFilters(
        country: String?,
        style: String?,
        type: String?,
        alcoholRange: String?,
        densityRange: String?
    ): List<Item> {
        return api.getItemFilter(country, style, type, alcoholRange, densityRange)
    }
}

interface ItemApi {
    @GET("items")
    suspend fun getItems(): List<Item>

    @GET("filter_items")
    suspend fun getItemFilter(
        @Query("country") country: String?,
        @Query("style") style: String?,
        @Query("type") type: String?,
        @Query("alcohol_range") alcoholRange: String?,
        @Query("density_range") densityRange: String?
    ): List<Item>
}


data class Item(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("average_rating") var averageRating: Int?,
    @SerializedName("rating_count") var ratingCount: Int?,
    @SerializedName("country") var country: String?,
    @SerializedName("volume") var volume: Double?,
    @SerializedName("alcohol_percentage") var alcoholPercentage: Int?,
    @SerializedName("density") var density: Int?,
    @SerializedName("style") var style: String?,
    @SerializedName("color") var color: String?,
    @SerializedName("filtration") var filtration: Int?,
    @SerializedName("brand") var brand: String?,
    @SerializedName("proteins") var proteins: Int?,
    @SerializedName("fats") var fats: Int?,
    @SerializedName("carbohydrates") var carbohydrates: Int?,
    @SerializedName("image") var image: String?

)