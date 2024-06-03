package com.community.codersaidhub.models

import java.io.Serializable

data class messageModel2(val messages:String, val date:Long,val email:String?):Serializable{
    constructor() : this("", 0L, null)
}
