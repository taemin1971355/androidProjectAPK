package com.example.project

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class MessagesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val messagesListView: ListView = findViewById(R.id.messagesListView)

        // 현재 로그인한 사용자의 이메일 가져오기
        val userEmail = auth.currentUser?.email

        if (userEmail != null) {
            // 사용자의 이메일을 기반으로 메시지를 Firestore에서 가져오기
            fetchMessages(userEmail, messagesListView)
        }
    }

    private fun fetchMessages(userEmail: String, messagesListView: ListView) {
        val messagesRef = db.collection("messages")

        // 현재 사용자가 수신자인 메시지를 쿼리
        messagesRef
            .whereEqualTo("receiver", userEmail)              //로그에서 링크타고 색인 추가해야만 동시에 사용가능
            .orderBy("timestamp", Query.Direction.ASCENDING) //로그에서 링크타고 색인 추가해야만 동시에 사용가능
            .get()
            .addOnSuccessListener { result ->
                val messageList = mutableListOf<String>()

                for (document in result) {
                    val sender = document.getString("sender")
                    val message = document.getString("message")

                    if (sender != null && message != null) {
                        val formattedMessage = "보낸 사람: $sender 메시지 : $message"
                        messageList.add(formattedMessage)
                    }
                }

                // 가져온 메시지를 리스트뷰에 표시
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
                messagesListView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // 메시지 가져오기 실패 시 처리
                // 예: Toast 메시지 출력 등
                Log.e("Firebase", "데이터 가져오기 실패", exception)
                exception?.printStackTrace() //다중 색인 추가를 위한 코드

            }
    }
}
