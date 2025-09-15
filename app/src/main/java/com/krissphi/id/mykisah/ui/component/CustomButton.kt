package com.krissphi.id.mykisah.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.krissphi.id.mykisah.R

class CustomButton : AppCompatButton {
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var defaultTextColor: Int = 0

    private var enabledText: CharSequence = text
    private var disabledText: CharSequence? = null

    private enum class ButtonVariant { PRIMARY, SECONDARY }

    constructor(context: Context) : super(context) {
        initAttrs(context, null)
    }

    // Constructor for inflating the view from XML
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    // Constructor for inflating the view from XML with a style
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        var variant: ButtonVariant

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        try {
            variant = ButtonVariant.entries.toTypedArray()[typedArray.getInt(R.styleable.CustomButton_buttonVariant, 0)]
            disabledText = typedArray.getString(R.styleable.CustomButton_disabledText)
        } finally {
            typedArray.recycle()
        }

        when (variant) {
            ButtonVariant.PRIMARY -> {
                enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_primary) as Drawable
                disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_primary_disable) as Drawable
                defaultTextColor = ContextCompat.getColor(context, android.R.color.white)
            }
            ButtonVariant.SECONDARY -> {
                enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_secondary) as Drawable
                disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_secondary_disable) as Drawable
                defaultTextColor = ContextCompat.getColor(context, R.color.coffee)
            }
        }

        gravity = Gravity.CENTER
        setTextColor(defaultTextColor)
        updateButtonState()
    }

    private fun updateButtonState() {
        background = if (isEnabled) enabledBackground else disabledBackground
        if (disabledText != null) {
            text = if (isEnabled) enabledText else disabledText
        }
    }

    override fun setEnabled(enabled: Boolean) {
        if (!::enabledBackground.isInitialized || !::disabledBackground.isInitialized) {
            return
        }

        if (enabled && text != enabledText) {
            enabledText = text
        }
        super.setEnabled(enabled)
        updateButtonState()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

}