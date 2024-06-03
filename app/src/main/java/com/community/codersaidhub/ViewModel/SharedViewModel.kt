package com.community.codersaidhub.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel (){
    val data= MutableLiveData<String>()
    val Postdata= MutableLiveData<String?>()
    val Reeldata= MutableLiveData<String?>()
    val follower= MutableLiveData<String?>()
    val following= MutableLiveData<String?>()
}