package com.kucingselfie.jetpackdipractice.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.kucingselfie.jetpackdipractice.AppExecutors
import com.kucingselfie.jetpackdipractice.R
import com.kucingselfie.jetpackdipractice.databinding.RepoItemBinding
import com.kucingselfie.jetpackdipractice.vo.Repo

class RepoListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val showFullname: Boolean,
    private val repoClickBack: ((Repo) -> Unit)?
) : DataBoundListAdapter<Repo, RepoItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.owner == newItem.owner && oldItem.name == newItem.name
        }
        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.description == newItem.description && oldItem.stars == newItem.stars
        }
    }
) {
    override fun createBinding(parent: ViewGroup): RepoItemBinding {
        val binding = DataBindingUtil.inflate<RepoItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.repo_item,
            parent, false, dataBindingComponent
        )
        binding.showFullName = showFullname
        binding.root.setOnClickListener {
            binding.repo?.let {
                repoClickBack?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: RepoItemBinding, item: Repo) {
        binding.repo = item
    }
}