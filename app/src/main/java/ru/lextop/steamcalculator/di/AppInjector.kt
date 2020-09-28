package ru.lextop.steamcalculator.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
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
            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}
            override fun onActivityStopped(activity: Activity) {}
            // try inject all Activities was created
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (activity is Injectable || activity is HasAndroidInjector) {
                    AndroidInjection.inject(activity)
                }
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                            object : FragmentManager.FragmentLifecycleCallbacks() {
                                override fun onFragmentCreated(
                                    fm: FragmentManager,
                                    f: Fragment,
                                    savedInstanceState: Bundle?
                                ) {
                                    if (f is Injectable) {
                                        AndroidSupportInjection.inject(f)
                                    }
                                }
                            }, true)
                }
            }
        })
    }
}
