package com.klim.typeless.util

import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VariableExpander @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun expand(content: String): String {
        var result = content
        result = result.replaceIfContains("{дата}") {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        }
        result = result.replaceIfContains("{время}") {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
        result = result.replaceIfContains("{буфер}") {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
        }
        return result
    }

    private fun String.replaceIfContains(pattern: String, replacement: () -> String): String =
        if (contains(pattern)) replace(pattern, replacement()) else this
}