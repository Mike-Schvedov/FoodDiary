package com.mikeschvedov.fooddiary.Logic

import androidx.lifecycle.*
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.Data.Repository.FoodRepository
import kotlinx.coroutines.launch
import java.util.*

class AppViewModel(val repository: FoodRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<FoodEntry>> = repository.allWords.asLiveData()

    // Get all Entries by Date (We also pass the date)
    fun getAllEntriesByDate (date: Date): LiveData<List<FoodEntry>>{
        return repository.getAllEntriesByDate(date).asLiveData()
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(food: FoodEntry) = viewModelScope.launch {
        repository.insert(food)
    }

    fun delete(food: FoodEntry) = viewModelScope.launch {
        repository.delete(food)
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