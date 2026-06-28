package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as DrawScopeStroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.Point
import com.example.data.Stroke

@Composable
fun DrawingCanvas(
    strokes: List<Stroke>,
    onStrokeStarted: (Point) -> Unit,
    onPointAdded: (Point) -> Unit,
    gridType: String, // "NONE", "RULED", "GRID"
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    
    // Grid colors that contrast subtly with the background
    val gridLineColor = if (isDark) {
        Color.White.copy(alpha = 0.07f)
    } else {
        Color.Black.copy(alpha = 0.06f)
    }
    
    val canvasBgColor = if (isDark) {
        Color(0xFF1E1E2A) // Sleek dark slate
    } else {
        Color(0xFFFCFCFD) // Warm off-white
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(canvasBgColor)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onStrokeStarted(Point(offset.x, offset.y))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val position = change.position
                            onPointAdded(Point(position.x, position.y))
                        }
                    )
                }
        ) {
            // 1. Draw grids if requested
            val width = size.width
            val height = size.height
            
            if (gridType == "GRID") {
                val gridSize = 40.dp.toPx()
                // Vertical lines
                var x = 0f
                while (x < width) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += gridSize
                }
                // Horizontal lines
                var y = 0f
                while (y < height) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    y += gridSize
                }
            } else if (gridType == "RULED") {
                val lineSpacing = 32.dp.toPx()
                var y = lineSpacing
                while (y < height) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    y += lineSpacing
                }
            }

            // 2. Draw all saved strokes
            strokes.forEach { stroke ->
                if (stroke.points.isNotEmpty()) {
                    val path = Path().apply {
                        val first = stroke.points.first()
                        moveTo(first.x, first.y)
                        for (i in 1 until stroke.points.size) {
                            val pt = stroke.points[i]
                            lineTo(pt.x, pt.y)
                        }
                    }
                    
                    // Determine actual drawing color. If it's a special Eraser flag, we paint over with canvas background color!
                    // Let's reserve color 0xFF000001 (or another constant) for the eraser, or simply use canvasBgColor.
                    // But wait! Storing the canvasBgColor directly inside the stroke might not work if the user toggles light/dark mode later!
                    // So, if the stroke color has a specific magic value like 0x01010101, we treat it as an Eraser and paint it with canvasBgColor!
                    val actualColor = if (stroke.color == 0x11111111L) {
                        canvasBgColor
                    } else {
                        Color(stroke.color)
                    }

                    drawPath(
                        path = path,
                        color = actualColor,
                        style = DrawScopeStroke(
                            width = stroke.width,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
