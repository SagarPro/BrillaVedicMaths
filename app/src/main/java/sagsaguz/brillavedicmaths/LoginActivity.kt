package sagsaguz.brillavedicmaths

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.amazonaws.AmazonClientException
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import sagsaguz.brillavedicmaths.utils.AWSProvider
import sagsaguz.brillavedicmaths.utils.BrillaVMUsersDO
import sagsaguz.brillavedicmaths.utils.Config
import sagsaguz.brillavedicmaths.utils.SendEMail

class LoginActivity : AppCompatActivity() {

    lateinit var etEmailAddress : EditText
    lateinit var etPassword : EditText
    lateinit var tvForgotPassword : TextView
    lateinit var btnLogin : Button
    lateinit var pbLogin : ProgressBar
    lateinit var rlLogin : RelativeLayout
    lateinit var tvRegister : TextView

    lateinit var userPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        userPreferences = getSharedPreferences("USERDETAILS", Context.MODE_PRIVATE)

        rlLogin = findViewById(R.id.rlLogin)

        etEmailAddress = findViewById(R.id.etEmailAddress)
        etPassword = findViewById(R.id.etPassword)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        btnLogin = findViewById(R.id.btnLogin)
        pbLogin = findViewById(R.id.pbLogin)
        pbLogin.indeterminateDrawable.setColorFilter(resources.getColor(R.color.colorNavy), android.graphics.PorterDuff.Mode.MULTIPLY)
        pbLogin.visibility = View.GONE

        tvRegister = findViewById(R.id.tvRegister)

        tvForgotPassword.setOnClickListener {
            if (TextUtils.isEmpty(etEmailAddress.text.toString())){
                basicSnackBar("Please enter your registered email address")
            } else {
                PasswordRecovery().execute(etEmailAddress.text.toString())
            }
        }

        btnLogin.setOnClickListener {
            login()
        }

        tvRegister.setOnClickListener { startActivity(Intent(applicationContext, ApplicantFormActivity::class.java)) }

    }

    private fun basicSnackBar(message: String) {
        val snackbar = Snackbar.make(rlLogin, message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNavy))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(rlLogin, message, Snackbar.LENGTH_SHORT)
                .setAction("Try Again") {
                    login()
                }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNavy))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        snackbar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
        snackbar.show()
    }

    private fun login() {
        if (TextUtils.isEmpty(etEmailAddress.text.toString()) || TextUtils.isEmpty(etPassword.text.toString())) {
            basicSnackBar("Please fill your login details.")
        } else {
            ValidateUser().execute(etEmailAddress.text.toString(), etPassword.text.toString())
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class ValidateUser : AsyncTask<String, Void, Boolean>() {

        internal var uEmail: String? = null
        internal var exception: Boolean? = null
        internal var brillavmDo:BrillaVMUsersDO? = null

        override fun onPreExecute() {
            if ( pbLogin.visibility == View.GONE)
                pbLogin.visibility = View.VISIBLE
            exception = false
            brillavmDo = BrillaVMUsersDO()
        }

        override fun doInBackground(vararg strings: String): Boolean? {

            uEmail = strings[0]

            val awsProvider = AWSProvider()
            val dynamoDBClient = AmazonDynamoDBClient(awsProvider.getCredentialsProvider(baseContext))
            dynamoDBClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1))

            try {
                val request = ScanRequest().withTableName(Config.USERTABLE)
                val response = dynamoDBClient.scan(request)
                val userRows = response.items
                for (map in userRows) {
                    if (map["email"]!!.s == strings[0]) {
                        if (map["password"]!!.s == strings[1]) {
                            val editor = userPreferences.edit()
                            editor.putString("LOGIN", "login")
                            editor.putString("EMAIL", map["email"]!!.s)
                            editor.putString("NAME", map["name"]!!.s)
                            editor.putString("PHONE", map["phone"]!!.s)
                            editor.putString("AGE", map["age"]!!.s)
                            editor.putString("CITY", map["city"]!!.s)
                            editor.putString("PASSWORD", map["password"]!!.s)
                            editor.putString("CATEGORY", map["category"]!!.s)
                            editor.apply()
                            return true
                        }
                    }
                }
            } catch (e: AmazonClientException) {
                exception = true
                showSnackBar("Network connection error!!")
            }

            return false
        }

        override fun onPostExecute(loginResult: Boolean?) {
            if (pbLogin.visibility == View.VISIBLE)
                pbLogin.visibility = View.GONE
            if ((!exception!!)) {
                if (loginResult!!) {
                    startActivity(Intent(baseContext, HomePageActivity::class.java))
                } else {
                    basicSnackBar("Please check your login details and try again")
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class PasswordRecovery : AsyncTask<String, Void, Boolean>() {

        //lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            pbLogin.visibility = View.VISIBLE
            /*progressDialog = ProgressDialog(baseContext, R.style.MyAlertDialogStyle)
            progressDialog.setMessage("Sending password, please wait.")
            progressDialog.show()*/
        }

        override fun doInBackground(vararg strings: String?): Boolean {
            val awsProvider = AWSProvider()
            val dynamoDBClient = AmazonDynamoDBClient(awsProvider.getCredentialsProvider(baseContext))
            dynamoDBClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1))
            try {
                val request = ScanRequest().withTableName(Config.USERTABLE)
                val response = dynamoDBClient.scan(request)
                val userRows = response.items
                for (map in userRows) {
                    try {
                        if (map["email"]!!.s == strings[0]) {
                            sendEmail(strings[0].toString(), map["password"]!!.s)
                            return true
                        }
                    } catch (e: NumberFormatException) {
                        Log.d("number_format_exception", e.message)
                    }

                }
            } catch (e: AmazonClientException) {
                showSnackBar("Network connection error!!")
                return false
            }

            return false
        }

        override fun onPostExecute(result: Boolean?) {
            pbLogin.visibility = View.GONE
            //progressDialog.dismiss()
            if (!result!!) {
                basicSnackBar("Please enter your registered email address and try again.")
            } else {
                basicSnackBar("Password sent to your email.")
            }
        }

    }

    private fun sendEmail(email: String, password: String) {
        val subject = "Forgot Password from Brilla MathsUp"
        val message = "Dear Math Enthusiast,\nIt looks like you have forgotten your password.\n\nKindly find your login credentials below,"
        //Creating SendMail object
        val sm = SendEMail(this@LoginActivity, email, subject, message, password)
        sm.execute()
    }

}
