package com.example.project


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Item(
    val id: String,
    val title: String,
    val price: Int,
    val text: String,
    val user: String?,
    val status: String
) {
    constructor(doc: QueryDocumentSnapshot) : this(
        doc.id,
        doc["title"].toString(),
        doc["price"].toString().toIntOrNull() ?: 0,
        doc["text"].toString(),
        doc["user"]?.toString(),
        doc["status"].toString()
    )
}

class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class MyAdapter(private val context: Context, private var items: List<Item>)
    : RecyclerView.Adapter<MyViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(student_id: Item)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun updateList(newList: List<Item>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]

        holder.view.findViewById<TextView>(R.id.textTitle).text = item.title
        holder.view.findViewById<TextView>(R.id.textPrice).text = item.price.toString()

        holder.view.findViewById<TextView>(R.id.textUser).text = item.user
        holder.view.findViewById<TextView>(R.id.textStatus).text = item.status


        holder.view.findViewById<TextView>(R.id.textTitle).setOnClickListener {
            itemClickListener?.onItemClick(item)
        }

        holder.view.findViewById<TextView>(R.id.textPrice).setOnClickListener {
            itemClickListener?.onItemClick(item)
        }

        holder.view.findViewById<TextView>(R.id.textUser).setOnClickListener {
            itemClickListener?.onItemClick(item)
        }

        holder.view.findViewById<TextView>(R.id.textStatus).setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
        // 다른 필드에 대한 클릭 이벤트 처리도 추가할 수 있습니다.

    }

    override fun getItemCount() = items.size
}