package sagsaguz.brillavedicmaths

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import sagsaguz.brillavedicmaths.utils.BrillaVMUsersDO
import sagsaguz.brillavedicmaths.utils.Config
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class QuizActivity : AppCompatActivity() {

    lateinit var tvQuestion : TextView

    lateinit var rgOpt : RadioGroup
    lateinit var rbOpt1 : RadioButton
    lateinit var rbOpt2 : RadioButton
    lateinit var rbOpt3 : RadioButton
    lateinit var rbOpt4 : RadioButton

    lateinit var pbQuiz : ProgressBar
    lateinit var rlQuiz : RelativeLayout

    lateinit var btnCheck : Button
    lateinit var btnNext : Button

    companion object {
        lateinit var s3Client: AmazonS3
    }

    var question = ArrayList<String>()
    var opt1 = ArrayList<String>()
    var opt2 = ArrayList<String>()
    var opt3 = ArrayList<String>()
    var opt4 = ArrayList<String>()
    var answer = ArrayList<String>()

    var rbId = ArrayList<Int>()

    var questionNumber = 0

    var selectedOpt : Int = 0
    var correctOpt : Int = 0
    var correctAnswer : Int = 0

    lateinit var tempRadioButton : RadioButton

    lateinit var context : Activity

    //stop watch variables
    var handler: Handler? = null
    var hour: TextView? = null
    var minute: TextView? = null
    var seconds: TextView? = null
    var milli_seconds: TextView? = null

    internal var MillisecondTime: Long = 0
    internal var StartTime: Long = 0
    internal var TimeBuff: Long = 0
    internal var UpdateTime = 0L


    internal var Seconds: Int = 0
    internal var Minutes: Int = 0
    internal var MilliSeconds: Int = 0

    internal var flag:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_layout)

        context = this

        rlQuiz = findViewById(R.id.rlQuiz)

        tvQuestion = findViewById(R.id.tvQuestion)

        rgOpt = findViewById(R.id.rgOpt)

        rbOpt1 = findViewById(R.id.rbOpt1)
        rbId.add(R.id.rbOpt1)
        rbOpt2 = findViewById(R.id.rbOpt2)
        rbId.add(R.id.rbOpt2)
        rbOpt3 = findViewById(R.id.rbOpt3)
        rbId.add(R.id.rbOpt3)
        rbOpt4 = findViewById(R.id.rbOpt4)
        rbId.add(R.id.rbOpt4)

        pbQuiz = findViewById(R.id.pbQuiz)
        pbQuiz.indeterminateDrawable.setColorFilter(resources.getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        pbQuiz.visibility = View.GONE

        btnCheck = findViewById(R.id.btnCheck)
        btnNext = findViewById(R.id.btnNext)

        hour = findViewById(R.id.hour)
        minute = findViewById(R.id.minute)
        seconds = findViewById(R.id.seconds)
        milli_seconds = findViewById(R.id.milli_seconds)

        handler = Handler()

        val quiz = intent.getStringExtra("QuizCode")

        //val subkey = "sensorial.csv"

        ReadCSV().execute("quiz/$quiz.csv")

        rgOpt.setOnCheckedChangeListener { radioGroup, i ->
            selectedOpt = radioGroup.checkedRadioButtonId
            setRBColor(selectedOpt)
            btnCheck.isClickable = selectedOpt != -1
        }

        btnCheck.setOnClickListener {
            setResultRBColor(selectedOpt, correctOpt)
            if (questionNumber < question.size)
                questionNumber++
            btnCheck.visibility = View.GONE
            btnNext.visibility = View.VISIBLE
            nonClickableRB()
            stopTimer()
        }

        btnNext.setOnClickListener {
            if (questionNumber < question.size) {
                setQuestion(questionNumber)
                btnNext.visibility = View.GONE
                btnCheck.visibility = View.VISIBLE
                btnCheck.isClickable = false
                setDefaultRBColor()
            } else {
                questionNumber = 0
                btnNext.isClickable = false
                finish()
            }
        }

        btnCheck.isClickable = false
        nonClickableRB()

    }

    private fun stopTimer() {
        if (flag){
            handler?.removeCallbacks(runnable)
            flag=false
        }else{
            StartTime = SystemClock.uptimeMillis()
            handler?.postDelayed(runnable, 0)
            flag=true
        }
    }

    private var runnable: Runnable = object : Runnable {

        @SuppressLint("SetTextI18n")
        override fun run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime

            UpdateTime = TimeBuff + MillisecondTime

            Seconds = (UpdateTime / 1000).toInt()

            Minutes = Seconds / 60

            Seconds = Seconds % 60

            MilliSeconds = (UpdateTime % 1000).toInt()

            if (Minutes.toString().length < 2) {
                minute?.text = "0" + Minutes.toString()
            } else {
                minute?.text = Minutes.toString()
            }
            if (Seconds.toString().length < 2) {
                seconds?.text = "0" + Seconds.toString()
            } else {
                seconds?.text = Seconds.toString()
            }

            milli_seconds?.text = MilliSeconds.toString()

            handler?.postDelayed(this, 0)
        }

    }

    private fun setRBColor(selectedRBId: Int) {
        for (i in 0..3) {
            if (rbId[i] == selectedRBId) {
                val selectedRadioButton = findViewById<RadioButton>(rbId[i])
                selectedRadioButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                selectedRadioButton.setTextColor(resources.getColor(R.color.colorNavy))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selectedRadioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorNavy))
                }
                selectedRadioButton.highlightColor = resources.getColor(R.color.colorNavy)
            } else {
                val selectedRadioButton = findViewById<RadioButton>(rbId[i])
                selectedRadioButton.setBackgroundColor(resources.getColor(R.color.colorNavy))
                selectedRadioButton.setTextColor(resources.getColor(R.color.colorWhite))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selectedRadioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
                }
                selectedRadioButton.highlightColor = resources.getColor(R.color.colorPrimary)
            }
        }
    }

    private fun setResultRBColor(selected: Int, correct: Int) {
        if (selected == correct) {
            correctAnswer++
            val radioButton = findViewById<RadioButton>(selected)
            radioButton.setBackgroundColor(resources.getColor(R.color.colorGreen))
            radioButton.setTextColor(resources.getColor(R.color.colorWhite))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite))
            }
            radioButton.highlightColor = resources.getColor(R.color.colorWhite)
        } else {
            val radioButtonS = findViewById<RadioButton>(selected)
            radioButtonS.setBackgroundColor(resources.getColor(R.color.colorRed))
            radioButtonS.setTextColor(resources.getColor(R.color.colorWhite))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButtonS.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite))
            }
            radioButtonS.highlightColor = resources.getColor(R.color.colorWhite)

            val radioButtonC = findViewById<RadioButton>(correct)
            radioButtonC.setBackgroundColor(resources.getColor(R.color.colorGreen))
            radioButtonC.setTextColor(resources.getColor(R.color.colorWhite))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButtonC.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite))
            }
            radioButtonC.highlightColor = resources.getColor(R.color.colorWhite)
        }
    }

    private fun setDefaultRBColor() {
        rgOpt.clearCheck()
        for (i in 0..3) {
            val radioButton = findViewById<RadioButton>(rbId[i])
            radioButton.setBackgroundColor(resources.getColor(R.color.colorNavy))
            radioButton.setTextColor(resources.getColor(R.color.colorWhite))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            radioButton.highlightColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun setQuestion(qNumber: Int) {
        tvQuestion.text = question[qNumber]
        rbOpt1.text = opt1[qNumber]
        rbOpt2.text = opt2[qNumber]
        rbOpt3.text = opt3[qNumber]
        rbOpt4.text = opt4[qNumber]
        for (i in 0..3) {
            tempRadioButton = findViewById(rbId[i])
            if (tempRadioButton.text == answer[qNumber])
                correctOpt = rbId[i]
        }
        stopTimer()
        clickableRB()
    }

    private fun nonClickableRB() {
        rbOpt1.isClickable = false
        rbOpt2.isClickable = false
        rbOpt3.isClickable = false
        rbOpt4.isClickable = false
    }

    private fun clickableRB() {
        rbOpt1.isClickable = true
        rbOpt2.isClickable = true
        rbOpt3.isClickable = true
        rbOpt4.isClickable = true
    }

    @SuppressLint("StaticFieldLeak")
    inner class ReadCSV : AsyncTask<String, Void, String>() {

        lateinit var s3Client : AmazonS3

        override fun onPreExecute() {
            pbQuiz.visibility = View.VISIBLE
            question.clear()
            opt1.clear()
            opt2.clear()
            opt3.clear()
            opt4.clear()
            answer.clear()
            val myCredentials = BasicAWSCredentials(Config.ACCESSKEY, Config.SECRETKEY)
            s3Client = AmazonS3Client(myCredentials)
        }

        override fun doInBackground(vararg str: String?): String {
            try {
                val listing = s3Client.listObjects(Config.BUCKETNAME, str[0])
                val summaries = listing.objectSummaries
                if (summaries.size == 0) {
                    return "false"
                } else {
                    val s3object = s3Client.getObject(GetObjectRequest(Config.BUCKETNAME, summaries[0].key))
                    println(s3object.objectMetadata.contentType)
                    println(s3object.objectMetadata.contentLength)

                    val reader = BufferedReader(InputStreamReader(s3object.objectContent))
                    try {
                        while (true) {
                            var line = reader.readLine() ?: break
                            line = line.replace("\"", "")
                            println(line)
                            if (line.contains("$")) {
                                val tokens = StringTokenizer(line, "$")
                                question.add(tokens.nextToken())
                                opt1.add(tokens.nextToken())
                                opt2.add(tokens.nextToken())
                                opt3.add(tokens.nextToken())
                                opt4.add(tokens.nextToken())
                                answer.add(tokens.nextToken())
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    return "true"
                }
            } catch (e: AmazonClientException) {
                //mcqActivity.showSnackBar("Network connection error!!", "csv")
                return "error"
            }

        }

        override fun onPostExecute(result: String?) {
            pbQuiz.visibility = View.GONE
            if (result == "true") {
                setQuestion(questionNumber)
            } else if (result == "false") {
                finish()
            }
        }

    }

}
