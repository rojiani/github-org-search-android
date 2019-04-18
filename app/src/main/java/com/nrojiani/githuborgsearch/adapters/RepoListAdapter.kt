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
import com.nrojiani.githuborgsearch.network.responsehandler.responseData
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel

/**
 * RecyclerView Adapter for the repos for an organization.
 */
class RepoListAdapter(
    viewModel: OrgDetailsViewModel,
    lifecycleOwner: LifecycleOwner,
    private val onRepoSelected: (Repo) -> Unit
) : RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

    private val TAG by lazy { this::class.java.simpleName }

    /**
     * The data source for the RecyclerView. Observes and mirrors the `topRepos` LiveData
     * in [OrgDetailsViewModel].
     */
    private val mostStarredRepos: MutableList<Repo> = ArrayList()

    init {
        // Subscribe to changes in the fetched repositories.
        viewModel.topRepos.observe(lifecycleOwner, Observer { apiResult ->
            Log.d(TAG, "(Observer) topRepos => $apiResult")
            mostStarredRepos.clear()

            apiResult.responseData?.let(mostStarredRepos::addAll)

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

    override fun getItemCount(): Int = mostStarredRepos.size

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

        /** Binds the data from the data source (a Repo) to the view. */
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
