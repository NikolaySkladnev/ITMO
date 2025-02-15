package com.example.beerin.goodshortname.samples


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.beerin.goodshortname.ProductDetailsScreen

@Composable
fun SampleArgsProductDetails() {
    val categoryNameList = remember {
        mutableListOf(
            "Страна производства",
            "Объём",
            "Градус",
            "Плотность"
        )
    } // todo апи запрос
    val answerTextList =
        remember { mutableListOf("Россия", "0.5 л", "4%", "10%") } // todo апи запрос
    val imageLink = "http://147.45.48.182:8000/item_image/baltika_7.jpg"
    val descriptionText =
        "Черный эль с бархатисто-сливочной текстурой и красивой бежевой пенной  шапкой. Созданный на основе молочного сахара и специальной смеси  пшеничного и ячменного солода, он предлагает насладиться великолепной  мягкой сладостью, уравновешенной легкой хмелевой горчинкой."
}


//@Composable
//fun SampleUseProductDetails() {
//    ProductDetailsScreen(
//        mutableListOf(
//            "Страна производства",
//            "Объём",
//            "Градус",
//            "Плотность"
//        ),
//        mutableListOf("Россия", "0.5 л", "4%", "10%"),
//        "http://147.45.48.182:8000/item_image/baltika_7.jpg",
//        "Черный эль с бархатисто-сливочной текстурой и красивой бежевой пенной  шапкой. Созданный на основе молочного сахара и специальной смеси  пшеничного и ячменного солода, он предлагает насладиться великолепной  мягкой сладостью, уравновешенной легкой хмелевой горчинкой.",
//    )
//}