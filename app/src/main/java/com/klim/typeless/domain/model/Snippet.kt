package com.klim.typeless.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    val id: Int = 0,
    val trigger: String,
    val content: String,
    val folder: String = "General",
    val arguments: List<String> = emptyList(),
    val usageCount: Int = 0,
    val savedCharsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)