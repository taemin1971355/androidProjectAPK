package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItemView: AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemview)

        // Intent에서 데이터 추출
        val itemId = intent.getStringExtra("itemId")
        val title = intent.getStringExtra("title")
        val price = intent.getIntExtra("price", 0)
        val text = intent.getStringExtra("text")
        val user = intent.getStringExtra("user")
        val status = intent.getStringExtra("status")

        // 화면에 표시
        findViewById<TextView>(R.id.view_contentTitle).text = title
        findViewById<TextView>(R.id.view_contentPrice).text = "$price KRW"
        findViewById<TextView>(R.id.view_editText).text = text
        findViewById<TextView>(R.id.view_contentUserId).text = user
        findViewById<TextView>(R.id.view_contentSale).text = status

        findViewById<Button>(R.id.message_send).setOnClickListener()
        {                   // 메시지 전송 예시
            showSendMessageDialog()
        }
    }
    private fun showSendMessageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_message_input, null)
        val editTextMessage = dialogView.findViewById<EditText>(R.id.editTextMessage)

        AlertDialog.Builder(this)
            .setTitle("메시지 전송")
            .setView(dialogView)
            .setPositiveButton("전송") { _, _ ->
                val auth = Firebase.auth
                val userId = auth.currentUser?.email

                if (userId != null) {
                    val receiver = intent.getStringExtra("user").toString()
                    val message = editTextMessage.text.toString()
                    if (message.isNotEmpty()) {
                        sendMessage(userId, receiver, message)
                    } else {
                        Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()

                    }
                }


            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 메시지 전송
    fun sendMessage(sender: String, receiver: String, message: String) {


        val messageData = hashMapOf(
            "sender" to sender,
            "receiver" to receiver,
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp() // 메시지를 전송한 시간
        )
        val path = if(sender.compareTo(receiver)> 0) "${sender}_${receiver}" else "${receiver}_${sender}"
        db.collection("messages").document(path).collection(path).add(messageData)
            //messagesRef.add(messageData)
            .addOnSuccessListener {
                // 메시지 전송 성공
                db.collection("messages").document(path).set(hashMapOf("room" to path))
                    //messagesRef.add(messageData)
                    .addOnSuccessListener {
                        // 메시지 전송 성공
                        Toast.makeText(this, "메시지 전송에 성공하였습니다. 전체 메시지를 채팅방에서 확인할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // 메시지 전송 실패
                        Toast.makeText(this, "메시지 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                // 메시지 전송 실패
            }

    }

}