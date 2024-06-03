package com.community.codersaidhub.models

import java.io.Serializable

class projectModel :Serializable{

    var projectName:String?=null
    var technologyName:String?=null
    var descriptionName:String?=null
    var remoteLink:String?=null
    var listOfPhoto:List<String>?=null
    var time:Long?=null
    var postUID:String?=null
    var like:ArrayList<String> = arrayListOf<String>()
    var user:User?=null
    var comment:ArrayList<UserComment> = arrayListOf<UserComment>()

    constructor()
    constructor(projectName:String,technologyName:String,descriptionName:String,remoteLink:String,
                listOfPhoto:List<String>,time:Long,postUID:String,like:ArrayList<String>
    ,user:User,comment:ArrayList<UserComment>){
        this.projectName=projectName
        this.technologyName=technologyName
        this.descriptionName=descriptionName
        this.remoteLink=remoteLink
        this.listOfPhoto=listOfPhoto
        this.time=time
        this.postUID=postUID
        this.like=like
        this.user=user
        this.comment=comment
    }
}