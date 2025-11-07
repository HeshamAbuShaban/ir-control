package dev.training.ir_control.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.training.ir_control.model.BrandPreset

@Composable
fun BrandSelector(
        brands: List<BrandPreset>,
        selected: BrandPreset?,
        onSelect: (BrandPreset) -> Unit,
        modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
                value = selected?.name ?: "Select Brand",
                onValueChange = {},
                readOnly = true,
                label = { Text("Brand") },
                trailingIcon = { IconButton(onClick = { expanded = !expanded }) { Text("▼") } },
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                enabled = brands.isNotEmpty()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            brands.forEach { brand ->
                DropdownMenuItem(
                        text = { Text(brand.name) },
                        onClick = {
                            onSelect(brand)
                            expanded = false
                        }
                )
            }
        }
    }
}
