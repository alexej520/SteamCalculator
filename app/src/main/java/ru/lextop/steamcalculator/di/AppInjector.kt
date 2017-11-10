package ru.lextop.steamcalculator.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dagger.android.AndroidInjection
import ru.lextop.steamcalculator.App

object AppInjector {
    fun init(app: App) {
        DaggerAppComponent.builder()
                .application(app)
                .build().inject(app)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle?) {}
            override fun onActivityStopped(activity: Activity) {}
            // try inject all Activities was created
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (activity is Injectable) {
                    AndroidInjection.inject(activity)
                }
            }
        })
    }
}
