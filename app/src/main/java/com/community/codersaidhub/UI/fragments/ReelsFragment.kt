package com.community.codersaidhub.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.community.codersaidhub.Adapters.ReelViewPagerAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.FragmentAddBinding
import com.community.codersaidhub.databinding.FragmentReelsBinding
import com.community.codersaidhub.models.ExoPlayerItem
import com.community.codersaidhub.models.NewReelModel
import com.community.codersaidhub.utils.POST_Reel_DETAILS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class ReelsFragment : Fragment() {
    lateinit var binding:FragmentReelsBinding
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var reelModelFromCombineAdapter:NewReelModel?= null

        reelModelFromCombineAdapter = arguments?.getSerializable("reelModel") as? NewReelModel

        binding= FragmentReelsBinding.inflate(layoutInflater,container,false)
        var reelList= arrayListOf<NewReelModel>()
        Firebase.firestore.collection(POST_Reel_DETAILS).get().addOnSuccessListener {
            var temp =  arrayListOf<NewReelModel>()
            for(i in it){
                var myreel= i.toObject<NewReelModel>()!!

                if(myreel.postUID==reelModelFromCombineAdapter?.postUID){
                   continue
                }
                temp.add(myreel)
            }
            reelList.addAll(temp)
            if(reelModelFromCombineAdapter!=null){
                reelList.add(reelModelFromCombineAdapter)
            }
            reelList.reverse()
            binding.reelsViewPager.adapter=ReelViewPagerAdapter(requireContext(),reelList, object : ReelViewPagerAdapter.OnVideoPreparedListener {
                override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                    exoPlayerItems.add(exoPlayerItem)
                }
            })


            binding.reelsViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                    if (previousIndex != -1) {
                        val player = exoPlayerItems[previousIndex].exoPlayer
                        player.pause()
                        player.playWhenReady = false
                    }
                    val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                    if (newIndex != -1) {
                        val player = exoPlayerItems[newIndex].exoPlayer
                        player.playWhenReady = true
                        player.play()
                    }
                }
            })

        }


        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.reelsViewPager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.reelsViewPager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            for (item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
    }
}