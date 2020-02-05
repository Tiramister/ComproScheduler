package com.example.comproscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        realm = Realm.getDefaultInstance()

        saveButton.setOnClickListener { view: View ->
            realm.executeTransaction { db: Realm ->
                // find unique ID and create a new schedule
                val maxId = db.where<Schedule>().max("id")
                val nextId = (maxId?.toLong() ?: -1L) + 1
                val schedule = db.createObject<Schedule>(nextId)

                // fill schedule with input
                schedule.run {
                    date = dateEdit.text.toString().toDate()
                    title = titleEdit.text.toString()
                    url = urlEdit.text.toString()
                }
            }

            Snackbar.make(view, "added", Snackbar.LENGTH_SHORT)
                .setAction("return") { finish() }
                .show()
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
