package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BrandCrimson
import com.example.ui.viewmodel.PinCraftViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePinScreen(
    viewModel: PinCraftViewModel,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.topic.collectAsStateWithLifecycle()
    val websiteUrl by viewModel.websiteUrl.collectAsStateWithLifecycle()
    val pinType by viewModel.pinType.collectAsStateWithLifecycle()
    val tone by viewModel.tone.collectAsStateWithLifecycle()
    val variationCount by viewModel.variationCount.collectAsStateWithLifecycle()
    val targetAudience by viewModel.targetAudience.collectAsStateWithLifecycle()
    val primaryKeyword by viewModel.primaryKeyword.collectAsStateWithLifecycle()
    val ctaStyle by viewModel.ctaStyle.collectAsStateWithLifecycle()
    val customCta by viewModel.customCta.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isBulkMode by viewModel.isBulkMode.collectAsStateWithLifecycle()
    val bulkTopics by viewModel.bulkTopics.collectAsStateWithLifecycle()

    val pinTypes = listOf("Blog Post", "Story", "Recipe", "Product", "Tutorial", "List", "Idea", "Quote", "Other")
    val tones = listOf("Click-worthy", "Emotional", "Professional", "Curious", "Inspirational", "Friendly", "Luxury", "Viral-style")
    val ctaOptions = listOf("Read More", "Learn More", "Get the Recipe", "Shop Now", "Discover More", "See How", "Custom")
    val variationOptions = listOf(1, 3, 5, 10)

    var expandedPinType by remember { mutableStateOf(false) }
    var expandedTone by remember { mutableStateOf(false) }
    var expandedCta by remember { mutableStateOf(false) }

    val quickExamples = listOf(
        "A mysterious story about a hidden room behind an old bookshelf",
        "Small bedroom organization hacks to double your closet space",
        "Creamy 20-minute Tuscan garlic chicken skillet recipe",
        "Beginner's guide to indoor fiddle leaf fig plant care"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Heading
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BrandCrimson.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = BrandCrimson,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Create a Pinterest Pin",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Fill in your core idea and let AI optimize for high Pinterest discovery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Mode Switcher: Single Pin vs Bulk Mode
        item {
            TabRow(
                selectedTabIndex = if (isBulkMode) 1 else 0,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clipShape(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = !isBulkMode,
                    onClick = { viewModel.isBulkMode.value = false },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Single Topic Mode", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = isBulkMode,
                    onClick = { viewModel.isBulkMode.value = true },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bulk Mode", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        // Quick Topic Inspiration Pills
        if (!isBulkMode) {
            item {
                Text(
                    text = "Quick Topic Ideas:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickExamples.forEach { ex ->
                        Surface(
                            onClick = { viewModel.topic.value = ex },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "💡 ${ex.take(34)}...",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main Input Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isBulkMode) {
                        // Bulk Mode Input (Section 17)
                        Column {
                            Text(
                                text = "What are your topics? (One per line)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter multiple topics. We'll generate a complete pin package for each one.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = bulkTopics,
                                onValueChange = { viewModel.bulkTopics.value = it },
                                placeholder = {
                                    Text("Hidden room mystery story\nSmall bedroom organization\nWeekend productivity tips\nEasy morning routine")
                                },
                                minLines = 5,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bulk_topics_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    } else {
                        // FIELD 1: What is your topic?
                        Column {
                            Text(
                                text = "What is your topic? *",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = topic,
                                onValueChange = { viewModel.topic.value = it },
                                placeholder = {
                                    Text("Example: A mysterious story about a hidden room behind an old bookshelf")
                                },
                                minLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("create_pin_topic_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // FIELD 2: Website URL (Optional)
                    Column {
                        Text(
                            text = "Website URL (Optional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = websiteUrl,
                            onValueChange = { viewModel.websiteUrl.value = it },
                            placeholder = { Text("https://yourwebsite.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_pin_url_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // FIELD 3: Pin Type
                        ExposedDropdownMenuBox(
                            expanded = expandedPinType,
                            onExpandedChange = { expandedPinType = !expandedPinType },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = pinType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pin Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPinType) },
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("pin_type_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedPinType,
                                onDismissRequest = { expandedPinType = false }
                            ) {
                                pinTypes.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            viewModel.pinType.value = item
                                            expandedPinType = false
                                        }
                                    )
                                }
                            }
                        }

                        // FIELD 4: Tone
                        ExposedDropdownMenuBox(
                            expanded = expandedTone,
                            onExpandedChange = { expandedTone = !expandedTone },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = tone,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tone") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTone) },
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("pin_tone_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTone,
                                onDismissRequest = { expandedTone = false }
                            ) {
                                tones.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            viewModel.tone.value = item
                                            expandedTone = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (!isBulkMode) {
                        // FIELD 5: Number of Variations (Options: 1, 3, 5, 10)
                        Column {
                            Text(
                                text = "Number of Variations",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                variationOptions.forEachIndexed { index, count ->
                                    SegmentedButton(
                                        selected = variationCount == count,
                                        onClick = { viewModel.variationCount.value = count },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = variationOptions.size)
                                    ) {
                                        Text("$count ${if (count == 1) "pin" else "pins"}")
                                    }
                                }
                            }
                        }
                    }

                    // FIELD 6: Target Audience (Optional)
                    Column {
                        Text(
                            text = "Target Audience (Optional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = targetAudience,
                            onValueChange = { viewModel.targetAudience.value = it },
                            placeholder = { Text("Example: Women interested in home organization") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_pin_audience_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // FIELD 7: Primary Keyword (Optional)
                    Column {
                        Text(
                            text = "Primary Keyword (Optional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = primaryKeyword,
                            onValueChange = { viewModel.primaryKeyword.value = it },
                            placeholder = { Text("Example: mystery stories") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_pin_primary_keyword_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // FIELD 8: CTA Style (Options: Read More, Learn More, Get the Recipe, Shop Now, Discover More, See How, Custom)
                    Column {
                        Text(
                            text = "CTA Style",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedCta,
                            onExpandedChange = { expandedCta = !expandedCta }
                        ) {
                            OutlinedTextField(
                                value = ctaStyle,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select CTA") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCta) },
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("cta_style_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCta,
                                onDismissRequest = { expandedCta = false }
                            ) {
                                ctaOptions.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            viewModel.ctaStyle.value = item
                                            expandedCta = false
                                        }
                                    )
                                }
                            }
                        }

                        // If Custom is selected, show a text input
                        AnimatedVisibility(visible = ctaStyle == "Custom") {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                OutlinedTextField(
                                    value = customCta,
                                    onValueChange = { viewModel.customCta.value = it },
                                    label = { Text("Custom CTA Text *") },
                                    placeholder = { Text("e.g. CLAIM FREE GUIDE →") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_cta_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    // Primary Generate Button
                    Button(
                        onClick = {
                            if (isBulkMode) {
                                viewModel.generateBulkPins()
                            } else {
                                viewModel.generatePins()
                            }
                        },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("generate_pinterest_pins_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generating Pins...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBulkMode) "✨ Generate Bulk Pins" else "✨ Generate Pinterest Pins",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Extension to clip tab row
private fun Modifier.clipShape(shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.clip(shape)
