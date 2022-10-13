package com.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iconImageView = findViewById<ImageView>(R.id.icon_image_view)
        scaleInOut(iconImageView)

        Thread {
            Thread.sleep(1500)

            val intent = Intent(this, ReaderActivity::class.java)
            startActivity(intent)
            finish()
        }.start()
    }

    private fun scaleInOut(view: View) {
        val anim = ScaleAnimation(
            1.0f,
            1.2f,
            1.0f,
            1.2f,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f
        )
        anim.duration = 750
        anim.repeatCount = 1
        anim.repeatMode = AlphaAnimation.REVERSE
        view.startAnimation(anim)
    }
}