package sagsaguz.brillavedicmaths

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.VideoView

class IntroductionActivity : AppCompatActivity() {

    lateinit var vvIntro : VideoView
    lateinit var btnNext : Button
    lateinit var pbIntro : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introduction_layout)

        vvIntro = findViewById(R.id.vvIntro)
        btnNext = findViewById(R.id.btnNext)
        pbIntro = findViewById(R.id.pbIntro)
        pbIntro.indeterminateDrawable.setColorFilter(resources.getColor(R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY)

        btnNext.setOnClickListener {
            startActivity(Intent(baseContext, HomePageActivity::class.java))
            finish()
        }

        val file = "https://s3.ap-south-1.amazonaws.com/brillavm/Program+Overview.mp4"

        val clip = Uri.parse(file)
        vvIntro.setVideoURI(clip)
        vvIntro.requestFocus()
        vvIntro.start()
        vvIntro.setOnPreparedListener {
            pbIntro.visibility = View.GONE
        }

    }
}
