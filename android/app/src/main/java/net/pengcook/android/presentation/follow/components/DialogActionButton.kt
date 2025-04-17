package net.pengcook.android.presentation.follow.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun RowScope.DialogActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        modifier = modifier
            .weight(1f)
            .fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = color,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White,
        ),
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = fontWeight,
            ),
        )
    }
}
