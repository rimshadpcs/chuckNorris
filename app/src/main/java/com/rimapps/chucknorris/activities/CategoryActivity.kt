package com.rimapps.chucknorris.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.rimapps.chucknorris.R
import com.rimapps.chucknorris.adapters.CatAdapter
import com.rimapps.chucknorris.databinding.ActivityCategoryBinding
import com.rimapps.chucknorris.model.Category
import org.json.JSONArray
import org.json.JSONTokener
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CategoryActivity : AppCompatActivity() {

    var itemsCat: ArrayList<Category> = ArrayList()
    lateinit var catadapter: CatAdapter
    lateinit var rvCat : RecyclerView

    private lateinit var binding: ActivityCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        rvCat = findViewById(R.id.json_category_recyclerview)


        rvCat.layoutManager = LinearLayoutManager(this);


        parseCategory()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.jsonCategoryRecyclerview.layoutManager = layoutManager
    }
    @SuppressLint("LongLogTag", "NotifyDataSetChanged")
    private fun parseCategory() {

        val url =
            URL("https://api.chucknorris.io/jokes/categories")
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
            //binding.jsonResultsTextview.text = prettyJson


            val  res = GsonBuilder().create().fromJson<ArrayList<String>>(response.toString(), object : TypeToken<ArrayList<String>>(){}.type)

            for( i in res){
                itemsCat.add(Category(i))
            }

            catadapter = CatAdapter(itemsCat)
            rvCat.adapter = catadapter}
        else {
            Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
        }

    }


}


