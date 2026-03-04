package org.karatomo.app.network

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("brand") val brand: String?,
    @SerializedName("no") val no: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("singer") val singer: String?,
    @SerializedName("composer") val composer: String?,
    @SerializedName("lyricist") val lyricist: String?,
    @SerializedName("release") val release: String?
)
