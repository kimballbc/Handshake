package com.bck.handshake.data

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
    val id: String,
    val name: String,
    val records: Records,
    val avatar: Int
)

data class Bet(
    val id: String,
    val participant: User,
    val description: String,
    val prideWagered: Int,
    val isConfirmed: Boolean = false,
    val status: String = "pending", // "pending", "accepted", "rejected", "completed"
    val isCreator: Boolean = true,
    val canAccept: Boolean = false,
    val canReject: Boolean = false,
    val canComplete: Boolean = false,
    val winnerId: String? = null,
    val created_at: String? = null
) {
    val statusDisplay: String
        get() = when (status) {
            "pending" -> if (isCreator) "Waiting for Response" else "Needs Your Response"
            "accepted" -> "In Progress"
            "rejected" -> "Rejected"
            "completed" -> "Completed"
            else -> status
        }
}

//val sampleUser = User("John", sampleRecords, R.drawable.footlegs)
//val sampleUser2 = User("Jane", sampleRecords, R.drawable.hornhead)
//val sampleUser3 = User("Jack", sampleRecords, R.drawable.longnose)

