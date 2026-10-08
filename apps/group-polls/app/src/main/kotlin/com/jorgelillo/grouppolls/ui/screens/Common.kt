package com.jorgelillo.grouppolls.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jorgelillo.grouppolls.R
import com.jorgelillo.grouppolls.domain.Poll
import com.jorgelillo.grouppolls.domain.Split
import com.jorgelillo.grouppolls.ui.theme.Polls

const val LINK_BASE = "https://jorgelillo7.github.io/q/?c="
const val PRIVACY_POLICY_URL = "https://jorgelillo7.github.io/privacy/group-polls/"
const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.jorgelillo.grouppolls"

/** Dialogs and sheets live in their own window: expose their test tags as resource ids too. */
@OptIn(ExperimentalComposeUiApi::class)
val DialogTags = Modifier.semantics { testTagsAsResourceId = true }

/** Full-screen page with a back button and an optional bottom action area. */
@Composable
fun Page(
    title: String?,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    background: Brush = Polls.Header,
    actions: @Composable RowScope.() -> Unit = {},
    bottom: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) = CompositionLocalProvider(LocalContentColor provides Polls.Ink) {
    Column(modifier.fillMaxSize().background(background).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                }
            } else {
                Spacer(Modifier.width(16.dp))
            }
            Text(
                title.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            actions()
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp), content = content)
        Column(Modifier.fillMaxWidth().padding(20.dp), content = bottom)
    }
}

@Composable
fun BigButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Polls.Blue,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White),
    ) { Text(text, fontSize = 18.sp, fontWeight = FontWeight.Black) }
}

/** The red/blue bar: proportional to the votes, half and half while nobody has voted. */
@Composable
fun SplitBar(poll: Poll, modifier: Modifier = Modifier, height: Int = 14) {
    val split = Split.of(poll.redVotes, poll.blueVotes)
    val red = if (poll.totalVotes == 0) 0.5f else split.red / 100f
    Row(modifier.fillMaxWidth().height(height.dp).clip(CircleShape)) {
        if (red > 0f) Box(Modifier.weight(red.coerceAtLeast(0.001f)).fillMaxHeight().background(Polls.Red))
        if (red < 1f) Box(Modifier.weight((1 - red).coerceAtLeast(0.001f)).fillMaxHeight().background(Polls.Blue))
    }
}

/** Opens the share sheet with the poll's link. */
fun sharePoll(context: Context, poll: Poll, text: String) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "$text\n$LINK_BASE${poll.code}")
    context.startActivity(Intent.createChooser(send, null))
}

/** "2 h", "3 d"… for the feed cards. */
@Composable
fun age(createdAt: Long, now: Long): String {
    val minutes = ((now - createdAt) / 60_000).coerceAtLeast(0)
    return when {
        minutes < 60 -> stringResource(R.string.age_minutes, minutes.coerceAtLeast(1))
        minutes < 24 * 60 -> stringResource(R.string.age_hours, minutes / 60)
        else -> stringResource(R.string.age_days, minutes / (24 * 60))
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(), color = Polls.Muted, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, modifier = modifier.padding(top = 16.dp, bottom = 8.dp))
}

@Composable
fun Dot(color: Color, size: Int = 10) {
    Box(Modifier.width(size.dp).height(size.dp).clip(CircleShape).background(color))
}
