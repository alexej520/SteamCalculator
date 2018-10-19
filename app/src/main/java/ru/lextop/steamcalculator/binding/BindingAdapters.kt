package ru.lextop.steamcalculator.binding

import android.content.Context
import androidx.databinding.BindingAdapter
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.AdapterView
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

fun getSpanned(context: Context, string: String?): Spanned? {
    if (string == null) return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(string)
    }
}

@BindingAdapter("android:setAdapter")
fun setAdapter(view: AdapterView<*>, adapter: Adapter?) {
    view.adapter = adapter
}

@BindingAdapter("android:focus")
fun focus(view: View, focus: Boolean) {
    if (!focus) return
    view.requestFocus()
    (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

@BindingAdapter("bind:onInputValue")
fun onInputValue(editText: EditText, onInputValue: OnInputValue) {
    val listener = editText.getTag(R.id.onChangedListener) as InternalTextWatcher?
            ?: InternalTextWatcher().also { editText.addTextChangedListener(it) }
    listener.afterTextChanged = onInputValue
    editText.setTag(R.id.onChangedListener, listener)
}

@BindingAdapter("bind:inputValue")
fun inputValue(editText: EditText, value: CharSequence?) {
    val listener = editText.getTag(R.id.onChangedListener) as InternalTextWatcher?
    listener?.isEnabled = false
    if (editText.text != value) {
        editText.setText(value)
        editText.setSelection(editText.length())
    }
    listener?.isEnabled = true
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
