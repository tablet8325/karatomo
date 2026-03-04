package org.karatomo.app.network

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("no") val no: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("singer") val singer: String? = null,
    @SerializedName("composer") val composer: String? = null,
    @SerializedName("lyricist") val lyricist: String? = null,
    @SerializedName("release") val release: String? = null
)
