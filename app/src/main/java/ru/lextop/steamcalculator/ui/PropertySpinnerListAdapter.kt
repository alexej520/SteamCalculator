package ru.lextop.steamcalculator.ui

import android.view.View
import kotlinx.android.synthetic.main.item_dropdown_property.*
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.toVisibleOrGone
import ru.lextop.steamcalculator.list.Holder
import ru.lextop.steamcalculator.list.ProvideHolderFabric
import ru.lextop.steamcalculator.list.SpinnerListAdapter

class PropertySpinnerListAdapter : SpinnerListAdapter<Pair<CharSequence, CharSequence>>(
    listOf(ViewHolder::class to DropdownHolder::class)
) {
    var isHintVisible: Boolean = false
}

@ProvideHolderFabric(
    layoutRes = R.layout.item_view_property,
    viewType = 0
)
private class ViewHolder(
    containerView: View
) : Holder<Pair<CharSequence, CharSequence>>(
    containerView
) {
    override fun bind(payload: Any?) {
        symbolTextView.text = item.first
    }
}

@ProvideHolderFabric(
    layoutRes = R.layout.item_dropdown_property,
    viewType = 0
)
private class DropdownHolder(
    containerView: View
) : Holder<Pair<CharSequence, CharSequence>>(
    containerView
) {
    val propertyAdapter by lazy { adapter as PropertySpinnerListAdapter }
    override fun bind(payload: Any?) {
        val (item, hint) = item
        symbolTextView.text = item
        nameTextView.text = hint
        nameTextView.visibility = propertyAdapter.isHintVisible.toVisibleOrGone()
    }
}