package org.karatomo.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface KaraokeApiService {
    // 1. .json을 제거하고 'karaoke'로 변경합니다.
    @GET("karaoke")
    suspend fun getSongs(
        @Query("brand") brand: String,
        @Query("query") query: String = "" // 검색어가 없으면 신곡 위주로 나옵니다.
    ): List<Song>
}

object KaraokeApi {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val service: KaraokeApiService = Retrofit.Builder()
        .baseUrl("https://api.manana.kr/") // 베이스 주소는 맞습니다!
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KaraokeApiService::class.java)
}
