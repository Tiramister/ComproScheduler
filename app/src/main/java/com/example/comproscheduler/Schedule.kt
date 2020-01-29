package com.example.comproscheduler

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Schedule(
    @PrimaryKey
    var id: Long = 0,
    var title: String = "",
    var url: String = "",
    var date: Date? = null
) : RealmObject()
