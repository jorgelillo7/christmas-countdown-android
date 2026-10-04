package com.jorgelillo.whoslying.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jorgelillo.whoslying.R
import com.jorgelillo.whoslying.domain.Game
import com.jorgelillo.whoslying.domain.Match
import com.jorgelillo.whoslying.domain.Role
import com.jorgelillo.whoslying.ui.DrawStroke
import com.jorgelillo.whoslying.ui.InkColors
import com.jorgelillo.whoslying.ui.PaperColor
import com.jorgelillo.whoslying.ui.drawStrokes
import com.jorgelillo.whoslying.ui.theme.Neon
import kotlinx.coroutines.delay

/** Drawing mode's clue round: everyone adds to one picture, passing the phone, no talking. */
@Composable
fun DrawingScreen(
    game: Game,
    alive: Set<String>,
    strokes: SnapshotStateList<DrawStroke>,
    discussionSeconds: Int,
    onVote: () -> Unit,
    onTimeUp: () -> Unit,
    onRevealAll: () -> Unit,
    onQuit: () -> Unit,
) {
    var ink by rememberSaveable { mutableIntStateOf(0) }
    Page(
        title = null,
        onBack = onQuit,
        background = Neon.Backdrop,
        bottom = {
            BigButton(stringResource(R.string.vote_button), onVote, Modifier.testTag("go_vote"), color = Color(0xFF00D26A), contentColor = Neon.Night)
            TextButton(onClick = onRevealAll, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("reveal_all")) {
                Text(stringResource(R.string.vote_reveal_all), color = Neon.Muted)
            }
        },
    ) {
        val starter = game.starter.takeIf { it in alive } ?: game.cards.first { it.player in alive && it.role != Role.DRIFTER }.player
        Text(
            stringResource(R.string.drawing_turns, starter),
            color = Neon.Amber,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (discussionSeconds > 0) {
            Spacer(Modifier.height(8.dp))
            key(alive.size) { TimerBar(discussionSeconds, onTimeUp) }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            DrawingCanvas(
                strokes = strokes,
                ink = InkColors[ink],
                modifier = Modifier.aspectRatio(4f / 5f),
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            InkColors.forEachIndexed { i, color ->
                Box(
                    Modifier
                        .size(if (i == ink) 34.dp else 28.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(3.dp, if (i == ink) Color.White else Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable { ink = i }
                        .testTag("ink_$i"),
                )
            }
            ToolButton("↶", "undo", enabled = strokes.isNotEmpty()) { strokes.removeAt(strokes.lastIndex) }
            ToolButton("🗑", "clear", enabled = strokes.isNotEmpty()) { strokes.clear() }
        }
    }
}

@Composable
private fun ToolButton(symbol: String, tag: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Neon.CardHigh)
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center,
    ) { Text(symbol, fontSize = 20.sp, color = if (enabled) Neon.Ink else Neon.Muted) }
}

@Composable
private fun DrawingCanvas(strokes: SnapshotStateList<DrawStroke>, ink: Color, modifier: Modifier) {
    val currentInk by rememberUpdatedState(ink)
    Canvas(
        modifier
            .clip(MaterialTheme.shapes.large)
            .background(PaperColor)
            .testTag("canvas")
            .pointerInput(Unit) {
                detectTapGestures { p ->
                    strokes += DrawStroke(currentInk).apply { points += Offset(p.x / size.width, p.y / size.height) }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { p -> strokes += DrawStroke(currentInk).apply { points += Offset(p.x / size.width, p.y / size.height) } },
                    onDrag = { change, _ ->
                        val p = change.position
                        strokes.lastOrNull()?.points?.add(Offset((p.x / size.width).coerceIn(0f, 1f), (p.y / size.height).coerceIn(0f, 1f)))
                    },
                )
            },
    ) { drawStrokes(strokes) }
}

/** Countdown as a shrinking bar. Tap to pause or resume; at zero it beeps and the vote is forced. */
@Composable
private fun TimerBar(seconds: Int, onTimeUp: () -> Unit) {
    var left by rememberSaveable { mutableIntStateOf(seconds) }
    var paused by rememberSaveable { mutableStateOf(false) }
    val timeUp by rememberUpdatedState(onTimeUp)
    LaunchedEffect(paused) {
        while (!paused && left > 0) {
            delay(1_000)
            left--
            if (left == 0) {
                beep()
                timeUp()
            }
        }
    }
    val color = when {
        left == 0 -> Neon.Pink
        left <= 10 -> Neon.Amber
        else -> Neon.Turquoise
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(CircleShape)
            .background(Neon.Card)
            .clickable(enabled = left > 0) { paused = !paused }
            .testTag("timer"),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.align(Alignment.CenterStart).fillMaxHeight().fillMaxWidth(left.toFloat() / seconds).background(color.copy(alpha = 0.35f)))
        Text(
            if (left == 0) stringResource(R.string.timer_up) else "%d:%02d".format(left / 60, left % 60) + if (paused) "  ⏸" else "",
            color = if (left == 0) Neon.Pink else Neon.Ink,
            fontWeight = FontWeight.Black,
        )
    }
}

/** The finished drawing, read only, for the result screen. */
@Composable
fun DrawingPreview(strokes: List<DrawStroke>, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(MaterialTheme.shapes.large).background(PaperColor).aspectRatio(4f / 5f)) { drawStrokes(strokes) }
}

/** Running scores across games, with the points the last game gave. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresSheet(
    players: List<String>,
    scores: Map<String, Int>,
    lastPoints: Map<String, Int>,
    roundsPlayed: Int,
    rounds: Int,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Neon.Card) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 32.dp).then(DialogTags).testTag("scores_sheet"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.scores_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(
                if (rounds > 0) pluralStringResource(R.plurals.scores_rounds_left, Match.remaining(roundsPlayed, rounds), Match.remaining(roundsPlayed, rounds))
                else stringResource(R.string.scores_rounds_free, roundsPlayed),
                color = Neon.Amber,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("rounds_left"),
            )
            Text(stringResource(R.string.scores_rules), color = Neon.Muted, style = MaterialTheme.typography.bodySmall)
            val ranking = (players + scores.keys).distinct().sortedByDescending { scores[it] ?: 0 }
            ranking.forEachIndexed { i, player ->
                Row(
                    Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(Neon.Night).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val medal = listOf("🥇", "🥈", "🥉").getOrNull(i)?.takeIf { (scores[player] ?: 0) > 0 }
                    Text(medal ?: "  ${i + 1}", fontSize = 22.sp, modifier = Modifier.width(40.dp))
                    Text(player, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    lastPoints[player]?.let { Text("+$it", color = Neon.Turquoise, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) }
                    Text("${scores[player] ?: 0}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Neon.Amber)
                }
            }
            TextButton(
                onClick = { if (confirmReset) { onReset(); confirmReset = false } else confirmReset = true },
                modifier = Modifier.align(Alignment.CenterHorizontally).testTag("reset_scores"),
            ) {
                Text(stringResource(if (confirmReset) R.string.scores_reset_confirm else R.string.scores_reset), color = Neon.Pink)
            }
        }
    }
}
