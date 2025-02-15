package com.example.beerin.letooow.response

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerin.bauerex.filtersDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemsViewModel(private val itemsResponse: ItemsResponse) : ViewModel() {

    private val _itemsState = MutableStateFlow<List<Item>>(emptyList())
    val itemsState: StateFlow<List<Item>> get() = _itemsState
    private val filtersDataClassLocal = filtersDataClass
    fun fetchItems() {
        viewModelScope.launch {
            try {
                val hasFilters = filtersDataClassLocal.selectedBeerStyle.value != null ||
                        filtersDataClassLocal.selectedBeerType.value != null ||
                        filtersDataClassLocal.selectedCountry.value != null ||
                        filtersDataClassLocal.selectedStrengthRange.value != null ||
                        filtersDataClassLocal.selectedDensityRange.value != null || filtersDataClassLocal.ratingsOnlyAbove4.value

                if (hasFilters) {
                    val items = itemsResponse.getItemsWithFilters(
                        country = filtersDataClassLocal.selectedCountry.value,
                        style = filtersDataClassLocal.selectedBeerStyle.value,
                        type = filtersDataClassLocal.selectedBeerType.value,
                        alcoholRange = filtersDataClassLocal.selectedStrengthRange.value,
                        densityRange = filtersDataClassLocal.selectedDensityRange.value
                    )
                    _itemsState.value = if (filtersDataClassLocal.ratingsOnlyAbove4.value) {
                        items.filter { (it.averageRating ?: 0) >= 4 }
                    } else {
                        items
                    }
                } else {
                    val items = itemsResponse.getItems()
                    _itemsState.value = items
                }
            } catch (e: Exception) {
                Log.e("ERROR VIEW MODEL", "$e")
            }
        }
    }

}
