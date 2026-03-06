package org.karatomo.app.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface KaraokeService {
    @Headers(
        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36",
        "Referer: https://api.manana.kr/",
        "Accept: application/json"
    )
    @GET("release")
    fun getReleaseSongs(@Query("searchmonth") month: String, @Query("brand") brand: String): Call<List<Song>>

    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
    @GET("search")
    fun searchSongs(@Query("query") query: String, @Query("brand") brand: String, @Query("type") type: String): Call<List<Song>>
}

object KaraokeApi {
    private const val BASE_URL = "https://api.manana.kr/karaoke/"

    val service: KaraokeService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaraokeService::class.java)
    }
}
