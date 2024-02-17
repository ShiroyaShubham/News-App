package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.databinding.ActivityNewsBinding
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.repository.NewsRepository
import retrofit2.Retrofit

class NewsActivity : AppCompatActivity() {
    private var _binding: ActivityNewsBinding? = null
    private val binding get() = _binding!!
    lateinit var newsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = NewsRepository(
            ArticleDatabase.getDatabase(applicationContext),
            RetrofitInstance.getNewsApi()
        )
        newsViewModel = ViewModelProvider(
            this,
            NewsViewModelProviderFactory(repository, application)
        )[NewsViewModel::class.java]
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
        binding.bottomNavigationView.setupWithNavController(navHostFragment!!.findNavController())

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}