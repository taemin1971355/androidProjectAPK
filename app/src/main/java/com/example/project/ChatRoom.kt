package com.example.project
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase


class ChatRoom: AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val otheruser = intent.getStringExtra("otheruser")
        val userEmail = intent.getStringExtra("userEmail")

        val messagesListView: ListView = findViewById(R.id.chatListView)
        if (userEmail != null) {
            // 사용자의 이메일을 기반으로 메시지를 Firestore에서 가져오기

            fetchMessages(userEmail, otheruser.toString() ,messagesListView)

            // Firestore에서 실시간 업데이트를 감지하는 리스너 등록
            registerRealtimeUpdates(userEmail,otheruser.toString(),messagesListView)
        }
    }

    private fun createRoom(userEmail: String, otheruser: String ,roomname : String){
        val chatRef = db.collection("messages").document(roomname).collection(roomname)

        chatRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val chatlist = mutableListOf<String>()
                for (document in result) {
                    val sender = document.getString("sender")
                    val message = document.getString("message")

                    if (sender != null && message != null) {
                        val formattedMessage = "보낸 사람: $sender \n메시지 : $message"
                        Toast.makeText(this,"100 ${formattedMessage}", Toast.LENGTH_SHORT).show()
                        chatlist.add(formattedMessage)
                    }
                }
                val messagesListView: ListView = findViewById(R.id.chatListView)
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_2, chatlist)
                messagesListView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // 메시지 가져오기 실패 시 처리
                // 예: Toast 메시지 출력 등
                Log.e("Firebase", "데이터 가져오기 실패", exception)
                exception?.printStackTrace() //다중 색인 추가를 위한 코드

            }

    }

    private fun fetchMessages(userEmail: String,otheruser: String ,messagesListView: ListView) {
        val path = if(userEmail.compareTo(otheruser)> 0) "${userEmail}_${otheruser}" else "${otheruser}_${userEmail}"
        val messagesRef = db.collection("messages").document(path).collection(path)

        messagesRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val chatlist = mutableListOf<String>()
                for (document in result) {
                        val sender = document.getString("sender")
                        val receiver = document.getString("receiver")
                        val message = document.getString("message")

                        if (sender != null && message != null) {
                            val formattedMessage = if(userEmail == sender)"내가 : ${sender}에게 \n메시지 : $message" else "${sender}가 나에게 \n메시지 : $message"
                            chatlist.add(formattedMessage)
                        }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chatlist)
                messagesListView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // 메시지 가져오기 실패 시 처리
                // 예: Toast 메시지 출력 등
                Log.e("Firebase", "데이터 가져오기 실패", exception)
                exception?.printStackTrace() //다중 색인 추가를 위한 코드

            }
    }


    private fun registerRealtimeUpdates(userEmail: String,otheruser: String ,messagesListView: ListView) {
        val messagesRef = db.collection("messages")


        messagesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // 오류 처리
                Log.e("Firebase", "데이터 실시간 업데이트 실패", exception)
                return@addSnapshotListener
            }

            // 실시간 업데이트가 발생했을 때 처리
            if (snapshot != null && !snapshot.isEmpty) {
                val chatlist = mutableListOf<String>()

                for (document in snapshot) {
                    val sender = document.getString("sender")
                    val message = document.getString("message")

                    if (sender != null && message != null) {
                        val formattedMessage = "보낸 사람: $sender \n메시지 : $message"
                        chatlist.add(formattedMessage)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chatlist)
                messagesListView.adapter = adapter
                }

            }
        }
    }
