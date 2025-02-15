package com.example.beerin.goodshortname

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.beerin.ui.theme.DarkSeparator
import com.example.beerin.ui.theme.DarkTextPrimary
import com.example.beerin.ui.theme.DarkTextSecondary


/**
 * Экран с карточкой товара. Размерность [categoryName] и [answerText] должна быть одинаковая!
 *
 * @sample com.example.beerin.goodshortname.samples.SampleArgsProductDetails
 * @sample com.example.beerin.goodshortname.samples.SampleUseProductDetails
 *
 * @param categoryName список заголовков из json от api. Они идут на на параметры, поэтому требуется фильтр на вход. **Размерность массивов должна быть одинаковая!**
 * @param answerText список ответов из json от api. Они идут на на параметры, поэтому требуется фильтр на вход. **Размерность массивов должна быть одинаковая!**
 * @param imageLink должен быть в формате полной ссылки
 * @param descriptionText тест для описания товара
 */
@Composable
fun ProductDetailsScreen(
    answerText: List<String>,
    imageLink: String,
    descriptionText: String,
    itemName: String,
    navController: NavController
) {

    val categoryName = listOf(
        "Страна производства",
        "Объём",
        "Градус",
        "Плотность",
        "Стиль",
        "Цвет",
        "Бренд",
        "Белки",
        "Жиры",
        "Углеводы",
    )

    val categoryNameList = remember { categoryName }
    val answerTextList = remember { answerText }

    val curState = remember { mutableIntStateOf(0) }
    // 0 -- description
    // 1 -- parameters
    // 2 -- opinions

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(28, 27, 27))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(281.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(38, 35, 35))
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(153.dp, 163.dp)
                    .weight(1f),
                model = imageLink,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(13.dp))
            Text(itemName, color = Color.White, fontSize = 32.sp)
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(38, 35, 35))
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(55, 55, 55))
                    .padding(vertical = 4.dp, horizontal = 22.dp)
                    .clickable {
                        curState.intValue = 0

                    }
            ) {
                Text("Описание", color = Color.White, fontSize = 20.sp, maxLines = 1)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(55, 55, 55))
                    .padding(vertical = 4.dp, horizontal = 22.dp)
                    .clickable {
                        curState.intValue = 1

                    }
            ) {
                Text("Параметры", color = Color.White, fontSize = 20.sp, maxLines = 1)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(55, 55, 55))
                    .padding(vertical = 4.dp, horizontal = 22.dp)
                    .clickable {
                        curState.intValue = 2

                    }
            ) {
                Text("Отзывы", color = Color.White, fontSize = 20.sp, maxLines = 1)
            }
        }


        when (curState.intValue) {
            0 -> {
                DescriptionItem(descriptionText ?: "")
            }

            1 -> {
                ParametersItem(categoryNameList, answerTextList)
            }

            2 -> {
                // todo плашка с отзывами
            }

            else -> {
                throw UnknownError("GG, you down")
            }
        }
    }
}

/**
 * Один из трёх экранов: Экран с описанием
 *
 * stateId = 0
 */
@Composable
private fun DescriptionItem(descriptionText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(38, 35, 35))
            .padding(vertical = 28.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            descriptionText,
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}


/**
 * Один из трёх экранов: Экран с параметрами и составом
 *
 * stateId = 1
 *
 * **Существует ограничение на состав характеристик! Ориентироваться на макет! Не все строки из запроса должны оказаться тут!**
 *
 * @param categoryName список заголовков из json от api. Они идут на на параметры, поэтому требуется фильтр на вход. **Размерность массивов должна быть одинаковая!**
 * @param answerText список ответов из json от api. Они идут на на параметры, поэтому требуется фильтр на вход. **Размерность массивов должна быть одинаковая!**
 *
 */
@Composable
private fun ParametersItem(categoryName: List<String>, answerText: List<String>) {
    val rememberedScroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(38, 35, 35))
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp)
            .verticalScroll(rememberedScroll),

        ) {

        for (i in categoryName.indices) {
            ElementOfParameter(categoryName[i], answerText[i])
        }
    }
}

/**
 * Конкретная строка(row) с парой (Характеристика --- значение)
 *
 * **Существует ограничение на состав характеристик! Ориентироваться на макет! Не все строки из запроса должны оказаться тут!**
 *
 * @param categoryName характеристика из апи
 * @param answerText значение этой характеристики
 */
@Composable
private fun ElementOfParameter(categoryName: String, answerText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            categoryName,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = DarkTextSecondary,
        )

        Text(
            answerText,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = DarkTextPrimary,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DarkSeparator)
    )
}



