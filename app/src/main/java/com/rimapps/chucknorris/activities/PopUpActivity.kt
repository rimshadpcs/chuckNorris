package com.rimapps.chucknorris.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.rimapps.chucknorris.R
import com.rimapps.chucknorris.databinding.ActivityPopUpBinding
import com.rimapps.chucknorris.model.Category
import kotlinx.android.synthetic.main.activity_pop_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class PopUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPopUpBinding
    private var popupText = ""
    private var darkStatusBar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        binding = ActivityPopUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_pop_up)

        if(intent.getIntExtra("FROM", 0) == 0){
            popupText = intent.extras?.getString("randomjoke", "rJoke") ?: ""
            //Toast.makeText(this@PopUpActivity, popupText, Toast.LENGTH_SHORT).show()
            tv_jokePopup.setText(popupText)
        }
         else if(intent.getIntExtra("FROM", 0) == 1){
            val category = getIntent().getSerializableExtra("category") as Category
            tv_jokePopup.setText(category.category)
            parsecatrandJoke(category)
        }
        var popup_window_background = findViewById(R.id.popup_window_background) as ConstraintLayout
        var btpopup_window_button = findViewById(R.id.popup_window_button) as Button
        var popup_window_view_with_border =
            findViewById(R.id.popup_window_view_with_border) as CardView


        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()


        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        // Set the Status bar appearance for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                if (darkStatusBar) {
                    this.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }

        }
        btpopup_window_button.setOnClickListener {
            onBackPressed()
        }

    }

    private fun parsecatrandJoke(category: Category) {

        GlobalScope.launch(Dispatchers.IO) {

            //Toast.makeText(this@PopUpActivity, rcjoke.toString(), Toast.LENGTH_SHORT).show()
            val url = URL("https://api.chucknorris.io/jokes/random?category=" + category.category)
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
                    val jsonObject = JSONTokener(response).nextValue() as JSONObject
                    val rcJoke = jsonObject.getString("value")
                    Log.i("value: ", rcJoke)
                    tv_jokePopup.setText(rcJoke)
                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }

        }}

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags =
                winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }
}
