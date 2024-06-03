package com.community.codersaidhub.UI.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.community.codersaidhub.Adapters.mymessageInboxAdapter
import com.community.codersaidhub.R
import com.community.codersaidhub.UI.fragments.search_ResultProfile_Fragment
import com.community.codersaidhub.databinding.ActivityMessageBinding
import com.community.codersaidhub.models.User
import com.community.codersaidhub.models.messageModel2
import com.community.codersaidhub.utils.MESSAGES
import com.community.codersaidhub.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.logging.Handler

class MessageActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMessageBinding.inflate(layoutInflater)
    }
lateinit var adapter:mymessageInboxAdapter

    private val handler = android.os.Handler(Looper.getMainLooper())


    private val runnable = Runnable {

        val input = capturedText?.toString() ?: ""

        searchUserAndSetRV(input)

    }
    var Activitylistofuser: MutableList<User> = mutableListOf<User>()

    private fun searchUserAndSetRV(input: String) {
           val listofsearcherUser=Activitylistofuser.filter ({

               val name=it.name?.toLowerCase()
               val smallcaseinput=input.toLowerCase()
               name!!.contains(smallcaseinput)
           })

        adapter.ondatachanged(listofsearcherUser)
    }

    // Variable to capture the CharSequence
    private var capturedText: CharSequence? = null


    val firebaseCurrentUser= Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
    var FirebaseUser= Firebase.firestore.collection(USER_NODE)
    lateinit var currentUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //setting toolbar
        setSupportActionBar(binding.messangerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.messangerToolbar.setNavigationOnClickListener {
            finish()

        }

        //setting primarydarkcolor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

//       val list= mutableListOf<User>()
//        list.add(User("https://images-na.ssl-images-amazon.com/images/G/01/gc/designs/livepreview/amazon_dkblue_noto_email_v2016_us-main._CB468775337_.png"
//        ,"hdkhcdkj","hdkdk,chj","kxsjbxks"
//        ))
//        val adapter= mymessageInboxAdapter(this@MessageActivity, list "lnkl.")
//        binding.inboxMessageRV.adapter


        CoroutineScope(Dispatchers.IO).launch {


            firebaseCurrentUser.get().addOnSuccessListener {
             val listofanotherUserEmail= mutableListOf<String>()
                currentUser = it.toObject<User>()!!

                 adapter= mymessageInboxAdapter(this@MessageActivity, listOf<User>() ,currentUser)
                binding.inboxMessageRV.adapter=adapter

                binding.messangerToolbar.title= currentUser.name
                val db = FirebaseFirestore.getInstance()

// Define the collection reference
                val messagesCollection = db.collection(MESSAGES)

// Fetch all documents in the collection
                messagesCollection.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                           if(  document.id.startsWith(currentUser.email.toString())){
                               val email= document.id.substringAfter("-")
                               listofanotherUserEmail.add(email)
                            }

                        }

//                        Log.d("dkihuekf","email"+ listofanotherUserEmail.get(0))
                        lifecycleScope.launch(Dispatchers.Main) {
                            try {
                                val listofuser= mutableListOf<User>()
                                val result = FirebaseUser.get().await()
                                val defaultUser = User()
                                for (user in result.documents) {
                                    val User = user.toObject<User>(User::class.java) ?: defaultUser
                                    val name = User.name.toString()
                                    val email = User.email.toString()


                                 listofanotherUserEmail.map {
                                    if(it.equals(email)) {
                                        listofuser.add(User)
                                    }
                                 }

                                }
                                if(listofuser.isEmpty()){
                                    binding.noMessagesText.visibility=View.VISIBLE
                                }else{
                                    binding.noMessagesText.visibility=View.GONE
                                }
                             //  Log.d("dkihuekf", listofuser.get(0).email.toString())
                                adapter.ondatachanged(listofuser)
                                Activitylistofuser=listofuser
                               searchUser()


                            } catch (e: Exception) {
                                Toast.makeText(this@MessageActivity, "Error : $e", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        println("Error getting documents: $exception")
                    }
            }
        }
    }

    private fun searchUser() {



// Variable to capture the CharSequence

        binding.searchUser.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                try {
                    // Remove pending callbacks to avoid multiple executions
                    handler.removeCallbacks(runnable)
                } catch (e: Exception) {
                    // Handle exception
                }

                // Capture the CharSequence for later use
                capturedText = s

                // Post a delayed execution of the runnable after 3 seconds
                handler.postDelayed(runnable, 1000)




            }

            override fun afterTextChanged(s: Editable?) {



            }

        })
    }
}