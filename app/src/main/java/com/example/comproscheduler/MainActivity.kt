package com.example.comproscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getDefaultInstance()

        // set layout and adapter of the recycler view
        scheduleListView.layoutManager = LinearLayoutManager(this)
        val schedules = realm.where<Schedule>().findAll()
        val adapter = ScheduleAdapter(schedules)

        // set callback listener
        // open edit activity with schedule id
        adapter.setOnClickListener { id ->
            val intent = Intent(this, EditActivity::class.java)
                .putExtra("schedule_id", id)
            startActivity(intent)
        }

        scheduleListView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun moveToEdit(view: View) {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }
}
