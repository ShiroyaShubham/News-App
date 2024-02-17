package com.example.newsapp.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.api.NewsApi
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsModel
import com.example.newsapp.utils.Constants.API_KEY
import retrofit2.Response

class NewsRepository(
    private val db: ArticleDatabase,
    private val newsApi: NewsApi
) {

    suspend fun getBreakingNews(countryCode: String, pageNum: Int): Response<NewsModel> {
        return newsApi.getBreakingNews(countryCode, pageNum)
    }

    suspend fun searchNews(searchQuery: String, page: Int): Response<NewsModel> {
        return newsApi.searchForNews(searchQuery, page)
    }

    suspend fun upsert(article: Article): Long {
        return db.getArticleDao().upsert(article)
    }

    fun getSavedNews(): LiveData<List<Article>> {
        return db.getArticleDao().getAllArticles()
    }

    suspend fun delete(article: Article) {
        db.getArticleDao().delete(article)
    }
}