package com.example.project

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)



        val channel = NotificationChannel(
            "firebase-messaging", "firebase-messaging channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "This is firebase-messaging channel."
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)




        findViewById<Button>(R.id.login).setOnClickListener(){
            var login = findViewById<EditText>(R.id.id_edit).text
            var pwd = findViewById<EditText>(R.id.pwd_edit).text
            if(login.length != 0 && pwd.length != 0){
                Firebase.auth.signInWithEmailAndPassword(login.toString(),pwd.toString())
                    .addOnCompleteListener(this){

                        if(it.isSuccessful){
                            startActivity(Intent(this,ItemList::class.java))
                        }
                        else{
                            Toast.makeText(this, "아이디 or 패스워드 오류!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(this, "아이디 or 패스워드 공백!", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.sign_up).setOnClickListener(){
            startActivity(Intent(this,SignUp::class.java))
        }


    }
    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return
        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == false) { // permission is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")

                }.show()
            }
        }
        if (shouldShowRequestPermissionRationale(permission)) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }
}
