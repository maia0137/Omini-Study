package com.example.ui.screens

import androidx.activity.compose.BackHandler
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VerticalSplit
import com.example.data.Stroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NotesViewModel
import com.example.ui.components.DrawingCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val note by viewModel.currentNote.collectAsState()
    val strokes by viewModel.strokes.collectAsState()
    val redoStack by viewModel.redoStack.collectAsState()
    val selectedColor by viewModel.selectedColor.collectAsState()
    val selectedWidth by viewModel.selectedWidth.collectAsState()
    val gridType by viewModel.gridType.collectAsState()

    // Title state
    val title by viewModel.editedTitle.collectAsState()

    // Automatically save and navigate back on system back gesture
    BackHandler {
        viewModel.saveCurrentNote {
            onNavigateBack()
        }
    }

    // 3 Layout Modes:
    // "SPLIT" - split view text and canvas
    // "TEXT" - text template maximized
    // "CANVAS" - freehand drawing canvas maximized
    var layoutMode by remember { mutableStateOf("SPLIT") }
    
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum rascunho selecionado.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentNote {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar e Salvar"
                        )
                    }
                },
                title = {
                    TextField(
                        value = title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = { Text("Título do Rascunho") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_input")
                    )
                },
                actions = {
                    // Layout Selector buttons
                    IconButton(
                        onClick = { layoutMode = "TEXT" },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (layoutMode == "TEXT") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ),
                        modifier = Modifier.testTag("layout_text_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = "Ver apenas texto"
                        )
                    }

                    IconButton(
                        onClick = { layoutMode = "SPLIT" },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (layoutMode == "SPLIT") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ),
                        modifier = Modifier.testTag("layout_split_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerticalSplit,
                            contentDescription = "Ver tela dividida"
                        )
                    }

                    IconButton(
                        onClick = { layoutMode = "CANVAS" },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (layoutMode == "CANVAS") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ),
                        modifier = Modifier.testTag("layout_canvas_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = "Ver apenas rascunho"
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Save Button
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentNote {
                                Toast.makeText(context, "Rascunho salvo!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Salvar Rascunho",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val isLandscape = maxWidth > maxHeight || maxWidth >= 600.dp
            
            // Dynamic structure based on layout mode selection and screen size (landscape tablets/split screen vs portrait phone)
            if (layoutMode == "TEXT") {
                // Maximize template text fields
                Box(modifier = Modifier.fillMaxSize()) {
                    TemplateFieldsColumn(viewModel = viewModel, type = note!!.type)
                }
            } else if (layoutMode == "CANVAS") {
                // Maximize drawing pad with its toolbar
                Column(modifier = Modifier.fillMaxSize()) {
                    DrawingCanvasToolbar(
                        viewModel = viewModel,
                        strokes = strokes,
                        redoStack = redoStack,
                        selectedColor = selectedColor,
                        selectedWidth = selectedWidth,
                        gridType = gridType,
                        isDark = isDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DrawingCanvas(
                        strokes = strokes,
                        onStrokeStarted = { viewModel.startNewStroke(it) },
                        onPointAdded = { viewModel.appendPointToLastStroke(it) },
                        gridType = gridType,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // SPLIT mode: dynamically distribute spaces side-by-side or stacked
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Left Column: Template fields
                        Box(modifier = Modifier.weight(1.1f)) {
                            TemplateFieldsColumn(viewModel = viewModel, type = note!!.type)
                        }
                        
                        // Right Column: Canvas + Toolbar
                        Column(modifier = Modifier.weight(1.2f)) {
                            DrawingCanvasToolbar(
                                viewModel = viewModel,
                                strokes = strokes,
                                redoStack = redoStack,
                                selectedColor = selectedColor,
                                selectedWidth = selectedWidth,
                                gridType = gridType,
                                isDark = isDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DrawingCanvas(
                                strokes = strokes,
                                onStrokeStarted = { viewModel.startNewStroke(it) },
                                onPointAdded = { viewModel.appendPointToLastStroke(it) },
                                gridType = gridType,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    // Mobile Split: 40% height template fields, 60% height Canvas
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.75f)
                        ) {
                            TemplateFieldsColumn(viewModel = viewModel, type = note!!.type)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.25f)
                        ) {
                            DrawingCanvasToolbar(
                                viewModel = viewModel,
                                strokes = strokes,
                                redoStack = redoStack,
                                selectedColor = selectedColor,
                                selectedWidth = selectedWidth,
                                gridType = gridType,
                                isDark = isDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DrawingCanvas(
                                strokes = strokes,
                                onStrokeStarted = { viewModel.startNewStroke(it) },
                                onPointAdded = { viewModel.appendPointToLastStroke(it) },
                                gridType = gridType,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateFieldsColumn(
    viewModel: NotesViewModel,
    type: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (type) {
                "MATEMATICA" -> {
                    val problem by viewModel.editedProblemStatement.collectAsState()
                    val solution by viewModel.editedStepByStepSolution.collectAsState()

                    Text(
                        text = "Enunciado do Problema",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = problem,
                        onValueChange = { viewModel.updateProblemStatement(it) },
                        placeholder = { Text("Insira a equação ou problema matemático aqui...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("math_problem_input"),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Resolução Passo a Passo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = solution,
                        onValueChange = { viewModel.updateStepByStepSolution(it) },
                        placeholder = { Text("Escreva a explicação lógica ou passo a passo aqui...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("math_solution_input"),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                "REDACAO" -> {
                    val theme by viewModel.editedEssayTheme.collectAsState()
                    val intro by viewModel.editedEssayIntro.collectAsState()
                    val d1 by viewModel.editedEssayD1.collectAsState()
                    val d2 by viewModel.editedEssayD2.collectAsState()
                    val concl by viewModel.editedEssayConclusion.collectAsState()

                    Text(
                        text = "Tema da Redação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedTextField(
                        value = theme,
                        onValueChange = { viewModel.updateEssayTheme(it) },
                        placeholder = { Text("Tema proposto...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("essay_theme_input"),
                        singleLine = true
                    )

                    Text(
                        text = "Introdução (Tese)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = intro,
                        onValueChange = { viewModel.updateEssayIntro(it) },
                        placeholder = { Text("Contextualização e tese...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("essay_intro_input")
                    )

                    Text(
                        text = "Desenvolvimento 1 (Argumento)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = d1,
                        onValueChange = { viewModel.updateEssayD1(it) },
                        placeholder = { Text("Primeiro argumento de apoio...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("essay_d1_input")
                    )

                    Text(
                        text = "Desenvolvimento 2 (Argumento)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = d2,
                        onValueChange = { viewModel.updateEssayD2(it) },
                        placeholder = { Text("Segundo argumento de apoio...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("essay_d2_input")
                    )

                    Text(
                        text = "Conclusão (Proposta)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = concl,
                        onValueChange = { viewModel.updateEssayConclusion(it) },
                        placeholder = { Text("Proposta de intervenção e fechamento...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("essay_concl_input")
                    )
                }

                "RESUMO" -> {
                    val summaryTitle by viewModel.editedSummaryTitle.collectAsState()
                    val topics by viewModel.editedSummaryTopics.collectAsState()
                    val notes by viewModel.editedSummaryNotes.collectAsState()

                    Text(
                        text = "Assunto do Resumo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    OutlinedTextField(
                        value = summaryTitle,
                        onValueChange = { viewModel.updateSummaryTitle(it) },
                        placeholder = { Text("Ex: Botânica - Angiospermas") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("summary_title_input"),
                        singleLine = true
                    )

                    Text(
                        text = "Tópicos Principais",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = topics,
                        onValueChange = { viewModel.updateSummaryTopics(it) },
                        placeholder = { Text("• Tópico 1\n• Tópico 2...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("summary_topics_input")
                    )

                    Text(
                        text = "Anotações Adicionais",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.updateSummaryNotes(it) },
                        placeholder = { Text("Conceitos, fórmulas extras...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("summary_notes_input")
                    )
                }

                else -> {
                    // LIVRE Mode
                    Text(
                        text = "Espaço Livre para Rascunho",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Este rascunho é focado 100% no desenho livre com o dedo. Use os controles de cor e espessura no painel ao lado para anotar o que precisar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DrawingCanvasToolbar(
    viewModel: NotesViewModel,
    strokes: List<Stroke>,
    redoStack: List<Stroke>,
    selectedColor: Long,
    selectedWidth: Float,
    gridType: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    // Dynamic default paint color
    val defaultPenColor = if (isDark) 0xFFF5F5F5 else 0xFF1C1B1F

    val colorOptions = listOf(
        defaultPenColor,             // Primary White/Black
        0xFF1976D2,                 // Blue
        0xFFD32F2F,                 // Red
        0xFF388E3C,                 // Green
        0xFFFF9800,                 // Orange
        0x11111111L                 // Magic Eraser constant
    )

    val widthOptions = listOf(3f, 6f, 12f, 24f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Colors and Undo / Redo / Clear Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color swatches
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorOptions.forEach { color ->
                        val isEraser = color == 0x11111111L
                        val isSelected = selectedColor == color
                        
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isEraser) {
                                        // Show special bicolor for eraser
                                        Color.LightGray
                                    } else {
                                        Color(color)
                                    }
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Gray.copy(alpha = 0.4f)
                                    },
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.selectColor(color)
                                }
                                .testTag("color_btn_$color"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEraser) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Borracha",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Grid Switcher & Clear Canvas
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grid background cycle button
                    IconButton(
                        onClick = { viewModel.toggleGridType() },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("toggle_grid_btn")
                    ) {
                        val gridIcon = when (gridType) {
                            "GRID" -> Icons.Default.GridOn
                            "RULED" -> Icons.Default.Grid3x3
                            else -> Icons.Default.GridOff
                        }
                        Icon(
                            imageVector = gridIcon,
                            contentDescription = "Alternar grade",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Undo
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("undo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Desfazer",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Redo
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = redoStack.isNotEmpty(),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("redo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Refazer",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Clear
                    IconButton(
                        onClick = { viewModel.clearDrawing() },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("clear_canvas_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Limpar desenho",
                            tint = if (strokes.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Brush width swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pincel:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                widthOptions.forEach { width ->
                    val isSelected = selectedWidth == width
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                viewModel.selectWidth(width)
                            }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Small visual line of width size
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height((width / 2).dp.coerceAtLeast(1.dp).coerceAtMost(8.dp))
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${width.toInt()}px",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
