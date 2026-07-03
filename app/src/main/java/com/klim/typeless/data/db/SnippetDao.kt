package com.klim.typeless.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {

    @Query("SELECT * FROM snippets ORDER BY usageCount DESC")
    fun getAllSnippets(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE folder = :folder ORDER BY usageCount DESC")
    fun getByFolder(folder: String): Flow<List<SnippetEntity>>

    @Query("SELECT DISTINCT folder FROM snippets ORDER BY folder ASC")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT * FROM snippets WHERE trigger_text LIKE :trigger || '%' ORDER BY LENGTH(trigger_text) DESC LIMIT 1")
    suspend fun findByTrigger(trigger: String): SnippetEntity?

    @Query("UPDATE snippets SET usageCount = usageCount + 1, savedCharsCount = savedCharsCount + :triggerLength WHERE id = :id")
    suspend fun incrementUsage(id: Int, triggerLength: Int)

    @Query("SELECT * FROM snippets ORDER BY usageCount DESC")
    suspend fun getAllSnippetsOnce(): List<SnippetEntity>

    @Query("SELECT * FROM snippets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): SnippetEntity?

    @Query("UPDATE snippets SET folder = :newName WHERE folder = :oldName")
    suspend fun renameFolder(oldName: String, newName: String)

    @Query("DELETE FROM snippets WHERE folder = :folderName")
    suspend fun deleteFolderSnippets(folderName: String)

    @Query("SELECT COUNT(*) FROM snippets")
    suspend fun getSnippetsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snippet: SnippetEntity)

    @Update
    suspend fun update(snippet: SnippetEntity)

    @Delete
    suspend fun delete(snippet: SnippetEntity)
}