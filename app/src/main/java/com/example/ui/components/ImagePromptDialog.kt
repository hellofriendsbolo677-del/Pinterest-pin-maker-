package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandCrimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePromptDialog(
    initialPrompt: String,
    isImproving: Boolean,
    onDismiss: () -> Unit,
    onImprovePrompt: (style: String, lighting: String, camera: String, composition: String) -> Unit
) {
    val styles = listOf("Photorealistic", "Editorial", "Cinematic", "Minimal", "Luxury", "Lifestyle", "Dramatic")
    val lightings = listOf("Natural", "Soft", "Golden Hour", "Studio", "Cinematic", "Moody")
    val cameras = listOf("Smartphone Realism", "DSLR", "Mirrorless", "Editorial Photography", "Cinematic Lens")
    val compositions = listOf("Hero Subject", "Flat Lay", "Close-Up", "Wide Scene", "Lifestyle Scene")

    var selectedStyle by remember { mutableStateOf(styles[1]) }
    var selectedLighting by remember { mutableStateOf(lightings[0]) }
    var selectedCamera by remember { mutableStateOf(cameras[3]) }
    var selectedComposition by remember { mutableStateOf(compositions[0]) }

    var expandedStyle by remember { mutableStateOf(false) }
    var expandedLighting by remember { mutableStateOf(false) }
    var expandedCamera by remember { mutableStateOf(false) }
    var expandedComposition by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isImproving) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BrandCrimson,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Improve Image Prompt",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CURRENT PROMPT:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = initialPrompt,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Orientation badge
                Surface(
                    color = BrandCrimson.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📐 Output Aspect Ratio: Pinterest 2:3 Vertical (~1000 × 1500)",
                        color = BrandCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Style Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedStyle,
                    onExpandedChange = { expandedStyle = !expandedStyle }
                ) {
                    OutlinedTextField(
                        value = selectedStyle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Style") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStyle) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStyle,
                        onDismissRequest = { expandedStyle = false }
                    ) {
                        styles.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedStyle = item
                                    expandedStyle = false
                                }
                            )
                        }
                    }
                }

                // Lighting Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedLighting,
                    onExpandedChange = { expandedLighting = !expandedLighting }
                ) {
                    OutlinedTextField(
                        value = selectedLighting,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Lighting") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLighting) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLighting,
                        onDismissRequest = { expandedLighting = false }
                    ) {
                        lightings.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedLighting = item
                                    expandedLighting = false
                                }
                            )
                        }
                    }
                }

                // Camera Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCamera,
                    onExpandedChange = { expandedCamera = !expandedCamera }
                ) {
                    OutlinedTextField(
                        value = selectedCamera,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Camera") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCamera) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCamera,
                        onDismissRequest = { expandedCamera = false }
                    ) {
                        cameras.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedCamera = item
                                    expandedCamera = false
                                }
                            )
                        }
                    }
                }

                // Composition Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedComposition,
                    onExpandedChange = { expandedComposition = !expandedComposition }
                ) {
                    OutlinedTextField(
                        value = selectedComposition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Composition") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedComposition) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedComposition,
                        onDismissRequest = { expandedComposition = false }
                    ) {
                        compositions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedComposition = item
                                    expandedComposition = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onImprovePrompt(selectedStyle, selectedLighting, selectedCamera, selectedComposition)
                },
                enabled = !isImproving,
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("apply_improved_prompt_button")
            ) {
                if (isImproving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Refining...")
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Improve Image Prompt")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isImproving,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
