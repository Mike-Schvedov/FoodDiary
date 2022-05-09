package com.mikeschvedov.fooddiary.Logic

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.lifecycle.*
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.Data.Repository.DataStoreRepository
import com.mikeschvedov.fooddiary.Data.Repository.FoodRepository
import com.mikeschvedov.fooddiary.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class AppViewModel(val repository: FoodRepository) : ViewModel() {


    /* -------------------------------- RELATED TO DATABASE --------------------------------  */
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<FoodEntry>> = repository.allWords.asLiveData()

    // Get all Entries by Date (We also pass the date)
    fun getAllEntriesByDate (date: String): LiveData<List<FoodEntry>>{
        return repository.getAllEntriesByDate(date).asLiveData()
    }

    fun insert(food: FoodEntry) = viewModelScope.launch {
        repository.insert(food)
    }

    fun delete(food: FoodEntry) = viewModelScope.launch {
        repository.delete(food)
    }

    /* -------------------------------- RELATED TO FLOATING ACTION BUTTONS --------------------------------  */

    private var fabClicked = false

    fun triggerFabClick(){
        fabClicked = !fabClicked
    }

    fun onFloatingButtonClicked(
        binding: ActivityMainBinding,
        rotateClose: Animation,
        rotateOpen: Animation,
        slideClose: Animation,
        slideOpen: Animation
    ) {
        setVisibility(fabClicked, binding)
        setAnimation(fabClicked, binding, rotateClose, rotateOpen, slideClose, slideOpen)
        // if it is true make false
        fabClicked = !fabClicked
    }

    private fun setAnimation(
        clicked: Boolean,
        binding: ActivityMainBinding,
        rotateClose: Animation,
        rotateOpen: Animation,
        slideClose: Animation,
        slideOpen: Animation
    ) {
        if(!clicked){
            binding.fabAddEntry.startAnimation(slideOpen)
            binding.fabAddWater.startAnimation(slideOpen)
            binding.fabAddWeight.startAnimation(slideOpen)
            //main fab
            binding.fab.startAnimation(rotateOpen)
        }else{
            binding.fabAddEntry.startAnimation(slideClose)
            binding.fabAddWater.startAnimation(slideClose)
            binding.fabAddWeight.startAnimation(slideClose)
            //main fab
            binding.fab.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean, binding: ActivityMainBinding) {
        if(!clicked){
            binding.fabAddEntry.visibility = View.VISIBLE
            binding.fabAddWater.visibility = View.VISIBLE
            binding.fabAddWeight.visibility = View.VISIBLE

        }else{
            binding.fabAddEntry.visibility = View.INVISIBLE
            binding.fabAddWater.visibility = View.INVISIBLE
            binding.fabAddWeight.visibility = View.INVISIBLE
        }
    }

    /* -------------------------------- RELATED TO OTHER --------------------------------  */

    suspend fun decideArrowVisibility(context: Context, binding: ActivityMainBinding, date: String ) {
        DataStoreRepository.readFirstDate(context).collect { stringBoolean ->
            // if the string we get for DataStore is equals the current date
            if (stringBoolean == date) {
                println("COMPARING FOR STORAGE: if stringBoolen(Date the app was installed): $stringBoolean equals currentDate $date}")

                binding.arrowBtnPrevDay.visibility = View.INVISIBLE
            } else {
                binding.arrowBtnPrevDay.visibility = View.VISIBLE
            }
        }
    }
}



class AppViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}