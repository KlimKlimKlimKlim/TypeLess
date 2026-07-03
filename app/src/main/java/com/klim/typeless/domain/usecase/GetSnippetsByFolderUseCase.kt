package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.domain.model.Snippet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSnippetsByFolderUseCase @Inject constructor(
    private val repository: SnippetRepository
) {
    operator fun invoke(folder: String): Flow<List<Snippet>> = repository.getByFolder(folder)
}