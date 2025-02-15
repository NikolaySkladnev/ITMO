package com.example.beerin.bauerex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import android.content.Intent
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import com.example.beerin.bauerex.response.Post
import com.example.beerin.bauerex.response.PostsViewModel
import com.example.beerin.ui.theme.ButtonColor

@Composable
fun PostDetailsScreen(post: Post, viewModel: PostsViewModel, onBack: () -> Unit) {
    val comments = viewModel.comments.collectAsState().value
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(post.id) {
        viewModel.fetchComments(post.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1B))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Детали поста",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            val shareContent = """
                                Ссылка: ${baseUrl}post/${post.id}
                                Лайков: ${post.likeCount}
                                Комментариев: ${post.commentCount}
                                Создано: ${post.createdAt}
                                Компания: ${post.companyName}                              
                            """.trimIndent()
                            putExtra(Intent.EXTRA_TEXT, shareContent)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, ""))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Поделиться",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = baseUrl + ("post_image/${post.image}"),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(comments) { comment ->
                Row(modifier = Modifier.padding(8.dp)) {
                    AsyncImage(
                        model = baseUrl + comment.userAvatarImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = comment.userLogin,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Text(
                            text = comment.createdAt,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = comment.text,
                            fontSize = 24.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ваш аккаунт не подходит для комментирования постов",
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
