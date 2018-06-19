package com.example.poorwahirve.exoplayer

import android.app.Dialog
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.example.poorwahirve.exoplayer.R.drawable.ic_fullscreen_expand
import com.example.poorwahirve.exoplayer.R.drawable.ic_fullscreen_skrink
import com.example.poorwahirve.exoplayer.R.id.progressBar
import com.example.poorwahirve.exoplayer.R.id.simpleExoPlayerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import java.text.ParsePosition
import com.google.android.exoplayer2.ui.PlaybackControlView
import io.reactivex.schedulers.Timed
import java.lang.Math.random
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), Player.EventListener {


    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private var playbackPosition = 0L
    private val dashUrl = "http://rdmedia.bbc.co.uk/dash/ondemand/bbb/2/client_manifest-separate_init.mpd"
    private val hlsUrl = "http://esioslive6-i.akamaihd.net/hls/live/202892/AL_P_ESP1_FR_FRA/playlist.m3u8"
    private val mp4Url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val mp3Url = "http://www.noiseaddicts.com/samples_1w72b820/2228.mp3"
    private var duration = 0L
    private val timelineObservable : Observable<Timed<Long>> = Observable
            .interval(1500, TimeUnit.MILLISECONDS).timeInterval()
            .filter {
                simpleExoPlayer.currentPosition > duration / 2
            }
            .doOnEach {
                Log.e("time", simpleExoPlayer.currentPosition.toString())
            }
    private val urlArray : Array<String> = arrayOf(dashUrl, mp4Url, hlsUrl, mp3Url)

    private lateinit var fullScreenDialog : Dialog
    private var fullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFullScreenDialog()
        initFullScreenButton()
        initExoPlayer()

    }

    override fun onStop() {
        releaseExoPlayer()
        super.onStop()
    }

    private fun initFullScreenDialog() {
        fullScreenDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val dialogObservable : Observable<Dialog> = Observable
                .just(fullScreenDialog)
                .doOnNext {
                    it.onBackPressed().takeIf { fullScreen }
                }
        dialogObservable.subscribe()

    }

    private fun openFullScreenDialog() {

        (simpleExoPlayerView.parent as ViewGroup).removeView(simpleExoPlayerView)
        fullScreenDialog.addContentView(simpleExoPlayerView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        exo_fullscreen_icon.setImageDrawable(getDrawable(ic_fullscreen_skrink))
        fullScreen = true
        fullScreenDialog.show()

    }

    private fun closeFullScreenDialog() {

        (simpleExoPlayerView.parent as ViewGroup).removeView(simpleExoPlayerView)
        mainFrameLayout.addView(simpleExoPlayerView)
        fullScreen = false
        fullScreenDialog.dismiss()
        exo_fullscreen_icon.setImageDrawable(getDrawable(ic_fullscreen_expand))

    }

    private fun initFullScreenButton() {

        val fullScreenIconObservable : Observable<ImageView> = Observable
                .just(exo_fullscreen_icon)
                .doOnNext {
                    it.setOnClickListener {
                        if(fullScreen)
                            closeFullScreenDialog()
                        else
                            openFullScreenDialog()
                    }
                }

        fullScreenIconObservable.subscribe()

    }

    // prepare the exoplayer
    private fun prepareExoPlayer() {
        val uri = urlArray[(0..urlArray.size).random()]
        val extension = uri.substring(uri.lastIndexOf("."), uri.length)
        videoTypeTitle.text = "Media Extension: " + extension
        val uriParsed = Uri.parse(uri)
        val videoSource = MediaSourceObject(uriParsed, extension).mediaSource
        simpleExoPlayer.prepare(videoSource)
    }

    // init exoplayer
    private fun initExoPlayer() {

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                this,
                trackSelector
        )

        prepareExoPlayer()

        simpleExoPlayerView.player = simpleExoPlayer
        simpleExoPlayer.seekTo(playbackPosition)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(this)

        timelineObservable.subscribe()

    }

    // release exoplayer resources
    private fun releaseExoPlayer() {
        playbackPosition = simpleExoPlayer.currentPosition
        simpleExoPlayer.release()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        if (playbackState == Player.STATE_READY) { // video loaded, hide progress bar
            Log.e("Duration Set", (duration > 0).toString())
            duration = simpleExoPlayer.duration
            progressBar.visibility = View.INVISIBLE

        }
        else if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE

    }

    // exoplayer overrides
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        
    }

    override fun onPositionDiscontinuity() {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {

    }

    // etc helper functions
    fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) +  start


}