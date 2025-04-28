package net.pengcook.android.presentation.block.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.R
import net.pengcook.android.presentation.block.model.BlockeeInfo
import net.pengcook.android.presentation.follow2.model.FollowInfo

@Composable
fun BlockActionButton(
    onButtonClick: (Long) -> Unit,
    blockeeInfo: BlockeeInfo,
) {
    Button(
        onClick = { onButtonClick(blockeeInfo.id) },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F7)),
    ) {
        Text(
            text = stringResource(R.string.block_unblock),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
