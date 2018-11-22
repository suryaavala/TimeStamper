package com.sardox.timestamper

import android.app.Application
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

class AppInstance : Application() {

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
        //var sharedPrefs: SharedPrefs? = null
    }

    override fun onCreate() {
        Log.d("sardox", "AppInstance created ")
        //sharedPrefs = SharedPrefs(applicationContext)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        super.onCreate()
    }
}