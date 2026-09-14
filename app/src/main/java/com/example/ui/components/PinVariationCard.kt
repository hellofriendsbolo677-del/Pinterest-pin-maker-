package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.PinItem
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PinVariationCard(
    pin: PinItem,
    index: Int,
    onCopySection: (label: String, text: String) -> Unit,
    onCopyEntirePin: () -> Unit,
    onEdit: () -> Unit,
    onRegenerate: () -> Unit,
    onDelete: () -> Unit,
    onOpenImageRefiner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showVisualMockup by remember { mutableStateOf(true) }

    // Map to keep track of temporary "Copied ✓" status on buttons
    val copiedMap = remember { mutableStateMapOf<String, Boolean>() }

    fun triggerCopy(key: String, label: String, content: String) {
        copiedMap[key] = true
        onCopySection(label, content)
    }

    LaunchedEffect(copiedMap.toMap()) {
        copiedMap.keys.forEach { key ->
            if (copiedMap[key] == true) {
                delay(2000)
                copiedMap[key] = false
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pin_variation_card_$index"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Variation badge, Toggle preview, Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BrandCrimson,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "PIN #${index + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = pin.boardName.take(24).ifBlank { "Board Ready" },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { showVisualMockup = !showVisualMockup },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (showVisualMockup) "Hide Preview" else "Show Preview",
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Pin Mockup (Section 7 Preview)
            AnimatedVisibility(visible = showVisualMockup) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                    ) {
                        PinVisualCard(pin = pin)
                    }
                }
            }

            // Structured content
            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // TITLE
                    ContentSection(
                        label = "TITLE",
                        content = pin.title,
                        isCopied = copiedMap["title"] == true,
                        onCopy = { triggerCopy("title", "Pin Title", pin.title) }
                    )

                    // HEADLINE
                    ContentSection(
                        label = "HEADLINE / OVERLAY TEXT",
                        content = pin.headline,
                        isCopied = copiedMap["headline"] == true,
                        onCopy = { triggerCopy("headline", "Pin Headline", pin.headline) }
                    )

                    // CTA
                    ContentSection(
                        label = "CALL TO ACTION (CTA)",
                        content = pin.cta,
                        isCopied = copiedMap["cta"] == true,
                        onCopy = { triggerCopy("cta", "CTA", pin.cta) }
                    )

                    // DESCRIPTION
                    ContentSection(
                        label = "PIN DESCRIPTION (SEO)",
                        content = pin.description,
                        isCopied = copiedMap["desc"] == true,
                        onCopy = { triggerCopy("desc", "Pin Description", pin.description) }
                    )

                    // KEYWORDS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KEYWORDS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BrandCrimson,
                                letterSpacing = 1.sp
                            )
                            CopyActionSmall(
                                isCopied = copiedMap["keywords"] == true,
                                onCopy = {
                                    val allKw = (listOf(pin.primaryKeyword) + pin.secondaryKeywords).joinToString(", ")
                                    triggerCopy("keywords", "Keywords", allKw)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "PRIMARY KEYWORD:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Surface(
                            color = BrandAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                        ) {
                            Text(
                                text = "★ ${pin.primaryKeyword}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "SECONDARY KEYWORDS (${pin.secondaryKeywords.size}):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            pin.secondaryKeywords.forEach { kw ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "#$kw",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // BOARD NAME
                    ContentSection(
                        label = "SUGGESTED BOARD NAME",
                        content = pin.boardName,
                        isCopied = copiedMap["board"] == true,
                        onCopy = { triggerCopy("board", "Board Name", pin.boardName) }
                    )

                    // HOOK
                    if (pin.hook.isNotBlank()) {
                        ContentSection(
                            label = "CONTENT HOOK",
                            content = pin.hook,
                            isCopied = copiedMap["hook"] == true,
                            onCopy = { triggerCopy("hook", "Content Hook", pin.hook) }
                        )
                    }

                    // IMAGE PROMPT & IMPROVE PROMPT ACTION
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IMAGE GENERATION PROMPT (2:3)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BrandCrimson,
                                letterSpacing = 1.sp
                            )
                            Row {
                                OutlinedButton(
                                    onClick = onOpenImageRefiner,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Improve Prompt", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                CopyActionSmall(
                                    isCopied = copiedMap["image"] == true,
                                    onCopy = { triggerCopy("image", "Image Prompt", pin.imagePrompt) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pin.imagePrompt,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // ALT TEXT
                    ContentSection(
                        label = "ALT TEXT (ACCESSIBILITY)",
                        content = pin.altText,
                        isCopied = copiedMap["alt"] == true,
                        onCopy = { triggerCopy("alt", "Alt Text", pin.altText) }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    // Bottom Row Buttons: [COPY ALL], [EDIT], [REGENERATE], [DELETE]
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCopyEntirePin,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("copy_entire_pin_btn_$index")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Entire Pin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("edit_pin_btn_$index")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onRegenerate,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("regen_pin_btn_$index")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Regenerate", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onDelete,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("delete_pin_btn_$index")
                        ) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentSection(
    label: String,
    content: String,
    isCopied: Boolean,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = BrandCrimson,
                letterSpacing = 1.sp
            )
            CopyActionSmall(isCopied = isCopied, onCopy = onCopy)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CopyActionSmall(
    isCopied: Boolean,
    onCopy: () -> Unit
) {
    Surface(
        onClick = onCopy,
        color = if (isCopied) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isCopied) SuccessGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = if (isCopied) "Copied" else "Copy",
                tint = if (isCopied) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isCopied) "Copied ✓" else "Copy",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCopied) SuccessGreen else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
