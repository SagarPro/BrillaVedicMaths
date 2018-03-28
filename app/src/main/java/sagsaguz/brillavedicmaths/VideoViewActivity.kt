package sagsaguz.brillavedicmaths

import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.VideoView
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import sagsaguz.brillavedicmaths.utils.Config

class VideoViewActivity : AppCompatActivity() {

    lateinit var pbVideoView : ProgressBar
    lateinit var vvFullScreen : VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.video_view_layout)

        pbVideoView = findViewById(R.id.pbVideoView)
        pbVideoView.indeterminateDrawable.setColorFilter(resources.getColor(R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY)
        vvFullScreen = findViewById(R.id.vvFullScreen)

        val video = intent.getStringExtra("VideoCode")

        playVideo("videos/$video.mp4")

        vvFullScreen.setOnCompletionListener { finish() }

    }

    private fun playVideo(objectName: String) {

        val myCredentials = BasicAWSCredentials(Config.ACCESSKEY, Config.SECRETKEY)
        val s3client = AmazonS3Client(myCredentials)
        val request = GeneratePresignedUrlRequest(Config.BUCKETNAME, objectName)
        val objectURL = s3client.generatePresignedUrl(request)

        window.setFormat(PixelFormat.TRANSLUCENT)
        val mediaCtrl = MediaController(this)
        mediaCtrl.setMediaPlayer(vvFullScreen)
        mediaCtrl.setAnchorView(vvFullScreen)
        mediaCtrl.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        vvFullScreen.setMediaController(mediaCtrl)
        val str = objectURL.toString()
        //val subStr = str.substring(0, str.indexOf("?"))
        val clip = Uri.parse(str)
        vvFullScreen.setVideoURI(clip)
        vvFullScreen.requestFocus()
        vvFullScreen.start()
        vvFullScreen.setOnPreparedListener {
            pbVideoView.visibility = View.GONE
        }

    }

}
