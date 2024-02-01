package com.example.instaranjan.models

import java.io.Serializable

class NewPostModel:Serializable {
    var image: String?=null
    var caption: String?=null
    var like:ArrayList<String> = arrayListOf<String>()
    var user:User?=null
    var comment:ArrayList<UserComment> = arrayListOf<UserComment>()
    var date:Long?=null
    var postUID:String?=null


    constructor()
    constructor(image: String?, caption: String?,user: User) {
        this.image = image
        this.caption = caption
        this.user=user

    }

    constructor(image: String?, caption: String?,user: User?,date:Long?,like:ArrayList<String>,postUID:String?,comment:ArrayList<UserComment>) {
        this.image = image
        this.caption = caption
        this.user=user
        this.like=like
        this.comment=comment
        this.date=date
        this.postUID=postUID

    }


}