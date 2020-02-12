package com.example.comproscheduler

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

// set appropriate view with realm object
class ScheduleAdapter(data: OrderedRealmCollection<Schedule>) :
    RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true) {

    // tell that schedule class has the primary key (id)
    init { setHasStableIds(true) }

    // return the primary key (id) of the item displayed at position-th position
    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }

    // callback listener
    private var listener: ((Long?) -> Unit)? = null

    // set listener with function variable
    fun setOnClickListener(listener: (Long?) -> Unit) {
        this.listener = listener
    }

    // information of a cell
    class ViewHolder(
        cell: View,
        val date : TextView = cell.findViewById(R.id.dateText),
        val title: TextView = cell.findViewById(R.id.titleText)
    ) : RecyclerView.ViewHolder(cell)

    // create view of each cell
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // instantiate the layout file (cell.xml) into view object with inflater
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell, parent, false)

        return ViewHolder(view)
    }

    // set contents of view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // fetch schedule from data base
        val schedule = getItem(position)

        holder.date.text = schedule?.let { sc ->
            // if date is unset, print --/-- instead
            sc.date?.let { DateFormat.format("MM/dd", it) } ?: "--/--"
        }
        holder.title.text = schedule?.title

        // set listener for cell view
        holder.itemView.setOnClickListener {
            listener?.invoke(schedule?.id)
        }
    }
}
