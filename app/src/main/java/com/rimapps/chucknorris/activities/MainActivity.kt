package com.rimapps.chucknorris.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.rimapps.chucknorris.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        parseRandomJoke()

        var cardSearchJoke = findViewById(R.id.card_search) as CardView
        cardSearchJoke.setOnClickListener {

            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)

        }
        var cardCat = findViewById(R.id.card_categories) as CardView

        cardCat.setOnClickListener {

            val intent = Intent(this@MainActivity, CategoryActivity::class.java)
            startActivity(intent)

        }
    }
    private fun parseRandomJoke() {
        GlobalScope.launch(Dispatchers.IO) {
            val url =
                URL("https://api.chucknorris.io/jokes/random")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.setRequestProperty(
                "Accept",
                "application/json"
            ) // The format of response we want to get from the server
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            // Check if the connection is successful
            val responseCode = httpsURLConnection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(JsonParser.parseString(response))
                    Log.d("Pretty Printed JSON :", prettyJson)
                    ///binding.jsonResultsTextview.text = prettyJson

                    val jsonObject = JSONTokener(response).nextValue() as JSONObject

                    val rJoke = jsonObject.getString("value")

                    Log.i("value: ", rJoke)

                    var cardrandom = findViewById(R.id.card_random) as CardView

                    cardrandom.setOnClickListener {
                        parseRandomJoke()
                        val intent = Intent(this@MainActivity, PopUpActivity::class.java)
                            intent.putExtra("randomjoke", rJoke)
                            intent.putExtra("FROM", 0);
                            startActivity(intent)
                            //Toast.makeText(this@MainActivity, rJoke, Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
                }


            }


        }
    }
