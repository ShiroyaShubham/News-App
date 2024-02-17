package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.model.Article


class NewsAdapter(private val onItemClick: (article: Article) -> Unit) :
    ListAdapter<Article, NewsAdapter.ViewHolder>(DiffUtils()) {
    inner class ViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.root.apply {
                Glide.with(this).load(article.urlToImage).into(binding.ivArticleImage)
                binding.tvSource.text = article.source.name
                binding.tvTitle.text = article.title
                binding.tvDescription.text = article.description
                binding.tvPublishedAt.text = article.publishedAt

                setOnClickListener {
                    onItemClick.invoke(article)
                }

            }
        }

    }

    class DiffUtils : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return newItem.id == oldItem.id
        }


        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return newItem == oldItem
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticlePreviewBinding.inflate(
            LayoutInflater.from(
                parent.context,
            ), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }


}