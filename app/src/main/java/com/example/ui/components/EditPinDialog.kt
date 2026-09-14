package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PinItem
import com.example.ui.theme.BrandCrimson

@Composable
fun EditPinDialog(
    pin: PinItem,
    onDismiss: () -> Unit,
    onSave: (PinItem) -> Unit
) {
    var title by remember { mutableStateOf(pin.title) }
    var headline by remember { mutableStateOf(pin.headline) }
    var cta by remember { mutableStateOf(pin.cta) }
    var description by remember { mutableStateOf(pin.description) }
    var primaryKeyword by remember { mutableStateOf(pin.primaryKeyword) }
    var secondaryKeywordsStr by remember { mutableStateOf(pin.secondaryKeywords.joinToString(", ")) }
    var boardName by remember { mutableStateOf(pin.boardName) }
    var hook by remember { mutableStateOf(pin.hook) }
    var imagePrompt by remember { mutableStateOf(pin.imagePrompt) }
    var altText by remember { mutableStateOf(pin.altText) }
    var websiteUrl by remember { mutableStateOf(pin.websiteUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Pinterest Pin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Pin Title") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_title"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Graphic Overlay Headline") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_headline"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = cta,
                    onValueChange = { cta = it },
                    label = { Text("Call to Action (CTA)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_cta"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Pin Description (SEO)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_description"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                OutlinedTextField(
                    value = primaryKeyword,
                    onValueChange = { primaryKeyword = it },
                    label = { Text("Primary Keyword") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_primary_keyword"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = secondaryKeywordsStr,
                    onValueChange = { secondaryKeywordsStr = it },
                    label = { Text("Secondary Keywords (comma separated)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_keywords"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                OutlinedTextField(
                    value = boardName,
                    onValueChange = { boardName = it },
                    label = { Text("Suggested Board Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_board"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = hook,
                    onValueChange = { hook = it },
                    label = { Text("Content Hook") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_hook"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = imagePrompt,
                    onValueChange = { imagePrompt = it },
                    label = { Text("Image Generation Prompt (2:3)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_image_prompt"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                OutlinedTextField(
                    value = altText,
                    onValueChange = { altText = it },
                    label = { Text("Alt Text (Accessibility)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_alt_text"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = websiteUrl,
                    onValueChange = { websiteUrl = it },
                    label = { Text("Website URL (Optional)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_pin_website_url"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val kws = secondaryKeywordsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val updatedPin = pin.copy(
                        title = title.trim(),
                        headline = headline.trim(),
                        cta = cta.trim(),
                        description = description.trim(),
                        primaryKeyword = primaryKeyword.trim(),
                        secondaryKeywords = kws,
                        boardName = boardName.trim(),
                        hook = hook.trim(),
                        imagePrompt = imagePrompt.trim(),
                        altText = altText.trim(),
                        websiteUrl = websiteUrl.trim()
                    )
                    onSave(updatedPin)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_edit_pin_button")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("cancel_edit_pin_button")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
