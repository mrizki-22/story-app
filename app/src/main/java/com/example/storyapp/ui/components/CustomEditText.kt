package com.example.storyapp.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomEditText : AppCompatEditText, OnTouchListener {

    private lateinit var showButtonImage: Drawable
    private var isShowingPassword = false
    private var editTextType: String = "text"

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        handleAttributes(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        handleAttributes(attrs)
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        setPadding(40, 40, 40, 40)
    }

    private fun init() {
        showButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_outline_eye_24) as Drawable

        setBackgroundResource(R.drawable.edit_text)

        when (editTextType) {
            "password" -> {
                setButtonDrawables(endOfTheText = showButtonImage)
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            "email" -> {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            "name" -> {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            }
        }

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editTextType == "password") {
                    if (s.toString().length < 8 && s.toString().isNotEmpty()) {
                        val msg = context.getString(R.string.error_min_password_length)
                        setError(msg, null)
                    } else {
                        error = null
                    }
                }

                //email validation
                if (editTextType == "email") {
                    if (s.toString()
                            .isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString())
                            .matches()
                    ) {
                        val msg = context.getString(R.string.error_invalid_email)
                        setError(msg, null)
                    } else {
                        error = null
                    }
                }

                //name validation
                if (editTextType == "name") {

                    if (s.toString().length < 3 && s.toString().isNotEmpty()) {
                        val msg = context.getString(R.string.error_min_name_length)
                        setError(msg, null)
                    } else if (s.toString().matches(".*\\d+.*".toRegex())) {
                        val msg = context.getString(R.string.error_invalid_name)
                        setError(msg, null)
                    } else {
                        error = null
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // Do nothing
            }
        })
    }

    private fun handleAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText)


        when (typedArray.getString(R.styleable.CustomEditText_type)) {
            "email" -> {
                editTextType = "email"
            }
            "password" -> {
                editTextType = "password"
            }
            "name" -> {
                editTextType = "name"
            }
        }

        typedArray.recycle()
    }


    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            val cursorPosition = selectionStart

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (showButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - showButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {

                    MotionEvent.ACTION_UP -> {
                        if (isShowingPassword) {
                            showButtonImage = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_outline_eye_24
                            ) as Drawable
                            inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            isShowingPassword = false
                        } else {
                            showButtonImage = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_filled_eye_24
                            ) as Drawable
                            inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            isShowingPassword = true
                        }
                        setSelection(cursorPosition)
                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }
}