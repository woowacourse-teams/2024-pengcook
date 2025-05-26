package net.pengcook.android.presentation.follow.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.ui.theme.DarkBlue60
import net.pengcook.android.ui.theme.SandYellow10
import net.pengcook.android.ui.theme.White50

@Composable
fun FollowSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides
            TextSelectionColors(
                handleColor = SandYellow10,
                backgroundColor = SandYellow10,
            ),
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            shape = RoundedCornerShape(100),
            colors =
                TextFieldDefaults.colors(
                    cursorColor = DarkBlue60,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = White50,
                    unfocusedContainerColor = White50,
                    disabledContainerColor = White50,
                ),
            placeholder = {
                Text(
                    text = "Search",
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f),
                )
            },
            singleLine = true,
            keyboardActions =
                KeyboardActions(
                    onSearch = {
                        onImeSearch()
                    },
                ),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank(),
                ) {
                    IconButton(
                        onClick = {
                            onSearchQueryChange("")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            },
            modifier =
                modifier
                    .background(
                        shape = RoundedCornerShape(16.dp),
                        color = White50,
                    )
                    .minimumInteractiveComponentSize(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowSearchBarPreview() {
    FollowSearchBar(
        searchQuery = "",
        onSearchQueryChange = {},
        onImeSearch = {},
    )
}
