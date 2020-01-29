package com.example.comproscheduler

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class ScheduleAdapter(data: OrderedRealmCollection<Schedule>) :
    RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true) {

    init { setHasStableIds(true) }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }

    // information of cells in view
    class ViewHolder(
        cell: View,
        val date : TextView = cell.findViewById(R.id.dateText),
        val title: TextView = cell.findViewById(R.id.titleText)
    ) : RecyclerView.ViewHolder(cell)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = getItem(position)
        holder.date.text = schedule?.let { sc ->
            sc.date?.let { DateFormat.format("MM/dd", it) ?: "--/--" }
        }
        holder.title.text = schedule?.title
    }
}
