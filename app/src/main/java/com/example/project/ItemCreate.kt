package com.example.project
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//itemUpdate와 코드 유사
//아이템 처음 등록
class ItemCreate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemupload)

        //등록 버튼 누르면
        findViewById<Button>(R.id.message_send).setOnClickListener() {
            val db: FirebaseFirestore = Firebase.firestore
            val itemsCollectionRef = db.collection("items")
            val title = findViewById<EditText>(R.id.view_contentTitle).text.toString()
            val price = findViewById<EditText>(R.id.view_contentPrice).text.toString()
            val text = findViewById<EditText>(R.id.view_editText).text.toString()
            val status = "판매중"

            val autoID = true
            val itemID = "1234"
            val itemMap = hashMapOf(
                "title" to title,
                "price" to price,
                "text" to text,
                "user" to Firebase.auth.currentUser?.email,
                "status" to status
            )
            if (autoID) { // Document의 ID를 자동으로 생성
                itemsCollectionRef.add(itemMap)
            } else { // Document의 ID를 itemID의 값으로 지정
                itemsCollectionRef.document(itemID).set(itemMap)
                // itemID에 해당되는 Document가 존재하면 내용을 업데이트
            }
            finish()
        }

        //취소 버튼 누를 경우
        findViewById<Button>(R.id.upload_cancel).setOnClickListener(){
            AlertDialog.Builder(this@ItemCreate)
                .setTitle("등록 취소")
                .setMessage("등록을 취소하시겠습니까?")
                .setNegativeButton("예",{ dialog, id ->
                    finish()
                })
                .setPositiveButton("아니요", { dialog, id ->
                })
                .create()
                .show()
        }
        //백 버튼 누를 경우
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@ItemCreate)
                    .setTitle("등록 취소")
                    .setMessage("등록을 취소하시겠습니까?")
                    .setNegativeButton("예",{ dialog, id ->
                        finish()
                    })
                    .setPositiveButton("아니요", { dialog, id ->
                    })
                    .create()
                    .show()
            }
        })
    }

}