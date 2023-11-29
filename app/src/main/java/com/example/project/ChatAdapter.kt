package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class ChatAdapter(private val context: Context, private var items: List<Chat>)
    : RecyclerView.Adapter<ChatViewHolder>() {

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

        holder.view.findViewById<TextView>(R.id.textUser).text = item.id
        holder.view.findViewById<TextView>(R.id.textMessage).text = item.text

        // 다른 필드에 대한 클릭 이벤트 처리도 추가할 수 있습니다.

    }

    override fun getItemCount() = items.size
}