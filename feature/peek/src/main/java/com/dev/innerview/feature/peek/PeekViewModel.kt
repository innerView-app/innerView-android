package com.dev.innerview.feature.peek

import androidx.lifecycle.ViewModel
import com.dev.innerview.feature.peek.model.MediaUiState
import com.dev.innerview.feature.peek.model.PeekUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PeekViewModel @Inject constructor() : ViewModel() {

    private val _peekUiState = MutableStateFlow(PeekUiState())
    val peekUiState = _peekUiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val testMediaItems = listOf(
        MediaUiState(
            title = "요리하기",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/661f570aab9d840019942b80-473e0b/video_h1.m3u8",
            userId = "user_1",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfMjU2/MDAxNjUzMDA0NjQzNzQw.YLo0qAgenkqR5O1M4ASLlyq-0FI82aTaKYb62k4NMVgg.MuCgixpiPX_IUTLKdhXuOXKT7GkdVLTxchmHqn-kdl8g.JPEG.jsgmh7695/IMG_4834.JPG?type=w966",
        ),
        MediaUiState(
            title = "충전기",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/662aae7a42cd740019b91dec-3e114f/video_h1.m3u8",
            userId = "user_2",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfMTYx/MDAxNjUzMDA0NjQxMDc3.fECEZdeaUutTdrrOoO7DdMTXTyNi6HzlO4UyExX6-ccg.SAJl6XRdJW06sXur5qD-3NOwZzfTvPxyEB8q-eSmEp4g.JPEG.jsgmh7695/IMG_4832.JPG?type=w966",
        ),
        MediaUiState(
            title = "피치망고",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/663e5a1542cd740019b97dfa-ccf0e6/video_h1.m3u8",
            userId = "user_4",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfOTEg/MDAxNjUzMDA0NjQyODIx.SXmfuEGq22Sw9CVTHxKJotII5EW1oghCGYCT8v1PLGAg.l5QHxbsXZGYNWtUXYnhtfpfAOxROxEsfbxIymw6F9Xwg.JPEG.jsgmh7695/IMG_4833.JPG?type=w966",
        ),
        MediaUiState(
            title = "마이크",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/663d1244f22a010019f3ec12-f3c958/video_h1.m3u8",
            userId = "user_5",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfNDkg/MDAxNjUzMDA0NjQ2NzI1.3iNg5UVvx2wnnEznDeHy2JfYLn6ckthMtfC9v2ji48Yg.eotDMYI8tEIzHjqCpUpuXwCDsJFX7o0D_9ab2Zbph24g.JPEG.jsgmh7695/IMG_4841.JPG?type=w966",
        ),
        MediaUiState(
            title = "오디오 3 2 1",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/664ce52bd6fcda001911a88c-8f1c4d/video_h1.m3u8",
            userId = "user_6",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfMjM4/MDAxNjUzMDA0NjU3NDkw.ULiA6O_Pi_n4jSrVWiH4IQkLCjB8a57fmM2OsXIgY3sg.Y1Bv6lgRDIR5XZIpM_xGPBPxIaMLsT-r93OPNf7eVTwg.JPEG.jsgmh7695/IMG_4844.JPG?type=w966",
        ),
        MediaUiState(
            title = "hp sprocket studio",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/664d87dfe8e47500199ee49e-dbd56b/video_h1.m3u8",
            userId = "user_7",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfMTM5/MDAxNjUzMDA0NjQyMjM5.CoQgjJcq1NvLBCjRKTC_tplSeldJapPRYIr8201safgg.dn_OjkamEPg3iFulBpF87IChCTbks6vmU4t-6SRFMvwg.JPEG.jsgmh7695/IMG_4845.JPG?type=w966",
        ),
        MediaUiState(
            title = "Ultra Light",
            mediaUri = "https://flipfit-cdn.akamaized.net/flip_hls/6656423247ffe600199e8363-15125d/video_h1.m3u8",
            userId = "user_8",
            userProfileUri = "https://postfiles.pstatic.net/MjAyMjA1MjBfMTEx/MDAxNjUzMDA0NjQxMTMw.QO40q3C4ry13ms-x59RV_qeiRqvA112dO0h3YG_2d-cg.RIPNqClzNAKGJG8V77eLjSsm3jRvnGf7ZH-CEBGGYT8g.JPEG.jsgmh7695/IMG_4850.JPG?type=w966",
        )
    )

    init {
        fetchNextPeek()
    }

    fun fetchNextPeek() {
        val newMedia = testMediaItems.random()
        _peekUiState.update {
            it.copy(
                media = it.media.toPersistentList().add(newMedia)
            )
        }
    }
}