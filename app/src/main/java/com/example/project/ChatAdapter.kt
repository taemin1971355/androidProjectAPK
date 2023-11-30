package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot

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
    private val currentUserEmail: String
) : RecyclerView.Adapter<ChatViewHolder>() {

    fun updateList(newList: List<Chat>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = items[position]

        val textUser = holder.view.findViewById<TextView>(R.id.textUser)
        val textMessage = holder.view.findViewById<TextView>(R.id.textMessage)

        textUser.text = item.id
        textMessage.text = item.text

        // 현재 아이템이 현재 사용자에서 온 것인지 확인
        val isCurrentUser = item.id == currentUserEmail

// 메시지가 현재 사용자에서 온 것인지, 다른 사용자에서 온 것인지에 따라 레이아웃 설정
        if (isCurrentUser) {
            // 현재 사용자가 보낸 메시지의 레이아웃
            textUser.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            textMessage.setBackgroundResource(R.drawable.chat_bubble_right)

            // ConstraintSet을 생성하고 textMessage와 textUser를 부모의 오른쪽에 연결
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.view as ConstraintLayout)
            constraintSet.connect(textMessage.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.connect(textUser.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            constraintSet.applyTo(holder.view as ConstraintLayout)
        } else {
            // 다른 사용자가 보낸 메시지의 레이아웃
            textUser.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

            textMessage.setBackgroundResource(R.drawable.chat_bubble_left)

            // ConstraintSet을 생성하고 textMessage와 textUser를 부모의 왼쪽에 연결
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.view as ConstraintLayout)
            constraintSet.connect(textMessage.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(textUser.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.applyTo(holder.view as ConstraintLayout)
        }



    }

    override fun getItemCount() = items.size
}
