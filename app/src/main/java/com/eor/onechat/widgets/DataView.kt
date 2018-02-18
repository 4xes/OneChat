package com.eor.onechat.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.eor.onechat.R
import com.eor.onechat.data.model.Data
import kotlinx.android.synthetic.main.layout_data.view.*

class DataView : FrameLayout {
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
        inflate(context, R.layout.layout_data, this)
    }

    fun bind(data: Data) {
        text.text = data.text

        var hasTop = false
        if (!data.text.isNullOrBlank()) {
            text.text = data.text
            text.visibility = View.VISIBLE
            hasTop = true
        } else {
            text.visibility = View.GONE
        }

        var hasBottom = false
        if (!data.title.isNullOrBlank()) {
            title.text = data.title
            title.visibility = View.VISIBLE
            hasBottom = true
        } else {
            title.visibility = View.GONE
        }

        if (!data.subtitle.isNullOrBlank()) {
            subtitle.text = data.subtitle
            subtitle.visibility = View.VISIBLE
            hasBottom = true
        } else {
            subtitle.visibility = View.GONE
        }

        divider.visibility = if (hasTop && hasBottom) View.VISIBLE else View.GONE
    }

}