package com.example.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.NewsApplication
import com.example.newsapp.repository.NewsRepository

class NewsViewModelProviderFactory(
    private val repository: NewsRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(application, repository) as T
    }
}