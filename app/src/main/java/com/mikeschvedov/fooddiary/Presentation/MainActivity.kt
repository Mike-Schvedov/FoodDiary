package com.mikeschvedov.fooddiary.Presentation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.Logic.AppViewModel
import com.mikeschvedov.fooddiary.Logic.AppViewModelFactory
import com.mikeschvedov.fooddiary.NewEntryActivity
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.Util.FoodApplication
import com.mikeschvedov.fooddiary.databinding.ActivityMainBinding
import com.mikeschvedov.whatieat.FoodEntriesListAdapter

class MainActivity : AppCompatActivity() {

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
                builder.setMessage("האם ברצונך למחוק את הרשומה?")

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
            var tempNum: Int = 0

            // Getting name extra
            intentData?.getStringExtra("name_extra")?.let { reply ->
                tempName = reply
            }
            // Getting name extra
            intentData?.getIntExtra("num_extra", 0)?.let { reply ->
                tempNum = reply
            }

            //Creating a temp Food Entry item and passing the extras into it
            val foodEntry = FoodEntry(tempName,tempNum, "20/10")
            //Add the Food Entry into the Database
            appViewModel.insert(foodEntry)

        } else {
            Toast.makeText(
                applicationContext,
                "Something is missing!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}