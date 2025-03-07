package com.bck.handshake.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object SupabaseHelper {
    val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://tmowqmkjntyxweemosdx.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRtb3dxbWtqbnR5eHdlZW1vc2R4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDEyOTc5NzYsImV4cCI6MjA1Njg3Mzk3Nn0.pQlVgxxH80FIhuyFRbtijHdVQNW-cuxueoojJHijhSc"
    ) {
        install(Auth)
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
}
