package com.example.weatherapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.AsyncTask
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import com.example.weatherapp1.R
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var CITY: String = ""
    private val API: String = "60e1bc5b9c6f5590f77bf44b2127543f" // Use API key
    private fun showError() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.searchButton)
        val searchInput = findViewById<EditText>(R.id.searchCity)

        searchButton.setOnClickListener {
            val userCity = searchInput.text.toString().trim()
            if (userCity.isNotEmpty()) {
                CITY = userCity
                weatherTask().execute()
            } else {
                Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }
    inner class weatherTask():AsyncTask<String,Void,String>(){
        override fun onPreExecute(){
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility=View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.GONE
            findViewById<TextView>(R.id.errorText).visibility=View.GONE
        }

        override fun doInBackground(vararg params: String?): String {
            var response:String?
            try{
                response=URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)
            }
            catch(e:Exception){
                response=null
            }
            return response ?:""
        }

        override fun onPostExecute(result:String?) {
            super.onPostExecute(result)

            if (result.isNullOrEmpty()) {
                showError()
                return
            }
            try{
                val jsonObj=JSONObject(result)
                if (jsonObj.getInt("cod") != 200) {
                    showError()
                    return
                }
                val main=jsonObj.getJSONObject("main")
                val sys=jsonObj.getJSONObject("sys")
                val wind=jsonObj.getJSONObject("wind")
                val weather=jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long=jsonObj.getLong("dt")
                val updatedAtText="Updated at: "+SimpleDateFormat("dd/MM/yyyy hh:mm a",Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp=main.getString("temp")+"°C"
                val tempMin="Min Temp: "+main.getString("temp_min")+"°C"
                val tempMax="Max Temp: "+main.getString("temp_max")+"°C"
                val pressure=main.getString("pressure")+"mbar"
                val humidity=main.getString("humidity")+"%"
                val sunrise:Long=sys.getLong("sunrise")
                val sunset:Long=sys.getLong("sunset")
                val windSpeed=wind.getString("speed")+"Km/h"
                val weatherDescription=weather.getString("description")
                val address=jsonObj.getString("name")+", "+sys.getString("country")


//                findViewById<TextView>(R.id.address).text=address

                findViewById<TextView>(R.id.updated_at).text=updatedAtText
                findViewById<TextView>(R.id.status).text=weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text=temp
                findViewById<TextView>(R.id.temp_min).text=tempMin
                findViewById<TextView>(R.id.temp_max).text=tempMax
                findViewById<TextView>(R.id.sunrise).text=SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text=SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text=windSpeed
                findViewById<TextView>(R.id.pressure).text=pressure
                findViewById<TextView>(R.id.humidity).text=humidity

                findViewById<ProgressBar>(R.id.loader).visibility=View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.VISIBLE
            }
            catch(e:Exception){
                findViewById<ProgressBar>(R.id.loader).visibility=View.GONE
                findViewById<TextView>(R.id.errorText).visibility=View.VISIBLE
            }
        }
    }

}
