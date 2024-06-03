package com.community.codersaidhub.models

import java.io.Serializable

data class listOfMessageModel(val list:MutableList<messageModel2>):Serializable{
    constructor() : this(mutableListOf())
}
