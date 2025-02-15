package com.example.beerin.bauerex

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class FiltersDataClass(
    var countries: List<String> = listOf("Германия", "Бельгия", "Россия"),
    var beerTypes: List<String> = listOf("Светлое", "Темное", "Лагер", "Эль", "Портер", "Стаут"),
    var beerStyles: List<String> = listOf("Пшеничное", "Рисовое", "Крем-эль", "Дюббель", "Кёльш"),
    val strengthRanges: List<String> = listOf("0-2", "2-4", "4-8", "8-12", "12-14"),
    val densityRanges: List<String> = listOf("0-5", "5-10", "10-15", "15-20", "20-30"),

    var selectedCountry: MutableState<String?> = mutableStateOf(null),
    var selectedBeerType: MutableState<String?> = mutableStateOf(null),
    var selectedBeerStyle: MutableState<String?> = mutableStateOf(null),
    var selectedStrengthRange: MutableState<String?> = mutableStateOf(null),
    var selectedDensityRange: MutableState<String?> = mutableStateOf(null),
    var ratingsOnlyAbove4: MutableState<Boolean> = mutableStateOf(false)
)
