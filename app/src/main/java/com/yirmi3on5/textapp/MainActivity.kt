package com.yirmi3on5.textapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yirmi3on5.textapp.raw.ForebackgroundService

class MainActivity : AppCompatActivity() {
    private lateinit var firstText: TextView
    private lateinit var secondText: TextView
    private lateinit var buttonClick: Button
    private var isServiceRunning = false
    private lateinit var myBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstText = findViewById(R.id.firstText)
        secondText = findViewById(R.id.secondText)
        buttonClick = findViewById(R.id.skylineButton)
        myBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.i("MainActivity", "onReceive: Intent received") // Log satırı eklendi
                val myVariable = intent?.getStringExtra("icDegisken")
                Log.i("MainActivity", "onReceive: icDegisken = $myVariable") // Log satırı eklendi
                secondText.text = myVariable
            }
        }
        buttonClick.setOnClickListener { buttonClickerFunction() }
    }

    private fun buttonClickerFunction() {
        Log.i("MainActivity", "buttonClickerFunction: Button clicked")
        if (isServiceRunning) {
            stopService()
            firstText.text = "Service is stopped"
            buttonClick.text = "Start Service"
        } else {
            startService()
            firstText.text = "Service is running"
            buttonClick.text = "Stop Service"
        }
        isServiceRunning = !isServiceRunning
    }

    private fun startService() {
        Log.i("MainActivity", "startService: Starting the service")
        val intent = Intent(this, ForebackgroundService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopService() {
        Log.i("MainActivity", "stopService: Stopping the service")
        val intent = Intent(this, ForebackgroundService::class.java)
        stopService(intent)
        Toast.makeText(this, secondText.text, Toast.LENGTH_SHORT).show()
        if (secondText.text.toString().toInt() > 50) {
            secondText.text = "The number is greater than 50"
        } else {
            secondText.text = "The number is less than 50"
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume: Registering the broadcast receiver")
        registerReceiver(myBroadcastReceiver, IntentFilter("myBroadcast"))
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "onPause: Unregistering the broadcast receiver")
        unregisterReceiver(myBroadcastReceiver)
    }
}