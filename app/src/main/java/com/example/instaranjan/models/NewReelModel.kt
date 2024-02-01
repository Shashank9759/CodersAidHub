package com.example.instaranjan.models

import java.io.Serializable

class NewReelModel :Serializable{
    var video: String?=null
    var caption: String?=null

    var postUID:String?=null
    var like:ArrayList<String> = arrayListOf<String>()
    var user:User?=null
    var comment:ArrayList<UserComment> = arrayListOf<UserComment>()
    var date:Long?=null
    constructor()
    constructor(video: String?, caption: String?) {
        this.video = video
        this.caption = caption


    }
    constructor(video: String?, caption: String,postUID:String?,like:ArrayList<String>
    , user:User?,comment:ArrayList<UserComment>,date:Long?) {
        this.video = video
        this.caption = caption
        this.like = like

        this.postUID=postUID
        this.user=user
        this.postUID=postUID
        this.comment=comment
        this.date=date

    }


}