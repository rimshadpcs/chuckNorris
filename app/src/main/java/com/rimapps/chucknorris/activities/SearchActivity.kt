package com.rimapps.chucknorris.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.rimapps.chucknorris.model.Cell
import com.rimapps.chucknorris.R
import com.rimapps.chucknorris.adapters.RVAdapter
import com.rimapps.chucknorris.databinding.ActivitySearchBinding
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.*
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@DelicateCoroutinesApi
class SearchActivity : AppCompatActivity() {
    var itemsArray: ArrayList<Cell> = ArrayList()
    lateinit var adapter: RVAdapter

    lateinit var rvSearch : RecyclerView

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        rvSearch = findViewById(R.id.json_results_recyclerview)
        rvSearch.layoutManager = LinearLayoutManager(this)

        setupRecyclerView()
        bt_search.setOnClickListener {
            parseSearchJokes()
        }
    }
    private fun setupRecyclerView() {

        val layoutManager = LinearLayoutManager(this)

        binding.jsonResultsRecyclerview.layoutManager = layoutManager

    }

    @SuppressLint("LongLogTag", "NotifyDataSetChanged")
    private fun parseSearchJokes() {

        val searchQuery = et_search.text;

        val url =
            URL("https://api.chucknorris.io/jokes/search?query=" + searchQuery)
        val httpsURLConnection = url.openConnection() as HttpsURLConnection
        httpsURLConnection.setRequestProperty(
            "Accept",
            "application/json"
        )


        // The format of response we want to get from the server
        httpsURLConnection.requestMethod = "GET"
        httpsURLConnection.doInput = true
        httpsURLConnection.doOutput = false


        //Check if the connection is successful
        val responseCode = httpsURLConnection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            // Convert raw JSON to pretty JSON using GSON library
            val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(JsonParser.parseString(response.toString()))
            Log.d("Pretty Printed JSON :", prettyJson)

            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            val jsonArray = jsonObject.getJSONArray("result")
            for (i in 0 until jsonArray.length()) {
            val joke = jsonArray.getJSONObject(i).getString("value")
            //Toast.makeText(this@SearchActivity, joke, Toast.LENGTH_SHORT).show()
                val model = Cell(
                    joke,
                )
                itemsArray.add(model)
            }
            adapter = RVAdapter(itemsArray)
            rvSearch.adapter = adapter}
        else {
            Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
        }
    }
}
