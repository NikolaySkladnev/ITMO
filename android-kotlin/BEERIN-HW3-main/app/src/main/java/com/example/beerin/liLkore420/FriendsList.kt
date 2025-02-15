package com.example.beerin.liLkore420

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.beerin.bauerex.baseUrl
import com.example.beerin.liLkore420.response.Friend
import com.example.beerin.liLkore420.response.FriendsViewModel
import com.example.beerin.liLkore420.FriendProfileScreen

val backgroundColor = Color(0xFF1C1B1B)
val cardColor = Color(0xFF434343)
val contentAreaColor = Color(0xFF262323)

const val baseUrl = "http://147.45.48.182:8000/"

@Composable
fun FriendsListScreen(viewModel: FriendsViewModel, topBar: @Composable () -> Unit) {
    viewModel.fetchFriends("Alexey", "321")
    val friends = viewModel.friends.collectAsState().value

    Column {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            topBar()
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(contentAreaColor)
        ) {
            items(friends) { friend ->
                FriendItem(friend)
                Spacer(
                    modifier = Modifier.height(
                        10.dp
                    )
                )
            }
        }
    }
}


@Composable
fun FriendItem(friend: Friend) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11))
            .background(contentAreaColor)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            FriendImage(imageUrl = friend.image, modifier = Modifier)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "${friend.name} ${friend.surname}",
                fontSize = 25.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material.MaterialTheme.typography.h6
            )
            Text(
                text = friend.status ?: "Нет статуса",
                color = Color(229, 229, 229),
                style = androidx.compose.material.MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun FriendImage(imageUrl: String?, modifier: Modifier) {
    AsyncImage(
        model = baseUrl + "user_image/$imageUrl",
        contentDescription = "Friend",
        modifier = modifier.fillMaxSize()
    )
}
