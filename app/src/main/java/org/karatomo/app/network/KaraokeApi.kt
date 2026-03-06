package org.karatomo.app.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface KaraokeService {
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
    @GET("release") // 실제 엔드포인트에 맞게 수정 필요
    fun getReleaseSongs(
        @Query("searchmonth") month: String,
        @Query("brand") brand: String
    ): Call<List<Song>>

    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
    @GET("search") // 실제 엔드포인트에 맞게 수정 필요
    fun searchSongs(
        @Query("query") query: String,
        @Query("brand") brand: String,
        @Query("type") type: String
    ): Call<List<Song>>
}

object KaraokeApi {
    private const val BASE_URL = "https://api.manana.kr/karaoke/" // 예시 URL

    val service: KaraokeService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaraokeService::class.java)
    }
}
