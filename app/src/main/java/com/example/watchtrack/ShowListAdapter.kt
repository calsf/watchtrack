package com.example.watchtrack

import android.graphics.Color
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchtrack.database.Show
import com.example.watchtrack.databinding.ShowItemBinding
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.lang.NumberFormatException

const val MAX_NUM_INPUT = 1000

// ListAdapter will use ShowDiffCallback to determine what Shows have changed with ViewHolder
class ShowListAdapter : ListAdapter<Show, ShowListAdapter.ViewHolder>(ShowDiffCallback()) {
    // When changed, ShowFragment will display a success or fail toast depending on true or false
    private var _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> get() = _updateStatus
    // Reset _updateStatus value after changing
    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    // When changed, ShowFragment will update the item in the database
    private var _showUpdate = MutableLiveData<Show?>()
    val showUpdate: LiveData<Show?> get() = _showUpdate
    // Reset _showUpdate value after changing
    fun resetShowUpdate() {
        _showUpdate.value = null
    }

    // When changed, ShowsFragment will enable actions to do something with the selected shows
    private var _showsSelected = MutableLiveData<MutableList<Show>?>()
    val showsSelected: LiveData<MutableList<Show>?> get() = _showsSelected
    // Clear _showsSelected list
    fun resetShowsSelected() {
        _showsSelected.value!!.clear()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _showsSelected.value = ArrayList()
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor
        (val binding: ShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show, adapter: ShowListAdapter) {
            // When click and held on cardView, select the item and add to list of selected
            binding.cardView.isClickable = true
            binding.cardView.setOnLongClickListener {
                // Remove from list if already selected, else add to list
                if (adapter._showsSelected.value!!.contains(item))
                {
                    removeFromSelected(adapter._showsSelected, item)
                }
                else
                {
                    // Hide expandable view if it is showing
                    hideExpandableView(binding.expandCollapseBtn)

                    adapter._showsSelected.value!!.add(item)
                    binding.cardView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.selectedItem))
                }

                // Update shows selected value
                adapter._showsSelected.value = adapter._showsSelected.value

                true
            }

            // Once selected, any further clicks should deselect and remove from selected
            binding.cardView.setOnClickListener {
                if (adapter._showsSelected.value!!.contains(item))
                {
                    removeFromSelected(adapter._showsSelected, item)

                    // Update shows selected value
                    adapter._showsSelected.value = adapter._showsSelected.value
                }
            }

            // Set onClick listener to expand and collapse view for show item
            binding.expandCollapseBtn.setOnClickListener(){
                if (binding.expandableView.visibility == View.GONE)
                {
                    // Remove from list if currently selected when trying to expand
                    if (adapter._showsSelected.value!!.contains(item))
                    {
                        removeFromSelected(adapter._showsSelected, item)

                        // Update shows selected value
                        adapter._showsSelected.value = adapter._showsSelected.value
                    }

                    showExpandableView(it)
                }
                else
                {
                    hideExpandableView(it)
                }
            }

            // Update data binding "show" variable in show_item
            binding.show = item

            // Initialize and set expandable view input values to item current values only once
            binding.expandSeasonNumber.setText(item.season.toString())
            binding.expandEpisodeNumber.setText(item.episode.toString())
            binding.hours.setText(item.hours.toString().padStart(2, '0'))
            binding.minutes.setText(item.minutes.toString().padStart(2, '0'))
            binding.seconds.setText(item.seconds.toString().padStart(2, '0'))

            // Assign the EditTextTimes
            binding.hours.hours = binding.hours
            binding.hours.minutes = binding.minutes
            binding.hours.seconds = binding.seconds
            binding.minutes.hours = binding.hours
            binding.minutes.minutes = binding.minutes
            binding.minutes.seconds = binding.seconds
            binding.seconds.hours = binding.hours
            binding.seconds.minutes = binding.minutes
            binding.seconds.seconds = binding.seconds

            // Set onClick listener to add 1 and subtract_box 1 to season input
            binding.addSeasonBtn.setOnClickListener {
                addOne(binding.expandSeasonNumber, MAX_NUM_INPUT)
            }
            binding.subtractSeasonBtn.setOnClickListener {
                subOne(binding.expandSeasonNumber, 0)
            }

            // Set onClick listener to add 1 and subtract_box 1 to episode input
            binding.addEpisodeBtn.setOnClickListener {
                addOne(binding.expandEpisodeNumber, MAX_NUM_INPUT)
            }
            binding.subtractEpisodeBtn.setOnClickListener {
                subOne(binding.expandEpisodeNumber, 0)
            }

            // Update show item with inputs in the expanded view
            binding.updateBtn.setOnClickListener {
                try {
                    item.season = binding.expandSeasonNumber.text.toString().toInt()
                    item.episode = binding.expandEpisodeNumber.text.toString().toInt()
                    item.hours = binding.hours.text.toString().toInt()
                    item.minutes = binding.minutes.text.toString().toInt()
                    item.seconds = binding.seconds.text.toString().toInt()
                    binding.show = item // Update the show binding so changes are displayed

                    // Update status to true to trigger success toast in ShowsFragment
                    adapter._updateStatus.value = true
                    adapter.resetUpdateStatus()

                    // Update show so that ShowsFragment knows to update the show in database
                    adapter._showUpdate.value = item
                    adapter.resetShowUpdate()
                }
                catch (nfe: NumberFormatException)
                {
                    // Update status to false to trigger fail toast in ShowsFragment
                    adapter._updateStatus.value = false
                    adapter.resetUpdateStatus()
                }
            }

            // Execute any pending bindings
            binding.executePendingBindings()
        }

        // Add one to the text in a TextView
        private fun addOne(view: TextView, limit: Int)
        {
            try {
                // Make sure adding 1 will not go over limit
                var curr = view.text.toString().toInt()
                if (curr + 1 < limit) {
                    view.text = (curr + 1).toString()
                }
            }
            catch (nfe: NumberFormatException)
            {
                view.text = "1"
            }
        }

        // Subtract one from the text in a TextView
        private fun subOne(view: TextView, min: Int)
        {
            try {
                // Make sure subtracting 1 does not go into negatives
                var curr = view.text.toString().toInt()
                if (curr - 1 >= min) {
                    view.text = (curr - 1).toString()
                }
            }
            catch (nfe: NumberFormatException)
            {
                view.text = "1"
            }
        }

        // Hide expandable view and change expand button image
        private fun hideExpandableView(view: View) {
            binding.expandableView.visibility = View.GONE
            binding.line.visibility = View.VISIBLE
            binding.expandedLine.visibility = View.GONE
            view.setBackgroundResource(R.drawable.arrow_down);
        }

        // Show expandable view and change expand button image
        private fun showExpandableView(view: View) {
            binding.expandableView.visibility = View.VISIBLE
            binding.line.visibility = View.GONE
            binding.expandedLine.visibility = View.VISIBLE
            view.setBackgroundResource(R.drawable.arrow_up);
        }

        // Remove item from selected shows and change background color
        private fun removeFromSelected(showsSelected: MutableLiveData<MutableList<Show>?>, item: Show) {
            showsSelected.value!!.remove(item)
            binding.cardView.setBackgroundColor(Color.WHITE)
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
