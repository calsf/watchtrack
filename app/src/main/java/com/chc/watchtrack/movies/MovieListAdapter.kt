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
    var selectedAll = false

    // When changed, MovieFragment will display a success or fail toast depending on true or false
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

    // When changed, MoviesFragment will enable actions to do something with the selected movies
    private var _moviesSelected = MutableLiveData<MutableList<MovieEntity>?>()
    val moviesSelected: LiveData<MutableList<MovieEntity>?> get() = _moviesSelected
    // Clear _moviesSelected list
    fun resetMoviesSelected() {
        _moviesSelected.value!!.clear()

        // Update movies selected value
        _moviesSelected.value = _moviesSelected.value
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _moviesSelected.value = ArrayList()
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor
        (val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieEntity, adapter: MovieListAdapter) {
            // Reset background color if not selected
            if (!adapter._moviesSelected.value!!.contains(item))
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
                // Remove from list if already selected, else add to list
                if (adapter._moviesSelected.value!!.contains(item))
                {
                    removeFromSelected(adapter._moviesSelected, item)
                }
                else
                {
                    // Hide expandable view if it is showing upon selecting
                    if (binding.expandableView.visibility == View.VISIBLE)
                    {
                        hideExpandableView()
                    }

                    addToSelected(adapter._moviesSelected, item)
                }

                // Update moviesSelected value
                adapter._moviesSelected.value = adapter._moviesSelected.value

                true
            }

            /*
            If selectedMovies list has at least 1 item, clicking on item will select/deselect item
            Else, clicking on item will show or hide expandable view to allow for quick edit
             */
            binding.cardView.setOnClickListener {
                //
                if (adapter._moviesSelected.value!!.size > 0) {
                    if (adapter._moviesSelected.value!!.contains(item))
                    {
                        removeFromSelected(adapter._moviesSelected, item)
                    }
                    else
                    {
                        addToSelected(adapter._moviesSelected, item)
                    }

                    // Update moviesSelected value
                    adapter._moviesSelected.value = adapter._moviesSelected.value
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
                    // Update status to false to trigger fail toast in MoviesFragment
                    adapter._updateStatus.value = false
                    adapter.resetUpdateStatus()
                }
            }

            // If all items are to be selected, check if this item is selected and add if not
            if (adapter.selectedAll)
            {
                if (!adapter._moviesSelected.value!!.contains(item))
                {
                    // Hide expandable view if it is showing upon selecting
                    if (binding.expandableView.visibility == View.VISIBLE)
                    {
                        hideExpandableView()
                    }

                    addToSelected(adapter._moviesSelected, item)
                }

                // Update movies selected value
                adapter._moviesSelected.value = adapter._moviesSelected.value
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

        // Remove item from selected movies and change background color
        private fun removeFromSelected
                    (moviesSelected: MutableLiveData<MutableList<MovieEntity>?>, item: MovieEntity)
        {
            moviesSelected.value!!.remove(item)
            binding.cardView.setBackgroundColor(Color.WHITE)
        }

        // Add item to selected movies and change background color
        private fun addToSelected(moviesSelected: MutableLiveData<MutableList<MovieEntity>?>, item: MovieEntity) {
            moviesSelected.value!!.add(item)
            binding.cardView.setBackgroundColor(
                ContextCompat.getColor(binding.root.context,
                    R.color.selectedItem
                ))
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