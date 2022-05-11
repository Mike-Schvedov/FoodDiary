package com.mikeschvedov.fooddiary.Presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.fooddiary.Data.Database.Entities.WeightEntry
import com.mikeschvedov.fooddiary.Logic.Adapters.WeightEntriesListAdapter
import com.mikeschvedov.fooddiary.Logic.AppViewModel
import com.mikeschvedov.fooddiary.Logic.AppViewModelFactory
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.Util.FoodApplication
import com.mikeschvedov.fooddiary.Util.TodaysDate
import com.mikeschvedov.fooddiary.databinding.ActivityWeightBinding
import com.mikeschvedov.whatieat.FoodEntriesListAdapter
import java.text.SimpleDateFormat
import java.util.*

class WeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeightBinding

    //Creating the view model
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as FoodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ------------- SETTING ADAPTER ------------- */
        // Setting the weight recyclerview adapter
          val adapter = WeightEntriesListAdapter()
          binding.recyclerviewWeight.adapter = adapter
          binding.recyclerviewWeight.layoutManager = LinearLayoutManager(this)

        /* ------------- OBSERVING FOR CHANGES IN ADAPTER TO UPDATE THE RECYCLERVIEW ------------- */
        observeChangeInWeightRecyclerView(adapter)
        /* ------------- CLICKING ON ADD NEW WEIGHT BUTTON ------------- */

        binding.addWeightButton.setOnClickListener {
            // Getting the input weight and converting to Double
            val weightInput = binding.weightInputEdittext.text.toString().toDouble()
            Log.e("WeightActivity", "THE WEIGHT INPUT IS : $weightInput")
            // Creating a new weight entry object
            val weightEntry = WeightEntry(weightInput, TodaysDate.todaysDate)
            Log.e("WeightActivity", "THE WEIGHT ENTRY IS : weight: ${weightEntry.weight} and date: ${weightEntry.date_added.time}")
            // Inserting the object into the database
            appViewModel.insertWeight(weightEntry)
            // Calling the observer again //TODO maybe not needed?
            observeChangeInWeightRecyclerView(adapter)
            // Deleting value in edit text
            binding.weightInputEdittext.text.clear()

        }
        /* ------------- CLICKING ON AN ITEM INSIDE THE RECYCLERVIEW (DELETE)------------- */
        adapter.setOnItemClickListener(object :
            WeightEntriesListAdapter.OnWeightClickListenerInterface {
            override fun onItemClick(position: Int) {
                // Create an AlertDialog
                val builder = AlertDialog.Builder(this@WeightActivity, R.style.MyDialogTheme)
                builder.setTitle("אזהרה")
                builder.setMessage("האם למחוק את הרשומה?")
                builder.setPositiveButton("כן") { dialog, which ->
                    appViewModel.deleteWeight(adapter.currentList[position])
                    adapter.notifyItemRemoved(position)
                    // Calling the observer again //TODO maybe not needed?
                    observeChangeInWeightRecyclerView(adapter)
                    Log.e("DELETED", "DELETED ITEM: $adapter.currentList[position]")
                }
                builder.setNegativeButton("לא") { dialog, which ->
                   Log.e("WeightActivity", "Deletion Canceled")
                }
                builder.show()
            }
        })



    } // Closing onCreate


    private fun observeChangeInWeightRecyclerView(
        passedAdapter: WeightEntriesListAdapter
    ) {
        appViewModel.getAllWeightEntries()
            .observe(this) { entries ->
                // Update the cached copy of the weight objects in the adapter.
                entries.let { passedAdapter.submitList(it) }
            }
    }





}