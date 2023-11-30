package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase

data class Chat(
    val id: String,
    val text: String,
) {
    constructor(doc: QueryDocumentSnapshot) : this(
        doc["sender"].toString(),
        doc["message"].toString()
    )
}

class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class ChatAdapter(
    private val context: Context,
    private var items: List<Chat>,
    private var currentUserEmail: String
) : RecyclerView.Adapter<ChatViewHolder>() {

    fun updateList(newList: List<Chat>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == 1) {
            val view = inflater.inflate(R.layout.s_res, parent, false)
            return ChatViewHolder(view)
        }
        else{
            val view = inflater.inflate(R.layout.sender, parent, false)
            return ChatViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position].id == currentUserEmail){
            true -> 1
            false -> 0
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = items[position]

        val textUser = holder.view.findViewById<TextView>(R.id.textUser)
        val textMessage = holder.view.findViewById<TextView>(R.id.textMessage)

        textUser.text = item.id
        textMessage.text = item.text
        Toast.makeText(context,"${item.id} ${currentUserEmail} ${item.text}", Toast.LENGTH_SHORT).show()
        // 현재 아이템이 현재 사용자에서 온 것인지 확인

// 메시지가 현재 사용자에서 온 것인지, 다른 사용자에서 온 것인지에 따라 레이아웃 설정




    }

    override fun getItemCount() = items.size
}
