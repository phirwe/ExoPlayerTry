package com.example.poorwahirve.exoplayer

import android.net.Uri
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifest
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import java.net.URI

class MediaSourceObject(uri : Uri, ext : String) {
    private val userAgent = "ua"
    var mediaSource : MediaSource
    private val bandwidthMeter by lazy {
        DefaultBandwidthMeter()
    }

    private val dataSourceFactory = DefaultHttpDataSourceFactory(userAgent, bandwidthMeter)

    init {
        mediaSource = if (ext.equals(".mpd")) DashMediaSource(uri, dataSourceFactory, DefaultDashChunkSource.Factory(dataSourceFactory), null, null)
        else if (ext.equals(".m3u8")) HlsMediaSource(uri, dataSourceFactory, null, null)
        else ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), null, null)
    }

}