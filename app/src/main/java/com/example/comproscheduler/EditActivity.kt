package com.example.comproscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private var scheduleId: Long? = null
    private var isNewSchedule: Boolean = false

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
                dateEdit.setText(DateFormat.format("yyyy/MM/dd", date))
                titleEdit.setText(title)
                urlEdit.setText(url)
            }
        }
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
                date = dateEdit.text.toString().toDate()
                title = titleEdit.text.toString()
                url = urlEdit.text.toString()
            }
        }

        Snackbar
            .make(view,
                if (wasNewSchedule) {"added"} else {"updated"},
                Snackbar.LENGTH_SHORT)
            .setAction("finish") { finish() }
            .show()
    }

    fun deleteSchedule(view: View) {
        if (isNewSchedule) {
            Snackbar
                .make(view, "cannot delete unregistered schedule", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            realm.executeTransaction { db ->
                db.where<Schedule>()
                    .equalTo("id", scheduleId)
                    ?.findFirst()
                    ?.deleteFromRealm()
            }

            // back to main activity
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd"): Date? {
        return try {
            SimpleDateFormat(pattern).parse(this)
        } catch (err: IllegalArgumentException) {
            return null
        } catch (err: ParseException) {
            return null
        }
    }
}
