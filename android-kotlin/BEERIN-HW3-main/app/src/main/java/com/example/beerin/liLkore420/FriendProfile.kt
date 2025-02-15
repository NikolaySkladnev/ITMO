package com.example.beerin.liLkore420

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.beerin.liLkore420.response.Friend
import com.example.beerin.liLkore420.response.FriendsViewModel


@Composable
fun FriendProfileScreen(
    viewModel: FriendsViewModel,
    login: String,
    password: String,
    friendId: Int
) {
    val friendInfo = viewModel.fetchFriendInfo(login, password, friendId).collectAsState().value

    if (friendInfo != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(28, 27, 27))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(38, 35, 35))
            ) {
                AsyncImage(
                    model = "http://147.45.48.182:8000/user_image/${friendInfo.image}",
                    contentDescription = "Avatar",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${friendInfo.name} ${friendInfo.surname}",
                fontSize = 24.sp,
                color = Color.White
            )

            Text(
                text = friendInfo.status ?: "Нет статуса",
                fontSize = 16.sp,
                color = Color(229, 229, 229)
            )
        }
    } else {
        Text(
            text = "Загрузка...",
            fontSize = 16.sp,
            color = Color.White,
        )
    }
}

@Composable
fun getDefaultFriend(): Friend {
    return Friend(
        userId = 1,
        name = "Коля",
        surname = "Складнев",
        status = "В поиске",
        phoneNumber = "+7-900-123-45-67",
        image = null
    )
}
