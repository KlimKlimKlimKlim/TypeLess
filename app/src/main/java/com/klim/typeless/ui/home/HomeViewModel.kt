package com.klim.typeless.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.domain.usecase.GetFoldersUseCase
import com.klim.typeless.domain.usecase.ObserveUnlockStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getFolders: GetFoldersUseCase,
    observeUnlockStatus: ObserveUnlockStatusUseCase,
    private val repository: SnippetRepository
) : ViewModel() {

    val folders: StateFlow<List<String>> = getFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val isUnlocked: StateFlow<Boolean> = observeUnlockStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    private val _snippetsCount = MutableStateFlow(0)
    val snippetsCount: StateFlow<Int> = _snippetsCount.asStateFlow()

    private val _pendingDeleteFolder = MutableStateFlow<String?>(null)
    val pendingDeleteFolder: StateFlow<String?> = _pendingDeleteFolder.asStateFlow()

    private val _renameTarget = MutableStateFlow<String?>(null)
    val renameTarget: StateFlow<String?> = _renameTarget.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllSnippets().collect { list ->
                _snippetsCount.value = list.size
            }
        }
    }

    fun requestDeleteFolder(folderName: String) {
        _pendingDeleteFolder.value = folderName
    }

    fun confirmDeleteFolder() {
        _pendingDeleteFolder.value?.let { name ->
            viewModelScope.launch { repository.deleteFolder(name) }
        }
        _pendingDeleteFolder.value = null
    }

    fun cancelDeleteFolder() {
        _pendingDeleteFolder.value = null
    }

    fun requestRename(folderName: String) {
        _renameTarget.value = folderName
    }

    fun confirmRename(newName: String) {
        _renameTarget.value?.let { oldName ->
            if (newName.isNotBlank() && newName != oldName) {
                viewModelScope.launch { repository.renameFolder(oldName, newName) }
            }
        }
        _renameTarget.value = null
    }

    fun cancelRename() {
        _renameTarget.value = null
    }
}