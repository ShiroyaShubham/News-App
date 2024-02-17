package com.example.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class SavedNewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSavedNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        setupRecyclerView()
        bindObserver()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            val bundle = Bundle().apply {
                putString("articles", Gson().toJson(article))
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment2,
                bundle
            )
        }
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @SuppressLint("ShowToast")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.currentList[position]
                viewModel.deleteArticle(article = article)
                Snackbar.make(requireView(), "Successfully deleted article", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Undo") {
                            viewModel.saveArticle(article)
                        }
                        show()
                    }
            }
        }

        ItemTouchHelper(itemTouchCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        binding.rvSavedNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun bindObserver() {
        viewModel.getSaveArticles().observe(viewLifecycleOwner) {
            newsAdapter.submitList(it)
        }
    }

}