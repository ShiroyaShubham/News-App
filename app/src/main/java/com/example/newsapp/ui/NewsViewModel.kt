package com.example.newsapp.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.NewsApplication
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsModel
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.Resource.Error
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {
    val breakingNews: MutableLiveData<Resource<NewsModel>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsModel? = null

    val searchNews: MutableLiveData<Resource<NewsModel>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsModel? = null

    init {
        getBreaksNews("in")
    }

    fun getBreaksNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsModel>)
            : Resource<NewsModel> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newsArticle = resultResponse.articles
                    oldArticles?.addAll(newsArticle)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        val errorObject = response.errorBody()?.charStream()?.readText()?.let { JSONObject(it) }
        val errorMessage = errorObject?.getString("message").toString()
        return Error(errorMessage)
    }

    private fun handleSearchNewsResponse(response: Response<NewsModel>)
            : Resource<NewsModel> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newsArticle = resultResponse.articles
                    oldArticles?.addAll(newsArticle)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        val errorObject = response.errorBody()?.charStream()?.readText()?.let { JSONObject(it) }
        val errorMessage = errorObject?.getString("message").toString()
        return Error(errorMessage)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSaveArticles() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }


    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleSearchNewsResponse(response))
            } else {
                breakingNews.postValue(Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Error("Network failure"))
                else -> breakingNews.postValue(Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, breakingNewsPage)
                searchNews.postValue(handleBreakingNewsResponse(response))
            } else {
                searchNews.postValue(Error("No internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Error("Network failure"))
                else -> searchNews.postValue(Error("Conversion Error"))
            }
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activityNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activityNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}