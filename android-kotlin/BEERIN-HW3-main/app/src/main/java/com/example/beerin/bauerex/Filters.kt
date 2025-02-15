package com.example.beerin.bauerex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beerin.ui.theme.BackgroundColor
import com.example.beerin.ui.theme.ItemBoxColor

val backgroundColor = BackgroundColor
val cardColor = Color(0xFF434343)
val contentAreaColor = ItemBoxColor

val filtersDataClass = FiltersDataClass()

@Composable
fun FiltersScreen(topBar: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier.background(Color(0xFF1C1B1B))
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            topBar()
        }
        FiltersArea(
            filterDataClass = filtersDataClass,
            contentAreaColor = contentAreaColor
        )
    }
}


@Composable
fun FiltersArea(
    filterDataClass: FiltersDataClass,
    contentAreaColor: Color
) {
    val filterCategories = listOf(
        Pair("Крепость", filterDataClass.strengthRanges to filterDataClass.selectedStrengthRange),
        Pair("Плотность", filterDataClass.densityRanges to filterDataClass.selectedDensityRange),
        Pair("Страны", filterDataClass.countries to filterDataClass.selectedCountry),
        Pair("Сорт пива", filterDataClass.beerTypes to filterDataClass.selectedBeerType),
        Pair("Тип пива", filterDataClass.beerStyles to filterDataClass.selectedBeerStyle)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(contentAreaColor, shape = RoundedCornerShape(30.dp))
            .padding(16.dp)
    ) {
        filterCategories.forEach { (label, optionsWithState) ->
            val (options, selectedOption) = optionsWithState
            item {
                FilterCategory(
                    label = label,
                    options = options,
                    selectedOption = selectedOption
                )
            }
        }
        item {
            CheckboxOption(
                label = "Оценки только 4+",
                isChecked = filterDataClass.ratingsOnlyAbove4.value,
                onCheckedChange = { checked ->
                    filterDataClass.ratingsOnlyAbove4.value = checked
                }
            )
        }
    }
}

@Composable
fun FilterCategory(
    label: String,
    options: List<String>,
    selectedOption: MutableState<String?>
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(cardColor, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = label, color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(16.dp)
        )
        if (expanded) {
            options.forEach { option ->
                CheckboxOption(
                    label = option,
                    isChecked = selectedOption.value == option,
                    onCheckedChange = { checked ->
                        selectedOption.value = if (checked) option else null
                    }
                )
            }
        }
    }
}

@Composable
fun CheckboxOption(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkmarkColor = Color.White, uncheckedColor = Color.Black, checkedColor = ItemBoxColor)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 20.sp
        )
    }
}
