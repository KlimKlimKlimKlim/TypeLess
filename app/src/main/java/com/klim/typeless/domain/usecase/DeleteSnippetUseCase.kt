package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.SnippetRepository
import com.klim.typeless.domain.model.Snippet
import javax.inject.Inject

class DeleteSnippetUseCase @Inject constructor(
    private val repository: SnippetRepository
) {
    suspend operator fun invoke(snippet: Snippet) = repository.delete(snippet)
}