package com.dani.naversearch.api

import com.dani.naversearch.BuildConfig
import com.dani.naversearch.data.ResultGetSearch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NaverAPI {
    @GET("v1/search/{type}.json")
    fun getSearch(
        @Path("type") type: String,
        @Query("query") query: String,
        @Query("display") display: Int? = 20,
        @Query("start") start: Int? = null
    ): Call<ResultGetSearch>

    companion object {
        private const val BASE_URL = "https://openapi.naver.com/"

        private val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        private val headerInterceptor = Interceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("X-Naver-Client-Id", BuildConfig.NAVER_CLIENT_ID)
                .addHeader("X-Naver-Client-Secret", BuildConfig.NAVER_CLIENT_SECRET)
                .build()
            return@Interceptor it.proceed(request)
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logger)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val naverAPI: NaverAPI = retrofit.create()
    }
}