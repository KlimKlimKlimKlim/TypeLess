package com.klim.typeless.data.repository

import com.klim.typeless.data.db.SnippetDao
import com.klim.typeless.data.db.SnippetEntity
import com.klim.typeless.domain.model.Snippet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnippetRepository @Inject constructor(
    private val dao: SnippetDao
) {

    fun getAllSnippets(): Flow<List<Snippet>> =
        dao.getAllSnippets().map { list -> list.map { it.toDomain() } }

    fun getByFolder(folder: String): Flow<List<Snippet>> =
        dao.getByFolder(folder).map { list -> list.map { it.toDomain() } }

    fun getAllFolders(): Flow<List<String>> =
        dao.getAllFolders()

    suspend fun findByTrigger(trigger: String): Snippet? =
        dao.findByTrigger(trigger)?.toDomain()

    suspend fun incrementUsage(id: Int, triggerLength: Int) =
        dao.incrementUsage(id, triggerLength)

    suspend fun save(snippet: Snippet) =
        dao.insert(snippet.toEntity())

    suspend fun update(snippet: Snippet) =
        dao.update(snippet.toEntity())

    suspend fun delete(snippet: Snippet) =
        dao.delete(snippet.toEntity())

    suspend fun getSnippetsCount(): Int =
        dao.getSnippetsCount()

    suspend fun exportToStream(outputStream: OutputStream) {
        val snippets = dao.getAllSnippetsOnce().map { it.toDomain() }
        outputStream.use { it.write(Json.encodeToString(snippets).toByteArray()) }
    }

    suspend fun getSnippetById(id: Int): Snippet? =
        dao.getById(id)?.toDomain()

    suspend fun renameFolder(oldName: String, newName: String) =
        dao.renameFolder(oldName, newName)

    suspend fun deleteFolder(folderName: String) =
        dao.deleteFolderSnippets(folderName)

    private fun SnippetEntity.toDomain() = Snippet(
        id = id,
        trigger = trigger,
        content = content,
        folder = folder,
        arguments = arguments,
        usageCount = usageCount,
        savedCharsCount = savedCharsCount,
        createdAt = createdAt
    )

    private fun Snippet.toEntity() = SnippetEntity(
        id = id,
        trigger = trigger,
        content = content,
        folder = folder,
        arguments = arguments,
        usageCount = usageCount,
        savedCharsCount = savedCharsCount,
        createdAt = createdAt
    )
}