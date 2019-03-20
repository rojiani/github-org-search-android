package com.nrojiani.githuborgsearch.ui.orgdetails

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.model.Repo

class RepoListAdapter(
    viewModel: OrgDetailsViewModel,
    lifecycleOwner: LifecycleOwner,
    private val onRepoSelected: (Repo) -> Unit
) : RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

    private val allRepos: MutableList<Repo> = ArrayList()
    private val mostStarredRepos: List<Repo>
        get() = allRepos.sortedByDescending { it.stars }
            .take(OrgDetailsViewModel.REPO_COUNT_TO_SHOW)

    private val TAG by lazy { this::class.java.simpleName }

    init {
        // Subscribe to changes in the fetched repositories.
        viewModel.getAllRepos().observe(lifecycleOwner, Observer { newRepoList ->
            Log.d(TAG, "(Observer) OrgDetailsViewModel getAllRepos() changed to $newRepoList")
            allRepos.clear()
            newRepoList?.let {
                allRepos.addAll(it)
            }
            // Notifies the attached observers that the underlying data has been changed and any
            // View reflecting the data set should refresh itself.
            notifyDataSetChanged()
        })
        // Sets whether the item ids are stable across changes to the underlying data.
        setHasStableIds(true)
    }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        // inflate the view for a single list item (repo)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_repo, parent, false)
        return RepoListAdapter.RepoViewHolder(view, onRepoSelected)
    }

    // Called by RecyclerView to display the data at the specified position.
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RepoListAdapter.RepoViewHolder, position: Int) =
        holder.bind(mostStarredRepos[position])

    override fun getItemCount(): Int = OrgDetailsViewModel.REPO_COUNT_TO_SHOW

    override fun getItemId(position: Int): Long = mostStarredRepos[position].id

    class RepoViewHolder(
        itemView: View,
        private val onRepoSelected: (Repo) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private lateinit var repo: Repo

        private val repoNameTextView: TextView = itemView.findViewById(R.id.repoNameTextView)
        private val repoDescriptionTextView: TextView = itemView.findViewById(R.id.repoDescriptionTextView)
        private val repoLanguageTextView: TextView = itemView.findViewById(R.id.repoLanguageTextView)
        private val forksTextView: TextView = itemView.findViewById(R.id.repoStarsTextView)
        private val starsTextView: TextView = itemView.findViewById(R.id.repoForksTextView)

        init {
            // Set click list listener for a list item
            itemView.setOnClickListener {
                if (repo != null) onRepoSelected(repo)
            }
        }

        fun bind(repo: Repo) {
            this.repo = repo
            repoNameTextView.text = repo.name
            repoDescriptionTextView.text = repo.description
            repoLanguageTextView.text = repo.language
            forksTextView.text = repo.forks.toString()
            starsTextView.text = repo.stars.toString()
        }
    }

}
