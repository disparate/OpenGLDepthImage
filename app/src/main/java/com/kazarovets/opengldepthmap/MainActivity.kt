package com.kazarovets.opengldepthmap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonFirst.setOnClickListener {
            startActivity(OpenGlActivity.getIntent(this, 0))
        }

        buttonSecond.setOnClickListener {
            startActivity(OpenGlActivity.getIntent(this, 1))
        }

        buttonThird.setOnClickListener {
            startActivity(OpenGlActivity.getIntent(this, 2))
        }

        buttonFourth.setOnClickListener {
            startActivity(OpenGlActivity.getIntent(this, 3))
        }
    }


}
