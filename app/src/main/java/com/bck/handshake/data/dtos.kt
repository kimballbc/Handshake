package com.bck.thesidebet.data

import com.bck.thesidebet.R

data class Records(
    val wins: String,
    val draws: String,
    val loss: String
) {
    val formattedRecords: String
        get() = "$wins-$draws-$loss"
}

val sampleRecords = Records("3", "1", "2")


data class User(
    val name: String,
    val records: Records,
    val avatar: Int
)

val sampleUser = User("John", sampleRecords, R.drawable.footlegs)
val sampleUser2 = User("Jane", sampleRecords, R.drawable.hornhead)
val sampleUser3 = User("Jack", sampleRecords, R.drawable.longnose)

