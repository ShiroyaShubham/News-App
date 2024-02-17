package com.example.newsapp.api

import com.example.newsapp.model.NewsModel
import com.example.newsapp.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "in",
        @Query("page")
        pageNo: Int = 1,
        @Query("apiKey")
        apiKey: String  = API_KEY
    ): Response<NewsModel>


    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String = "in",
        @Query("page")
        pageNo: Int = 1,
        @Query("apiKey")
        apiKey: String  = API_KEY
    ): Response<NewsModel>
}