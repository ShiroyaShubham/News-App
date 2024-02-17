package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.model.Article
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class ArticleFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var article: Article
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArticleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        val articleString = arguments?.getString("articles")
        Log.d("TAG_ARICLE", "onViewCreated: $articleString")
        article = Gson().fromJson(articleString, Article::class.java)
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        handlerListener()
    }

    private fun handlerListener() {
        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(requireView(), "Article save successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

}