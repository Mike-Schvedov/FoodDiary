package com.mikeschvedov.fooddiary.Logic.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.fooddiary.Data.Database.Entities.WeightEntry
import com.mikeschvedov.fooddiary.R
import com.mikeschvedov.fooddiary.Util.TodaysDate
import java.text.SimpleDateFormat
import java.util.*


class WeightEntriesListAdapter : ListAdapter<WeightEntry, WeightEntriesListAdapter.WeightViewHolder>(WordsComparator()) {

    private lateinit var mListenerInterface : OnWeightClickListenerInterface

    interface OnWeightClickListenerInterface{

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listenerInterface: OnWeightClickListenerInterface){
        mListenerInterface = listenerInterface
    }

    //We create the view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        return WeightViewHolder.create(parent, mListenerInterface)
    }

    //We bind the data we got to every view holder
    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem.weight, dateToFormatted(currentItem.date_added))
    }


    //We first define a view holder:
    class WeightViewHolder(itemView: View, listenerInterface: OnWeightClickListenerInterface) : RecyclerView.ViewHolder(itemView) {

        // We get needed references to all items in View (our XML)
        private val date_xml: TextView = itemView.findViewById(R.id.date_added_weight)
        private val weight_xml: TextView = itemView.findViewById(R.id.weight_in_kilos)

        // We provide the data we get and bind it to them
        fun bind(weight: Double, dateAdded: String) {
            date_xml.text = dateAdded
            weight_xml.text = weight.toString()
        }

        init {
            //When we click on the itemview ->
            itemView.setOnClickListener {
                // we are going call "onItemClick" method of this listener's instance.
                listenerInterface.onItemClick(adapterPosition)
            }
        }

        companion object {
            fun create(parent: ViewGroup, listenerInterface: OnWeightClickListenerInterface): WeightViewHolder {
                //We inflate our recyclerview_item layout and place it inside a view variable
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_weight, parent, false)

                return WeightViewHolder(view, listenerInterface)
            }
        }
    }






    class WordsComparator : DiffUtil.ItemCallback<WeightEntry>() {
        override fun areItemsTheSame(oldItem: WeightEntry, newItem: WeightEntry): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: WeightEntry, newItem: WeightEntry): Boolean {
            return oldItem.date_added == newItem.date_added
        }
    }

    private fun dateToFormatted(dateObject: Date): String {
        return SimpleDateFormat(TodaysDate.DAY_FORMAT, Locale.getDefault()).format(dateObject)
    }
}