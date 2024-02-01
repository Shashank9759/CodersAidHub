package com.example.instaranjan.models

import java.io.Serializable

class CombineReelPostModel :Serializable{
    var postModel: NewPostModel= NewPostModel()
    var reelModel: NewReelModel=NewReelModel()
    var viewType: Int=0
    constructor()

    constructor(postModel: NewPostModel,reelModel: NewReelModel,viewType:Int){
        this.postModel=postModel
        this.reelModel=reelModel
        this.viewType=viewType
    }




}