package com.udacity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadContent(
    val title: String,
    val status: DownloadState
) : Parcelable