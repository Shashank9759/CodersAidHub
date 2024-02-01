package com.example.instaranjan.models

data class messageModel(val message:String,val type :Int){



    fun toMap(): Map<String, Any> {
        return mapOf(
            "message" to message,
            "type" to type
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): messageModel {
            val message = data["message"].toString()
            val type = (data["type"] as? Long)?.toInt() ?: 0
            return messageModel(message, type)
        }
    }
}
