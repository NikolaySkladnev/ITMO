package com.example.beerin.bauerex

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.beerin.bauerex.response.Post
import com.example.beerin.bauerex.response.PostsViewModel
import com.example.beerin.ui.theme.BackgroundColor
import com.example.beerin.ui.theme.ItemBoxColor

const val baseUrl = "http://147.45.48.182:8000/"

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewsScreen(viewModel: PostsViewModel, topBar: @Composable () -> Unit) {
    viewModel.fetchPosts()
    val posts = viewModel.posts.collectAsState().value

    val selectedPost = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Post?>(null) }

    if (selectedPost.value != null) {
        PostDetailsScreen(
            post = selectedPost.value!!,
            viewModel = viewModel,
            onBack = { selectedPost.value = null }
        )
    } else {
        Column {
            Box(
                modifier = Modifier.background(BackgroundColor)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                topBar()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {
                if (posts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn {
                        items(posts) { post ->
                            PostItem(post, onLikeClicked = { viewModel.likePost(it) }) {
                                selectedPost.value = it
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post, onLikeClicked: (Int) -> Unit, onCommentClicked: (Post) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ItemBoxColor)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color.White),
                model = baseUrl + ("business_image/${post.companyLogoImage}"),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = post.companyName ?: "Компания не указана",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = baseUrl + ("post_image/${post.image}"),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .graphicsLayer { alpha = 0.5f }
                    .blur(16.dp)
            )

            AsyncImage(
                model = baseUrl + ("post_image/${post.image}"),
                contentDescription = null,
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
            )
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Favorite,
                contentDescription = "Like",
                tint = Color.Red,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onLikeClicked(post.id) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${post.likeCount}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            androidx.compose.material.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Comment,
                contentDescription = "Comment",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCommentClicked(post) } // Вызываем обработчик комментариев
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${post.commentCount}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
