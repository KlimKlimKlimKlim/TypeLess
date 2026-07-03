package com.klim.typeless.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.domain.model.Snippet
import com.klim.typeless.domain.usecase.DeleteSnippetUseCase
import com.klim.typeless.domain.usecase.GetSnippetsByFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSnippetsByFolder: GetSnippetsByFolderUseCase,
    private val deleteSnippet: DeleteSnippetUseCase
) : ViewModel() {

    private val folderName: String = checkNotNull(savedStateHandle["folderName"])

    val snippets: StateFlow<List<Snippet>> = getSnippetsByFolder(folderName)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _pendingDelete = MutableStateFlow<Snippet?>(null)
    val pendingDelete: StateFlow<Snippet?> = _pendingDelete.asStateFlow()

    fun requestDelete(snippet: Snippet) {
        _pendingDelete.value = snippet
    }

    fun confirmDelete() {
        _pendingDelete.value?.let { snippet ->
            viewModelScope.launch { deleteSnippet(snippet) }
        }
        _pendingDelete.value = null
    }

    fun cancelDelete() {
        _pendingDelete.value = null
    }
}