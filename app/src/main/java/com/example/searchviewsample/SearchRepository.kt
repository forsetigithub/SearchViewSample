package com.example.searchviewsample

import android.content.res.AssetManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

interface SearchApi {
    suspend fun performSearch(query: String): List<String>
}

class SearchRepository(private val assets: AssetManager,
                      private val maxResult: Int = DEFAULT_RESULT_MAX_SIZE): SearchApi{
    companion object {
        private const val DEFAULT_RESULT_MAX_SIZE = 250
    }

    override suspend fun performSearch(query: String): List<String> {
        return withContext(Dispatchers.IO) {
            Log.i("performSearch","Search for $query")
            val inputStream = assets.open("ken_all_2.txt")
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            bufferedReader.use { reader: BufferedReader ->
                reader.lineSequence()
                    .filter { it.trim().isNotEmpty() && it.replace("　","").contains(query.trim(),true) }
                    .take(maxResult)
                    .toList()
            }
        }
    }
}