package com.mikeschvedov.fooddiary.Presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mikeschvedov.fooddiary.Data.LocalData.FoodArchive
import com.mikeschvedov.fooddiary.Data.Models.FoodSaved
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.databinding.ActivityNewEntryBinding


class NewEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewEntryBinding

    private var thisItemsCalPer100: Int = 0
    var selectedImageUrl: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creating the list Adapter
        val listAdapter: ArrayAdapter<FoodSaved> = ArrayAdapter(
            this,
            R.layout.simple_list_item_custom,
            FoodArchive.foodDataList
        )


        binding.apply {

            // Attaching our adapter to the list view xml
            listviewXml.adapter = listAdapter

            // Setting the list Adapter
            searchviewXml.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                // Runs when the user presses enter inside the search view
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchviewXml.clearFocus()

                    // We check if the one of those name matches the query
                  //  if (FoodArchive.foodDataList.contains(query)) {
                 //       listAdapter.filter.filter(query)
                  //  }
                    return false
                }

                // Runs each time there is a change inside the search view
                override fun onQueryTextChange(query: String?): Boolean {
                    listAdapter.filter.filter(query)
                    submitingSectionXml.visibility = View.GONE
                    listviewXml.visibility = View.VISIBLE
                    return false
                }


            })



            // When we click on an item it will appear in the search view
            listviewXml.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                // We set the text to the search view
                searchviewXml.setQuery(listAdapter.getItem(position).toString(), false);
                // We hide the list after clicking on the item
                listviewXml.visibility = View.GONE
                // We store our selected item's core calories
                thisItemsCalPer100 = listAdapter.getItem(position)?.calPer100gr ?: 0
                // We store our selected item's image
                selectedImageUrl = listAdapter.getItem(position)?.image ?: 0
                // We set it in our textview
                itemsCaloriesPer100Xml.text = thisItemsCalPer100.toString()
                // We make our submitting section visible
                submitingSectionXml.visibility = View.VISIBLE

            })




            // Save Button
            buttonSave.setOnClickListener {

                val insertedGrams: String = gramsEdittextXml.text.toString()

                if(validateWeight(insertedGrams)){

                    Log.d("newEntry", "BO is True")

                    val replyIntent = Intent()
                    if (TextUtils.isEmpty(searchviewXml.toString())) {
                        setResult(Activity.RESULT_CANCELED, replyIntent)
                        finish()
                    } else {

                        val itemName = searchviewXml.query.toString()
                        val calories = calculateFinalCalories(insertedGrams.toInt(), thisItemsCalPer100)
                        val image = selectedImageUrl

                        // Passing the values we got from the form as extras
                        replyIntent.putExtra("name_extra", itemName)
                        replyIntent.putExtra("calories_extra", calories)
                        replyIntent.putExtra("image_extra", image)
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }


                }else{

                    Toast.makeText(
                        applicationContext,
                        "יש להכניס משקל תקין",
                        Toast.LENGTH_LONG
                    ).show()
                }




            }
        }


    }

    private fun calculateFinalCalories(inputGrams: Int, thisItemsCalPer100: Int): Int {
        // We convert out calories into a Double for the sake of division
        val tempCal: Double = thisItemsCalPer100.toDouble()
        // We divide the calories by 100 and multiply by the given Grams
        return ((tempCal / 100) * inputGrams).toInt()
    }

    private fun validateWeight(value: String): Boolean {
        // If the vale is not empty and bigger than 0 return true
        return value != "" && value.toInt() > 0

    }

}