package com.eor.onechat.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import com.eor.onechat.R
import com.eor.onechat.data.model.Actions
import kotlinx.android.synthetic.main.layout_buttons.view.*

class ActionsView : HorizontalScrollView {
    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        initViews(context)
    }

    private fun initViews(context: Context) {
        inflate(context, R.layout.layout_buttons, this)
    }

    fun bind(actions: Actions) {
        if (actions.actions.isNotEmpty()) {
            button1.text = actions.actions[0].text
            button1.setOnClickListener {
                actions.actions[0].action?.invoke()
            }
            button1.visibility = View.VISIBLE
        } else {
            button1.text = ""
            button1.visibility = View.GONE
        }

        if (actions.actions.size > 1) {
            button2.text = actions.actions[1].text
            button2.setOnClickListener {
                actions.actions[1].action?.invoke()
            }
            button2.visibility = View.VISIBLE
        } else {
            button2.text = ""
            button2.visibility = View.GONE
        }
    }

}