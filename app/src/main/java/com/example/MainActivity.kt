package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.CommunicatorViewModel
import com.example.ui.components.EmergencyAlertBanner
import com.example.ui.dialogs.AccuracyTestingDialog
import com.example.ui.dialogs.ArchitectureDiagramDialog
import com.example.ui.dialogs.DiagnosticsDialog
import com.example.ui.dialogs.EmergencyDialog
import com.example.ui.dialogs.InteractiveDemoDialog
import com.example.ui.dialogs.ModelManagerDialog
import com.example.ui.dialogs.TtsTestingDialog
import com.example.ui.dialogs.TwoPhonesSetupDialog
import com.example.ui.screens.CommunicationScreen
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosCanvas
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.IosTextTertiary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    viewModel: CommunicatorViewModel = viewModel()
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val showEmergencyPanel by viewModel.showEmergencyPanel.collectAsStateWithLifecycle()
    val showDemoDialog by viewModel.showDemoDialog.collectAsStateWithLifecycle()
    val showDiagnostics by viewModel.showDiagnostics.collectAsStateWithLifecycle()
    val showModelManager by viewModel.showModelManager.collectAsStateWithLifecycle()
    val showAccuracyTesting by viewModel.showAccuracyTesting.collectAsStateWithLifecycle()
    val showTtsTesting by viewModel.showTtsTesting.collectAsStateWithLifecycle()
    val showArchitectureDiagram by viewModel.showArchitectureDiagram.collectAsStateWithLifecycle()
    val showTwoPhonesGuide by viewModel.showTwoPhonesGuide.collectAsStateWithLifecycle()
    val activeEmergencyAlert by viewModel.activeEmergencyAlert.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Handle System Back Button & Emulator Back Navigation
    BackHandler(enabled = true) {
        when {
            showEmergencyPanel -> viewModel.setShowEmergencyPanel(false)
            showDemoDialog -> viewModel.setShowDemoDialog(false)
            showDiagnostics -> viewModel.setShowDiagnostics(false)
            showModelManager -> viewModel.setShowModelManager(false)
            showAccuracyTesting -> viewModel.setShowAccuracyTesting(false)
            showTtsTesting -> viewModel.setShowTtsTesting(false)
            showArchitectureDiagram -> viewModel.setShowArchitectureDiagram(false)
            showTwoPhonesGuide -> viewModel.setShowTwoPhonesGuide(false)
            activeEmergencyAlert != null -> viewModel.dismissEmergencyAlert()
            activeTab != AppTab.COMMUNICATE -> viewModel.selectTab(AppTab.COMMUNICATE)
            else -> {
                (context as? ComponentActivity)?.moveTaskToBack(true)
            }
        }
    }

    // Dynamic Record Audio permission request on startup
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (!viewModel.audioRecorder.hasPermission()) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = IosCard,
                contentColor = IosTextPrimary,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .background(IosCard)
                    .border(0.5.dp, IosBorder)
                    .navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == AppTab.COMMUNICATE,
                    onClick = { viewModel.selectTab(AppTab.COMMUNICATE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = "Communicate",
                            tint = if (activeTab == AppTab.COMMUNICATE) IosPrimary else IosTextTertiary
                        )
                    },
                    label = {
                        Text(
                            text = "Transmit",
                            fontSize = 10.sp,
                            fontWeight = if (activeTab == AppTab.COMMUNICATE) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IosPrimary,
                        selectedTextColor = IosPrimary,
                        indicatorColor = IosPrimaryLight,
                        unselectedIconColor = IosTextTertiary,
                        unselectedTextColor = IosTextTertiary
                    ),
                    modifier = Modifier.testTag("tab_communicate")
                )

                NavigationBarItem(
                    selected = activeTab == AppTab.DEVICES,
                    onClick = { viewModel.selectTab(AppTab.DEVICES) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Nearby Devices",
                            tint = if (activeTab == AppTab.DEVICES) IosPrimary else IosTextTertiary
                        )
                    },
                    label = {
                        Text(
                            text = "Devices",
                            fontSize = 10.sp,
                            fontWeight = if (activeTab == AppTab.DEVICES) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IosPrimary,
                        selectedTextColor = IosPrimary,
                        indicatorColor = IosPrimaryLight,
                        unselectedIconColor = IosTextTertiary,
                        unselectedTextColor = IosTextTertiary
                    ),
                    modifier = Modifier.testTag("tab_devices")
                )

                NavigationBarItem(
                    selected = activeTab == AppTab.HISTORY,
                    onClick = { viewModel.selectTab(AppTab.HISTORY) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = if (activeTab == AppTab.HISTORY) IosPrimary else IosTextTertiary
                        )
                    },
                    label = {
                        Text(
                            text = "History",
                            fontSize = 10.sp,
                            fontWeight = if (activeTab == AppTab.HISTORY) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IosPrimary,
                        selectedTextColor = IosPrimary,
                        indicatorColor = IosPrimaryLight,
                        unselectedIconColor = IosTextTertiary,
                        unselectedTextColor = IosTextTertiary
                    ),
                    modifier = Modifier.testTag("tab_history")
                )

                NavigationBarItem(
                    selected = activeTab == AppTab.PERFORMANCE,
                    onClick = { viewModel.selectTab(AppTab.PERFORMANCE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Performance",
                            tint = if (activeTab == AppTab.PERFORMANCE) IosPrimary else IosTextTertiary
                        )
                    },
                    label = {
                        Text(
                            text = "Metrics",
                            fontSize = 10.sp,
                            fontWeight = if (activeTab == AppTab.PERFORMANCE) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IosPrimary,
                        selectedTextColor = IosPrimary,
                        indicatorColor = IosPrimaryLight,
                        unselectedIconColor = IosTextTertiary,
                        unselectedTextColor = IosTextTertiary
                    ),
                    modifier = Modifier.testTag("tab_performance")
                )

                NavigationBarItem(
                    selected = activeTab == AppTab.SETTINGS,
                    onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (activeTab == AppTab.SETTINGS) IosPrimary else IosTextTertiary
                        )
                    },
                    label = {
                        Text(
                            text = "Settings",
                            fontSize = 10.sp,
                            fontWeight = if (activeTab == AppTab.SETTINGS) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IosPrimary,
                        selectedTextColor = IosPrimary,
                        indicatorColor = IosPrimaryLight,
                        unselectedIconColor = IosTextTertiary,
                        unselectedTextColor = IosTextTertiary
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(IosCanvas)
                .padding(innerPadding)
        ) {
            // Main Screen Display
            when (activeTab) {
                AppTab.COMMUNICATE -> CommunicationScreen(viewModel = viewModel)
                AppTab.DEVICES -> DevicesScreen(viewModel = viewModel)
                AppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                AppTab.PERFORMANCE -> PerformanceScreen(viewModel = viewModel)
                AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }

            // Top Alert Banner for Active Critical Emergency Messages
            EmergencyAlertBanner(
                alertPacket = activeEmergencyAlert,
                onDismiss = { viewModel.dismissEmergencyAlert() }
            )
        }
    }

    // Modal Overlays
    if (showEmergencyPanel) {
        EmergencyDialog(
            onDismiss = { viewModel.setShowEmergencyPanel(false) },
            onSendAlert = { category, custom ->
                viewModel.sendEmergencyAlert(category, custom)
            }
        )
    }

    if (showDemoDialog) {
        InteractiveDemoDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowDemoDialog(false) }
        )
    }

    if (showDiagnostics) {
        DiagnosticsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowDiagnostics(false) }
        )
    }

    if (showModelManager) {
        ModelManagerDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowModelManager(false) }
        )
    }

    if (showAccuracyTesting) {
        AccuracyTestingDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowAccuracyTesting(false) }
        )
    }

    if (showTtsTesting) {
        TtsTestingDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowTtsTesting(false) }
        )
    }

    if (showArchitectureDiagram) {
        ArchitectureDiagramDialog(
            onDismiss = { viewModel.setShowArchitectureDiagram(false) }
        )
    }

    if (showTwoPhonesGuide) {
        TwoPhonesSetupDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowTwoPhonesGuide(false) }
        )
    }
}
