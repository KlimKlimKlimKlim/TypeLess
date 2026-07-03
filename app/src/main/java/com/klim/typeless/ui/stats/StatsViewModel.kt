package com.klim.typeless.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klim.typeless.domain.model.Snippet
import com.klim.typeless.domain.usecase.GetSnippetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FolderStats(
    val folder: String,
    val snippetCount: Int,
    val totalUsages: Int,
    val totalSavedChars: Int,
    val topSnippets: List<Snippet>
)

data class StatsUiState(
    val totalSnippets: Int = 0,
    val totalUsages: Int = 0,
    val totalSavedChars: Int = 0,
    val topSnippets: List<Snippet> = emptyList(),
    val folderStats: List<FolderStats> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    getSnippets: GetSnippetsUseCase
) : ViewModel() {

    val state: StateFlow<StatsUiState> = getSnippets()
        .map { list ->
            val byFolder = list.groupBy { it.folder }
            StatsUiState(
                totalSnippets = list.size,
                totalUsages = list.sumOf { it.usageCount },
                totalSavedChars = list.sumOf { it.savedCharsCount },
                topSnippets = list.sortedByDescending { it.usageCount }.take(5),
                folderStats = byFolder.map { (folder, snippets) ->
                    FolderStats(
                        folder = folder,
                        snippetCount = snippets.size,
                        totalUsages = snippets.sumOf { it.usageCount },
                        totalSavedChars = snippets.sumOf { it.savedCharsCount },
                        topSnippets = snippets.sortedByDescending { it.usageCount }.take(3)
                    )
                }.sortedByDescending { it.totalUsages }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState()
        )
}