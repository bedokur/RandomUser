package com.example.randomuser.di

import com.example.randomuser.RandUserApp
import com.example.randomuser.di.modules.ApplicationModule
import com.example.randomuser.di.modules.ViewModelModule
import com.example.randomuser.ui.view.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(app: RandUserApp)
    fun inject(activity: MainActivity)
}