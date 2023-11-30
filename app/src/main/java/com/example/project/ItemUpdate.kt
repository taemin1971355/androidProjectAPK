package com.example.project

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//itemCreate와 코드 유사
//아이템 처음 등록
class ItemUpdate: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemupdate)

        // Intent에서 데이터 추출
        val itemId = intent.getStringExtra("itemId")
        val title = intent.getStringExtra("title")
        val price = intent.getIntExtra("price", 0)
        val text = intent.getStringExtra("text")
        val user = intent.getStringExtra("user")
        val status = intent.getStringExtra("status")

        // 화면에 표시
        findViewById<TextView>(R.id.view_contentTitle).text = title
        findViewById<EditText>(R.id.view_contentPrice).setText(price.toString())
        findViewById<TextView>(R.id.view_editText).text = text



        val radioGroup = findViewById<RadioGroup>(R.id.upload_sellStatus)
        when (status) {
            "판매중" -> radioGroup.check(R.id.upload_selling)
            "판매완료" -> radioGroup.check(R.id.upload_selled)
        }

        //등록 버튼 누르면
        findViewById<Button>(R.id.message_send).setOnClickListener() {
            val db: FirebaseFirestore = Firebase.firestore
            val itemsCollectionRef = db.collection("items")
            val title = findViewById<TextView>(R.id.view_contentTitle).text.toString()
            val price = findViewById<EditText>(R.id.view_contentPrice).text.toString()
            val text = findViewById<TextView>(R.id.view_editText).text.toString()
            val sellStatus = findViewById<RadioGroup>(R.id.upload_sellStatus).checkedRadioButtonId
            var status = ""
            when(sellStatus){
                R.id.upload_selling-> status = "판매중"
                R.id.upload_selled -> status = "판매완료"
            }
            if (TextUtils.isEmpty(price)) {
                // 빈 칸이 있는 경우 사용자에게 알림
                Toast.makeText(this, "가격을 입력하세요(KRW)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val itemMap = hashMapOf(
                "title" to title,
                "price" to price,
                "text" to text,
                "user" to Firebase.auth.currentUser?.email,
                "status" to status
            )
            if (itemId !=null) { // Document의 ID를 자동으로 생성
                itemsCollectionRef.document(itemId).set(itemMap)
                // itemID에 해당되는 Document가 존재하면 내용을 업데이트
            } else { // Document의 ID를 itemID의 값으로 지정
                itemsCollectionRef.add(itemMap)
            }
            finish()
        }

        //취소 버튼 누를 경우
        findViewById<Button>(R.id.upload_cancel).setOnClickListener(){
            AlertDialog.Builder(this@ItemUpdate)
                .setTitle("등록 취소")
                .setMessage("등록을 취소하시겠습니까?")
                .setNegativeButton("예") { dialog, id ->
                    finish()
                }
                .setPositiveButton("아니요") { dialog, id ->
                }
                .create()
                .show()
        }
        //백 버튼 누를 경우
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@ItemUpdate)
                    .setTitle("등록 취소")
                    .setMessage("등록을 취소하시겠습니까?")
                    .setNegativeButton("예") { dialog, id ->
                        finish()
                    }
                    .setPositiveButton("아니요") { dialog, id ->
                    }
                    .create()
                    .show()
            }
        })
    }

}