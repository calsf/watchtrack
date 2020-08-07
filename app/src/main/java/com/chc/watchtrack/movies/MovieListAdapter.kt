package com.chc.watchtrack.movies

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chc.watchtrack.R
import com.chc.watchtrack.database.MovieEntity
import com.chc.watchtrack.databinding.MovieItemBinding
import java.lang.NumberFormatException

// ListAdapter will use MovieDiffCallback to determine what Movies have changed with ViewHolder
class MovieListAdapter : ListAdapter<MovieEntity, MovieListAdapter.ViewHolder>(MovieDiffCallback())
{
    lateinit var selectedItems: MutableList<MovieEntity?>
    var selectedMode = false

    // When changed, MoviesFragment will display a success or fail toast depending on true or false
    private var _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> get() = _updateStatus
    // Reset _updateStatus value after changing
    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    // When changed, MoviesFragment will update the item in the database
    private var _movieUpdate = MutableLiveData<MovieEntity?>()
    val movieEntityUpdate: LiveData<MovieEntity?> get() = _movieUpdate
    // Reset _movieUpdate value after changing
    fun resetMovieUpdate() {
        _movieUpdate.value = null
    }

    // When changed, MoviesFragment will add or remove from selected
    private var _selectedItem = MutableLiveData<ViewHolder?>()
    val selectedItem: LiveData<ViewHolder?> get() = _selectedItem

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor
        (val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieEntity, adapter: MovieListAdapter) {
            // Check if current item is selected and set view background accordingly
            if (adapter.selectedItems.contains(item))
            {
                binding.cardView.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context,
                        R.color.selectedItem
                    ))
            }
            else
            {
                binding.cardView.setBackgroundColor(Color.WHITE)
            }

            // Update data binding "movie" variable in movie_item
            binding.movie = item

            // Assign the EditTextTimes, setEditTexts ensures they are only assigned once
            binding.hours.setEditTexts(binding.hours, binding.minutes, binding.seconds)
            binding.minutes.setEditTexts(binding.hours, binding.minutes, binding.seconds)
            binding.seconds.setEditTexts(binding.hours, binding.minutes, binding.seconds)

            // Initialize and set expandable view input values to item current values
            binding.hours.setText(item.hours.toString().padStart(3, '0'))
            binding.minutes.setText(item.minutes.toString().padStart(3, '0'))
            binding.seconds.setText(item.seconds.toString().padStart(3, '0'))

            // When click and held on cardView, select the item and add to list of selected
            binding.cardView.isClickable = true
            binding.cardView.setOnLongClickListener {
                adapter._selectedItem.value = this

                true
            }

            /*
            If selectedMovies list has at least 1 item, then we are in select mode
            In selectedMode, clicking on item will select/deselect item
            Else, clicking on item will show or hide expandable view to allow for quick edit
             */
            binding.cardView.setOnClickListener {
                //
                if (adapter.selectedMode) {
                    adapter._selectedItem.value = this
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

            // Update movie item with inputs in the expanded view
            binding.updateBtn.setOnClickListener {
                try {
                    item.hours = binding.hours.text.toString().toInt()
                    item.minutes = binding.minutes.text.toString().toInt()
                    item.seconds = binding.seconds.text.toString().toInt()
                    binding.movie = item // Update movie binding

                    // Update status to true to trigger success toast in MoviesFragment
                    adapter._updateStatus.value = true
                    adapter.resetUpdateStatus()

                    // Update movie so that MoviesFragment knows to update the movie in database
                    adapter._movieUpdate.value = item
                    adapter.resetMovieUpdate()

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

            // Execute any pending bindings
            binding.executePendingBindings()
        }

        // Hide expandable view and change expand button image
        private fun hideExpandableView() {
            binding.expandableView.visibility = View.GONE
            binding.line.visibility = View.VISIBLE
            binding.expandedLine.visibility = View.GONE
            focusView()
        }

        // Show expandable view and change expand button image
        private fun showExpandableView() {
            binding.expandableView.visibility = View.VISIBLE
            binding.line.visibility = View.GONE
            binding.expandedLine.visibility = View.VISIBLE
            focusView()
        }

        // Focus on view when clicked on
        private fun focusView() {
            binding.cardView.post {
                binding.cardView.parent.requestChildFocus(
                    binding.cardView,
                    binding.cardView
                )
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    MovieItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(
                    binding
                )
            }
        }
    }
}

// DiffUtil
class MovieDiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
    // For detecting if an item was added, removed, or moved
    override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
        return oldItem.movieId == newItem.movieId
    }

    //Detect differences between oldItem and newItem,
    override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
        return oldItem == newItem
    }
}
