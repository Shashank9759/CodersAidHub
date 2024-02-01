package com.example.instaranjan.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instaranjan.Adapters.ReelViewPagerAdapter
import com.example.instaranjan.R
import com.example.instaranjan.databinding.FragmentAddBinding
import com.example.instaranjan.databinding.FragmentReelsBinding
import com.example.instaranjan.models.NewReelModel
import com.example.instaranjan.utils.POST_Reel_DETAILS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

lateinit var binding:FragmentReelsBinding

class ReelsFragment : Fragment() {

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
            binding.reelsViewPager.adapter=ReelViewPagerAdapter(requireContext(),reelList)

        }


        return binding.root
    }


}