package com.klim.typeless.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.domain.model.Snippet
import com.klim.typeless.domain.usecase.GetFoldersUseCase
import com.klim.typeless.domain.usecase.SaveSnippetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val saveSnippet: SaveSnippetUseCase,
    private val repository: SnippetRepository,
    private val getFolders: GetFoldersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val snippetId: Int = savedStateHandle.get<String>("snippetId")?.toIntOrNull() ?: -1
    private val defaultFolder: String = savedStateHandle.get<String>("defaultFolder") ?: "General"

    private val _trigger = MutableStateFlow("")
    val trigger: StateFlow<String> = _trigger.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _folder = MutableStateFlow(defaultFolder)
    val folder: StateFlow<String> = _folder.asStateFlow()

    private val _triggerError = MutableStateFlow<String?>(null)
    val triggerError: StateFlow<String?> = _triggerError.asStateFlow()

    private val _saveError = MutableStateFlow<SaveSnippetUseCase.Result?>(null)
    val saveError: StateFlow<SaveSnippetUseCase.Result?> = _saveError.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    val folders: StateFlow<List<String>> = getFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val isEditMode: Boolean = snippetId != -1

    init {
        if (snippetId != -1) loadSnippet(snippetId)
    }

    private fun loadSnippet(id: Int) {
        viewModelScope.launch {
            repository.getSnippetById(id)?.let { snippet ->
                _trigger.value = snippet.trigger
                _content.value = snippet.content
                _folder.value = snippet.folder
            }
        }
    }

    fun onTriggerChange(value: String) {
        _trigger.value = value
        _triggerError.value = null
    }

    fun onContentChange(value: String) {
        _content.value = value
    }

    fun onFolderChange(value: String) {
        _folder.value = value
    }

    fun save() {
        if (_trigger.value.isBlank() || _content.value.isBlank()) return
        viewModelScope.launch {
            val trimmedTrigger = _trigger.value.trim()
            val existing = repository.findByTrigger(trimmedTrigger)
            if (existing != null && existing.id != snippetId) {
                _triggerError.value = "Триггер «$trimmedTrigger» уже используется"
                return@launch
            }

            val arguments = extractArguments(_content.value)

            val result = saveSnippet(
                Snippet(
                    id = if (snippetId == -1) 0 else snippetId,
                    trigger = trimmedTrigger,
                    content = _content.value.trim(),
                    folder = _folder.value.trim().ifBlank { "General" },
                    arguments = arguments
                )
            )

            when (result) {
                SaveSnippetUseCase.Result.Success -> _saved.value = true
                else -> _saveError.value = result
            }
        }
    }

    fun clearSaveError() {
        _saveError.value = null
    }

    private fun extractArguments(content: String): List<String> {
        val regex = Regex("\\{([^}]+)\\}")
        return regex.findAll(content)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()
    }
}