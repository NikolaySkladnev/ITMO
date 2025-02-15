package com.example.beerin.letooow

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.beerin.CustomButton
import com.example.beerin.R
import com.example.beerin.bauerex.baseUrl
import com.example.beerin.letooow.response.ItemsViewModel
import com.example.beerin.ui.theme.BackgroundColor
import com.example.beerin.ui.theme.ButtonColor
import com.example.beerin.ui.theme.ItemBoxColor
import com.example.beerin.ui.theme.YellowItemColor
import com.google.gson.Gson


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeFeedScreen(
    viewModel: ItemsViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.fetchItems()
    }
    val listItems = viewModel.itemsState.collectAsState().value

    Column(modifier = Modifier.background(BackgroundColor)) {

        CustomButton(
            text = "BEERIN",
            leftIcon = painterResource(id = R.drawable.filter),
            rightIcon = painterResource(id = R.drawable.search),
            onLeftIconClick = {
                navController.navigate("filter") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            },
            onRightIconClick = { println("Right icon clicked!") },
            modifier = Modifier.padding(8.dp)
        )


        if (listItems.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }

        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {
                LazyColumn {
                    items(listItems.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .aspectRatio(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8))
                                .background(
                                    ItemBoxColor
                                )
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .clip(RoundedCornerShape(11))
                                        .padding(20.dp)
                                        .aspectRatio(1f / 2.4f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BeerImage(
                                        imageUrl = listItems[index].image,
                                        modifier = Modifier
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text(
                                        text = "${listItems[index].name}", fontSize = 32.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    RatingBar(rating = listItems[index].ratingCount ?: 0)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.absolutePadding(top = 20.dp)) {

                                        Image(
                                            Icons.Rounded.Grass,
                                            modifier = Modifier.size(34.dp),
                                            contentDescription = "Grass",
                                            colorFilter = ColorFilter.tint(YellowItemColor)
                                        )

                                        Column(
                                            modifier = Modifier.absolutePadding(left = 5.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = "${listItems[index].alcoholPercentage}%",
                                                color = Color.White,
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                text = "${listItems[index].density}%",
                                                color = Color.White,
                                                fontSize = 20.sp
                                            )
                                        }


                                        Column(
                                            modifier = Modifier.absolutePadding(left = 7.dp),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = "алкоголя",
                                                color = Color.White,
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                text = "плотности",
                                                color = Color.White,
                                                fontSize = 20.sp
                                            )
                                        }


                                    }
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        Row(modifier = Modifier.absolutePadding(bottom = 20.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                                    .background(ButtonColor)
                                                    .clickable { /* TODO Like */ },
                                                contentAlignment = Alignment.Center,
                                            ) {

                                                Image(
                                                    Icons.Rounded.HeartBroken,
                                                    modifier = Modifier.size(34.dp),
                                                    contentDescription = "Heart"
                                                )

                                            }

                                            Box(
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .height(50.dp)
                                                    .absolutePadding(left = 5.dp)
                                                    .clip(CircleShape)
                                                    .background(ButtonColor)
                                                    .clickable {

                                                        val itemJson =
                                                            Gson().toJson(listItems[index])
                                                        navController.navigate("product_details/${itemJson}") {
                                                            popUpTo(navController.graph.startDestinationId) {
                                                                saveState = true
                                                            }
                                                            launchSingleTop = true

                                                        }

                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "Читать",
                                                    fontSize = 20.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

            }
        }
    }
}

@Composable
fun RatingBar(rating: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Star",
                tint = if (i <= rating) YellowItemColor else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BeerImage(imageUrl: String?, modifier: Modifier) {
    AsyncImage(
        model = baseUrl + "item_image/$imageUrl",
        contentDescription = "Blur_Beer",
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.75f }
            .blur(20.dp)
            .clip(RoundedCornerShape(11)),
        contentScale = ContentScale.Crop
    )

    AsyncImage(
        model = baseUrl + "item_image/$imageUrl",
        contentDescription = "Beer",
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(11))
            .padding(10.dp),
        contentScale = ContentScale.FillHeight
    )
}