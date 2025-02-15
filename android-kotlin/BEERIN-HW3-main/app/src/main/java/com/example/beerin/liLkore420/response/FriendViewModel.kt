
package com.example.beerin.liLkore420.response

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: FriendsRepository) : ViewModel() {
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> get() = _friends

    fun fetchFriends(login: String, password: String) {
        viewModelScope.launch {
            try {
                _friends.value = repository.getFriends(login, password)
            } catch (e: Exception) {
                Log.e("FriendsViewModel", "Error fetching friends", e)
            }
        }
    }

    fun fetchFriendInfo(login: String, password: String, friendId: Int): StateFlow<Friend?> {
        val friendInfo = MutableStateFlow<Friend?>(null)
        viewModelScope.launch {
            try {
                friendInfo.value = repository.getFriendInfo(login, password, friendId)
            } catch (e: Exception) {
                Log.e("FriendsViewModel", "Error fetching friend info", e)
            }
        }
        return friendInfo
    }
}
