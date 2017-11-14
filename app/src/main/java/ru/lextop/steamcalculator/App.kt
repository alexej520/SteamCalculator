package ru.lextop.steamcalculator

import android.app.Activity
import android.app.Application
import android.support.v7.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ru.lextop.steamcalculator.di.AppInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector{
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }*/
        //refWatcher = LeakCanary.install(this)
        AppInjector.init(this)
    }
    companion object {
        //lateinit var refWatcher: RefWatcher
    }
}