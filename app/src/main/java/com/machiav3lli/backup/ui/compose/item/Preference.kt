package com.machiav3lli.backup.ui.compose.item

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machiav3lli.backup.ICON_SIZE_SMALL
import com.machiav3lli.backup.preferences.pref_allPrefsShouldLookEqual
import com.machiav3lli.backup.ui.compose.ifThen
import com.machiav3lli.backup.ui.item.BooleanPref
import com.machiav3lli.backup.ui.item.EnumPref
import com.machiav3lli.backup.ui.item.IntPref
import com.machiav3lli.backup.ui.item.ListPref
import com.machiav3lli.backup.ui.item.Pref
import com.machiav3lli.backup.ui.item.Pref.Companion.prefChangeListeners
import kotlin.math.roundToInt

@Composable
fun BasePreference(
    modifier: Modifier = Modifier,
    pref: Pref,
    summary: String? = null,
    @StringRes titleId: Int = -1,
    @StringRes summaryId: Int = -1,
    index: Int = 0,
    groupSize: Int = 1,
    icon: (@Composable () -> Unit)? = null,
    endWidget: (@Composable (isEnabled: Boolean) -> Unit)? = null,
    bottomWidget: (@Composable (isEnabled: Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    var isEnabled by remember {
        mutableStateOf(pref.enableIf?.invoke() ?: true)
    }   //TODO hg42 remove remember ???

    SideEffect {
        pref.enableIf?.run {
            prefChangeListeners.put(pref) {
                isEnabled = pref.enableIf.invoke()
            }
        }
    }

    val base = index.toFloat() / groupSize
    val rank = (index + 1f) / groupSize

    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (base == 0f) 16.dp else 6.dp,
                    topEnd = if (base == 0f) 16.dp else 6.dp,
                    bottomStart = if (rank == 1f) 16.dp else 6.dp,
                    bottomEnd = if (rank == 1f) 16.dp else 6.dp
                )
            )
            .background(
                MaterialTheme.colorScheme
                    .surfaceColorAtElevation(
                        if (pref_allPrefsShouldLookEqual.value)
                            24.dp
                        else
                            (rank * 24).dp
                    )
            )
            .ifThen(onClick != null) {
                clickable(enabled = isEnabled, onClick = onClick!!)
            }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                icon()
                Spacer(modifier = Modifier.requiredWidth(8.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .ifThen(!isEnabled) {
                        alpha(0.3f)
                    }
            ) {
                Text(
                    text = if (titleId != -1) stringResource(id = titleId) else pref.key,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp
                )
                var summaryText = if (summaryId != -1) stringResource(id = summaryId) else ""
                if (summaryText.isNotEmpty() && !summary.isNullOrEmpty())
                    summaryText += " : "
                summaryText += summary ?: ""
                Text(
                    text = summaryText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                bottomWidget?.let { widget ->
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    widget(isEnabled)
                }
            }
            endWidget?.let { widget ->
                Spacer(modifier = Modifier.requiredWidth(8.dp))
                widget(isEnabled)
            }
        }
    }
}

@Composable
fun LaunchPreference(
    modifier: Modifier = Modifier,
    pref: Pref,
    index: Int = 0,
    groupSize: Int = 1,
    summary: String? = null,
    onClick: (() -> Unit) = {},
) {
    BasePreference(
        modifier = modifier,
        pref = pref,
        summary = summary,
        titleId = pref.titleId,
        summaryId = pref.summaryId,
        icon = {
            pref.icon?.let { icon ->
                PrefIcon(
                    icon = icon,
                    text = stringResource(id = pref.titleId),
                )
            } ?: run {
                Spacer(modifier = Modifier.requiredWidth(36.dp))
            }
        },
        index = index,
        groupSize = groupSize,
        onClick = onClick,
    )
}

@Composable
fun EnumPreference(
    modifier: Modifier = Modifier,
    pref: EnumPref,
    index: Int = 0,
    groupSize: Int = 1,
    onClick: (() -> Unit) = {},
) {
    BasePreference(
        modifier = modifier,
        pref = pref,
        titleId = pref.titleId,
        summaryId = pref.entries[pref.value] ?: pref.summaryId,
        icon = {
            if (pref.icon != null) PrefIcon(
                icon = pref.icon,
                text = stringResource(id = pref.titleId),
            )
            else Spacer(modifier = Modifier.requiredWidth(36.dp))
        },
        index = index,
        groupSize = groupSize,
        onClick = onClick,
    )
}

