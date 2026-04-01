package com.aivoicepower.ui.screens.debug

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.data.content.CourseContentProvider
import com.aivoicepower.domain.model.exercise.ExerciseContent
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.domain.model.exercise.LessonExercise
import com.aivoicepower.domain.model.exercise.toDisplayString

private data class ExerciseItem(
    val id: String,
    val title: String,
    val type: ExerciseType,
    val courseName: String,
    val dayNumber: Int,
    val lessonTitle: String,
    val contentPreview: String,
    val courseId: String,
    val lessonId: String,
    val exerciseIndexInLesson: Int
) {
    // Always evaluates emotionality
    val hasEmotionality: Boolean get() = type == ExerciseType.EMOTION_READING
    // May evaluate emotionality depending on topic (AI decides)
    val hasMaybeEmotionality: Boolean get() = type == ExerciseType.FREE_SPEECH
}

// Exercise types that already have good prompts or don't need AI analysis
private val EXCLUDED_TYPES = setOf(
    ExerciseType.TONGUE_TWISTER,
    ExerciseType.ARTICULATION,
    ExerciseType.BREATHING
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptIterationScreen(
    onNavigateBack: () -> Unit,
    onLaunchExercise: (courseId: String, lessonId: String, exerciseIndex: Int) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("prompt_iteration_v1", Context.MODE_PRIVATE) }

    var iteratedIds by remember {
        mutableStateOf(prefs.getStringSet("iterated", emptySet())!!.toSet())
    }
    var showOnlyPending by remember { mutableStateOf(false) }
    var expandedTypes by remember { mutableStateOf(setOf<ExerciseType>()) }

    val allExercises = remember { buildExerciseList() }
    val totalCount = allExercises.size
    val iteratedCount = iteratedIds.size.coerceAtMost(totalCount)

    val groupedByType: Map<ExerciseType, List<ExerciseItem>> = remember(allExercises) {
        allExercises
            .groupBy { it.type }
            .entries
            .sortedBy { it.key.toDisplayString() }
            .associate { it.key to it.value }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ітерація промптів", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Зроблено: $iteratedCount / $totalCount",
                            fontSize = 12.sp,
                            color = if (iteratedCount == totalCount) Color(0xFF4CAF50) else Color(0xFFFFB300)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(onClick = { showOnlyPending = !showOnlyPending }) {
                        Text(
                            text = if (showOnlyPending) "Всі" else "Тільки нові",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            groupedByType.forEach { (type, exercises) ->
                val visible = if (showOnlyPending) exercises.filter { it.id !in iteratedIds } else exercises
                if (visible.isEmpty()) return@forEach

                val isExpanded = type in expandedTypes
                val doneCount = exercises.count { it.id in iteratedIds }

                item(key = "header_${type.name}") {
                    TypeHeader(
                        label = type.toDisplayString(),
                        total = exercises.size,
                        done = doneCount,
                        isExpanded = isExpanded,
                        onClick = {
                            expandedTypes = if (isExpanded) expandedTypes - type else expandedTypes + type
                        }
                    )
                }

                if (isExpanded) {
                    items(visible, key = { it.id }) { item ->
                        val done = item.id in iteratedIds
                        ExerciseCard(
                            item = item,
                            isDone = done,
                            onToggle = {
                                val newSet = if (done) iteratedIds - item.id else iteratedIds + item.id
                                iteratedIds = newSet
                                prefs.edit().putStringSet("iterated", newSet).apply()
                            },
                            onLaunch = {
                                onLaunchExercise(item.courseId, item.lessonId, item.exerciseIndexInLesson)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeHeader(
    label: String,
    total: Int,
    done: Int,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val allDone = done == total
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1A237E)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$done/$total",
                color = if (allDone) Color(0xFF4CAF50) else Color(0xFFFFB300),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isExpanded) "▲" else "▼",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    item: ExerciseItem,
    isDone: Boolean,
    onToggle: () -> Unit,
    onLaunch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isDone) Color(0xFF1B5E20) else Color(0xFF1E1E1E)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        color = if (isDone) Color(0xFF81C784) else Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (item.hasEmotionality) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF6A1B9A)
                        ) {
                            Text(
                                text = "💜 Емоц.",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    } else if (item.hasMaybeEmotionality) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF4A148C).copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "💜 можл.",
                                color = Color(0xFFCE93D8),
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "📚 ${item.courseName}  ·  День ${item.dayNumber}",
                    color = Color(0xFF9E9E9E),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (item.contentPreview.isNotBlank()) {
                    Text(
                        text = item.contentPreview,
                        color = Color(0xFF757575),
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            // Launch exercise button
            IconButton(onClick = onLaunch) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Запустити",
                    tint = Color(0xFF42A5F5),
                    modifier = Modifier.size(26.dp)
                )
            }
            // Mark as iterated button
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (isDone) "Зроблено" else "Позначити",
                    tint = if (isDone) Color(0xFF4CAF50) else Color(0xFF616161),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

private fun buildExerciseList(): List<ExerciseItem> {
    return CourseContentProvider.getAllCourses().flatMap { course ->
        course.lessons.flatMap { lesson ->
            lesson.exercises
                .mapIndexed { index, ex -> Pair(index, ex) }
                .filter { (_, ex) -> ex.type !in EXCLUDED_TYPES }
                .map { (index, ex) ->
                    ExerciseItem(
                        id = "${course.id}__${lesson.id}__${ex.id}",
                        title = ex.title,
                        type = ex.type,
                        courseName = course.title,
                        dayNumber = lesson.dayNumber,
                        lessonTitle = lesson.title,
                        contentPreview = ex.contentPreview(),
                        courseId = course.id,
                        lessonId = lesson.id,
                        exerciseIndexInLesson = index
                    )
                }
        }
    }
}

private fun LessonExercise.contentPreview(): String {
    return when (val c = content) {
        is ExerciseContent.ReadingText -> c.text.take(120)
        is ExerciseContent.TongueTwister -> c.text
        is ExerciseContent.FreeSpeechTopic -> c.topic
        is ExerciseContent.Dialogue -> c.lines.firstOrNull()?.text?.take(100) ?: ""
        is ExerciseContent.Retelling -> c.sourceText.take(120)
        is ExerciseContent.Pitch -> c.scenario.take(120)
        is ExerciseContent.MinimalPairs -> c.pairs.take(4).joinToString(" / ") { "${it.first}–${it.second}" }
        is ExerciseContent.ContrastSounds -> c.sequence
        is ExerciseContent.TongueTwisterBattle -> c.twisters.firstOrNull()?.text ?: ""
        is ExerciseContent.SlowMotion -> c.text
        is ExerciseContent.ArticulationExercise -> ""
        is ExerciseContent.BreathingExercise -> c.instruction
    }
}
