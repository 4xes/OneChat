package com.eor.onechat.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import com.eor.onechat.R
import com.eor.onechat.data.model.Places
import kotlinx.android.synthetic.main.layout_gallery.view.*

class PlacesView : HorizontalScrollView {
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
        inflate(context, R.layout.layout_gallery, this)
    }

    fun bind(places: Places) {
        card1.bind(places.places[0])
        card2.bind(places.places[1])
    }

}