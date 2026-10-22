package com.example.gimnasiovirtual

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.routines.RutinasActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, RutinasActivity::class.java)
        startActivity(intent)
        finish()
    }
}