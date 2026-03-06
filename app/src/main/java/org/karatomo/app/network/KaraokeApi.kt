package org.karatomo.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KaraokeApiService {
    // [검색 API: Choice 2 방식]
    @GET("karaoke/song/{title}.json")
    suspend fun searchByTitle(@Path("title") title: String, @Query("brand") brand: String): List<Song>

    @GET("karaoke/singer/{singer}.json")
    suspend fun searchBySinger(@Path("singer") singer: String, @Query("brand") brand: String): List<Song>

    @GET("karaoke/no/{no}.json")
    suspend fun searchByNo(@Path("no") no: String, @Query("brand") brand: String): List<Song>

    @GET("karaoke/composer/{composer}.json")
    suspend fun searchByComposer(@Path("composer") composer: String, @Query("brand") brand: String): List<Song>

    @GET("karaoke/lyricist/{lyricist}.json")
    suspend fun searchByLyricist(@Path("lyricist") lyricist: String, @Query("brand") brand: String): List<Song>

    // [월별 신곡 API: Choice 2 방식]
    @GET("karaoke/release.json")
    suspend fun getReleaseSongs(@Query("release") release: String, @Query("brand") brand: String): List<Song>
}

object KaraokeApi {
    private const val BASE_URL = "https://api.manana.kr/"
    val service: KaraokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaraokeApiService::class.java)
    }
}
