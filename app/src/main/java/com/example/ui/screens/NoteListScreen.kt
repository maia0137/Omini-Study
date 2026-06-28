package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Note
import com.example.ui.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NotesViewModel,
    onNavigateToEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.allNotes.collectAsState()
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD3E3FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Σ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF041E49)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "OmniStudy",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Pronto para rascunhar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    // Quick stats/info pill
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (notes.isEmpty()) "Vazio" else "${notes.size} Notas",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Bento Grid: Header / Split View status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isSystemInDarkTheme()) {
                            Color(0xFF1F2937) // Slate 800
                        } else {
                            Color(0xFFF3F4F6) // Slate 100
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // Green-500
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Otimizado para Split View",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSystemInDarkTheme()) Color(0xFFD1D5DB) else Color(0xFF4B5563)
                    )
                }
                Text(
                    text = "GITHUB SYNC",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSystemInDarkTheme()) Color(0xFF9CA3AF) else Color(0xFF9CA3AF)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bento Grid Template Buttons
            Text(
                text = "Rascunhos Pré-formatados (Bento Templates)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // First row of Bento Templates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoTemplateBtn(
                    title = "Matemática",
                    subtitle = "Resolução",
                    iconString = "√x",
                    containerColor = Color(0xFFD3E3FD),
                    contentColor = Color(0xFF041E49),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createNewNote("MATEMATICA")
                        onNavigateToEditor()
                    }
                )
                BentoTemplateBtn(
                    title = "Redação",
                    subtitle = "Estrutura de",
                    iconString = "✍",
                    containerColor = Color(0xFFEADDFF),
                    contentColor = Color(0xFF21005D),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createNewNote("REDACAO")
                        onNavigateToEditor()
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Second row of Bento Templates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoTemplateBtn(
                    title = "Resumo",
                    subtitle = "Tópicos de",
                    iconString = "📋",
                    containerColor = Color(0xFFD1E7DD),
                    contentColor = Color(0xFF0F5132),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createNewNote("RESUMO")
                        onNavigateToEditor()
                    }
                )
                BentoTemplateBtn(
                    title = "Livre",
                    subtitle = "Desenho",
                    iconString = "🎨",
                    containerColor = Color(0xFFE7D1E7),
                    contentColor = Color(0xFF510F51),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createNewNote("LIVRE")
                        onNavigateToEditor()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notes Section
            Text(
                text = "Meus Rascunhos Recentes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Toque em um dos botões acima para começar!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(
                            note = note,
                            onClick = {
                                viewModel.selectNote(note)
                                onNavigateToEditor()
                            },
                            onDelete = {
                                noteToDelete = note
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Excluir Rascunho?") },
            text = { Text("Tem certeza que deseja excluir o rascunho \"${note.title}\"? Esta ação é irreversível.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(note) {
                            noteToDelete = null
                        }
                    },
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun BentoTemplateBtn(
    title: String,
    subtitle: String,
    iconString: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .height(110.dp)
            .testTag("btn_create_${title.lowercase()}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconString,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            Column {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.7f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val relativeTime = DateUtils.getRelativeTimeSpanString(
        note.updatedAt,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()

    val isDark = isSystemInDarkTheme()
    val badgeText = when (note.type) {
        "MATEMATICA" -> "Matemática"
        "REDACAO" -> "Redação"
        "RESUMO" -> "Resumo"
        else -> "Livre"
    }
    val badgeColor = when (note.type) {
        "MATEMATICA" -> if (isDark) Color(0xFFA8C7FA) else Color(0xFF041E49)
        "REDACAO" -> if (isDark) Color(0xFFEADDFF) else Color(0xFF21005D)
        "RESUMO" -> if (isDark) Color(0xFF81C784) else Color(0xFF0F5132)
        else -> if (isDark) Color(0xFFE1BEE7) else Color(0xFF510F51)
    }
    val badgeBg = when (note.type) {
        "MATEMATICA" -> if (isDark) Color(0xFF041E49) else Color(0xFFD3E3FD).copy(alpha = 0.8f)
        "REDACAO" -> if (isDark) Color(0xFF21005D) else Color(0xFFEADDFF).copy(alpha = 0.8f)
        "RESUMO" -> if (isDark) Color(0xFF0F5132) else Color(0xFFD1E7DD).copy(alpha = 0.8f)
        else -> if (isDark) Color(0xFF510F51) else Color(0xFFE7D1E7).copy(alpha = 0.8f)
    }
    val cardBg = when (note.type) {
        "MATEMATICA" -> if (isDark) Color(0xFF1E2530) else Color(0xFFF7F9FF)
        "REDACAO" -> if (isDark) Color(0xFF231E2A) else Color(0xFFFAF5FF)
        "RESUMO" -> if (isDark) Color(0xFF18241D) else Color(0xFFF2FDF6)
        else -> if (isDark) Color(0xFF241824) else Color(0xFFFDF2FD)
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("note_card_${note.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Badge Type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("delete_note_btn_${note.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Excluir Rascunho",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Preview subtitle
            val subtitle = when (note.type) {
                "MATEMATICA" -> note.problemStatement.ifBlank { "Sem enunciado anotado" }
                "REDACAO" -> note.essayTheme.ifBlank { "Sem tema definido" }
                "RESUMO" -> note.summaryTitle.ifBlank { "Sem título de resumo" }
                else -> "Desenho e anotações livres"
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(32.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            
            // Last Updated Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = relativeTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
