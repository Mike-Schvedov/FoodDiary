package com.mikeschvedov.fooddiary.Presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mikeschvedov.fooddiary.Data.Database.Entities.WeightEntry
import com.mikeschvedov.fooddiary.Logic.Adapters.WeightEntriesListAdapter
import com.mikeschvedov.fooddiary.Logic.AppViewModel
import com.mikeschvedov.fooddiary.Logic.AppViewModelFactory
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.Util.FoodApplication
import com.mikeschvedov.fooddiary.Util.TodaysDate
import com.mikeschvedov.fooddiary.databinding.ActivityWeightBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
            val weightInput = binding.weightInputEdittext.text.toString()
            if (appViewModel.isValidWeightInput(weightInput)) {
                // Only now convert to Double
                val weightAsDouble = weightInput.toDouble()
                Log.e("WeightActivity", "THE WEIGHT INPUT IS : $weightAsDouble")
                // Creating a new weight entry object
                val weightEntry = WeightEntry(weightAsDouble, TodaysDate.todaysDate)
                Log.e(
                    "WeightActivity",
                    "THE WEIGHT ENTRY IS : weight: ${weightEntry.weight} and date: ${weightEntry.date_added.time}"
                )
                // Inserting the object into the database
                appViewModel.insertWeight(weightEntry)
                // Calling the observer again //TODO maybe not needed?
                observeChangeInWeightRecyclerView(adapter)
                // Deleting value in edit text
                binding.weightInputEdittext.text.clear()
            } else {
                Toast.makeText(this, "משקל לא תקין", Toast.LENGTH_SHORT).show()
            }

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

    private fun setUpLineChart(listOfDates: ArrayList<Date>) {

        binding.weightLineChart.apply {

            animateX(1200, Easing.EaseInSine)
            description.isEnabled = false

            xAxis.setDrawGridLines(false) //lines that extend from the x-axis values
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F // spacing between each of x-axis values
            xAxis.valueFormatter = MyAxisFormatter(listOfDates)

            axisRight.isEnabled = false
            extraRightOffset = 30f

            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.textSize = 15F
            legend.form = Legend.LegendForm.LINE
        }
    }


    private fun setDataToLineChart(weightLineChart: LineChart, listOfWeight: ArrayList<Double>) {

        val weekOneSales = LineDataSet(weightData(listOfWeight), "")
        weekOneSales.lineWidth = 3f
        weekOneSales.valueTextSize = 15f
        weekOneSales.mode = LineDataSet.Mode.LINEAR
        weekOneSales.color = ContextCompat.getColor(this, R.color.darker_brown)
        //weekOneSales.valueTextColor = ContextCompat.getColor(this, R.color.green) // Color of values
        weekOneSales.setDrawValues(false) // hiding the values
        //weekOneSales.enableDashedLine(20F, 10F, 4F) // If we want the line to be dashed
        weekOneSales.setCircleColor(R.color.darker_brown)
        //weekOneSales.setDrawCircleHole() //DEFAULT = WHITE (THE INNER CIRCLE)

        val dataSet = ArrayList<ILineDataSet>()
        dataSet.add(weekOneSales)

        val lineData = LineData(dataSet)

        weightLineChart.data = lineData
        weightLineChart.invalidate()

    }

    // We convert entries for the database "listOfWeight" into Entry objects (stored into "entriesList" list), then we return it and pass into the Chart
    private fun weightData(listOfWeight: ArrayList<Double>): ArrayList<Entry> {
        val entriesList = ArrayList<Entry>()
        var counter = 0

        for (item in listOfWeight) {
            // Entry(X, Y)
            entriesList.add(Entry(counter.toFloat(), item.toFloat()))
            counter += 1
        }
        return entriesList
    }


    inner class MyAxisFormatter(listOfDates: ArrayList<Date>) : IndexAxisValueFormatter() {
        private var datesAsDates = listOfDates
        private var datesAsStrings = arrayListOf<String>()

        private fun datesIntoStrings() {

            // We take all Dates and transform them into Strings
            for (date in datesAsDates) {
                datesAsStrings.add(dateToFormatted(date))
            }

        }

        private fun dateToFormatted(dateObject: Date): String {
            return SimpleDateFormat(TodaysDate.DAY_FORMAT, Locale.getDefault()).format(dateObject)
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
            // We get the dates as strings
            datesIntoStrings()
            // The value is the index of the X axis
            val index = value.toInt() + 1
            // We return the strings according the index of the entry. So the X values (index) will display the correct Date - in the same index.
            return datesAsStrings[index]
        }
    }


    private fun observeChangeInWeightRecyclerView(
        passedAdapter: WeightEntriesListAdapter
    ) {
        appViewModel.getAllWeightEntries()
            .observe(this) { entries ->
                // Update the cached copy of the weight objects in the adapter.
                entries.reversed().let { passedAdapter.submitList(it) }
                // We get our entries and then cut out the weights/dates into the designated lists (to pass into the Chart)
                val listOfWeight = ArrayList<Double>()
                val listOfDates = ArrayList<Date>()
                for (entry in entries) {
                    listOfWeight.add(entry.weight)
                    listOfDates.add(entry.date_added)
                }
                // Updating the Line Chart according to the changes in the weight data
                updateAndSetLineChart(listOfWeight, listOfDates)
            }
    }

    private fun updateAndSetLineChart(
        listOfWeight: ArrayList<Double>,
        listOfDates: ArrayList<Date>
    ) {
        setUpLineChart(listOfDates)
        setDataToLineChart(binding.weightLineChart, listOfWeight)
    }


}