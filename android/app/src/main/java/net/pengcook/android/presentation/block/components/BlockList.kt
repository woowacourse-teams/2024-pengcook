package net.pengcook.android.presentation.block.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.presentation.block.model.BlockeeInfo

@Composable
fun BlockList(
    blockees: List<BlockeeInfo>,
    onButtonClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(blockees) { blockee ->
            BlockItem(
                blockeeInfo = blockee,
                modifier = Modifier.padding(horizontal = 16.dp),
                actionContent = {
                    BlockActionButton(
                        onButtonClick = onButtonClick,
                        blockeeInfo = blockee,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BlockListPreview() {
    val blockees = List(10) {
        BlockeeInfo(
            id = it.toLong(),
            email = "sample@email.com",
            image = "https://randomuser.me/api/portraits",
            nickname = "nickname",
            region = "Region",
            username = "Username",
        )
    }
    BlockList(
        blockees = blockees,
        onButtonClick = {},
    )
}
