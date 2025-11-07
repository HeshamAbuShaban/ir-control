package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.training.ir_control.model.BrandPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandSelector(
    brands: List<BrandPreset>,
    selected: BrandPreset?,
    onSelect: (BrandPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = selected?.name ?: "Select Brand",
            onValueChange = {},
            readOnly = true,
            label = { Text("Brand") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = brands.isNotEmpty()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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

