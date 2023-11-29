package com.example.project
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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

            // Firestore에서 실시간 업데이트를 감지하는 리스너 등록
            registerRealtimeUpdates(userEmail, messagesListView)
        }
    }

    private fun fetchMessages(userEmail: String, messagesListView: ListView) {
        val messagesRef = db.collection("messages")

        messagesRef.get()
            .addOnSuccessListener {
                val messageList = mutableListOf<String>()

                for (i in it.documents){
                    //document의 저장되는 이름이 구매자_판매자
                    //_로 잘라서 비교
                    //그 2개중에 한개라도 있으면 채팅방 가져옴
                    if(userEmail == (i.id).split("_")[0]  ||userEmail == (i.id).split("_")[1]) {
                        //잘린 부분 중 자신의 이름이 아닌 부분을 리스트에 추가
                        val ChatTitle = if(userEmail == (i.id).split("_")[0]) (i.id).split("_")[1] +"님과의 채팅방"
                                        else (i.id).split("_")[0]+"님과의 채팅방"
                        messageList.add(ChatTitle)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
                messagesListView.adapter = adapter
                messagesListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val text = (messageList.get(position).split("님과의 채팅방"))[0]
                    //Toast.makeText(this,"뭐야 ${text}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ChatRoom::class.java)
                    intent.putExtra("userEmail", userEmail)
                    intent.putExtra("otheruser", text)
                    startActivity(intent)

                }
            }
            .addOnFailureListener { exception ->
//                // 메시지 가져오기 실패 시 처리
//                // 예: Toast 메시지 출력 등
//                Log.e("Firebase", "데이터 가져오기 실패", exception)
//                exception?.printStackTrace() //다중 색인 추가를 위한 코드
//
            }


    }


    private fun registerRealtimeUpdates(userEmail: String, messagesListView: ListView) {
        val messagesRef = db.collection("messages")


        messagesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // 오류 처리
                Log.e("Firebase", "데이터 실시간 업데이트 실패", exception)
                return@addSnapshotListener
            }

            // 실시간 업데이트가 발생했을 때 처리
            if (snapshot != null && !snapshot.isEmpty) {
                val messageList = mutableListOf<String>()

                for (document in snapshot){
                    //document의 저장되는 이름이 구매자_판매자
                    //_로 잘라서 비교
                    //그 2개중에 한개라도 있으면
                    if(userEmail == (document.id).split("_")[0]  ||userEmail == (document.id).split("_")[1]) {
                        //잘린 부분 중 자신의 이름이 아닌 부분을 리스트에 추가
                        val ChatTitle = if(userEmail == (document.id).split("_")[0]) (document.id).split("_")[1] +"님과의 채팅방"
                        else (document.id).split("_")[0]+"님과의 채팅방"
                        messageList.add(ChatTitle)
                    }
                }
                // 업데이트된 메시지를 리스트뷰에 표시
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
                messagesListView.adapter = adapter
                messagesListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val text = (messageList.get(position).split("님과의 채팅방"))[0]
                    //Toast.makeText(this,"${text}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ChatRoom::class.java)
                    intent.putExtra("userEmail", userEmail)
                    intent.putExtra("otheruser", text)
                    startActivity(intent)
                }

            }
        }
    }



}
