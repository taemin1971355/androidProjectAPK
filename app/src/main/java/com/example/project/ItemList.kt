package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ItemList : AppCompatActivity() {

    private var adapter: MyAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")

    private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.recyclerview) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemlist)

        //리싸이클러뷰 사이의 구분선
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager(this).orientation)
        recyclerViewItems.addItemDecoration(dividerItemDecoration)
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this, emptyList())
        recyclerViewItems.adapter = adapter


        // Firestore에서 데이터 가져와서 어댑터에 설정
        fetchDataFromFirestore()

        // 아이템 클릭 이벤트 처리
        adapter?.setOnItemClickListener {
            val item = it
            itemsCollectionRef.document(item.id).get().addOnSuccessListener {
                if (it["user"] == Firebase.auth.currentUser?.email) {
                    val intent = Intent(this, ItemUpdate::class.java)
                    intent.putExtra("itemId", item.id)
                    intent.putExtra("title", item.title)
                    intent.putExtra("price", item.price)
                    intent.putExtra("text", item.text)
                    intent.putExtra("user", item.user)
                    intent.putExtra("status", item.status)
                    startActivity(intent)
                } else {

                    val intent = Intent(this, ItemView::class.java)
                    intent.putExtra("itemId", item.id)
                    intent.putExtra("title", item.title)
                    intent.putExtra("price", item.price)
                    intent.putExtra("text", item.text)
                    intent.putExtra("user", item.user)
                    intent.putExtra("status", item.status)
                    startActivity(intent)

                }
            }
        }
        initializeSpinner()
        findViewById<Button>(R.id.create).setOnClickListener(){
            startActivity(Intent(this,ItemCreate::class.java))
        }
        findViewById<Button>(R.id.showMessage).setOnClickListener(){
            startActivity(Intent(this, MessagesActivity::class.java))
        }

    }
    private fun initializeSpinner() {
        // Spinner에 연결할 데이터 배열
        val filterOptions = arrayOf("전체", "판매중", "판매완료")
        val spinnerFilter = findViewById<Spinner>(R.id.spinnerFilter)

        // ArrayAdapter를 사용하여 Spinner에 데이터 연결
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        // Spinner 선택 시 처리
        spinnerFilter.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFilter = filterOptions[position]
                handleFilterSelection(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        })
    }

    private fun handleFilterSelection(selectedFilter: String) {
        when (selectedFilter) {
            "전체" -> fetchDataFromFirestore()
            "판매중" -> queryWhere("판매중")
            "판매완료" -> queryWhere("판매완료")
        }
    }



    override fun onResume() {
        super.onResume()
        // 다른 액티비티에서 돌아왔을 때 리사이클러뷰를 업데이트
        fetchDataFromFirestore()
    }
    private fun fetchDataFromFirestore() {
        itemsCollectionRef.get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<Item>()
                for (document in result) {
                    itemList.add(Item(document))
                }
                adapter?.updateList(itemList)
            }
            .addOnFailureListener { exception ->
                // Firestore에서 데이터를 가져오는데 실패한 경우 처리
            }
    }



    private fun queryWhere(status:String) {
        itemsCollectionRef.whereEqualTo("status", status).get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<Item>()
                for (document in result) {
                    itemList.add(Item(document))
                }
                adapter?.updateList(itemList)
            }
            .addOnFailureListener { exception ->
                // Firestore에서 데이터를 가져오는데 실패한 경우 처리
            }
    }



}
