package com.mikeschvedov.fooddiary.Presentation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.Logic.AppViewModel
import com.mikeschvedov.fooddiary.Logic.AppViewModelFactory
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.Util.FoodApplication
import com.mikeschvedov.fooddiary.Util.TodaysDate
import com.mikeschvedov.fooddiary.databinding.ActivityMainBinding
import com.mikeschvedov.whatieat.FoodEntriesListAdapter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //TODO working on laptop 3/5/22

    private lateinit var binding: ActivityMainBinding

    private val newWordActivityRequestCode = 1

    //Creating the view model
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as FoodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = FoodEntriesListAdapter()

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        //
        adapter.setOnItemClickListener(object: FoodEntriesListAdapter.onClickListenerInterface{
            override fun onItemClick(position: Int) {

                val builder = AlertDialog.Builder(this@MainActivity, R.style.MyDialogTheme)
                builder.setTitle("אזהרה")

                // FOR TESTING --------------------------------------------------- VVVVV

                //If todays date equals the date of the item
                if (TodaysDate.todaysDateFormatted == convertDateObjectToFormattedString(appViewModel.allWords.value?.get(position)?.date)){
                    builder.setMessage("היום של המוצר שווה להיום")
                }else{
                    builder.setMessage("היום של המוצר לא שווה להיום")

                }


                builder.setPositiveButton("כן") { dialog, which ->

                    appViewModel.allWords.value?.let { appViewModel.delete(it[position]) }

                }

                builder.setNegativeButton("לא") { dialog, which ->
                    Toast.makeText(applicationContext,
                        "Canceled", Toast.LENGTH_SHORT).show()
                }
                builder.show()


            }
        })

        //TODO - When we are ready to get all data by date, we can switch between allWords to getAllEntriesByDate()


        // Add an observer on the LiveData returned by getAllEntries.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        appViewModel.allWords.observe(this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let { adapter.submitList(it) }
        }


        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEntryActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {

            //initializing variables to store our extra results
            var tempName: String = ""
            var tempCalories: Int = 0
            var tempImage: Int = 0

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
            val foodEntry = FoodEntry(tempName,tempCalories, tempImage, getCurrentDateTime())
            //Add the Food Entry into the Database
            appViewModel.insert(foodEntry)

        } else {
           Log.d("Main", "user went back.")
        }
    }

    private fun getCurrentDateTime(): Date {

            return Calendar.getInstance().time

    }


    // Creating a formatted string of today's date
    fun convertDateObjectToFormattedString(dateObject: Date?): String {

        return SimpleDateFormat(TodaysDate.DAY_FORMAT, Locale.getDefault()).format(dateObject)
    }
}