package com.mikeschvedov.fooddiary.Presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.fooddiary.Data.Database.Entities.FoodEntry
import com.mikeschvedov.fooddiary.Data.Database.Entities.WaterEntry
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
    val adapter = FoodEntriesListAdapter()

    val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    val slideOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.slide_open_anim) }
    val slideClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.slide_close_anim
        )
    }

    //Creating the view model
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as FoodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ------------- SAVING THE FIRST DATE THE APP WAS INSTALLED ------------- */
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
        /* ------------- SETTING ADAPTERS ------------- */
        // Setting the main recyclerview adapter
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        //Setting the water spinner adapter
        val spinnerItems = arrayListOf<String>("כוס שתייה קרה | 140 מ\"ל", "כוס זכוכית גדולה | 140 מ\"ל", "בקבוק חצי ליטר | 500 מ\"ל", "כמות מוגדרת | 50 מ\"ל", "כמות מוגדרת | 100 מ\"ל", "כמות מוגדרת | 250 מ\"ל")
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
            applicationContext,
            R.layout.simple_list_item_custom,
            spinnerItems
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.spinnerXml.adapter = spinnerAdapter
        /* ------------- ADD AN OBSERVER ON THE LIVEDATA RETURNED BY getAllEntries -------------*/
        // Get the data using the provided "dynamicTodayDate" (as String) (date selected by arrows)
        observeChangeInRecyclerView(adapter, dateToFormatted(dynamicTodayDate))
        /* ------------- SETTING VISUAL DATA ------------- */
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
        // We set the daily water amount (for the first date when the app opens)
        observeWaterChange(dateToFormatted(dynamicTodayDate))
        /* ------------- CLICKING ON THE ARROWS ------------- */
        // Clicking to go to Next Day
        binding.arrowBtnPrevDay.setOnClickListener {
            arrowConfiguration(adapter, -1)
        }
        // Clicking to go to Previous Day
        binding.arrowBtnNextDay.setOnClickListener {
            arrowConfiguration(adapter, 1)
        }
        /* ------------- CLICKING ON AN ITEM INSIDE THE RECYCLERVIEW ------------- */
        adapter.setOnItemClickListener(object :
            FoodEntriesListAdapter.onClickListenerInterface {
            override fun onItemClick(position: Int) {
                // Create an AlertDialog
                val builder = AlertDialog.Builder(this@MainActivity, R.style.MyDialogTheme)
                builder.setTitle("אזהרה")
                builder.setMessage("האם למחוק את הרשומה?")
                builder.setPositiveButton("כן") { dialog, which ->
                    appViewModel.delete(adapter.currentList[position])
                    adapter.notifyItemRemoved(position)
                    observeChangeInRecyclerView(adapter, dateToFormatted(dynamicTodayDate))
                    Log.e("DELETED", "DELETED ITEM: $adapter.currentList[position]")
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
        /* ------------- CLICKING ON THE FLOATING ACTION BUTTONS ------------- */
        binding.fab.setOnClickListener {
            appViewModel.onFloatingButtonClicked(
                binding,
                rotateClose,
                rotateOpen,
                slideClose,
                slideOpen
            )
        }
        binding.fabAddEntry.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEntryActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
            // So we trigger the fab to be closed
            appViewModel.onFloatingButtonClicked(
                binding,
                rotateClose,
                rotateOpen,
                slideClose,
                slideOpen
            )
        }
        binding.fabAddWater.setOnClickListener {
            // Making the layout visible
            binding.addWaterLayout.visibility = View.VISIBLE
            // So we trigger the fab to be closed
            appViewModel.onFloatingButtonClicked(
                binding,
                rotateClose,
                rotateOpen,
                slideClose,
                slideOpen
            )
            // Hiding the fab
            binding.fab.hide()
            binding.fab.isClickable = false
        }
        /* ------------- CLICKING ON THE ADD WATER SCREEN ------------- */
        //  Clicking on the add water screen "add" button
        binding.addButton.setOnClickListener {
            val selectedSpinnerItem = binding.spinnerXml.selectedItemPosition
            var waterQuantity = 0
            when(selectedSpinnerItem)
            {
                0 -> waterQuantity = 140 // כוס שתייה קרה - 140
                1 -> waterQuantity = 140 // כוס זכוכית גדולה - 140
                2 -> waterQuantity = 500 // בקבוק חצי ליטר - 500
                3 -> waterQuantity = 50 // כמות מוגדרת - 50
                4 -> waterQuantity = 100 // כמות מוגדרת - 100
                5 -> waterQuantity = 250 // כמות מוגדרת - 250
                else -> {
                    Log.e("MainActivity", "Clicked on an item that does not exist")
                }
            }
            Log.e("MainActivity", "WATER QUANTITY: $waterQuantity")
            // Insert the new Water Entry into the data base
            val waterEntry = WaterEntry(waterQuantity, dateToFormatted(dynamicTodayDate))
            appViewModel.insertWater(waterEntry)
            // Updating the view again
            observeWaterChange(dateToFormatted(dynamicTodayDate))
            // Closing the screen
            closingTheWaterScreen()
        }
        // -------- Clicking on the cancel button -------- //
        binding.closeButton.setOnClickListener {
            // Closing the screen
            closingTheWaterScreen()
        }
    } // Closing MainActivity

    private fun closingTheWaterScreen() {
        binding.addWaterLayout.visibility = View.GONE
        // Making the fab visible again
        binding.fab.show()
        binding.fab.isClickable = true
    }

    private fun observeWaterChange(relevantDate: String) {
        appViewModel.getAllWaterEntriesByDate(relevantDate)
            .observe(this) { entries ->
                Log.e("ENTRIES", "WATER ENTRIES: $entries")
                var sum: Int = 0
                for (item in entries) {
                    sum += item.waterQuantity
                }
                Log.e("MAIN", "THE SUM: $sum")
                binding.waterDrankTodayXml.text = sum.toString()
            }
    }

    private fun arrowConfiguration(adapter: FoodEntriesListAdapter, direction: Int) {
        dynamicTodayDate = TodaysDate.daysToAdd(
            direction,
            dynamicTodayDate
        )
        // Observing the RecyclerView
        observeChangeInRecyclerView(adapter, dateToFormatted(dynamicTodayDate))
        // Setting the New Date
        binding.todaysDateView.text = dateToFormatted(dynamicTodayDate)
        // Observing the Water Quantity
        observeWaterChange(dateToFormatted(dynamicTodayDate))
        // Checking again if yesterday is beyond the first day we installed the app, to hide the arrow.
        lifecycleScope.launch {
            appViewModel.decideArrowVisibility(
                applicationContext,
                binding,
                dateToFormatted(dynamicTodayDate)
            )
        }
    }

    private fun observeChangeInRecyclerView(
        passedAdapter: FoodEntriesListAdapter,
        relevantDate: String
    ) {
        appViewModel.getAllEntriesByDate(relevantDate)
            .observe(this) { entries ->
                // Update the cached copy of the words in the adapter.
                entries.let { passedAdapter.submitList(it) }
                adapter.notifyDataSetChanged()
                // We add all calories to totalCal
                var totalCal: Int = 0
                for (entry in entries) {
                    totalCal += entry.calories
                }
                binding.caloriesEatenTodayXml.text = totalCal.toString()
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
            adapter.notifyDataSetChanged()
            observeChangeInRecyclerView(adapter, dateToFormatted(dynamicTodayDate))
        } else {
            Log.d("Main", "user went back.")
        }
    }

    private fun dateToFormatted(dateObject: Date): String {
        return SimpleDateFormat(TodaysDate.DAY_FORMAT, Locale.getDefault()).format(dateObject)
    }

}