package org.karatomo.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface KaraokeApiService {
    // 엔드포인트를 .json으로 명시하고 query 파라미터를 확실히 잡습니다.
    @GET("karaoke.json")
    suspend fun getSongs(
        @Query("brand") brand: String,
        @Query("query") query: String? = null // 검색용
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
