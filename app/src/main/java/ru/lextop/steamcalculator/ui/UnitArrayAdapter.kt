package ru.lextop.steamcalculator.ui

import android.view.View
import android.widget.TextView
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.list.Holder
import ru.lextop.steamcalculator.list.ProvideHolderFabric
import ru.lextop.steamcalculator.list.SpinnerListAdapter

fun UnitArrayAdapter(): SpinnerListAdapter<CharSequence> {
    return SpinnerListAdapter(
        listOf(UnitHolder::class to UnitHolder::class)
    )
}

@ProvideHolderFabric(
    layoutRes = R.layout.item_view_unit
)
class UnitHolder(containerView: View) : Holder<CharSequence>(containerView) {
    override fun bind(payload: Any?) {
        (containerView as TextView).text = item
    }
}