package com.example.randomuser

import android.app.Application
import com.example.randomuser.di.AppComponent
import com.example.randomuser.di.DaggerAppComponent

class RandUserApp : Application() {
    val appComponent: AppComponent = DaggerAppComponent
        .builder()
        .build()
}