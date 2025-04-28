package net.pengcook.android.presentation.block

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.domain.model.usercontrol.Blockee
import net.pengcook.android.presentation.block.model.BlockeeInfo
import javax.inject.Inject

@HiltViewModel
class BlockViewModel
    @Inject
    constructor(
        private val userControlRepository: UserControlRepository,
        private val profileRepository: ProfileRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(BlockState())
        val state: StateFlow<BlockState> = _state
            .onStart {
                fetchBlockees()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                _state.value,
            )

        fun onAction(action: BlockAction) {
            when (action) {
                is BlockAction.UnblockUser -> {
                    val blockeeId = action.blockeeId
                    val blockee = state.value.blockees.find { it.id == blockeeId }
                    if (blockee != null) {
                        val updatedBlockees = state.value.blockees - blockee
                        _state.value = state.value.copy(blockees = updatedBlockees)

                        viewModelScope.launch {
                            unblockUser(blockeeId)
                        }
                    }
                }

                BlockAction.NavigateBack -> {}
            }
        }

        private suspend fun unblockUser(blockeeId: Long) {
            userControlRepository.unblockUser(blockeeId)
        }

        private fun fetchBlockees() {
            _state.value = state.value.copy(isLoading = true)
            viewModelScope.launch {
                try {
                    val username = fetchUsername()
                    val blockees = userControlRepository.fetchBlockees().getOrThrow().map { it.blockee.toBlockeeInfo() }
                    _state.update {
                        it.copy(
                            username = username,
                            blockees = blockees,
                            isLoading = false,
                        )
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }

        private fun Blockee.toBlockeeInfo(): BlockeeInfo {
            return BlockeeInfo(
                id = id,
                email = email,
                username = username,
                nickname = nickname,
                image = image,
                region = region,
            )
        }

        private suspend fun fetchUsername(): String {
            return profileRepository.fetchMyUserInformation().getOrThrow().username
        }
    }
