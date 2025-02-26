package com.machiav3lli.backup.ui.compose.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.machiav3lli.backup.R
import com.machiav3lli.backup.items.Package
import com.machiav3lli.backup.ui.compose.icons.Phosphor
import com.machiav3lli.backup.ui.compose.icons.phosphor.AsteriskSimple
import com.machiav3lli.backup.ui.compose.icons.phosphor.CircleWavyWarning
import com.machiav3lli.backup.ui.compose.icons.phosphor.Spinner
import com.machiav3lli.backup.ui.compose.icons.phosphor.User
import com.machiav3lli.backup.ui.compose.theme.ColorDisabled
import com.machiav3lli.backup.ui.compose.theme.ColorSpecial
import com.machiav3lli.backup.ui.compose.theme.ColorSystem
import com.machiav3lli.backup.ui.compose.theme.ColorUpdated
import com.machiav3lli.backup.ui.compose.theme.ColorUser
import com.machiav3lli.backup.utils.getFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchPackageItem(
    item: Package,
    restore: Boolean,
    isApkChecked: Boolean,
    isDataChecked: Boolean,
    onClick: (Package, Boolean, Boolean) -> Unit = { _: Package, _: Boolean, _: Boolean -> },
    onApkClick: (Package, Boolean) -> Unit = { _: Package, _: Boolean -> },
    onDataClick: (Package, Boolean) -> Unit = { _: Package, _: Boolean -> },
) {
    val packageItem by remember(item) { mutableStateOf(item) }
    var apkChecked by remember(isApkChecked) { mutableStateOf(isApkChecked) }
    var dataChecked by remember(isDataChecked) { mutableStateOf(isDataChecked) }
    val showApk by remember(packageItem) {
        mutableStateOf(
            when {
                packageItem.isSpecial || (restore && !packageItem.hasApk) -> false
                else                                                      -> true
            }
        )
    }
    val showData by remember(packageItem) {
        mutableStateOf(
            when {
                restore && !packageItem.hasData -> false
                else                            -> true
            }
        )
    }
    //Timber.i("recompose BatchPackageItem ${packageItem.packageName}")

    Card(
        modifier = Modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = {
            val checked = (apkChecked || !showApk) && (dataChecked || !showData)
            if (showApk) apkChecked = !checked
            if (showData) dataChecked = !checked
            onClick(packageItem, apkChecked, dataChecked)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = apkChecked,
                enabled = showApk,
                onCheckedChange = {
                    apkChecked = it
                    onApkClick(packageItem, it)
                }
            )
            Checkbox(checked = dataChecked,
                enabled = showData,
                onCheckedChange = {
                    dataChecked = it
                    onDataClick(packageItem, it)
                }
            )

            Column(
                modifier = Modifier.wrapContentHeight()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = packageItem.packageLabel,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    PackageLabels(item = packageItem)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = packageItem.packageName,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                    AnimatedVisibility(visible = packageItem.hasBackups) {
                        Text(
                            text = (packageItem.latestBackup?.backupDate?.getFormattedDate(
                                false
                            ) ?: "") + " • ${packageItem.numberOfBackups}",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestorePackageItem(
    item: Package,
    apkBackupChecked: MutableState<Int?>,
    dataBackupChecked: MutableState<Int?>,
    onClick: (Package, Boolean, Boolean) -> Unit = { _: Package, _: Boolean, _: Boolean -> },
    onBackupApkClick: (String, Boolean, Int) -> Unit = { _: String, _: Boolean, _: Int -> },
    onBackupDataClick: (String, Boolean, Int) -> Unit = { _: String, _: Boolean, _: Int -> },
) {
    val packageItem by remember(item) { mutableStateOf(item) }
    val apkBC by apkBackupChecked
    val dataBC by dataBackupChecked
    var apkChecked by remember(apkBackupChecked) { mutableStateOf(apkBC == 0) }
    var dataChecked by remember(dataBackupChecked) { mutableStateOf(dataBC == 0) }
    val checkableApk by remember(packageItem) {
        mutableStateOf(packageItem.latestBackup?.hasApk == true)
    }
    val checkableData by remember(packageItem) {
        mutableStateOf(packageItem.latestBackup?.hasData == true)
    }

    Card(
        modifier = Modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = {
            val checked = (apkChecked || !checkableApk) && (dataChecked || !checkableData)
            if (checkableApk) apkChecked = !checked
            if (checkableData) dataChecked = !checked
            onClick(packageItem, apkChecked, dataChecked)
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = packageItem.packageLabel,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f),
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (item.isUpdated) {
                            ButtonIcon(
                                Phosphor.CircleWavyWarning, R.string.radio_updated,
                                tint = ColorUpdated
                            )
                        }
                        ButtonIcon(
                            when {
                                item.isSpecial -> Phosphor.AsteriskSimple
                                item.isSystem  -> Phosphor.Spinner
                                else           -> Phosphor.User
                            },
                            R.string.app_s_type_title,
                            tint = when {
                                !item.isInstalled -> ColorDisabled
                                item.isDisabled   -> ColorDisabled
                                item.isSpecial    -> ColorSpecial
                                item.isSystem     -> ColorSystem
                                else              -> ColorUser
                            }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = packageItem.packageName,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f),
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                        AnimatedVisibility(visible = packageItem.hasBackups) {
                            Text(
                                text = (packageItem.latestBackup?.backupDate?.getFormattedDate(
                                    false
                                ) ?: "") + " • ${packageItem.numberOfBackups}",
                                modifier = Modifier.align(Alignment.CenterVertically),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            item.backupsNewestFirst.forEachIndexed { index, item ->
                RestoreBackupItem(
                    item = item,
                    index = index,
                    isApkChecked = index == apkBC,
                    isDataChecked = index == dataBC,
                    onApkClick = onBackupApkClick,
                    onDataClick = onBackupDataClick,
                )
            }
        }
    }
}