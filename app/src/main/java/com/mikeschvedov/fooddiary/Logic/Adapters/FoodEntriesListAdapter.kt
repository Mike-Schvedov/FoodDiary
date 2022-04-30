package com.mikeschvedov.whatieat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.fooddiary.Data.Database.FoodEntry
import com.mikeschvedov.fooddiary.R


class FoodEntriesListAdapter : ListAdapter<FoodEntry, FoodEntriesListAdapter.FoodViewHolder>(WordsComparator()) {

    private lateinit var mListenerInterface : onClickListenerInterface

    interface onClickListenerInterface{

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listenerInterface: onClickListenerInterface){
        mListenerInterface = listenerInterface
    }

    //We first define a view holder:
    class FoodViewHolder(itemView: View, listenerInterface: onClickListenerInterface) : RecyclerView.ViewHolder(itemView) {

        // We get needed references to all items in View (our XML)
        private val gameName_xml: TextView = itemView.findViewById(R.id.gameName_xml)
        private val totalhours_xml: TextView = itemView.findViewById(R.id.gameTotalHours_xml)

        // We provide the data we get and bind it to them
        fun bind(text: String?, calories: Int) {
            gameName_xml.text = text
            totalhours_xml.text = calories.toString()
        }

        init {
            //When we click on the itemview ->
            itemView.setOnClickListener {
                // we are going call "onItemClick" method of this listener's instance.
                listenerInterface.onItemClick(adapterPosition)
            }
        }


        companion object {
            fun create(parent: ViewGroup, listenerInterface: onClickListenerInterface): FoodViewHolder {
                //We inflate our recyclerview_item layout and place it inside a view variable
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)

                return FoodViewHolder(view, listenerInterface)
            }
        }
    }

    //We create the view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder.create(parent, mListenerInterface)
    }

    //We bind the data we got to every view holder
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem.foodname, currentItem.calories)
    }





    class WordsComparator : DiffUtil.ItemCallback<FoodEntry>() {
        override fun areItemsTheSame(oldItem: FoodEntry, newItem: FoodEntry): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FoodEntry, newItem: FoodEntry): Boolean {
            return oldItem.foodname == newItem.foodname
        }
    }
}