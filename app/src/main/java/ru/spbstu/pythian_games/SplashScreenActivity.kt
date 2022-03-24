package ru.spbstu.pythian_games

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.spbstu.pythian_games.root.presentation.RootActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, RootActivity::class.java)
        startActivity(intent)
        finish()
    }
}
