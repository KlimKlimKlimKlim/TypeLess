package com.klim.typeless.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "trigger_text") val trigger: String,
    val content: String,
    val folder: String = "General",
    val arguments: List<String> = emptyList(),
    val usageCount: Int = 0,
    val savedCharsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)