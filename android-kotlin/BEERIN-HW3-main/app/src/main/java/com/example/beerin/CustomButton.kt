package com.example.beerin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    text: String = "BEERIN",
    leftIcon: Any? = null,
    rightIcon: Any? = null,
    onLeftIconClick: (() -> Unit)? = null,
    onRightIconClick: (() -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color(0xFF1C1B1B))
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = onLeftIconClick != null) {
                    onLeftIconClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            when (leftIcon) {
                is ImageVector -> Icon(
                    imageVector = leftIcon,
                    contentDescription = "Left Icon",

                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                is Painter -> Icon(
                    painter = leftIcon,
                    contentDescription = "Left Icon",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Box(
            Modifier
                .size(width = 160.dp, height = 42.dp)
                .background(Color.Red, RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = onRightIconClick != null) {
                    onRightIconClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            when (rightIcon) {
                is ImageVector -> Icon(
                    imageVector = rightIcon,
                    contentDescription = "Right Icon",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                is Painter -> Icon(
                    painter = rightIcon,
                    contentDescription = "Right Icon",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun ButtonPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Пример 1: Фильтр слева, Поиск справа, с обработкой кликов
        CustomButton(
            leftIcon = Icons.Default.FilterList,
            rightIcon = Icons.Default.Search,
            onLeftIconClick = { println("Left icon clicked!") },
            onRightIconClick = { println("Right icon clicked!") }
        )

        // Пример 2: Настройки слева, Поделиться справа, с обработкой кликов
        CustomButton(
            leftIcon = Icons.Default.Settings,
            rightIcon = Icons.Default.Share,
            onLeftIconClick = { println("Settings clicked!") },
            onRightIconClick = { println("Share clicked!") }
        )

        // Пример 3: Назад слева, ничего справа, с обработкой клика
        CustomButton(
            leftIcon = Icons.Default.ArrowBack,
            onLeftIconClick = { println("Back clicked!") }
        )

        // Пример 4: Ничего слева, Выйти справа, с обработкой клика
        CustomButton(
            rightIcon = Icons.Default.ExitToApp,
            onRightIconClick = { println("Exit clicked!") }
        )

        // Пример 5: Только текст в центре
        CustomButton()

        // Пример 6: Фильтр слева, справа пусто, с обработкой клика
        CustomButton(
            leftIcon = Icons.Default.FilterList,
            onLeftIconClick = { println("Filter clicked!") }
        )

        // Пример 7: Фильтр слева в нажатом состоянии, справа пусто
//        CustomButton(
//            leftIcon = Icons.Default.FilterList,
//            isLeftIconPressed = true,
//            onLeftIconClick = { println("Pressed Filter clicked!") }
//        )
    }
}


/*
fun NewsScreen(viewModel: PostsViewModel, topBar: @Composable () -> Unit) {
topBar() - переданная вам CustomButton
Column {
            Box(
                modifier = Modifier.background(Color(0xFF1C1B1B))
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                topBar()
            }
            {
            ......ВАШ КОД........
            }
        }
 */