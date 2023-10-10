package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var continuebtn : Button
    private lateinit var yourLocation : TextInputEditText
    private lateinit var destinationLocation : TextInputEditText

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    private var userId: String? = null
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null)
        Log.d("+++++++++++++", "user ID--- " + userId)


        continuebtn = view.findViewById(R.id.continueBtn)
        yourLocation = view.findViewById(R.id.StartPointID)
        destinationLocation = view.findViewById(R.id.EndPointID)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        continuebtn.setOnClickListener {

//            val intent = Intent(activity, MapsActivity::class.java)
//            startActivity(intent)

            val startPoint = yourLocation.text.toString()
            val endPoint = destinationLocation.text.toString()


            val startPointLatLng = getLatLngFromAddress(startPoint)
            val endPointLatLng = getLatLngFromAddress( endPoint)

            Log.d("---------------", "onCreateView: points ${startPointLatLng} --- ${endPointLatLng}")

            val apiService = RetrofitClient.getClient().create(ApiService::class.java)

            if (startPointLatLng != null && endPointLatLng != null) {

                val startLatitude = startPointLatLng.first
                val startLongitude = startPointLatLng.second
                val endLatitude = endPointLatLng.first
                val endLongitude = endPointLatLng.second

                val startPointname = startPoint
                val endPointname = endPoint
                val startPoint = StartPoint( startPointname,startLatitude,startLongitude)
                val endPoint = EndPoint(endPointname , endLatitude,endLongitude)

                val locationData = LocationInfo(
                    userId!!, startPoint,endPoint)
                Log.d("---------------", "onCreateView: points ${locationData}")
                val call = apiService.postLocationData(locationData)
                call.enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Log.d("API Success", "Response successful  ")
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                            Log.d("API Success", "Response successful")
                            val intent = Intent(activity, MapsActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "Faill to save ", Toast.LENGTH_SHORT).show()
                            Log.e("API Error", response.message())
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                        Log.e("Network Error", t.message ?: "Unknown error")
                    }
                })
            }
        }
        return view
    }

    fun getLatLngFromAddress(address :String): Pair<Double,     Double>? {
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(address, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    return Pair(latitude, longitude)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}

private fun <T> Call<T>.enqueue(t: T) {

}


data class LocationInfo(
    val userId: String,
    val startPoint: StartPoint,
    val endPoint: EndPoint
)

data class StartPoint(
    val startPointname: String,
    val startLatitude: Double,
    val startLongitude: Double
)

data class EndPoint(
    val endPointname: String,
    val endLatitude: Double,
    val endLongitude: Double
)











