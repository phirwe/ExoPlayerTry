package com.example.poorwahirve.exoplayer

enum class MediaType(val exts: Array<String>) {
    MUSIC(arrayOf(".mp3")),
    VIDEO(arrayOf(".mp4", ".m3u8", ".flv", ".m4v", ".avi", ".mov", ".mpd")),
    IMAGE(arrayOf(".jpg", ".png", ".gif", ".tiff", ".jpeg"))
}