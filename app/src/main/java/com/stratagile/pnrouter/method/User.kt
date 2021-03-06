package com.stratagile.pnrouter.method

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.iyao.eastat.span.DataBindingSpan
import com.iyao.eastat.span.DirtySpan
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig

data class User(val id: String, var name: String,var content:String): DataBindingSpan,
                                                   DirtySpan {

    fun getSpannedName(colorFlag:Int): Spannable {
        return SpannableString("$name").apply {
            var color = AppConfig.instance.getResources().getColor(R.color.sent_contacts)
            if(colorFlag != 0)
            {
                color = colorFlag
            }
            setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun isDirty(text: Spannable): Boolean {
        val spanStart = text.getSpanStart(this)
        val spanEnd = text.getSpanEnd(this)
        return spanStart >= 0 && spanEnd >= 0 && text.substring(spanStart, spanEnd) != "$name"
    }
}