package com.example.secret.palpatine.ui.mainmenu

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.secret.palpatine.R

import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return true
    }

}
