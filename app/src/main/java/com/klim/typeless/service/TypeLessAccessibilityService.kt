package com.klim.typeless.service

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.util.PlaceholderParser
import com.klim.typeless.util.VariableExpander
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class TypeLessAccessibilityService : AccessibilityService() {

    @Inject lateinit var repository: SnippetRepository
    @Inject lateinit var variableExpander: VariableExpander

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var activeJob: Job? = null

    private val withArgsRegex = Regex("""(/[a-zA-Zа-яА-ЯёЁ]+)\{([^}]*)\}""")
    private val noArgsRegex = Regex("""(/[a-zA-Zа-яА-ЯёЁ]+)""")

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return
        val text = event.text.firstOrNull()?.toString() ?: return
        val sourceNode = event.source ?: return

        activeJob?.cancel()
        activeJob = serviceScope.launch {
            try {
                val match = extractTriggerWithInput(text) ?: return@launch

                val cleanTrigger = PlaceholderParser.stripTrigger(match.rawTrigger)
                val snippet = repository.findByTrigger(cleanTrigger) ?: return@launch

                val expanded = variableExpander.expand(snippet.content)

                val filled = if (match.hasArgs && PlaceholderParser.hasPlaceholders(expanded)) {
                    val argNames = PlaceholderParser.extractArgNames(snippet.trigger)
                    val argValues = PlaceholderParser.extractArgs(match.fullMatch, cleanTrigger)
                    if (argNames.isNotEmpty()) {
                        PlaceholderParser.fillNamed(expanded, argNames, argValues)
                    } else {
                        PlaceholderParser.fillPositional(expanded, argValues)
                    }
                } else {
                    expanded
                }

                val newText = text.replace(match.fullMatch, filled)

                withContext(Dispatchers.Main) {
                    try {
                        val arguments = Bundle().apply {
                            putCharSequence(
                                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                                newText
                            )
                        }
                        sourceNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    } catch (e: Exception) {
                        Log.e("TypeLessService", "performAction failed", e)
                    }
                }

                repository.incrementUsage(snippet.id, filled.length - cleanTrigger.length)
            } catch (e: Exception) {
                Log.e("TypeLessService", "onAccessibilityEvent processing failed", e)
            } finally {
                try {
                    sourceNode.recycle()
                } catch (e: Exception) {
                    Log.e("TypeLessService", "sourceNode.recycle failed", e)
                }
            }
        }
    }

    private data class TriggerMatch(
        val rawTrigger: String,
        val fullMatch: String,
        val hasArgs: Boolean
    )

    private fun extractTriggerWithInput(text: String): TriggerMatch? {
        val trimmed = text.trimEnd()

        if (trimmed.endsWith("}")) {
            val match = withArgsRegex.findAll(trimmed).lastOrNull()
            if (match != null) {
                return TriggerMatch(
                    rawTrigger = match.groupValues[1],
                    fullMatch = match.value,
                    hasArgs = true
                )
            }
        }

        if (trimmed.endsWith(" ") || text.endsWith(" ")) {
            val words = text.trimEnd().split(" ")
            val lastWord = words.lastOrNull() ?: return null
            if (lastWord.startsWith("/") && lastWord.length > 1) {
                return TriggerMatch(
                    rawTrigger = lastWord,
                    fullMatch = lastWord,
                    hasArgs = false
                )
            }
            return null
        }

        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        activeJob?.cancel()
        serviceScope.cancel()
    }

    override fun onInterrupt() {}
}