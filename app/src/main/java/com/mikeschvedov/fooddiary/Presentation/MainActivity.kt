package com.mikeschvedov.fooddiary.Presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.Data.Repository.DataStoreRepository
import com.mikeschvedov.fooddiary.Logic.AppViewModel
import com.mikeschvedov.fooddiary.Logic.AppViewModelFactory
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.Util.FoodApplication
import com.mikeschvedov.fooddiary.Util.TodaysDate
import com.mikeschvedov.fooddiary.databinding.ActivityMainBinding
import com.mikeschvedov.whatieat.FoodEntriesListAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val newWordActivityRequestCode = 1

    private var dynamicTodayDate: Date = TodaysDate.todaysDate
    private var dailyTotalCalories: Int = 0


    //Creating the view model
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as FoodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ------------- Saving the First Date the App was installed ------------- */
        // If first date already exists in storage (Flow<Boolean> == true) then do nothing, else add this date into storage
        lifecycleScope.launch {
            DataStoreRepository.readIsFirstDateExists(applicationContext).collect { flowBoolean ->
                if (flowBoolean) {
                    //the first date already exists in storage
                } else {
                    DataStoreRepository.setFirstDate(
                        applicationContext,
                        dateToFormatted(dynamicTodayDate)
                    )
                    // Now after we added the first date, we set that is now exists for the next time
                    DataStoreRepository.setIsFirstDateExists(applicationContext, true)
                }
            }
        }

        /* ------------- Setting the RecyclerView Adapter ------------- */
        val adapter = FoodEntriesListAdapter()
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        /* ------------- Add an observer on the LiveData returned by getAllEntries.
        The onChanged() method fires when the observed data changes and the activity is in the foreground. ------------- */
        // Get the data using the provided "dynamicTodayDate" (as String) (date selected by arrows)
        observeChangeInRecyclerView(adapter, dateToFormatted(dynamicTodayDate))

        /* ------------- When we click on an Item inside the RecyclerView ------------- */
        adapter.setOnItemClickListener(object :
            FoodEntriesListAdapter.onClickListenerInterface {
            override fun onItemClick(position: Int) {

                // Create an AlertDialog
                val builder = AlertDialog.Builder(this@MainActivity, R.style.MyDialogTheme)
                builder.setTitle("אזהרה")
                builder.setMessage("האם למחוק את הרשומה?")
                builder.setPositiveButton("כן") { dialog, which ->
                    appViewModel.delete(adapter.currentList[position])
                    println("Deleted: ${adapter.currentList[position]}")
                }
                builder.setNegativeButton("לא") { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        "Canceled", Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()

            }
        })

        /* ------------- Clicking on the Floating Action Button ------------- */
        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEntryActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        /* ------------- Setting The Date and Day Changing Arrows ------------- */
        // We set the current date to be displayed as Text
        binding.todaysDateView.text = TodaysDate.todaysDateFormatted
        // We need to check if yesterday is beyond the first day we installed the app, to hide the arrow.
        lifecycleScope.launch {
            appViewModel.decideArrowVisibility(
                applicationContext,
                binding,
                dateToFormatted(dynamicTodayDate)
            )
        }
        // Clicking to go to Next Day
        binding.arrowBtnPrevDay.setOnClickListener {
            arrowConfiguration(adapter, -1)
        }
        // Clicking to go to Previous Day
        binding.arrowBtnNextDay.setOnClickListener {
            arrowConfiguration(adapter, 1)
        }

    } // Closing MainActivity


    private fun arrowConfiguration(adapter: FoodEntriesListAdapter, direction: Int) {
        dynamicTodayDate = TodaysDate.daysToAdd(
            direction,
            dynamicTodayDate
        )
        observeChangeInRecyclerView(adapter, dateToFormatted(dynamicTodayDate))
        binding.todaysDateView.text = dateToFormatted(dynamicTodayDate)
        // Checking again if yesterday is beyond the first day we installed the app, to hide the arrow.
        lifecycleScope.launch {
            appViewModel.decideArrowVisibility(
                applicationContext,
                binding,
                dateToFormatted(dynamicTodayDate)
            )
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun observeChangeInRecyclerView(adapter: FoodEntriesListAdapter, relevantDate: String) {

        appViewModel.getAllEntriesByDate(relevantDate)
            .observe(this) { entries ->
                // Update the cached copy of the words in the adapter.
                entries.let { adapter.submitList(it) }
                // We add all calories to totalCal
                var totalCal: Int = 0
                for (entry in entries) {
                    totalCal += entry.calories
                }
                binding.caloriesEatenTodayXml.text = totalCal.toString()
                if (totalCal < 1000) {
                    binding.caloriesEatenTodayXml.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.green
                        )
                    )
                } else if (totalCal in 1001..1499) {
                    binding.caloriesEatenTodayXml.setTextColor(Color.YELLOW)
                } else {
                    binding.caloriesEatenTodayXml.setTextColor(Color.RED)
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {

            //initializing variables to store our extra results
            var tempName = ""
            var tempCalories = 0
            var tempImage = 0

            // Getting name extra
            intentData?.getStringExtra("name_extra")?.let { reply ->
                tempName = reply
            }
            // Getting calories extra
            intentData?.getIntExtra("calories_extra", 0)?.let { reply ->
                tempCalories = reply
            }
            // Getting image extra
            intentData?.getIntExtra("image_extra", 0)?.let { reply ->
                tempImage = reply
            }

            //Creating a temp Food Entry item and passing the extras into it
            val foodEntry =
                FoodEntry(tempName, tempCalories, tempImage, dateToFormatted(dynamicTodayDate))
            //Add the Food Entry into the Database
            appViewModel.insert(foodEntry)

        } else {
            Log.d("Main", "user went back.")
        }
    }

    private fun dateToFormatted(dateObject: Date): String {
        return SimpleDateFormat(TodaysDate.DAY_FORMAT, Locale.getDefault()).format(dateObject)
    }

}