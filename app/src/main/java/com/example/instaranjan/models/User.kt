package com.example.instaranjan.models

import java.io.Serializable

class User :Serializable{

    var image: String?=null
    var name: String?=null
    var email: String?=null
    var password: String?=null

    var bio:String=""



    constructor()
    constructor(image: String?, name: String?, email: String?, password: String?,bio:String ) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
        this.bio = bio

    }

    constructor( name: String?, email: String?, password: String?) {
        this.name = name
        this.email = email
        this.password = password
    }

}