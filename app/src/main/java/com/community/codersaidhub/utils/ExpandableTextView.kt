package com.community.codersaidhub.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat


class ExpandableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle), View.OnClickListener {

    private var originalText: CharSequence? = null
    private var isExpanded = false

    init {
        setOnClickListener(this)
    }

    fun setTextWithEllipsis(text: CharSequence, maxLength: Int) {
        originalText = text
        if (text.length > maxLength) {
            val truncatedText = text.subSequence(0, maxLength).toString() + "... " + "<font color='blue'><u>More</u></font>"
            setText(HtmlCompat.fromHtml(truncatedText, HtmlCompat.FROM_HTML_MODE_LEGACY))
        } else {
            setText(text)
        }
    }

    override fun onClick(v: View?) {
        if (!isExpanded) {
            text = originalText
        } else {
            setTextWithEllipsis(originalText ?: "", maxLength = 100) // Set your desired maxLength
        }
        isExpanded = !isExpanded
    }
}
