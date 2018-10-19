package ru.lextop.steamcalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ab = (activity!! as AppCompatActivity).supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setTitle(R.string.menu_title_preferences)
        return try {
            super.onCreateView(inflater, container, savedInstanceState)
        } finally {
           // setDividerPreferences(DIVIDER_DEFAULT)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity!!.onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}