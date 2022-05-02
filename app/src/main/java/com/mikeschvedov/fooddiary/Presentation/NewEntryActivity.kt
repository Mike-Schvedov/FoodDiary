package com.mikeschvedov.fooddiary.Presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.mikeschvedov.fooddiary.Data.LocalData.FoodArchive
import com.mikeschvedov.fooddiary.databinding.ActivityNewEntryBinding


class NewEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewEntryBinding

    var existingItemSelected: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creating the list Adapter
        val listAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
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
                    if (FoodArchive.foodDataList.contains(query)) {
                        listAdapter.filter.filter(query)
                    }
                    return false
                }

                // Runs each time there is a change inside the search view
                override fun onQueryTextChange(query: String?): Boolean {
                    listAdapter.filter.filter(query)
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

                existingItemSelected = true

            })


            // Save Button
            buttonSave.setOnClickListener {


                if (existingItemSelected) {
                    val replyIntent = Intent()
                    if (TextUtils.isEmpty(searchviewXml.toString())) {
                        setResult(Activity.RESULT_CANCELED, replyIntent)
                    } else {

                        val word = searchviewXml.query.toString()
                        val number = 20
                        // Passing the values we got from the form as extras
                        replyIntent.putExtra("name_extra", word)
                        replyIntent.putExtra("num_extra", number)
                        setResult(Activity.RESULT_OK, replyIntent)

                    }
                    finish()
                }else(
                        Toast.makeText(
                            applicationContext,
                             "בחר מאכל מהרשימה!",
                            Toast.LENGTH_LONG
                        ).show()
                )


            }
        }


    }

}