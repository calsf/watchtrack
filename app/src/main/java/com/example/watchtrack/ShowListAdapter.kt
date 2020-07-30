package com.example.watchtrack

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchtrack.database.Show
import com.example.watchtrack.databinding.ShowItemBinding

// ListAdapter will use ShowDiffCallback to determine what Shows have changed with ViewHolder
class ShowListAdapter : ListAdapter<Show, ShowListAdapter.ViewHolder>(ShowDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor
        (val binding: ShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show) {
            // Display item data
            binding.showTitle.text = item.title
            binding.seasonNum.text = item.season.toString()
            binding.episodeNum.text = item.episode.toString()
            binding.hours.text = item.hours.toString().padStart(2, '0')
            binding.minutes.text = item.minutes.toString().padStart(2, '0')
            binding.seconds.text = item.seconds.toString().padStart(2, '0')

            // Update data binding "show" variable in show_item
            binding.show = item
            // Execute any pending bindings
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ShowItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


// DiffUtil
class ShowDiffCallback : DiffUtil.ItemCallback<Show>() {
    // For detecting if an item was added, removed, or moved
    override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
        return oldItem.showId == newItem.showId
    }

    //Detect differences between oldItem and newItem,
    override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean {
        return oldItem == newItem
    }
}