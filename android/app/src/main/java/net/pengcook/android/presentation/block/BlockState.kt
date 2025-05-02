package net.pengcook.android.presentation.block

import net.pengcook.android.presentation.block.model.BlockeeInfo

data class BlockState(
    val username: String = "",
    val blockees: List<BlockeeInfo> = emptyList(),
    val isLoading: Boolean = true,
)
