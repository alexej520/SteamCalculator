package ru.lextop.steamcalculator.ui

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.layoutInflater
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.databinding.DialogYesnoBinding

class RateView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {
    var onRatedListener: ((success: Boolean, positive: Boolean) -> Unit)? = null
    private var positive = true
    private var success = true
    private var nextState: Int = 0
    private val transitionListener = object : LayoutTransition.TransitionListener {
        override fun startTransition(
            transition: LayoutTransition,
            container: ViewGroup,
            target: View,
            transitionType: Int
        ) {
        }

        override fun endTransition(
            transition: LayoutTransition,
            container: ViewGroup,
            target: View,
            transitionType: Int
        ) {
            if (transitionType == LayoutTransition.DISAPPEARING) {
                setContent(nextState)
            }
        }
    }

    init {
        // by default LinearLayout has transparent background
        backgroundColor = TypedValue().also {
            context.theme.resolveAttribute(
                android.R.attr.windowBackground,
                it,
                true
            )
        }.data
        layoutTransition = LayoutTransition().apply {
            R.style.Widget_Design_Snackbar
            addTransitionListener(transitionListener)
        }
        addView(
            ImageView(context).apply {
                setPadding(context.dip(8), context.dip(8), 0, context.dip(8))
                setImageResource(R.mipmap.ic_launcher_round)
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        setContent(ASK_LIKE)
    }

    companion object {
        private const val COMPLETED = -1
        private const val ASK_LIKE = 0
        private const val ASK_GP_FEEDBACK = 1
        private const val ASK_EMAIL_FEEDBACK = 2
    }

    private fun setState(state: Int) {
        nextState = state
        this.removeViewAt(1)
    }

    private fun yesNo(string: String, yes: OnClickListener, no: OnClickListener) {
        val binding = DialogYesnoBinding.inflate(context.layoutInflater, this@RateView, true)
        binding.text = string
        binding.onYes = yes
        binding.onNo = no
    }

    private fun setContent(state: Int) {
        when (state) {
            ASK_LIKE -> yesNo(context.getString(
                R.string.rateViewAskLike,
                context.getString(R.string.app_name)
            ),
                yes = OnClickListener { positive = true; setState(ASK_GP_FEEDBACK) },
                no = OnClickListener { positive = false; setState(ASK_EMAIL_FEEDBACK) })
            ASK_GP_FEEDBACK -> yesNo(context.getString(R.string.rateViewAskGPFeedback),
                yes = OnClickListener { success = true; setState(COMPLETED) },
                no = OnClickListener { success = false; setState(COMPLETED) })
            ASK_EMAIL_FEEDBACK -> yesNo(context.getString(R.string.rateViewAskEmailFeedback),
                yes = OnClickListener { success = true; setState(COMPLETED) },
                no = OnClickListener { success = false; setState(COMPLETED) })
            COMPLETED -> onRatedListener?.invoke(success, positive)
        }
    }
}
