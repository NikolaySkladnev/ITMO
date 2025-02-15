package com.example.beerin

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//import com.google.accompanist.svg.rememberSvgResource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beerin.bauerex.FiltersScreen
import com.example.beerin.bauerex.NewsScreen
import com.example.beerin.bauerex.baseUrl
import com.example.beerin.bauerex.response.PostFactory
import com.example.beerin.bauerex.response.PostRepository
import com.example.beerin.bauerex.response.PostsViewModel
import com.example.beerin.goodshortname.ProductDetailsScreen
import com.example.beerin.letooow.CalendarScreen
import com.example.beerin.letooow.HomeFeedScreen
import com.example.beerin.letooow.response.FriendsFactory
import com.example.beerin.letooow.response.Item
import com.example.beerin.letooow.response.ItemFactory
import com.example.beerin.letooow.response.ItemsResponse
import com.example.beerin.letooow.response.ItemsViewModel
import com.example.beerin.liLkore420.FriendsListScreen
import com.example.beerin.liLkore420.response.FriendsRepository
import com.example.beerin.liLkore420.response.FriendsViewModel
import com.example.beerin.wasshabbba.LoginScreen
import com.example.beerin.wasshabbba.ProfileDetailsScreen
import com.example.beerin.wasshabbba.response.RegistrationFactory
import com.example.beerin.wasshabbba.RegistrationProfileScreen
import com.example.beerin.wasshabbba.UnregisteredScreen
import com.example.beerin.wasshabbba.response.RegistrationRepository
import com.example.beerin.wasshabbba.response.RegistrationViewModel
import com.google.gson.Gson

lateinit var viewModelRegGlobal: RegistrationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelPost = ViewModelProvider(
            this,
            PostFactory(PostRepository())
        )[PostsViewModel::class.java]
        val viewModelItem = ViewModelProvider(
            this,
            ItemFactory(ItemsResponse())
        )[ItemsViewModel::class.java]

        val viewModelReg = ViewModelProvider(
            this,
            RegistrationFactory(RegistrationRepository())
        )[RegistrationViewModel::class.java]

        val viewModelFriend = ViewModelProvider(
            this,
            FriendsFactory(FriendsRepository())
        )[FriendsViewModel::class.java]
        viewModelRegGlobal = viewModelReg
        setContent {
            MyApp(viewModelPost, viewModelItem, viewModelReg, viewModelFriend)
        }
    }
}

@Composable
fun MyApp(
    viewModelPost: PostsViewModel,
    viewModelItems: ItemsViewModel,
    viewModelReg: RegistrationViewModel,
    friendsViewModel: FriendsViewModel
) {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.background(Color(28, 27, 27)),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_feed",
            Modifier.padding(innerPadding)
        ) {
            composable("profile_details") {
                ProfileDetailsScreen(
                    onExitApp = { navController.navigate("unregistered") }, navController
                )
            }
            composable("home_feed") { HomeFeedScreen(viewModelItems, navController) }
            composable("product_details/{itemJson}") { backStackEntry ->
                val itemJson = backStackEntry.arguments?.getString("itemJson") ?: ""
                val item = Gson().fromJson(itemJson, Item::class.java)

                val answerText = listOf(
                    "${item.country}",
                    "${item.volume}",
                    "${item.alcoholPercentage}",
                    "${item.density}",
                    "${item.style}",
                    "${item.color}",
                    "${item.brand}",
                    "${item.proteins} г",
                    "${item.fats} г",
                    "${item.carbohydrates} г",
                )

                val itemImage = baseUrl + "item_image/${item.image}"
                val descriptionName = "${item.description}"
                val itemName = "${item.name}"
                ProductDetailsScreen(
                    answerText,
                    itemImage,
                    descriptionName,
                    itemName,
                    navController
                )
            }
            composable("news") {
                NewsScreen(
                    viewModel = viewModelPost
                ) { CustomButton() }
            }
            composable("calendar") { CalendarScreen() }
            composable("registration") {
                RegistrationProfileScreen(
                    onSwitchToLogin = { navController.navigate("login") }, viewModelReg
//                    onEnerApp = { navController.navigate("home_feed") }
                )
            }
            composable("filter") {
                FiltersScreen {
                    CustomButton(leftIcon = Icons.Default.ArrowBack,
                        onLeftIconClick = { navController.navigate("home_feed") })
                }
            }
            composable("friend") {
                FriendsListScreen(viewModel = friendsViewModel) {
                    CustomButton(leftIcon = Icons.Default.ArrowBack,
                        onLeftIconClick = { navController.navigate("profile_details") })
                }
            }
            composable("login") {
                LoginScreen(
                    onSwitchToRegistration = { navController.navigate("registration") }
                )
            }
            composable("unregistered") {
                UnregisteredScreen(
                    onRegister = { navController.navigate("registration") },
                    onLogin = { navController.navigate("login") }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Profile", "profile_details", painterResource(id = R.drawable.profile)),
        BottomNavItem("HomeFeed", "home_feed", painterResource(id = R.drawable.home_dark)),
        BottomNavItem("News", "news", painterResource(id = R.drawable.news)),
        BottomNavItem("Calendar", "calendar", painterResource(id = R.drawable.calender))
    )

    BottomNavigation(backgroundColor = Color(28, 27, 27), contentColor = Color.White) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    when (val icon = item.icon) {
                        is Painter -> Image(
                            painter = icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    when {
                        currentRoute in listOf(
                            "registration",
                            "login"
                        ) && item.route == "profile_details" -> {
                            navController.navigate("unregistered") {
                                popUpTo("unregistered") { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        currentRoute == item.route -> {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }

                        else -> {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    }
}


data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: Any
)
