package com.bck.handshake.data

import com.bck.handshake.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


@Serializable
data class SupabaseUser(
    val id: String,
    val email: String,
    val display_name: String
)

@Serializable
data class UserRecord(
    val id: String,
    val user_id: String,
    val wins: Int,
    val draws: Int,
    val losses: Int
)

@Serializable
data class SupabaseBet(
    val id: String,
    val creator_id: String,
    val participant_id: String,
    val description: String,
    val pride_wagered: Int,
    val status: String,
    val winner_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

object SupabaseHelper {
    val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://tmowqmkjntyxweemosdx.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRtb3dxbWtqbnR5eHdlZW1vc2R4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDEyOTc5NzYsImV4cCI6MjA1Njg3Mzk3Nn0.pQlVgxxH80FIhuyFRbtijHdVQNW-cuxueoojJHijhSc"
    ) {
        install(Auth)
        install(Postgrest)
    }

    fun signUpWithEmail(email: String, password: String, displayName: String, onResult: (Boolean, String?) -> Unit) {
        runBlocking {
            try {
                val userData = buildJsonObject {
                    put("display_name", JsonPrimitive(displayName))
                }
                
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = userData
                }
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        runBlocking {
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun isSignedIn(): Boolean {
        return try {
            supabase.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            false
        }
    }

    fun signOut(onResult: (Boolean, String?) -> Unit) {
        runBlocking {
            try {
                supabase.auth.signOut()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            supabase.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUserDisplayName(): String? {
        return try {
            val user = supabase.auth.currentUserOrNull()
            if (user != null) {
                // Try to get display name from user metadata
                val displayName = user.userMetadata?.get("display_name")?.toString()
                if (!displayName.isNullOrEmpty()) {
                    return displayName
                }
                // If no display name, return email
                return user.email
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun getAvailableUsers(onResult: (List<User>, String?) -> Unit) {
        runBlocking {
            try {
                val currentUserId = getCurrentUserId()
                if (currentUserId == null) {
                    onResult(emptyList(), "Not signed in")
                    return@runBlocking
                }

                // Get all users except current user
                val supabaseUsers: List<SupabaseUser> = supabase.postgrest.from("users")
                    .select(columns = Columns.list("id", "email", "display_name")) {
                        filter {
                            neq("id", currentUserId)
                        }
                    }
                    .decodeList()

                // Get all user records
                val userRecords: Map<String, UserRecord> = supabase.postgrest.from("user_records")
                    .select()
                    .decodeList<UserRecord>()
                    .associateBy { it.user_id }

                val users = supabaseUsers.map { supabaseUser ->
                    val record = userRecords[supabaseUser.id]
                    User(
                        id = supabaseUser.id,
                        name = supabaseUser.display_name,
                        records = Records(
                            wins = (record?.wins ?: 0).toString(),
                            draws = (record?.draws ?: 0).toString(),
                            loss = (record?.losses ?: 0).toString()
                        ),
                        avatar = R.drawable.footlegs // Default avatar for now
                    )
                }

                onResult(users, null)
            } catch (e: Exception) {
                onResult(emptyList(), e.localizedMessage)
            }
        }
    }

    fun createBet(participantId: String, description: String, prideWagered: Int, onResult: (Boolean, String?) -> Unit) {
        runBlocking {
            try {
                val creatorId = getCurrentUserId()
                if (creatorId == null) {
                    onResult(false, "Not signed in")
                    return@runBlocking
                }

                supabase.postgrest.from("bets").insert(
                    value = buildJsonObject {
                        put("creator_id", JsonPrimitive(creatorId))
                        put("participant_id", JsonPrimitive(participantId))
                        put("description", JsonPrimitive(description))
                        put("pride_wagered", JsonPrimitive(prideWagered))
                        put("status", JsonPrimitive("pending"))
                    }
                )

                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun getUserBets(onResult: (List<Bet>, String?) -> Unit) {
        runBlocking {
            try {
                val currentUserId = getCurrentUserId()
                if (currentUserId == null) {
                    onResult(emptyList(), "Not signed in")
                    return@runBlocking
                }

                // Get all bets where user is either creator or participant
                val bets: List<SupabaseBet> = supabase.postgrest.from("bets")
                    .select() {
                        filter {
                            or {
                                eq("creator_id", currentUserId)
                                eq("participant_id", currentUserId)
                            }
                        }
                    }
                    .decodeList()

                if (bets.isEmpty()) {
                    onResult(emptyList(), null)
                    return@runBlocking
                }

                // Get all users involved in these bets
                val userIds = bets.flatMap { listOf(it.creator_id, it.participant_id) }.distinct()
                val users = mutableMapOf<String, SupabaseUser>()
                val userRecords = mutableMapOf<String, UserRecord>()

                // Fetch users one by one to avoid array filter issues
                for (userId in userIds) {
                    try {
                        val user = supabase.postgrest.from("users")
                            .select(columns = Columns.list("id", "email", "display_name")) {
                                filter {
                                    eq("id", userId)
                                }
                            }
                            .decodeSingle<SupabaseUser>()
                        users[userId] = user

                        // Also fetch their record
                        try {
                            val record = supabase.postgrest.from("user_records")
                                .select() {
                                    filter {
                                        eq("user_id", userId)
                                    }
                                }
                                .decodeSingle<UserRecord>()
                            userRecords[userId] = record
                        } catch (e: Exception) {
                            // User might not have a record yet, that's okay
                        }
                    } catch (e: Exception) {
                        // Skip users that can't be found
                        println("Failed to fetch user $userId: ${e.localizedMessage}")
                    }
                }

                val mappedBets = bets.map { bet ->
                    val isCreator = bet.creator_id == currentUserId
                    val otherUserId = if (isCreator) bet.participant_id else bet.creator_id
                    val otherUser = users[otherUserId]
                    val record = userRecords[otherUserId]
                    
                    Bet(
                        id = bet.id,
                        participant = User(
                            id = otherUserId,
                            name = otherUser?.display_name ?: "Unknown User",
                            records = Records(
                                wins = (record?.wins ?: 0).toString(),
                                draws = (record?.draws ?: 0).toString(),
                                loss = (record?.losses ?: 0).toString()
                            ),
                            avatar = R.drawable.footlegs
                        ),
                        description = bet.description,
                        prideWagered = bet.pride_wagered,
                        isConfirmed = bet.status == "accepted",
                        status = bet.status,
                        isCreator = isCreator,
                        winnerId = bet.winner_id
                    )
                }

                onResult(mappedBets, null)
            } catch (e: Exception) {
                onResult(emptyList(), e.localizedMessage)
            }
        }
    }

    fun updateBetStatus(betId: String, newStatus: String, winnerId: String? = null, onResult: (Boolean, String?) -> Unit) {
        runBlocking {
            try {
                val currentUserId = getCurrentUserId()
                if (currentUserId == null) {
                    onResult(false, "Not signed in")
                    return@runBlocking
                }

                // Get the bet first to determine the participants
                val bet = supabase.postgrest.from("bets")
                    .select() {
                        filter {
                            eq("id", betId)
                        }
                    }
                    .decodeSingle<SupabaseBet>()

                // Update the bet status
                val updateObject = buildJsonObject {
                    put("status", JsonPrimitive(newStatus))
                    if (winnerId != null) {
                        put("winner_id", JsonPrimitive(winnerId))
                    }
                }

                supabase.postgrest.from("bets")
                    .update(
                        value = updateObject
                    ) {
                        filter {
                            eq("id", betId)
                            or {
                                eq("creator_id", currentUserId)
                                eq("participant_id", currentUserId)
                            }
                        }
                    }

                // If the bet is completed, update user records
                if (newStatus == "completed") {
                    if (winnerId == null) {
                        // Draw - both users get a draw
                        updateUserRecord(bet.creator_id, "draw")
                        updateUserRecord(bet.participant_id, "draw")
                    } else {
                        // One user won, one lost
                        val loserId = if (winnerId == bet.creator_id) bet.participant_id else bet.creator_id
                        updateUserRecord(winnerId, "win")
                        updateUserRecord(loserId, "loss")
                    }
                }

                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    private suspend fun updateUserRecord(userId: String, outcome: String) {
        try {
            // Get current record
            val record = try {
                supabase.postgrest.from("user_records")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeSingle<UserRecord>()
            } catch (e: Exception) {
                // Create new record if it doesn't exist
                UserRecord(
                    id = "",
                    user_id = userId,
                    wins = 0,
                    draws = 0,
                    losses = 0
                )
            }

            // Update the appropriate counter
            val updateObject = buildJsonObject {
                put("user_id", JsonPrimitive(userId))
                when (outcome) {
                    "win" -> put("wins", JsonPrimitive(record.wins + 1))
                    "draw" -> put("draws", JsonPrimitive(record.draws + 1))
                    "loss" -> put("losses", JsonPrimitive(record.losses + 1))
                }
            }

            // Upsert the record
            supabase.postgrest.from("user_records")
                .upsert(
                    value = updateObject
                ) {
                    filter {
                        eq("user_id", userId)
                    }
                }
        } catch (e: Exception) {
            // Log error but don't fail the bet update
            println("Failed to update user record: ${e.localizedMessage}")
        }
    }
}
