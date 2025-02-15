package com.example.beerin.wasshabbba.response

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("error_code") val errorCode: Int?,
    @SerializedName("error") val error: String?,
    @SerializedName("time") val time: String?
)
