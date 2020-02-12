package com.example.comproscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditActivity :
    AppCompatActivity(),
    DatePickerFragment.OnDateSelectedListener {

    private lateinit var realm: Realm
    private var scheduleId: Long? = null
    private var isNewSchedule: Boolean = false
    private var selectedDate: Date? = null

    private fun updateScheduleId(id: Long?) {
        scheduleId = id
        isNewSchedule = id == -1L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        realm = Realm.getDefaultInstance()

        // receive schedule id
        updateScheduleId(intent?.getLongExtra("schedule_id", -1L))

        if (scheduleId != -1L) {
            // fetch schedule
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId)
                .findFirst()

            // set contents
            schedule?.run {
                dateEdit.text = date?.let {
                    DateFormat.format("yyyy/MM/dd", it)
                } ?: resources.getString(R.string.require_date)
                titleEdit.setText(title)
                urlEdit.setText(url)
            }
        }
    }

    override fun onSelected(year: Int, month: Int, date: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, date)

        // update date and text
        selectedDate = cal.time
        dateEdit.text = DateFormat.format("yyyy/MM/dd", cal)
    }

    fun chooseDate(view: View) {
        val dialog = DatePickerFragment()
        dialog.show(supportFragmentManager, "date_dialog")
    }

    fun saveSchedule(view: View) {
        val wasNewSchedule = isNewSchedule

        realm.executeTransaction { db: Realm ->
            val schedule =
                if (isNewSchedule) {
                    // find unique ID and create a new schedule
                    val maxId = db.where<Schedule>().max("id")
                    val nextId = (maxId?.toLong() ?: -1L) + 1
                    updateScheduleId(nextId)
                    db.createObject(nextId)
                } else {
                    // fetch from data base
                    realm.where<Schedule>()
                        .equalTo("id", scheduleId)
                        .findFirst()
                }

            // fill schedule with input
            schedule?.run {
                date = selectedDate
                title = titleEdit.text.toString()
                url = urlEdit.text.toString()
            }
        }

        Toast.makeText(this, if (wasNewSchedule) {"added"} else {"updated"}, Toast.LENGTH_SHORT)
            .show()
    }

    fun deleteSchedule(view: View) {
        if (isNewSchedule) {
            Toast.makeText(this, "cannot delete unregistered schedule", Toast.LENGTH_SHORT)
                .show()
        } else {
            realm.executeTransaction { db ->
                db.where<Schedule>()
                    .equalTo("id", scheduleId)
                    ?.findFirst()
                    ?.deleteFromRealm()
            }

            // back to main activity
            Toast.makeText(this, "deleted", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
