package com.example.project
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase


class ChatRoom: AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private var adapter: ChatAdapter? = null
    private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.Chatrecyclerview) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(this, emptyList())
        recyclerViewItems.adapter = adapter



        //사용자 id와 채팅방의 다른 이용자 아이디 받아옴.
        val otheruser = intent.getStringExtra("otheruser")
        val userEmail = intent.getStringExtra("userEmail")


        findViewById<Button>(R.id.sendMessage).setOnClickListener {
            val messageData = hashMapOf(
                "sender" to userEmail.toString(),
                "receiver" to otheruser.toString(),
                "message" to (findViewById<EditText>(R.id.sendChatMsg).text).toString(),
                "timestamp" to FieldValue.serverTimestamp() // 메시지를 전송한 시간
            )
            val path = if((userEmail.toString()).compareTo(otheruser.toString())> 0) "${userEmail}_${otheruser}" else "${otheruser}_${userEmail}"
            if(findViewById<EditText>(R.id.sendChatMsg).text != null) {
                db.collection("messages").document(path).collection(path).add(messageData)
                    //messagesRef.add(messageData)
                    .addOnSuccessListener {
                        // 메시지 전송 성공
                        db.collection("messages").document(path).set(hashMapOf("room" to path))
                            //messagesRef.add(messageData)
                            .addOnSuccessListener {
                                // 메시지 전송 성공
                                findViewById<EditText>(R.id.sendChatMsg).setText(null)
                            }
                            .addOnFailureListener {
                                // 메시지 전송 실패
                            }
                    }
                    .addOnFailureListener {
                        // 메시지 전송 실패
                    }
            }
        }

        if (userEmail != null) {
            // 사용자의 이메일을 기반으로 메시지를 Firestore에서 가져오기

            fetchChat(userEmail, otheruser.toString())

            // Firestore에서 실시간 업데이트를 감지하는 리스너 등록
            registerRealtimeUpdates(userEmail,otheruser.toString())
        }
    }
    private fun fetchChat(userEmail: String,otheruser: String) {
        //채팅방에 있는 사람들(사용자id, 채팅 주고 받은 사용자)을
        //사전순서로 해서 _로 연결해서 String 만듬
        //만든 String으로 해당하는 message collection에서 document와 collection에 접속
        val path = if(userEmail.compareTo(otheruser)> 0) "${userEmail}_${otheruser}" else "${otheruser}_${userEmail}"
        val messagesRef = db.collection("messages").document(path).collection(path)

        //접속한 DB에는 채팅방에 있는 2명에 관한 정보 밖에 없음으로
        //단순히 timestamp만 읽어서 정렬 후 표시
        messagesRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val chatlist = mutableListOf<Chat>()
                for (document in result) {
                        val sender = document.getString("sender")
                        val receiver = document.getString("receiver")
                        val message = document.getString("message")

                        if (sender != null && message != null) {
                            //누가 보냈는지에 따라 fomatted의 String 변경
                            chatlist.add(Chat(document))
                        }
                }
                adapter?.updateList(chatlist)
            }
            .addOnFailureListener { exception ->
                // 메시지 가져오기 실패 시 처리
                // 예: Toast 메시지 출력 등
                Log.e("Firebase", "데이터 가져오기 실패", exception)
                exception?.printStackTrace() //다중 색인 추가를 위한 코드

            }
    }


    private fun registerRealtimeUpdates(userEmail: String,otheruser: String) {
        //fetchMessages 함수와 비슷한 원리
        val path = if(userEmail.compareTo(otheruser)> 0) "${userEmail}_${otheruser}" else "${otheruser}_${userEmail}"
        val messagesRef = db.collection("messages").document(path).collection(path)

        messagesRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // 오류 처리
                Log.e("Firebase", "채팅방 실시간 업데이트 실패", exception)
                return@addSnapshotListener
            }

            // 실시간 업데이트가 발생했을 때 처리
            if (snapshot != null && !snapshot.isEmpty) {
                val chatlist = mutableListOf<Chat>()

                for (document in snapshot) {
                    val sender = document.getString("sender")
                    val message = document.getString("message")

                    if (sender != null && message != null) {
                        chatlist.add(Chat(document))
                    }
                }
                adapter?.updateList(chatlist)
                recyclerViewItems.layoutManager = LinearLayoutManager(this).apply {
                    this.stackFromEnd = true
                }
                }

            }
        }
    }
