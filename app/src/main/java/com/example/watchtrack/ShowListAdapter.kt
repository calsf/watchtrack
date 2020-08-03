package com.example.watchtrack

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchtrack.database.Show
import com.example.watchtrack.databinding.ShowItemBinding
import java.lang.NumberFormatException

const val MAX_NUM_INPUT = 1000

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
            // Update data binding "show" variable in show_item
            binding.show = item

            // Set onClick listener to add 1 to season input
            binding.addSeasonBtn.setOnClickListener {
                try {
                    // Make sure adding 1 will not go over num limit for input
                    var currSeason = binding.expandSeasonNumber.text.toString().toInt()
                    if (currSeason + 1 < MAX_NUM_INPUT) {
                        binding.expandSeasonNumber.setText((currSeason + 1).toString())
                    }
                }
                catch (nfe: NumberFormatException)
                {
                    binding.expandSeasonNumber.setText("1")
                }
            }

            // Set onClick listener to subtract 1 from season input
            binding.subtractSeasonBtn.setOnClickListener {
                try {
                    // Make sure subtracting 1 does not go into negatives
                    var currSeason = binding.expandSeasonNumber.text.toString().toInt()
                    if (currSeason - 1 >= 0) {
                        binding.expandSeasonNumber.setText((currSeason - 1).toString())
                    }
                }
                catch (nfe: NumberFormatException)
                {
                    binding.expandSeasonNumber.setText("1")
                }
            }

            // Update show item with inputs in the expanded view
            binding.updateBtn.setOnClickListener {
                item.season = binding.expandSeasonNumber.text.toString().toInt()
                binding.show = item
            }

            // Execute any pending bindings
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ShowItemBinding.inflate(layoutInflater, parent, false)

                // Set onClick listener to expand and collapse view for show item
                binding.expandCollapseBtn.setOnClickListener(){
                    if (binding.expandableView.visibility == View.GONE)
                    {
                        binding.expandableView.visibility = View.VISIBLE
                        binding.line.visibility = View.GONE
                        binding.expandedLine.visibility = View.VISIBLE
                        it.setBackgroundResource(R.drawable.arrow_up);
                    }
                    else
                    {
                        binding.expandableView.visibility = View.GONE
                        binding.line.visibility = View.VISIBLE
                        binding.expandedLine.visibility = View.GONE
                        it.setBackgroundResource(R.drawable.arrow_down);
                    }
                }

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
