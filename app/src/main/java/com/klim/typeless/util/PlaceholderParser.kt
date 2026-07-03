package com.klim.typeless.util

object PlaceholderParser {

    private val placeholderRegex = Regex("\\{([^}]+)\\}")
    private val argsBlockRegex = Regex("\\{([^}]*)\\}")

    fun extractPlaceholders(content: String): List<String> =
        placeholderRegex.findAll(content).map { it.groupValues[1] }.distinct().toList()

    fun stripTrigger(trigger: String): String =
        trigger.replace(argsBlockRegex, "").trim()

    fun extractArgNames(trigger: String): List<String> {
        val block = argsBlockRegex.find(trigger)?.groupValues?.get(1) ?: return emptyList()
        return block.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun extractArgs(input: String, trigger: String): List<String> {
        val cleanTrigger = stripTrigger(trigger)
        val afterTrigger = input.removePrefix(cleanTrigger)
        val block = argsBlockRegex.find(afterTrigger)?.groupValues?.get(1) ?: return emptyList()
        return block.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun fillNamed(content: String, argNames: List<String>, argValues: List<String>): String {
        val map = argNames.zip(argValues).toMap()
        return placeholderRegex.replace(content) { match ->
            map[match.groupValues[1]] ?: match.value
        }
    }

    fun fillPositional(content: String, args: List<String>): String {
        var index = 0
        return placeholderRegex.replace(content) {
            if (index < args.size) args[index++] else it.value
        }
    }

    fun hasPlaceholders(content: String): Boolean =
        placeholderRegex.containsMatchIn(content)
}