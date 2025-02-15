package com.example.beerin.wasshabbba

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import com.example.beerin.CustomButton
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White)
    )
}

@Composable
fun UnregisteredScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(27, 28, 28)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        CustomButton(modifier = Modifier.padding(8.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "ЗАРЕГИСТРИРУЙТЕСЬ\nИЛИ\nВОЙДИТЕ",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(
                text = "Зарегистрироваться",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Войти",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { onLogin() },
            textAlign = TextAlign.Center,
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )

        Spacer(modifier = Modifier.height(24.dp))

        DividerLine()

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionItem(icon = Icons.Default.Favorite, text = "ИЗБРАННОЕ") {}
        ProfileOptionItem(icon = Icons.Default.StarBorder, text = "ЖЕЛАЕМОЕ") {}
        ProfileOptionItem(icon = Icons.Default.Group, text = "ДРУЗЬЯ") {}
        ProfileOptionItem(icon = Icons.Default.ShoppingCart, text = "ИСТОРИЯ ПОКУПОК") {}
    }
}


@Composable
fun ProfileDetailsScreen(onExitApp: () -> Unit, navController: NavController) {
    val firstName = "НИКИТИЧ"
    val lastName = "ФАМИЛЬЯРОВ"
    val phoneNumber = "+7 900-000-30-20"
    val context = LocalContext.current // Правильное получение контекста

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(27, 28, 28)),
    ) {
        CustomButton(
            text = "BEERIN",
            leftIcon = Icons.Default.Settings,
            rightIcon = Icons.AutoMirrored.Filled.ExitToApp,
            onLeftIconClick = { TODO() },
            onRightIconClick = onExitApp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ФОТО",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = firstName,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = lastName,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = phoneNumber,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            DividerLine()

            Spacer(modifier = Modifier.height(24.dp))

            ProfileOptionItem(icon = Icons.Default.Favorite, text = "ИЗБРАННОЕ") {}
            ProfileOptionItem(icon = Icons.Default.StarBorder, text = "ЖЕЛАЕМОЕ") {}
            ProfileOptionItem(icon = Icons.Default.Group, text = "ДРУЗЬЯ") {
                navController.navigate("friend") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }
            ProfileOptionItem(icon = Icons.Default.ShoppingCart, text = "ИСТОРИЯ ПОКУПОК") {}
            ProfileOptionItem(icon = Icons.Default.Share, text = "ПОДЕЛИТЬСЯ") {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Данные пользователя:\nИмя: $firstName\nФамилия: $lastName\nТелефон: $phoneNumber"
                    )
                }
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Поделиться данными о пользователе"
                    )
                )
            }
        }
    }
}


@Composable
fun ProfileOptionItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Start
        )
    }
}