@Composable
fun ListPreference(
    modifier: Modifier = Modifier,
    pref: ListPref,
    index: Int = 0,
    groupSize: Int = 1,
    onClick: (() -> Unit) = {},
) {
    BasePreference(
        modifier = modifier,
        pref = pref,
        titleId = pref.titleId,
        summaryId = pref.summaryId,
        summary = pref.entries[pref.value],
        icon = {
            if (pref.icon != null) PrefIcon(
                icon = pref.icon,
                text = stringResource(id = pref.titleId),
            )
            else Spacer(modifier = Modifier.requiredWidth(36.dp))
        },
        index = index,
        groupSize = groupSize,
        onClick = onClick,
    )
}

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    pref: BooleanPref,
    index: Int = 0,
    groupSize: Int = 1,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    val context = LocalContext.current
    var checked by remember(pref.value) { mutableStateOf(pref.value) }  //TODO hg42 remove remember ???
    val check = { value: Boolean ->
        pref.value = value
        checked = value
    }

    BasePreference(
        modifier = modifier,
        pref = pref,
        summary = pref.summary,
        titleId = pref.titleId,
        summaryId = pref.summaryId,
        icon = {
            if (pref.icon != null) PrefIcon(
                icon = pref.icon,
                text = stringResource(id = pref.titleId),
            )
            else Spacer(modifier = Modifier.requiredWidth(36.dp))
        },
        index = index,
        groupSize = groupSize,
        onClick = {
            onCheckedChange(!checked)
            check(!checked)
        },
        endWidget = { isEnabled ->
            Switch(
                modifier = Modifier
                    .height(ICON_SIZE_SMALL),
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                    check(it)
                },
                enabled = isEnabled,
            )
        },
    )
}

@Composable
fun CheckboxPreference(
    modifier: Modifier = Modifier,
    pref: BooleanPref,
    index: Int = 0,
    groupSize: Int = 1,
    onCheckedChange: ((Boolean) -> Unit) = {},
) {
    var checked by remember(pref.value) { mutableStateOf(pref.value) }  //TODO hg42 remove remember ???
    val check = { value: Boolean ->
        pref.value = value
        checked = value
    }

    BasePreference(
        modifier = modifier,
        pref = pref,
        summary = pref.summary,
        titleId = pref.titleId,
        summaryId = pref.summaryId,
        icon = {
            if (pref.icon != null) PrefIcon(
                icon = pref.icon,
                text = stringResource(id = pref.titleId),
            )
            else Spacer(modifier = Modifier.requiredWidth(36.dp))
        },
        index = index,
        groupSize = groupSize,
        onClick = {
            onCheckedChange(!checked)
            check(!checked)
        },
        endWidget = { isEnabled ->
            Checkbox(
                modifier = Modifier
                    .height(ICON_SIZE_SMALL),
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                    check(it)
                },
                enabled = isEnabled,
            )
        },
    )
}

@Composable
fun SeekBarPreference(
    modifier: Modifier = Modifier,
    pref: IntPref,
    index: Int = 0,
    groupSize: Int = 1,
    onValueChange: ((Int) -> Unit) = {},
) {
    var sliderPosition by remember {    //TODO hg42 remove remember ???
        mutableStateOf(
            pref.entries.indexOfFirst { it >= pref.value }.let {
                if (it < 0)
                    pref.entries.indexOfFirst { it >= (pref.defaultValue as Int) }
                else
                    it
            }.let {
                if (it < 0)
                    0
                else
                    it
            }
        )
    }
    val savePosition = { pos: Int ->
        val value = pref.entries[pos]
        pref.value = value
        sliderPosition = pos
    }
    val last = pref.entries.size - 1

    BasePreference(
        modifier = modifier,
        pref = pref,
        summary = pref.summary,
        titleId = pref.titleId,
        summaryId = pref.summaryId,
        icon = {
            if (pref.icon != null) PrefIcon(
                icon = pref.icon,
                text = stringResource(id = pref.titleId),
            )
            else Spacer(modifier = Modifier.requiredWidth(36.dp))
        },
        index = index,
        groupSize = groupSize,
        bottomWidget = { isEnabled ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    modifier = Modifier.weight(1f, false),
                    value = sliderPosition.toFloat(),
                    valueRange = 0.toFloat()..last.toFloat(),
                    onValueChange = { sliderPosition = it.roundToInt() },
                    onValueChangeFinished = {
                        onValueChange(sliderPosition)
                        savePosition(sliderPosition)
                    },
                    steps = last - 1,
                    enabled = isEnabled
                )
                Spacer(modifier = Modifier.requiredWidth(8.dp))
                Text(
                    text = pref.entries[sliderPosition].toString(),
                    modifier = Modifier.widthIn(min = 48.dp)
                )
            }
        },
    )
}
