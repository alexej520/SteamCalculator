package ru.lextop.steamcalculator.binding

import android.content.Context
import android.databinding.BindingAdapter
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.widget.EditText
import ru.lextop.steamcalculator.R

fun dp(context: Context, size: Float): Int =
    (context.resources.displayMetrics.density * size).toInt()

fun sp(context: Context, size: Float): Int =
    (context.resources.displayMetrics.scaledDensity * size).toInt()

fun getSpanned(context: Context, resId: Int): Spanned? {
    if (resId == 0) return null
    val string = context.getString(resId)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(string)
    }
}

@BindingAdapter("bind:onInputValue", requireAll = true)
fun textListener(editText: EditText, onInputValue: OnInputValue) {
    val  text: CharSequence? = null
    val listener = editText.getTag(R.id.onChangedListener) as InternalTextWatcher?
            ?: InternalTextWatcher().also { editText.addTextChangedListener(it) }
    listener.afterTextChanged = onInputValue
    editText.setTag(R.id.onChangedListener, listener)
    listener.isEnabled = false
    if (editText.text != text) {
        editText.setText(text)
        editText.setSelection(editText.length())
    }
    listener.isEnabled = true
}

private class InternalTextWatcher(
    var afterTextChanged: OnInputValue? = null
): TextWatcher {
    var isEnabled = true
    override fun afterTextChanged(input: Editable) {
        if (!isEnabled) return
        afterTextChanged?.onInputValue(input)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}

interface OnInputValue {
    fun onInputValue(input: Editable)
}
