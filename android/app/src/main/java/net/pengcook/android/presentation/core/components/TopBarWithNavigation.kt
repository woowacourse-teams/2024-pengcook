package net.pengcook.android.presentation.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.pengcook.android.ui.theme.Notosans
import net.pengcook.android.ui.theme.PengCookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithNavigation(
    name: String,
    navigation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = name,
                style = Notosans.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { navigation() },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier,
    )
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun BarLayoutPreview() {
    PengCookTheme {
        Column {
            TopBarWithNavigation(
                name = "내 댓글 목록",
                navigation = { },
            )

            TopBarWithNavigation(
                name = "PengCook",
                navigation = { },
            )
        }
    }
}
