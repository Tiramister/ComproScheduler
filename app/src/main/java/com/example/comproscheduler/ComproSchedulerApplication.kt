package com.example.comproscheduler

import android.app.Application
import io.realm.Realm

class ComproSchedulerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize realm
        Realm.init(this)
    }
}
