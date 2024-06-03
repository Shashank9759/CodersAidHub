package com.community.codersaidhub.UI.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.community.codersaidhub.Adapters.messageAdapter2
import com.community.codersaidhub.R
import com.community.codersaidhub.databinding.ActivityMessage2Binding
import com.community.codersaidhub.models.NewPostModel
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.listOfMessageModel
import com.community.codersaidhub.models.messageModel2
import com.community.codersaidhub.models.messageModel2Type
import com.community.codersaidhub.utils.MESSAGES
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore


class MessageActivity2 : AppCompatActivity() {
    private val binding by lazy{
        ActivityMessage2Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//setting adapters

        val listadapter= arrayListOf<messageModel2Type>()
//        listadapter.add(messageModel2Type("dnk,jf",2323132,0))
//        listadapter.add(messageModel2Type("hbdtb",2323132,1))
//        listadapter.add(messageModel2Type("dnk,jf",2323132,0))
//        listadapter.add(messageModel2Type("dnk,jf",2323132,1))
//        listadapter.add(messageModel2Type("dnk,jf",2323132,0))
        val adapter=messageAdapter2(this, listadapter)
        binding.messageactivity2RV.adapter=adapter

        val anotherUser=intent.getSerializableExtra("anotherUser") as? User
        val currentUser=intent.getSerializableExtra("currentUser") as? User

        val anotherUserEmail=anotherUser?.email
        val currentUserEmail=currentUser?.email


        // Get an instance of Firestore
        val db = FirebaseFirestore.getInstance()

// Reference to the collection where you want to store the list
        val messagesCollection = db.collection(MESSAGES)


        val documentId = "$currentUserEmail-$anotherUserEmail"
        val documentId2 = "$anotherUserEmail-$currentUserEmail"

        binding.messageactivity2send.setOnClickListener {
            val text=binding.messageactivity2edittext.text.toString()
            binding.messageactivity2edittext.setText("")
           val currentdate=  System.currentTimeMillis()



// Retrieve the existing list from Firestore
            messagesCollection.document(documentId).get()
                .addOnSuccessListener { documentSnapshot ->
          if(documentSnapshot.exists()){
              val existingMessages: List<Map<String, Any>> = documentSnapshot["messages"] as? List<Map<String, Any>> ?: emptyList()
              //   val existingMessagesList = existingMessages as? MutableList<Map<String, Any>> ?: mutableListOf()

              // Create a new message model
              val newMessage = messageModel2(text, currentdate, currentUserEmail)

              // Convert the new message to a map
              val newMessageMap: Map<String, Any> = mapOf(
                  "messages" to newMessage.messages as Any,
                  "date" to newMessage.date as Any,
                  "email" to newMessage.email as Any
              )
              val existingMessagesList =  existingMessages.mapNotNull { it as? Map<String, Any> }
              val updatedMessagesList = existingMessagesList.toMutableList().apply {
                  add(newMessageMap)
              }

              // Update the Firestore document with the updated list
              messagesCollection.document(documentId).set(mapOf("messages" to updatedMessagesList))
                  .addOnSuccessListener {
                      messagesCollection.document(documentId2).set(mapOf("messages" to updatedMessagesList))
                  }
                  .addOnFailureListener { e ->
                      // Handle failure
                      println("Error adding message: $e")
                  }
          }

                    else{

              iffirebaseEmpty(text,currentdate,currentUserEmail.toString(),anotherUserEmail.toString(),messagesCollection)

          }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    println("Error getting document: $e")
                }






//            val text=binding.messageactivity2edittext.text.toString()
//            binding.messageactivity2edittext.setText("")
//            val currentdate=  System.currentTimeMillis()
//            val listofmessages= mutableListOf<messageModel2>()
//            listofmessages.add(messageModel2(text,currentdate,currentUserEmail))
//            val listofmessage= listOfMessageModel(listofmessages)
//
//
//           Firebase.firestore.collection(MESSAGES).document(currentUserEmail+" "+anotherUserEmail) .get().addOnSuccessListener {
//               val result=it.toObject(listOfMessageModel::class.java)
//               if(result!=null){
//                   result.list.add(messageModel2(text,currentdate,currentUserEmail))
//                   Firebase.firestore.collection(MESSAGES).document(currentUserEmail+" "+anotherUserEmail).set(result)
//                   Firebase.firestore.collection(MESSAGES).document(anotherUserEmail+" "+currentUserEmail).set(result)
//                       }
//               else{
//                   Log.d("fkfj","null")
//                   Firebase.firestore.collection(MESSAGES).document(currentUserEmail+" "+anotherUserEmail).set(listOfMessageModel(
//                       mutableListOf(messageModel2(text,currentdate,currentUserEmail))
//                   ))
//
//                   Firebase.firestore.collection(MESSAGES).document(anotherUserEmail+" "+currentUserEmail).set(listOfMessageModel(
//                       mutableListOf(messageModel2(text,currentdate,currentUserEmail))
//                   ))
//                   }
//
//               }
//
//            Firebase.firestore.collection(MESSAGES).document(currentUserEmail+" "+anotherUserEmail).addSnapshotListener{
//                    documentSnapshot, error ->
//                if (error != null) {
//                    // Handle the error
//                    return@addSnapshotListener
//                }
//
//                val listofmessage = documentSnapshot?.toObject<listOfMessageModel>(listOfMessageModel::class.java)
//
//
//
//                if(listofmessage!=null){
//
//                    val messagelisttype= arrayListOf<messageModel2Type>()
//                    for(i in listofmessage?.list!!){
//                        var type =0
//                        if(i.email.equals(currentUserEmail)){
//                            type=0
//                        }else{
//                            type=1
//                        }
//                        messagelisttype.add(messageModel2Type(i.messages,i.date,type))
//                    }
//
//                    adapter.ondatechanged(messagelisttype)
//                }
//
//           }


        }

        listenForUpdates(messagesCollection,documentId,currentUserEmail.toString()){
            list->
            adapter.ondatechanged(list)

        }

    }

fun iffirebaseEmpty(text:String,date:Long,currentUserEmail:String,anotherUserEmail:String,messagesCollection:CollectionReference){
    // Example list of MessageModel2 objects
    val listOfMessages = mutableListOf<messageModel2>()
    listOfMessages.add(messageModel2(text,date,currentUserEmail))

// Convert each MessageModel2 object into a Map
    val messageMaps = listOfMessages.map { message ->
        mapOf(
            "messages" to message.messages,
            "date" to message.date,
            "email" to message.email
        )
    }

// Set the list of messages under the document with specific ID

    val documentId = "$currentUserEmail-$anotherUserEmail"
    val documentId2 = "$anotherUserEmail-$currentUserEmail"

    messagesCollection.document(documentId).set(mapOf("messages" to messageMaps))
        .addOnSuccessListener {
            messagesCollection.document(documentId2).set(mapOf("messages" to messageMaps))
        }
        .addOnFailureListener { e ->
            // Handle failure
            println("Error adding messages: $e")
        }
}

    fun listenForUpdates(messagesCollection :CollectionReference,documentId:String,currentUserEmail:String,Callback:(ArrayList<messageModel2Type>)->Unit) {
        var listener: ListenerRegistration? = null
        listener = messagesCollection.document(documentId).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle any errors
                println("Listen failed: $exception")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val existingMessages = snapshot["messages"] as? List<Map<String, Any>> ?: emptyList()
                val messageModelList = existingMessages.map { messageMap ->
                    messageModel2(
                        messageMap["messages"] as String,
                        messageMap["date"] as Long,
                        messageMap["email"] as String?
                    )
                }
              val messagemodellistType= messageModelList.map {
                  var type:Int=0
                  if(it.email.equals(currentUserEmail)){
                      type=0
                  }else{
                      type=1
                  }
                  messageModel2Type(it.messages,it.date,type)
              }
                Callback(messagemodellistType as ArrayList<messageModel2Type>)


            } else {
                println("Current data: null")
            }
        }
    }
}