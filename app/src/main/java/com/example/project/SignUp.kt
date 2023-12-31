package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        findViewById<Button>(R.id.ok).setOnClickListener(){
            val id = findViewById<EditText>(R.id.Sign_id_editText).text
            val pwd = findViewById<EditText>(R.id.Sign_pwd_editText).text
            val name = findViewById<EditText>(R.id.Sign_name_editText).text
            val birth = findViewById<EditText>(R.id.Sign_birth_editText).text

            // YYYYMMDD 형식을 강제하는 정규 표현식
            val datePattern = Regex("""^\d{8}$""")
            // 이름에는 한글, 영문 대/소문자만 허용
            val namePattern = Regex("""^[가-힣a-zA-Z]+$""")

            if(id.length != 0 && pwd.length >=8 && name.length !=0&& datePattern.matches(birth)&& namePattern.matches(name) ) {
                val email = id.split("@")
                val last = email[email.lastIndex]
                if(email.lastIndex == 1){
                    Firebase.auth.createUserWithEmailAndPassword(id.toString(),pwd.toString())
                        .addOnCompleteListener(this){

                            if(it.isSuccessful){
                                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else{
                                Toast.makeText(this, "이미 존재하는 email입니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else{
                    Toast.makeText(this, "email형식에 맞게 적어주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else if (!namePattern.matches(name)) {
                Toast.makeText(this, "이름에는 영문과 한글만 가능합니다.", Toast.LENGTH_SHORT).show()
            }
            else if(pwd.length < 8){
                Toast.makeText(this, "비밀번호는 8자 이상입니다.", Toast.LENGTH_SHORT).show()
            }
            else if (!datePattern.matches(birth)) {
                Toast.makeText(this, "올바른 생년월일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            }

            else{
                Toast.makeText(this, "빈칸이 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.cancel).setOnClickListener(){
            finish()
        }




        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        })



    }
}