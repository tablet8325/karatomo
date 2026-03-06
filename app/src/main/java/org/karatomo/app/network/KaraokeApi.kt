package org.karatomo.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * [KaraokeApiService]
 * 기존 검색 방식(@Query)을 유지하면서, 신곡 조회를 위한 경로(@Path)를 추가했습니다.
 */
interface KaraokeApiService {
    
    // [기존 기능 유지] 검색 API (예: /karaoke.json?brand=tj&query=응급실)
    @GET("karaoke.json")
    suspend fun getSongs(
        @Query("brand") brand: String,
        @Query("query") query: String? = null
    ): List<Song>

    // [신규 기능 추가] 신곡 출시 데이터 (예: /karaoke/release/202603/tj.json)
    // 이 경로는 API 서버의 실제 스펙에 맞게 조정이 필요할 수 있습니다.
    @GET("karaoke/release/{date}/{brand}.json")
    suspend fun getNewSongs(
        @Path("date") date: String,
        @Path("brand") brand: String
    ): Response<List<Song>>
}

/**
 * [KaraokeApi]
 * 기존의 로깅 인터셉터와 OkHttpClient 설정을 그대로 유지합니다.
 */
object KaraokeApi {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // 기존의 베이스 URL(api.manana.kr)을 유지합니다.
    val service: KaraokeApiService = Retrofit.Builder()
        .baseUrl("https://api.manana.kr/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KaraokeApiService::class.java)
}
