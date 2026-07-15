package dev.wceng.seaofvoice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit,
    onMinutesSelected: (Int) -> Unit
) {
    val options = listOf(
        0 to "关闭定时器",
        15 to "15 分钟",
        30 to "30 分钟",
        45 to "45 分钟",
        60 to "60 分钟",
        90 to "90 分钟"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "睡眠定时器") },
        text = {
            LazyColumn {
                items(options) { (minutes, label) ->
                    ListItem(
                        headlineContent = { Text(text = label) },
                        modifier = Modifier.clickable {
                            onMinutesSelected(minutes)
                            onDismiss()
                        },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
