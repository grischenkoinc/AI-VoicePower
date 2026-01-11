package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * AI VoicePower Card Components v2.0
 *
 * Джерело: Design_Example_react.md
 * MainCard з темною градієнтною шапкою + білий body
 */

/**
 * Головна картка (Theory Card)
 * CSS: .main-card
 *
 * Структура:
 * - Card Header (темний градієнт #3D266A → #1F1F2E)
 * - Card Body (білий фон)
 */
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Elevation.Card.elevation,
                shape = RoundedCornerShape(32.dp),
                spotColor = Elevation.Card.color
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColors.surface
        )
    ) {
        Column {
            // Header (темна градієнтна шапка)
            CardHeader(
                gradient = Gradients.cardHeaderTheory,
                content = header
            )

            // Body (білий фон)
            CardBody(content = content)
        }
    }
}

/**
 * Practice Card (альтернативна шапка)
 * CSS: .practice-card
 *
 * Відрізняється тільки градієнтом шапки
 */
@Composable
fun PracticeCard(
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Elevation.PracticeCard.elevation,
                shape = RoundedCornerShape(32.dp),
                spotColor = Elevation.PracticeCard.color
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColors.surface
        )
    ) {
        Column {
            // Header (альтернативний темний градієнт)
            CardHeader(
                gradient = Gradients.cardHeaderPractice,
                content = header
            )

            // Body (білий фон)
            CardBody(content = content)
        }
    }
}

/**
 * Card Header (внутрішній компонент)
 * CSS: .card-header
 * padding: 28px 28px 24px
 */
@Composable
private fun CardHeader(
    gradient: androidx.compose.ui.graphics.Brush,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(start = 28.dp, end = 28.dp, top = 28.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

/**
 * Card Body (внутрішній компонент)
 * CSS: .card-body
 * padding: 24px 28px 32px
 */
@Composable
private fun CardBody(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        content()
    }
}
