package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Note
import com.example.data.NoteRepository
import com.example.data.Stroke
import com.example.data.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    // List of all notes
    val allNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Note currently being edited or created
    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()

    // Freehand drawing state (active strokes on canvas)
    private val _strokes = MutableStateFlow<List<Stroke>>(emptyList())
    val strokes: StateFlow<List<Stroke>> = _strokes.asStateFlow()

    // Redo stack for drawing
    private val _redoStack = MutableStateFlow<List<Stroke>>(emptyList())
    val redoStack: StateFlow<List<Stroke>> = _redoStack.asStateFlow()

    // Selected draw configurations
    private val _selectedColor = MutableStateFlow(0xFF2196F3) // Default Blue
    val selectedColor: StateFlow<Long> = _selectedColor.asStateFlow()

    private val _selectedWidth = MutableStateFlow(5f) // Default Brush Width
    val selectedWidth: StateFlow<Float> = _selectedWidth.asStateFlow()

    // Grid configuration ("NONE", "RULED", "GRID")
    private val _gridType = MutableStateFlow("GRID") // Default grid for math/sketching
    val gridType: StateFlow<String> = _gridType.asStateFlow()

    // Temporary/Draft values during editing to avoid writing to DB on every keystroke
    private val _editedTitle = MutableStateFlow("")
    val editedTitle: StateFlow<String> = _editedTitle.asStateFlow()

    private val _editedProblemStatement = MutableStateFlow("")
    val editedProblemStatement: StateFlow<String> = _editedProblemStatement.asStateFlow()

    private val _editedStepByStepSolution = MutableStateFlow("")
    val editedStepByStepSolution: StateFlow<String> = _editedStepByStepSolution.asStateFlow()

    private val _editedEssayTheme = MutableStateFlow("")
    val editedEssayTheme: StateFlow<String> = _editedEssayTheme.asStateFlow()

    private val _editedEssayIntro = MutableStateFlow("")
    val editedEssayIntro: StateFlow<String> = _editedEssayIntro.asStateFlow()

    private val _editedEssayD1 = MutableStateFlow("")
    val editedEssayD1: StateFlow<String> = _editedEssayD1.asStateFlow()

    private val _editedEssayD2 = MutableStateFlow("")
    val editedEssayD2: StateFlow<String> = _editedEssayD2.asStateFlow()

    private val _editedEssayConclusion = MutableStateFlow("")
    val editedEssayConclusion: StateFlow<String> = _editedEssayConclusion.asStateFlow()

    private val _editedSummaryTitle = MutableStateFlow("")
    val editedSummaryTitle: StateFlow<String> = _editedSummaryTitle.asStateFlow()

    private val _editedSummaryTopics = MutableStateFlow("")
    val editedSummaryTopics: StateFlow<String> = _editedSummaryTopics.asStateFlow()

    private val _editedSummaryNotes = MutableStateFlow("")
    val editedSummaryNotes: StateFlow<String> = _editedSummaryNotes.asStateFlow()

    // Navigation and UX state helpers
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun selectColor(colorArgb: Long) {
        _selectedColor.value = colorArgb
    }

    fun selectWidth(width: Float) {
        _selectedWidth.value = width
    }

    fun toggleGridType() {
        _gridType.value = when (_gridType.value) {
            "NONE" -> "RULED"
            "RULED" -> "GRID"
            else -> "NONE"
        }
    }

    fun setGridType(type: String) {
        _gridType.value = type
    }

    // Drawing operations
    fun startNewStroke(point: Point) {
        val newStroke = Stroke(
            points = listOf(point),
            color = _selectedColor.value,
            width = _selectedWidth.value
        )
        _strokes.value = _strokes.value + newStroke
        _redoStack.value = emptyList() // Clear redo on new action
    }

    fun appendPointToLastStroke(point: Point) {
        val currentStrokes = _strokes.value
        if (currentStrokes.isEmpty()) return
        
        val lastStroke = currentStrokes.last()
        val updatedStroke = lastStroke.copy(points = lastStroke.points + point)
        
        _strokes.value = currentStrokes.dropLast(1) + updatedStroke
    }

    fun undo() {
        val currentStrokes = _strokes.value
        if (currentStrokes.isNotEmpty()) {
            val last = currentStrokes.last()
            _strokes.value = currentStrokes.dropLast(1)
            _redoStack.value = _redoStack.value + last
        }
    }

    fun redo() {
        val currentRedo = _redoStack.value
        if (currentRedo.isNotEmpty()) {
            val last = currentRedo.last()
            _redoStack.value = currentRedo.dropLast(1)
            _strokes.value = _strokes.value + last
        }
    }

    fun clearDrawing() {
        if (_strokes.value.isNotEmpty()) {
            _redoStack.value = emptyList()
            _strokes.value = emptyList()
        }
    }

    // Setters for text edits
    fun updateTitle(value: String) { _editedTitle.value = value }
    fun updateProblemStatement(value: String) { _editedProblemStatement.value = value }
    fun updateStepByStepSolution(value: String) { _editedStepByStepSolution.value = value }
    fun updateEssayTheme(value: String) { _editedEssayTheme.value = value }
    fun updateEssayIntro(value: String) { _editedEssayIntro.value = value }
    fun updateEssayD1(value: String) { _editedEssayD1.value = value }
    fun updateEssayD2(value: String) { _editedEssayD2.value = value }
    fun updateEssayConclusion(value: String) { _editedEssayConclusion.value = value }
    fun updateSummaryTitle(value: String) { _editedSummaryTitle.value = value }
    fun updateSummaryTopics(value: String) { _editedSummaryTopics.value = value }
    fun updateSummaryNotes(value: String) { _editedSummaryNotes.value = value }

    // Notes persistence
    fun selectNote(note: Note?) {
        _currentNote.value = note
        if (note != null) {
            // Load drawing
            _strokes.value = Note.deserializeStrokes(note.drawingData)
            _redoStack.value = emptyList()
            
            // Load text fields
            _editedTitle.value = note.title
            _editedProblemStatement.value = note.problemStatement
            _editedStepByStepSolution.value = note.stepByStepSolution
            _editedEssayTheme.value = note.essayTheme
            _editedEssayIntro.value = note.essayIntro
            _editedEssayD1.value = note.essayD1
            _editedEssayD2.value = note.essayD2
            _editedEssayConclusion.value = note.essayConclusion
            _editedSummaryTitle.value = note.summaryTitle
            _editedSummaryTopics.value = note.summaryTopics
            _editedSummaryNotes.value = note.summaryNotes
        } else {
            _strokes.value = emptyList()
            _redoStack.value = emptyList()
            _editedTitle.value = ""
            _editedProblemStatement.value = ""
            _editedStepByStepSolution.value = ""
            _editedEssayTheme.value = ""
            _editedEssayIntro.value = ""
            _editedEssayD1.value = ""
            _editedEssayD2.value = ""
            _editedEssayConclusion.value = ""
            _editedSummaryTitle.value = ""
            _editedSummaryTopics.value = ""
            _editedSummaryNotes.value = ""
        }
    }

    fun createNewNote(type: String) {
        val defaultTitle = when (type) {
            "MATEMATICA" -> "Resolução Matemática"
            "REDACAO" -> "Estrutura de Redação"
            "RESUMO" -> "Resumo de Conteúdo"
            else -> "Rascunho Livre"
        }
        val emptyNote = Note(
            title = defaultTitle,
            type = type
        )
        selectNote(emptyNote)
    }

    fun saveCurrentNote(onComplete: () -> Unit) {
        val current = _currentNote.value ?: return
        _isSaving.value = true
        
        viewModelScope.launch {
            val updatedDrawingData = Note.serializeStrokes(_strokes.value)
            val updatedNote = current.copy(
                title = _editedTitle.value.ifBlank { "Sem Título" },
                updatedAt = System.currentTimeMillis(),
                drawingData = updatedDrawingData,
                problemStatement = _editedProblemStatement.value,
                stepByStepSolution = _editedStepByStepSolution.value,
                essayTheme = _editedEssayTheme.value,
                essayIntro = _editedEssayIntro.value,
                essayD1 = _editedEssayD1.value,
                essayD2 = _editedEssayD2.value,
                essayConclusion = _editedEssayConclusion.value,
                summaryTitle = _editedSummaryTitle.value,
                summaryTopics = _editedSummaryTopics.value,
                summaryNotes = _editedSummaryNotes.value
            )
            
            if (updatedNote.id == 0) {
                // Insert
                val newId = repository.insertNote(updatedNote)
                _currentNote.value = updatedNote.copy(id = newId.toInt())
            } else {
                // Update
                repository.updateNote(updatedNote)
            }
            _isSaving.value = false
            onComplete()
        }
    }

    fun deleteNote(note: Note, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteNote(note)
            if (_currentNote.value?.id == note.id) {
                selectNote(null)
            }
            onComplete()
        }
    }
}

class NotesViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
