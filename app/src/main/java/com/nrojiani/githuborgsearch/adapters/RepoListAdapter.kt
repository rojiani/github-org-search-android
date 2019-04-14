package com.nrojiani.githuborgsearch.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.extensions.formatted
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel

/**
 * RecyclerView Adapter for the repos for an organization.
 */
class RepoListAdapter(
    viewModel: OrgDetailsViewModel,
    lifecycleOwner: LifecycleOwner,
    private val onRepoSelected: (Repo) -> Unit
) : RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

    /** All repositories for an organization */
    private val allRepos: MutableList<Repo> = ArrayList()

    /** The most-starred repos */
    private val mostStarredRepos: List<Repo>
        get() = allRepos.sortedByDescending { it.stars }
            .take(OrgDetailsViewModel.NUM_REPOS_TO_DISPLAY)

    private val TAG by lazy { this::class.java.simpleName }

    init {
        // Subscribe to changes in the fetched repositories.
        viewModel.allRepos.observe(lifecycleOwner, Observer { newRepoList ->
            Log.d(TAG, "(Observer) OrgDetailsViewModel getAllRepos() changed to $newRepoList")
            allRepos.clear()
            newRepoList?.let(allRepos::addAll)

            // Notifies the attached observers that the underlying data has been changed and any
            // View reflecting the data set should refresh itself.
            notifyDataSetChanged()
        })
        // Sets whether the item ids are stable across changes to the underlying data.
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_repo, parent, false)
        return RepoViewHolder(view, onRepoSelected)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) =
        holder.bind(mostStarredRepos[position])

    override fun getItemCount(): Int = OrgDetailsViewModel.NUM_REPOS_TO_DISPLAY

    override fun getItemId(position: Int): Long = mostStarredRepos[position].id

    class RepoViewHolder(
        itemView: View,
        private val onRepoSelected: (Repo) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private lateinit var repo: Repo

        private val repoNameTextView: TextView = itemView.findViewById(R.id.repoNameTextView)
        private val repoDescriptionTextView: TextView =
            itemView.findViewById(R.id.repoDescriptionTextView)
        private val repoLanguageChip: TextView = itemView.findViewById(R.id.repoLanguageChip)
        private val starsChip: TextView = itemView.findViewById(R.id.repoStarsChip)
        private val forksChip: TextView = itemView.findViewById(R.id.repoForksChip)

        init {
            itemView.setOnClickListener {
                onRepoSelected(repo)
            }
        }

        fun bind(repo: Repo) {
            this.repo = repo
            repoNameTextView.text = repo.name
            repoDescriptionTextView.text = repo.description
            if (repo.language.isNullOrBlank()) {
                repoLanguageChip.isGone = true
            } else {
                repoLanguageChip.isVisible = true
                repoLanguageChip.text = repo.language
            }
            forksChip.text = repo.forks.formatted()
            starsChip.text = repo.stars.formatted()
        }
    }

}
