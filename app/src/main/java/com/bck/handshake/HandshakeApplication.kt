package com.bck.handshake

import android.app.Application
import com.bck.handshake.data.SupabaseHelper

class HandshakeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SupabaseHelper.initialize(this)
    }
} 