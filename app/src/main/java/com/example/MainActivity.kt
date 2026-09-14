package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppScreen
import com.example.ui.components.EditPinDialog
import com.example.ui.components.GenerationLoadingDialog
import com.example.ui.components.ImagePromptDialog
import com.example.ui.screens.CreatePinScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MyPinsScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PinCraftViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: PinCraftViewModel = viewModel()
            val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()

            val darkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                PinCraftApp(viewModel = mainViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinCraftApp(viewModel: PinCraftViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationStatus by viewModel.generationStatus.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val credits by viewModel.credits.collectAsStateWithLifecycle()
    val currentPins by viewModel.currentPins.collectAsStateWithLifecycle()

    val editingIndex by viewModel.editingPinIndex.collectAsStateWithLifecycle()
    val improvingPromptIndex by viewModel.improvingImagePromptIndex.collectAsStateWithLifecycle()
    val isImprovingPrompt by viewModel.isImprovingPrompt.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(BrandCrimson, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "PinCraft AI Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "PinCraft",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "AI",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = BrandCrimson
                                    )
                                }
                                Text(
                                    text = "Pinterest Pin Maker",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Credits Badge (Clickable to refill / open Settings)
                        Surface(
                            onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            color = BrandCrimson.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BrandCrimson.copy(alpha = 0.25f)),
                            modifier = Modifier.padding(end = 12.dp).testTag("top_bar_credits_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BrandCrimson,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$credits Credits",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandCrimson
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("main_bottom_nav_bar")
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = {
                                    if (item.screen == AppScreen.RESULTS && currentPins.isNotEmpty()) {
                                        BadgedBox(badge = {
                                            Badge(containerColor = BrandCrimson) {
                                                Text("${currentPins.size}")
                                            }
                                        }) {
                                            Icon(imageVector = item.icon, contentDescription = item.label)
                                        }
                                    } else {
                                        Icon(imageVector = item.icon, contentDescription = item.label)
                                    }
                                },
                                label = { Text(item.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandCrimson,
                                    selectedTextColor = BrandCrimson,
                                    indicatorColor = BrandCrimson.copy(alpha = 0.12f)
                                ),
                                modifier = Modifier.testTag("nav_item_${item.screen.route}")
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Wide Screen Navigation Rail for Tablets / Desktop
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        navItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = {
                                    if (item.screen == AppScreen.RESULTS && currentPins.isNotEmpty()) {
                                        BadgedBox(badge = {
                                            Badge(containerColor = BrandCrimson) {
                                                Text("${currentPins.size}")
                                            }
                                        }) {
                                            Icon(imageVector = item.icon, contentDescription = item.label)
                                        }
                                    } else {
                                        Icon(imageVector = item.icon, contentDescription = item.label)
                                    }
                                },
                                label = { Text(item.label, fontSize = 11.sp) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = BrandCrimson,
                                    selectedTextColor = BrandCrimson,
                                    indicatorColor = BrandCrimson.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                }

                // Main Content Animated Transition
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.weight(1f),
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        AppScreen.CREATE_PIN -> CreatePinScreen(viewModel = viewModel)
                        AppScreen.RESULTS -> ResultsScreen(viewModel = viewModel)
                        AppScreen.MY_PINS -> MyPinsScreen(viewModel = viewModel)
                        AppScreen.TEMPLATES -> TemplatesScreen(viewModel = viewModel)
                        AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Rotating Status Generating Overlay
    if (isGenerating) {
        GenerationLoadingDialog(status = generationStatus)
    }

    // Edit Pin Modal Dialog
    if (editingIndex != null && editingIndex!! in currentPins.indices) {
        val pin = currentPins[editingIndex!!]
        EditPinDialog(
            pin = pin,
            onDismiss = { viewModel.editingPinIndex.value = null },
            onSave = { updated ->
                viewModel.updatePin(editingIndex!!, updated)
                viewModel.editingPinIndex.value = null
            }
        )
    }

    // Improve Image Prompt Modal Dialog
    if (improvingPromptIndex != null && improvingPromptIndex!! in currentPins.indices) {
        val pin = currentPins[improvingPromptIndex!!]
        ImagePromptDialog(
            initialPrompt = pin.imagePrompt,
            isImproving = isImprovingPrompt,
            onDismiss = { viewModel.improvingImagePromptIndex.value = null },
            onImprovePrompt = { style, lighting, camera, composition ->
                viewModel.improveImagePrompt(improvingPromptIndex!!, style, lighting, camera, composition)
            }
        )
    }
}

private data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

private val navItems = listOf(
    NavItem(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    NavItem(AppScreen.CREATE_PIN, "Create Pin", Icons.Default.AddCircle),
    NavItem(AppScreen.RESULTS, "Results", Icons.Default.Visibility),
    NavItem(AppScreen.MY_PINS, "My Pins", Icons.Default.Folder),
    NavItem(AppScreen.TEMPLATES, "Templates", Icons.Default.AutoStories),
    NavItem(AppScreen.SETTINGS, "Settings", Icons.Default.Settings)
)

// Preserved for screenshot test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
