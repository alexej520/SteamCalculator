package ru.lextop.steamcalculator.ui

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_view_unit.*
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.list.Holder
import ru.lextop.steamcalculator.list.ProvideHolderFabric
import ru.lextop.steamcalculator.list.SpinnerListAdapter

fun UnitArrayAdapter(): SpinnerListAdapter<CharSequence> {
    return SpinnerListAdapter(
        listOf(UnitViewHolder::class to UnitDropdownHolder::class)
    )
}

@ProvideHolderFabric(
    layoutRes = R.layout.item_view_unit,
    viewType = 0
)
class UnitViewHolder(containerView: View) : Holder<CharSequence>(containerView) {
    override fun bind(payload: Any?) {
        symbolTextView.text = item
    }
}

@ProvideHolderFabric(
    layoutRes = R.layout.item_dropdown_unit,
    viewType = 0
)
class UnitDropdownHolder(containerView: View) : Holder<CharSequence>(containerView) {
    override fun bind(payload: Any?) {
        symbolTextView.text = item
    }
}