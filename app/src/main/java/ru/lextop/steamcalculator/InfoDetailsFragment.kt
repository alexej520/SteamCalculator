package ru.lextop.steamcalculator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.ctx
import ru.lextop.steamcalculator.binding.getSpanned
import ru.lextop.steamcalculator.binding.textBody1
import ru.lextop.steamcalculator.binding.textBody2

class InfoDetailsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ab = (activity!! as AppCompatActivity).supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setTitle(R.string.menu_title_info_details)
        return AnkoContext.create(context!!)
                .verticalLayout {
                    lparams(matchParent, matchParent)
                    verticalPadding = dip(16)
                    textBody1 {
                        text = ctx.getSpanned(R.string.info_details)
                        movementMethod = LinkMovementMethod.getInstance()
                        horizontalPadding = dip(16)
                    }.lparams(matchParent, wrapContent)
                    textBody2 {
                        text = getString(R.string.contact_developer)
                        horizontalPadding = dip(16)
                    }.lparams(matchParent, wrapContent){
                        topMargin = dip(16)
                    }
                    textBody1 {
                        text = ctx.getSpanned(R.string.contact_developer_twitter)
                        movementMethod = LinkMovementMethod.getInstance()
                        horizontalPadding = dip(32)
                    }
                    textBody1 {
                        text = getString(R.string.contact_developer_email)
                        horizontalPadding = dip(32)
                    }
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