package com.mikeschvedov.fooddiary.Presentation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
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



        val allFoodList = mutableListOf(
            FoodEntry(
                "coffee",
                200,
                "20/10"
            ),
        )

        val adapter = FoodEntriesListAdapter()

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        appViewModel.allWords.observe(this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEntryActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(NewEntryActivity.EXTRA_REPLY)?.let { reply ->
                val word = FoodEntry(reply, 10, "20/10")
                appViewModel.insert(word)
            }
        } else {
            Toast.makeText(
                applicationContext,
                "empty not saved",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}