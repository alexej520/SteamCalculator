package ru.lextop.steamcalculator

import android.app.Activity
import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
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