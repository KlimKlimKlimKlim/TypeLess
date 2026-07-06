package com.klim.typeless.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.klim.typeless.domain.usecase.SaveSnippetUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val trigger by viewModel.trigger.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val folder by viewModel.folder.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val triggerError by viewModel.triggerError.collectAsStateWithLifecycle()
    val saveError by viewModel.saveError.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()

    var dropdownExpanded by remember { mutableStateOf(false) }
    val filteredFolders = folders.filter {
        it.contains(folder, ignoreCase = true) && it != folder
    }

    LaunchedEffect(saved) {
        if (saved) navController.popBackStack()
    }

    if (saveError != null) {
        val message = when (saveError) {
            SaveSnippetUseCase.Result.LimitReached ->
                "В бесплатной версии можно сохранить не более 5 сниппетов. Открой Premium, чтобы снять ограничение."
            SaveSnippetUseCase.Result.FolderRestricted ->
                "В бесплатной версии доступна только папка General. Открой Premium, чтобы создавать свои папки."
            SaveSnippetUseCase.Result.ArgumentsRestricted ->
                "Аргументы и переменные ({name}, {дата}, {время}, {буфер}) доступны только в Premium. Открой Premium, чтобы использовать их в сниппетах."
            else -> null
        }

        if (message != null) {
            AlertDialog(
                onDismissRequest = viewModel::clearSaveError,
                title = { Text("Нужен Premium") },
                text = { Text(message) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearSaveError()
                            navController.navigate("paywall")
                        }
                    ) {
                        Text("Открыть Premium")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::clearSaveError) {
                        Text("Отмена")
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isEditMode) {
                            "Редактировать сниппет"
                        } else {
                            "Новый сниппет"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TextField(
                value = trigger,
                onValueChange = viewModel::onTriggerChange,
                placeholder = { Text("Триггер, например /привет{name}") },
                singleLine = true,
                isError = triggerError != null,
                supportingText = {
                    Text(triggerError ?: "Например: /тел или /привет{name}")
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = content,
                onValueChange = viewModel::onContentChange,
                placeholder = { Text("Привет, {name}! Рад познакомиться.") },
                minLines = 6,
                supportingText = {
                    Text("Аргументы вроде {name}, {дата}, {время}, {буфер} доступны в Premium.")
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded && filteredFolders.isNotEmpty(),
                onExpandedChange = { dropdownExpanded = it }
            ) {
                TextField(
                    value = folder,
                    onValueChange = {
                        viewModel.onFolderChange(it)
                        dropdownExpanded = true
                    },
                    placeholder = { Text("Папка") },
                    singleLine = true,
                    trailingIcon = {
                        if (filteredFolders.isNotEmpty()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = dropdownExpanded
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded && filteredFolders.isNotEmpty(),
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    filteredFolders.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion) },
                            onClick = {
                                viewModel.onFolderChange(suggestion)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = viewModel::save,
                enabled = trigger.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Сохранить",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}