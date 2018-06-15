package com.example.poorwahirve.exoplayer

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.poorwahirve.exoplayer.R.id.progressBar
import com.example.poorwahirve.exoplayer.R.id.simpleExoPlayerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParsePosition

class MainActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private var playbackPosition = 0L
    private val dashUrl = "http://rdmedia.bbc.co.uk/dash/ondemand/bbb/2/client_manifest-separate_init.mpd"
    private val mp4Url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
//    private val bandwidthMeter = DefaultBandwidthMeter()

    private val bandwidthMeter by lazy {
        DefaultBandwidthMeter()
    }
    private val adaptiveTrackSelectionFactory by lazy {
        AdaptiveTrackSelection.Factory(bandwidthMeter)
    }

    // helper method to create the media source
    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this,"ua", bandwidthMeter)
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        val extractorsFactory = DefaultExtractorsFactory()
        return ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null)
    }

    // prepare the exoplayer
    private fun prepareExoPlayer() {
        val uri = Uri.parse(mp4Url)
        val videoSource = buildMediaSource(uri)
        simpleExoPlayer.prepare(videoSource)
    }

    // init exoplayer
    private fun initializeExoPlayer() {

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                this,
                trackSelector,
                DefaultLoadControl()
        )

        prepareExoPlayer()

        simpleExoPlayerView.player = simpleExoPlayer
        simpleExoPlayer.seekTo(playbackPosition)
        simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.addListener(this)
    }

    // release exoplayer resources
    private fun releaseExoPlayer() {
        playbackPosition = simpleExoPlayer.currentPosition
        simpleExoPlayer.release()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        if (playbackState == Player.STATE_READY)  // video loaded, hide progress bar
            progressBar.visibility = View.INVISIBLE
        else if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Log.e("playbackPositionType", (playbackPosition is Long).toString())

        initializeExoPlayer()
        val playbackPositionObservable : Observable<Long> = Observable
                .just(playbackPosition)
                .doOnNext {
                    if (it > (simpleExoPlayer.duration / 2))
                    Log.e(it.toString(), "greater than 1/2 * ${simpleExoPlayer.duration}")
                }

        playbackPositionObservable.subscribe()

    }

//    override fun onStart() {
//        super.onStart()
//        initializeExoPlayer()
//
//    }

    override fun onStop() {
        releaseExoPlayer()
        super.onStop()
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        
    }

    override fun onPositionDiscontinuity() {
        
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        
    }
}
