package dev.wceng.seaofvoice.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wceng.seaofvoice.data.model.DarkModeConfig
import dev.wceng.seaofvoice.ui.components.SearchAreaSpacer
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onDarkModeChange = viewModel::updateDarkModeConfig,
        onDynamicColorChange = viewModel::updateDynamicColorPreference,
        onAutoPlayChange = viewModel::updateAutoPlayPreference,
        onPauseOnHeadsetDisconnectChange = viewModel::updatePauseOnHeadsetDisconnect,
        onClearImageCache = {
            viewModel.clearImageCache()
            scope.launch {
                snackbarHostState.showSnackbar("图片缓存已清理")
            }
        },
        onClearPlaybackHistory = {
            viewModel.clearPlaybackHistory()
            scope.launch {
                snackbarHostState.showSnackbar("播放历史已清除")
            }
        },
        onClearSearchHistory = {
            viewModel.clearSearchHistory()
            scope.launch {
                snackbarHostState.showSnackbar("搜索历史已清除")
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsUiState,
    snackbarHostState: SnackbarHostState,
    onDarkModeChange: (DarkModeConfig) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onAutoPlayChange: (Boolean) -> Unit,
    onPauseOnHeadsetDisconnectChange: (Boolean) -> Unit,
    onClearImageCache: () -> Unit,
    onClearPlaybackHistory: () -> Unit,
    onClearSearchHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearSearchDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 92.dp)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SettingsUiState.Success -> {
                Column(
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    SearchAreaSpacer()

                    SettingsSection(title = "外观") {
                        ThemeSettingRow(
                            config = uiState.settings.darkModeConfig,
                            onConfigChange = onDarkModeChange
                        )
                        SwitchSettingRow(
                            title = "动态色彩 (Material You)",
                            subtitle = "根据系统壁纸调整应用配色",
                            icon = Icons.Default.Palette,
                            checked = uiState.settings.useDynamicColor,
                            onCheckedChange = onDynamicColorChange
                        )
                    }

                    SettingsSection(title = "播放") {
                        SwitchSettingRow(
                            title = "启动时自动播放",
                            subtitle = "打开应用时继续收听上次的电台",
                            icon = Icons.Default.PlayCircle,
                            checked = uiState.settings.autoPlayOnStart,
                            onCheckedChange = onAutoPlayChange
                        )
                        SwitchSettingRow(
                            title = "断开耳机时暂停",
                            subtitle = "拔出耳机或断开蓝牙设备时停止播放",
                            icon = Icons.Default.Headphones,
                            checked = uiState.settings.pauseOnHeadsetDisconnect,
                            onCheckedChange = onPauseOnHeadsetDisconnectChange
                        )
                    }

                    SettingsSection(title = "数据") {
                        ClickableRow(
                            title = "清除播放历史",
                            icon = Icons.Default.History,
                            onClick = { showClearHistoryDialog = true }
                        )
                        ClickableRow(
                            title = "清除搜索历史",
                            icon = Icons.Default.Search,
                            onClick = { showClearSearchDialog = true }
                        )
                        ClickableRow(
                            title = "清除封面缓存",
                            icon = Icons.Default.Cached,
                            onClick = onClearImageCache
                        )
                    }

                    SettingsSection(title = "关于") {
                        InfoRow(
                            title = "版本",
                            value = "1.0.0 (Alpha)",
                            icon = Icons.Default.Info
                        )
                        InfoRow(
                            title = "数据来源",
                            value = "Radio Browser API",
                            icon = Icons.Default.Public
                        )
                        ClickableRow(
                            title = "开发者信息",
                            icon = Icons.Default.Code,
                            onClick = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("清除播放历史") },
            text = { Text("确定要清除所有的播放历史记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearPlaybackHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("确定", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showClearSearchDialog) {
        AlertDialog(
            onDismissRequest = { showClearSearchDialog = false },
            title = { Text("清除搜索历史") },
            text = { Text("确定要清除所有的搜索关键词记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearSearchHistory()
                        showClearSearchDialog = false
                    }
                ) {
                    Text("确定", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearSearchDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun ThemeSettingRow(
    config: DarkModeConfig,
    onConfigChange: (DarkModeConfig) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text("主题模式") },
        supportingContent = {
            Text(
                when (config) {
                    DarkModeConfig.FOLLOW_SYSTEM -> "跟随系统"
                    DarkModeConfig.LIGHT -> "浅色"
                    DarkModeConfig.DARK -> "深色"
                }
            )
        },
        leadingContent = { Icon(Icons.Default.DarkMode, contentDescription = null) },
        trailingContent = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = Modifier.clickable { showDialog = true },
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("选择主题") },
            text = {
                Column {
                    ThemeOption("跟随系统", config == DarkModeConfig.FOLLOW_SYSTEM) {
                        onConfigChange(DarkModeConfig.FOLLOW_SYSTEM)
                        showDialog = false
                    }
                    ThemeOption("浅色", config == DarkModeConfig.LIGHT) {
                        onConfigChange(DarkModeConfig.LIGHT)
                        showDialog = false
                    }
                    ThemeOption("深色", config == DarkModeConfig.DARK) {
                        onConfigChange(DarkModeConfig.DARK)
                        showDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun SwitchSettingRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    )
}

@Composable
private fun InfoRow(title: String, value: String, icon: ImageVector) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = { Text(text = value, style = MaterialTheme.typography.bodyMedium) },
        leadingContent = { Icon(icon, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    )
}

@Composable
private fun ClickableRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    )
}
