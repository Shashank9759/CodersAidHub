package com.community.codersaidhub.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.community.codersaidhub.databinding.ReelsItemviewBinding
import com.community.codersaidhub.models.ExoPlayerItem
import com.community.codersaidhub.models.NewReelModel



class ReelViewPagerAdapter(
    private val context: Context,
    private val reelList: ArrayList<NewReelModel>,
    var videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<ReelViewPagerAdapter.ViewHolder>() {



    inner class ViewHolder(val binding: ReelsItemviewBinding, var context: Context, var videoPreparedListener: OnVideoPreparedListener) : RecyclerView.ViewHolder(binding.root){
        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource

        fun setVideoPath(url: String) {

            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        binding.progessVideoItem.visibility = View.VISIBLE
                    } else if (playbackState == Player.STATE_READY) {
                        binding.progessVideoItem.visibility = View.GONE
                    }
                }
            })

            binding.playerview.player = exoPlayer

            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)

            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)))

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReelsItemviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = reelList[position]

        Glide.with(context).load(currentItem.user?.image).into(holder.binding.profileImage)
        holder.binding.viewpagerReelCaption.text = currentItem.caption
        holder.binding.viewpagerReelUsername.text = currentItem.user?.name

        holder.setVideoPath(currentItem.video.toString())
    }

    override fun getItemCount(): Int = reelList.size




    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }

}

