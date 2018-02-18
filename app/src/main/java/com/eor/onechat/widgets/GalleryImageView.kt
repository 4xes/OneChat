package com.eor.onechat.widgets

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.eor.onechat.R
import com.eor.onechat.data.model.Place
import com.squareup.picasso.Picasso

class GalleryImageView : CardView {
    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    lateinit var preview: ImageView
    lateinit var title: TextView
    lateinit var subtitle: TextView


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        initViews(context)
    }

    private fun initViews(context: Context) {
        inflate(context, R.layout.layout_card, this)

        radius = resources.getDimension(R.dimen.card_radius)
        preventCornerOverlap = true
        useCompatPadding = true

        preview = findViewById(R.id.preview)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
    }

    fun bind(place: Place) {
        Picasso.with(preview.context)
                .load(place.preview)
                .into(preview)
        title.text = place.title
        subtitle.text = place.subtile
    }

}