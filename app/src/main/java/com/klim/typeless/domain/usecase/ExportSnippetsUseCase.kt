package com.klim.typeless.domain.usecase

import com.klim.typeless.data.repository.SnippetRepository
import java.io.OutputStream
import javax.inject.Inject

class ExportSnippetsUseCase @Inject constructor(
    private val repository: SnippetRepository
) {
    suspend operator fun invoke(outputStream: OutputStream) =
        repository.exportToStream(outputStream)
}