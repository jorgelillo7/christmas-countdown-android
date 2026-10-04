package com.jorgelillo.whoslying.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jorgelillo.whoslying.R
import com.jorgelillo.whoslying.domain.WordPack
import com.jorgelillo.whoslying.domain.WordPacks
import com.jorgelillo.whoslying.ui.theme.Neon

@Composable
fun PacksScreen(builtIn: List<WordPack>, custom: List<WordPack>, onEdit: (String?) -> Unit, onBack: () -> Unit) {
    Page(
        title = stringResource(R.string.packs),
        onBack = onBack,
        bottom = { BigButton("＋ " + stringResource(R.string.packs_new), { onEdit(null) }, Modifier.testTag("new_pack")) },
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (custom.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.packs_custom)) }
                items(custom, key = { it.id }) { PackRow(it, onClick = { onEdit(it.id) }) }
            }
            item { SectionTitle(stringResource(R.string.packs_builtin)) }
            items(builtIn, key = { it.id }) { PackRow(it, onClick = null) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Neon.Muted, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
}

@Composable
private fun PackRow(pack: WordPack, onClick: (() -> Unit)?) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Neon.Card)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(pack.emoji, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(pack.name, fontWeight = FontWeight.Bold)
            Text(
                pluralStringResource(R.plurals.pack_words, pack.entries.size, pack.entries.size),
                style = MaterialTheme.typography.bodySmall,
                color = Neon.Muted,
            )
        }
        if (onClick != null) Icon(Icons.Filled.Edit, contentDescription = null, tint = Neon.Muted)
    }
}

/** Editor for a custom pack: one entry per line, "word / similar word" with the second part optional. */
@Composable
fun PackEditScreen(initial: WordPack, isNew: Boolean, onSave: (WordPack) -> Unit, onDelete: () -> Unit, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf(initial.name) }
    var text by rememberSaveable {
        mutableStateOf(initial.entries.joinToString("\n", transform = WordPacks::toLine))
    }
    val entries = WordPacks.parseEntries(text)
    val valid = name.isNotBlank() && entries.size >= 3

    Page(
        title = stringResource(if (isNew) R.string.packs_new else R.string.packs),
        onBack = onBack,
        modifier = Modifier.imePadding(),
        bottom = {
            BigButton(stringResource(R.string.pack_save), {
                onSave(initial.copy(name = name.trim(), entries = entries))
            }, enabled = valid, modifier = Modifier.testTag("save_pack"))
            if (!isNew) {
                TextButton(onClick = onDelete, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(R.string.pack_delete), color = Neon.Pink)
                }
            }
        },
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.pack_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("pack_name"),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.pack_words_label)) },
            modifier = Modifier.fillMaxWidth().weight(1f).testTag("pack_words"),
        )
        Text(
            if (valid) stringResource(R.string.pack_words_help) else stringResource(R.string.pack_need),
            style = MaterialTheme.typography.bodySmall,
            color = if (valid) Neon.Muted else Neon.Pink,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
