package ru.lextop.steamcalculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
            dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(AnkoContext.create(this, this).frameLayout {
            lparams(matchParent, matchParent)
            id = R.id.fragmentContainer
        })
        if (supportFragmentManager.backStackEntryCount == 0) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, SteamFragment()).commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}