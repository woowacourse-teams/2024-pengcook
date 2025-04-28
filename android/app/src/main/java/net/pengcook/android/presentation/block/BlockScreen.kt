package net.pengcook.android.presentation.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.pengcook.android.presentation.block.components.BlockList
import net.pengcook.android.presentation.block.model.BlockeeInfo
import net.pengcook.android.presentation.core.components.TopBarWithNavigation
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun BlockScreenRoot(
    viewModel: BlockViewModel,
    navigateBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    BlockScreen(
        state = state.value,
        onAction = { action ->
            when (action) {
                BlockAction.NavigateBack -> navigateBack()
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
private fun BlockScreen(
    state: BlockState,
    onAction: (BlockAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        topBar = {
            TopBarWithNavigation(
                name = state.username,
                navigation = { onAction(BlockAction.NavigateBack) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
        ) {
            BlockList(
                blockees = state.blockees,
                onButtonClick = { blockeeId ->
                    onAction(BlockAction.UnblockUser(blockeeId))
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BlockScreenPreview() {
    val blockees = List<BlockeeInfo>(10) {
        BlockeeInfo(
            id = it.toLong(),
            email = "sample@email.com",
            image = "https://randomuser.me/api/portraits",
            nickname = "nickname",
            region = "Region",
            username = "Username",
        )
    }

    PengCookTheme {
        BlockScreen(
            state = BlockState(
                username = "Block List",
                blockees = blockees,
            ),
            onAction = {},
        )
    }
}
