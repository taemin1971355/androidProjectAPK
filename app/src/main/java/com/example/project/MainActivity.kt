package com.example.project
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            // 이미 로그인된 경우
            startItemListActivity()
        }

        findViewById<Button>(R.id.login).setOnClickListener(){
            var login = findViewById<EditText>(R.id.id_edit).text
            var pwd = findViewById<EditText>(R.id.pwd_edit).text
            if(login.length != 0 && pwd.length != 0){
                Firebase.auth.signInWithEmailAndPassword(login.toString(),pwd.toString())
                    .addOnCompleteListener(this){

                        if(it.isSuccessful){
                            startItemListActivity()
                        }
                        else{
                            Toast.makeText(this, "아이디 또는 패스워드에 오류가 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(this, "아이디 또는 패스워드에 빈칸이 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.sign_up).setOnClickListener(){
            startActivity(Intent(this,SignUp::class.java))
        }


    }
    private fun startItemListActivity() {
        startActivity(Intent(this, ItemList::class.java))
        finish()
    }


}
