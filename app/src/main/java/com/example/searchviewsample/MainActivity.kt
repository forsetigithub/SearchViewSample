package com.example.searchviewsample


import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.searchviewsample.databinding.ActivityMainBinding
import com.example.searchviewsample.utils.getQueryTextChangeStateFlow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()
        setUpSearchStateFlow()

    }

    private fun setUpSearchStateFlow() {

        launch {
            binding.search.getQueryTextChangeStateFlow()
                .debounce(500)
                .filter { query ->
                    return@filter !query.isEmpty()
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    getDataFromText(query)
                        .catch {
                            emitAll(flowOf(""))
                        }
                }
                .flowOn(Dispatchers.Default)
                .collect { result ->
                    val searcher:SearchApi = SearchRepository(assets)
                    val resultList = searcher.performSearch(result)

                    val adapter = ArrayAdapter (
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        resultList
                    )
                    Log.i("getQueryTextChangeStateFlow",result)
                    Log.i("getQueryTextChangeStateFlow",resultList.count().toString())
                    binding.listView.adapter = adapter

                }
        }
    }


    private fun getDataFromText(query: String): Flow<String> {
        return flow {
            kotlinx.coroutines.delay(500)
            emit(query)
        }
    }
}