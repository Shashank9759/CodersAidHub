package com.community.codersaidhub.models

import java.io.Serializable


class storyModel :Serializable{
    var imageLink:String?=null
    var currentTime:Long?=null
    var name:String?=null
    var profileImage:String?=null


    constructor()

    constructor(imageLink: String,currentTime: Long,name:String,profileImage:String){
       this.imageLink=imageLink
        this.currentTime=currentTime
        this.name=name
        this.profileImage=profileImage


    }




}