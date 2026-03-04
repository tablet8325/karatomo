package org.karatomo.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface KaraokeApiService {
    @GET("karaoke.json")
    suspend fun getSongs(
        @Query("brand") brand: String,
        @Query("query") query: String? = null
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
        .baseUrl("https://api.manana.kr/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KaraokeApiService::class.java)
}
