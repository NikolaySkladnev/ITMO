package com.example.beerin.letooow

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beerin.CustomButton
import com.example.beerin.ui.theme.BackgroundColor
import com.example.beerin.ui.theme.ButtonColor
import com.example.beerin.ui.theme.CalendarBoxColor
import com.example.beerin.ui.theme.ItemBoxColor
import com.example.beerin.ui.theme.NotCurrentCalendarDaysColor
import com.example.beerin.ui.theme.WeekendDaysColor
import com.example.beerin.ui.theme.YellowItemColor
import java.time.LocalDate


@Preview(showSystemUi = true)

@Composable
fun CalendarScreen() {

    var currentDate by remember {
        mutableStateOf(LocalDate.now())
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        CustomButton(modifier = Modifier.padding(8.dp))

        Box(
            modifier = Modifier
                .absolutePadding(left = 30.dp, right = 30.dp, top = 8.dp, bottom = 200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ItemBoxColor)
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Новое Свершение?",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(CalendarBoxColor)
                        .fillMaxSize()
                ) {

                    Row {
                        Column(
                            modifier = Modifier
                                .absolutePadding(19.dp, 5.dp, 14.dp, 5.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = englishToRussianMonth(currentDate.month.name),
                                color = Color.White,
                                fontSize = 25.sp
                            )

                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = currentDate.year.toString(),
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    currentDate = currentDate.minusMonths(1)
                                    Log.d("Date", "$currentDate")
                                },
                                Modifier.align(Alignment.Top)
                            ) {
                                Text(text = "<", color = Color.White, fontSize = 25.sp)
                            }


                            IconButton(
                                onClick = {
                                    currentDate = currentDate.plusMonths(1)
                                    Log.d("Date", "$currentDate")
                                },
                                Modifier.align(Alignment.Top)
                            ) {
                                Text(text = ">", color = Color.White, fontSize = 25.sp)
                            }

                        }


                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                            Text(text = day, color = NotCurrentCalendarDaysColor, fontSize = 16.sp)
                        }
                    }

                    var selectedElement by remember {
                        mutableStateOf(listOf(-1))
                    }
                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(10.dp),
                        columns = GridCells.Fixed(7),
                        content = {
                            items(42) { index ->
                                val date = indexOfDayToCalendarDay(index, currentDate)
                                val day = date.first
                                val currentIndex =
                                    131 * index + 239 * currentDate.month.value + 1109 * currentDate.year
                                val colorOfDay = date.second


                                DayElement(
                                    day = day,
                                    colorOfDay = colorOfDay,
                                    selected = currentIndex in selectedElement,
                                    clickAction = {
                                        selectedElement = if (currentIndex in selectedElement) {
                                            selectedElement - currentIndex
                                        } else {
                                            selectedElement + currentIndex
                                        }
                                    }
                                )

                            }
                        }
                    )

                }


            }
        }

    }
}


@Composable
fun DayElement(
    day: Int,
    colorOfDay: Color,
    selected: Boolean,
    clickAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .aspectRatio(1f)
            .background(if (!selected) ButtonColor else YellowItemColor, RoundedCornerShape(4.dp))
            .clickable { clickAction() },
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = day.toString(),
            color = colorOfDay,
            fontSize = 14.sp
        )
    }
}


fun indexOfDayToCalendarDay(
    index: Int,
    currentDate: LocalDate
): Pair<Int, Color> {
    val lengthOfMonth = currentDate.lengthOfMonth() // количество дней в месяце

    val lastDayOfLastMonth =
        currentDate.minusDays(currentDate.dayOfMonth.toLong()).dayOfMonth // последний день предыдущего месяца

    val firstDayOfWeek =
        currentDate.minusDays(currentDate.dayOfMonth.toLong()).dayOfWeek.value // первый день недели в месяце

    if (index < firstDayOfWeek) {
        return Pair(lastDayOfLastMonth - (firstDayOfWeek - index - 1), Color(131, 128, 128))
    }

    val day = (index - firstDayOfWeek + 1)

    if (day == LocalDate.now().dayOfMonth && currentDate == LocalDate.now()) return Pair(
        day,
        Color.Yellow
    )

    return if (day > lengthOfMonth) {
        Pair(day - lengthOfMonth, NotCurrentCalendarDaysColor)

    } else {
        if (LocalDate.of(
                currentDate.year,
                currentDate.month.value,
                day
            ).dayOfWeek.value in listOf(6, 7)
        ) {
            Pair(day, WeekendDaysColor)

        } else {
            Pair(day, Color.White)
        }

    }
}

fun englishToRussianMonth(month: String): String {
    return when (month) {
        "DECEMBER" -> "Декабрь"
        "JANUARY" -> "Январь"
        "FEBRUARY" -> "Февраль"
        "MARCH" -> "Март"
        "APRIL" -> "Апрель"
        "MAY" -> "Май"
        "JUNE" -> "Июнь"
        "JULY" -> "Июль"
        "AUGUST" -> "Август"
        "SEPTEMBER" -> "Сентябрь"
        "OCTOBER" -> "Октябрь"
        "NOVEMBER" -> "Ноябрь"
        else -> ""
    }
}