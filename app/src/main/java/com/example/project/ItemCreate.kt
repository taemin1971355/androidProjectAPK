package com.example.project

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class ItemCreate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemupload)

        findViewById<Button>(R.id.message_send).setOnClickListener() {
            val db: FirebaseFirestore = Firebase.firestore
            val itemsCollectionRef = db.collection("items")
            val title = findViewById<EditText>(R.id.view_contentTitle).text.toString()
            val price = findViewById<EditText>(R.id.view_contentPrice).text.toString()
            val text = findViewById<EditText>(R.id.view_editText).text.toString()
            val status = "판매중"

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(price) || TextUtils.isEmpty(text)) {
                Toast.makeText(this, "빈 칸이 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val autoID = true
            val itemID = "1234"
            val itemMap = hashMapOf(
                "title" to title,
                "price" to price,
                "text" to text,
                "user" to Firebase.auth.currentUser?.email,
                "status" to status,
                "timestamp" to  FieldValue.serverTimestamp(), // Add timestamp field
            )

            if (autoID) {
                itemsCollectionRef.add(itemMap)
            } else {
                itemsCollectionRef.document(itemID).set(itemMap)
            }
            finish()
        }

        findViewById<Button>(R.id.upload_cancel).setOnClickListener() {
            AlertDialog.Builder(this@ItemCreate)
                .setTitle("등록 취소")
                .setMessage("등록을 취소하시겠습니까?")
                .setNegativeButton("예", { dialog, id ->
                    finish()
                })
                .setPositiveButton("아니요", { dialog, id ->
                })
                .create()
                .show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@ItemCreate)
                    .setTitle("등록 취소")
                    .setMessage("등록을 취소하시겠습니까?")
                    .setNegativeButton("예", { dialog, id ->
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
