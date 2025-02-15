package com.example.beerin.wasshabbba

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beerin.CustomButton
import com.example.beerin.wasshabbba.response.RegistrationViewModel

@Composable
fun RegistrationOrLoginScreen(viewModel: RegistrationViewModel) {
    var isRegistrationScreen by remember { mutableStateOf(true) }

    if (isRegistrationScreen) {
        RegistrationProfileScreen(onSwitchToLogin = { isRegistrationScreen = false }, viewModel)
    } else {
        LoginScreen(onSwitchToRegistration = { isRegistrationScreen = true })
    }
}

@Composable
fun RegistrationProfileScreen(onSwitchToLogin: () -> Unit, viewModel: RegistrationViewModel) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val nickname = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(38, 37, 37))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
//        Header(title = "РЕГИСТРАЦИЯ")
        CustomButton()

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Фото",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Добавить фото",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { /* TODO: Добавить действие для фото */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithPlaceholder(
            value = nickname.value,
            onValueChange = { nickname.value = it },
            placeholder = "Введите Имя"
        )
        TextFieldWithPlaceholder(
            value = nickname.value,
            onValueChange = { nickname.value = it },
            placeholder = "Введите Фамилию"
        )
        TextFieldWithPlaceholder(
            value = nickname.value,
            onValueChange = { nickname.value = it },
            placeholder = "Введите Ник"
        )
        TextFieldWithPlaceholder(
            value = phone.value,
            onValueChange = { phone.value = it },
            placeholder = "Введите номер телефона"
        )
        TextFieldWithPlaceholder(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = "Введите почту"
        )
        TextFieldWithPlaceholder(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = "Введите пароль",
            isPassword = true
        )
        TextFieldWithPlaceholder(
            value = repeatPassword.value,
            onValueChange = { repeatPassword.value = it },
            placeholder = "Повторите пароль",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = true,
                onCheckedChange = { /* TODO: Реализовать обработку */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Red,
                    uncheckedColor = Color.White
                )
            )
            Text(text = "сохранить данные для входа", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = if (password.value != repeatPassword.value) {
                    "Пароли не совпадают!"
                } else ({
                    viewModel.registerUser(
                        "$nickname",
                        "$password",
                        "$phone",
                        "${nickname}1234",
                        "${nickname}12412412"
                    )
                }).toString()
            },
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
            modifier = Modifier.clickable { onSwitchToLogin() },
            textAlign = TextAlign.Center,
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }
}

@Composable
fun LoginScreen(onSwitchToRegistration: () -> Unit) {
    val emailOrPhone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(38, 37, 37))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
//        Header(title = "ВХОД")
        CustomButton()

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithPlaceholder(
            value = emailOrPhone.value,
            onValueChange = { emailOrPhone.value = it },
            placeholder = "Телефон или почта"
        )
        TextFieldWithPlaceholder(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = "Введите пароль",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = true,
                onCheckedChange = { /* TODO: Реализовать обработку */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Red,
                    uncheckedColor = Color.White
                )
            )
            Text(text = "сохранить данные для входа", color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Обработать вход */ },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(
                text = "Войти",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Регистрация",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { onSwitchToRegistration() },
            textAlign = TextAlign.Center,
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }
}

@Composable
fun TextFieldWithPlaceholder(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.5f)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            color = Color.White
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.Gray
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}
