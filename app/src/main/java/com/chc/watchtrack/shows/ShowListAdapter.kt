package com.chc.watchtrack.shows

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chc.watchtrack.R
import com.chc.watchtrack.database.ShowEntity
import com.chc.watchtrack.databinding.ShowItemBinding
import java.lang.NumberFormatException

const val MAX_NUM_INPUT = 1000

// ListAdapter will use ShowDiffCallback to determine what Shows have changed with ViewHolder
class ShowListAdapter : ListAdapter<ShowEntity, ShowListAdapter.ViewHolder>(ShowDiffCallback())
{
    var selectedAll = false

    // When changed, ShowFragment will display a success or fail toast depending on true or false
    private var _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> get() = _updateStatus
    // Reset _updateStatus value after changing
    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    // When changed, ShowFragment will update the item in the database
    private var _showUpdate = MutableLiveData<ShowEntity?>()
    val showEntityUpdate: LiveData<ShowEntity?> get() = _showUpdate
    // Reset _showUpdate value after changing
    fun resetShowUpdate() {
        _showUpdate.value = null
    }

    // When changed, ShowsFragment will enable actions to do something with the selected shows
    private var _showsSelected = MutableLiveData<MutableList<ShowEntity>?>()
    val showsSelected: LiveData<MutableList<ShowEntity>?> get() = _showsSelected
    // Clear _showsSelected list
    fun resetShowsSelected() {
        _showsSelected.value!!.clear()

        // Update shows selected value
        _showsSelected.value = _showsSelected.value
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
        fun bind(item: ShowEntity, adapter: ShowListAdapter) {
            // Reset background color if not selected
            if (!adapter._showsSelected.value!!.contains(item))
            {
                binding.cardView.setBackgroundColor(Color.WHITE)
            }

            // Update data binding "show" variable in show_item
            binding.show = item

            // Assign the EditTextTimes, setEditTexts ensures they are only assigned once
            binding.hours.setEditTexts(binding.hours, binding.minutes, binding.seconds)
            binding.minutes.setEditTexts(binding.hours, binding.minutes, binding.seconds)
            binding.seconds.setEditTexts(binding.hours, binding.minutes, binding.seconds)

            // Initialize and set expandable view input values to item current values
            binding.expandSeasonNumber.setText(item.season.toString())
            binding.expandEpisodeNumber.setText(item.episode.toString())
            binding.hours.setText(item.hours.toString().padStart(3, '0'))
            binding.minutes.setText(item.minutes.toString().padStart(3, '0'))
            binding.seconds.setText(item.seconds.toString().padStart(3, '0'))

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
                    // Hide expandable view if it is showing upon selecting
                    if (binding.expandableView.visibility == View.VISIBLE)
                    {
                        hideExpandableView()
                    }

                    addToSelected(adapter._showsSelected, item)
                }

                // Update shows selected value
                adapter._showsSelected.value = adapter._showsSelected.value

                true
            }

            /*
            If selectedShows list has at least 1 item, clicking on item will select/deselect item
            Else, clicking on item will show or hide expandable view to allow for quick edit
             */
            binding.cardView.setOnClickListener {
                //
                if (adapter._showsSelected.value!!.size > 0) {
                    if (adapter._showsSelected.value!!.contains(item))
                    {
                        removeFromSelected(adapter._showsSelected, item)
                    }
                    else
                    {
                        addToSelected(adapter._showsSelected, item)
                    }

                    // Update shows selected value
                    adapter._showsSelected.value = adapter._showsSelected.value
                }
                else
                {
                    if (binding.expandableView.visibility == View.GONE)
                    {
                        showExpandableView()
                    } else
                    {
                        hideExpandableView()
                    }
                }
            }

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
                    binding.show = item // Update show binding

                    // Update status to true to trigger success toast in ShowsFragment
                    adapter._updateStatus.value = true
                    adapter.resetUpdateStatus()

                    // Update show so that ShowsFragment knows to update the show in database
                    adapter._showUpdate.value = item
                    adapter.resetShowUpdate()

                    // Hide expandable view upon successful update
                    hideExpandableView()
                }
                catch (nfe: NumberFormatException)
                {
                    // Update status to false to trigger fail toast in ShowsFragment
                    adapter._updateStatus.value = false
                    adapter.resetUpdateStatus()
                }
            }

            // If all items are to be selected, check if this item is selected and add if not
            if (adapter.selectedAll)
            {
                if (!adapter._showsSelected.value!!.contains(item))
                {
                    // Hide expandable view if it is showing upon selecting
                    if (binding.expandableView.visibility == View.VISIBLE)
                    {
                        hideExpandableView()
                    }

                    addToSelected(adapter._showsSelected, item)
                }

                // Update shows selected value
                adapter._showsSelected.value = adapter._showsSelected.value
            }

            // Execute any pending bindings
            binding.executePendingBindings()
        }

        // Add one to the text in a TextView
        private fun addOne(view: TextView, limit: Int)
        {
            try {
                // Make sure adding 1 will not go over limit
                val curr = view.text.toString().toInt()
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
                val curr = view.text.toString().toInt()
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
        private fun hideExpandableView() {
            binding.expandableView.visibility = View.GONE
            binding.line.visibility = View.VISIBLE
            binding.expandedLine.visibility = View.GONE
        }

        // Show expandable view and change expand button image
        private fun showExpandableView() {
            binding.expandableView.visibility = View.VISIBLE
            binding.line.visibility = View.GONE
            binding.expandedLine.visibility = View.VISIBLE
        }

        // Remove item from selected shows and change background color
        private fun removeFromSelected
                    (showsSelected: MutableLiveData<MutableList<ShowEntity>?>, item: ShowEntity)
        {
            showsSelected.value!!.remove(item)
            binding.cardView.setBackgroundColor(Color.WHITE)
        }

        // Add item to selected shows and change background color
        private fun addToSelected(showsSelected: MutableLiveData<MutableList<ShowEntity>?>, item: ShowEntity) {
            showsSelected.value!!.add(item)
            binding.cardView.setBackgroundColor(
                ContextCompat.getColor(binding.root.context,
                    R.color.selectedItem
                ))
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ShowItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(
                    binding
                )
            }
        }
    }
}

// DiffUtil
class ShowDiffCallback : DiffUtil.ItemCallback<ShowEntity>() {
    // For detecting if an item was added, removed, or moved
    override fun areItemsTheSame(oldItem: ShowEntity, newItem: ShowEntity): Boolean {
        return oldItem.showId == newItem.showId
    }

    //Detect differences between oldItem and newItem,
    override fun areContentsTheSame(oldItem: ShowEntity, newItem: ShowEntity): Boolean {
        return oldItem == newItem
    }
}
