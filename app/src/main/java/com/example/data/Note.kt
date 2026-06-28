package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Point(val x: Float, val y: Float)

data class Stroke(
    val points: List<Point>,
    val color: Long, // Color ARGB as Long
    val width: Float
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "LIVRE", "MATEMATICA", "REDACAO", "RESUMO"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Serialized freehand drawing strokes
    val drawingData: String = "",
    
    // Mathematical template fields
    val problemStatement: String = "",
    val stepByStepSolution: String = "",
    
    // Essay template fields
    val essayTheme: String = "",
    val essayIntro: String = "",
    val essayD1: String = "",
    val essayD2: String = "",
    val essayConclusion: String = "",
    
    // Study Summary template fields
    val summaryTitle: String = "",
    val summaryTopics: String = "",
    val summaryNotes: String = ""
) {
    companion object {
        fun serializeStrokes(strokes: List<Stroke>): String {
            if (strokes.isEmpty()) return ""
            return strokes.joinToString("|") { stroke ->
                val pointsStr = stroke.points.joinToString(";") { "${it.x},${it.y}" }
                "$pointsStr:${stroke.color}:${stroke.width}"
            }
        }

        fun deserializeStrokes(serialized: String?): List<Stroke> {
            if (serialized.isNullOrEmpty()) return emptyList()
            return try {
                serialized.split("|").mapNotNull { strokeStr ->
                    if (strokeStr.isEmpty()) return@mapNotNull null
                    val parts = strokeStr.split(":")
                    if (parts.size < 3) return@mapNotNull null
                    val pointsStr = parts[0]
                    val color = parts[1].toLongOrNull() ?: 0xFFFFFFFF
                    val width = parts[2].toFloatOrNull() ?: 5f
                    val points = pointsStr.split(";").mapNotNull { ptStr ->
                        val coords = ptStr.split(",")
                        if (coords.size == 2) {
                            val x = coords[0].toFloatOrNull()
                            val y = coords[1].toFloatOrNull()
                            if (x != null && y != null) Point(x, y) else null
                        } else null
                    }
                    if (points.isNotEmpty()) Stroke(points, color, width) else null
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
