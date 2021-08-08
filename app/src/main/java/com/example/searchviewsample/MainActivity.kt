package com.example.searchviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

import com.example.searchviewsample.R
import com.example.searchviewsample.utils.getQueryTextChangeStateFlow
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
        setUpSearchStateFlow()


//
//        val searcher = findViewById<SearchView>(R.id.search)
//
//        searcher.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(p0: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                adapter.filter.filter(p0)
//                return false
//            }
//        })
    }

    private fun setUpSearchStateFlow() {

        launch {
            search.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query ->
                    return@filter !query.isEmpty()
                }.distinctUntilChanged()
                .flatMapConcat { query ->
                    getDataFromText(query)
                        .catch {
                            emitAll(flowOf(""))
                        }
                }
                .flowOn(Dispatchers.Default)
                .collect { result ->
                    var adapter = ArrayAdapter (
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        ereaList.filter { s -> s.contains(result) }
                    )
                    Log.i("getQueryTextChangeStateFlow",
                        ereaList.filter { s -> s.contains(result) }.count().toString())
                    listView.adapter = adapter

                }
        }
    }


    private fun getDataFromText(query: String): Flow<String> {
        return flow {
            kotlinx.coroutines.delay(500)
            emit(query)
        }
    }

    companion object {
        val ereaList = listOf<String>(
            "北海道",
            "青森県",
            "岩手県",
            "宮城県",
            "秋田県",
            "山形県",
            "福島県",
            "茨城県",
            "栃木県",
            "群馬県",
            "埼玉県",
            "千葉県",
            "東京都",
            "神奈川県",
            "新潟県",
            "富山県",
            "石川県",
            "福井県",
            "山梨県",
            "長野県",
            "岐阜県",
            "静岡県",
            "愛知県",
            "三重県",
            "滋賀県",
            "京都府",
            "大阪府",
            "兵庫県",
            "奈良県",
            "和歌山県",
            "鳥取県",
            "島根県",
            "岡山県",
            "広島県",
            "山口県",
            "徳島県",
            "香川県",
            "愛媛県",
            "高知県",
            "福岡県",
            "佐賀県",
            "長崎県",
            "熊本県",
            "大分県",
            "宮崎県",
            "鹿児島県",
            "沖縄県"
        )
    }
}