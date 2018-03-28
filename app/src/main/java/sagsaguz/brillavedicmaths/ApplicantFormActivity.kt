package sagsaguz.brillavedicmaths

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.amazonaws.AmazonClientException
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import sagsaguz.brillavedicmaths.utils.AWSProvider
import sagsaguz.brillavedicmaths.utils.BrillaVMUsersDO
import sagsaguz.brillavedicmaths.utils.Config
import java.util.regex.Pattern

class ApplicantFormActivity : AppCompatActivity() {

    private lateinit var rlForm : RelativeLayout

    lateinit var etFullName : EditText
    lateinit var etEmail : EditText
    lateinit var etPhone : EditText
    lateinit var etAge : EditText
    lateinit var etCity : EditText
    lateinit var etPassword : EditText

    private lateinit var btnRegister : Button

    lateinit var pbForm : ProgressBar

    lateinit var dynamoDBClient : AmazonDynamoDBClient
    lateinit var dynamoDBMapper : DynamoDBMapper

    lateinit var userPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.applicant_form_layout)

        userPreferences = getSharedPreferences("USERDETAILS", Context.MODE_PRIVATE)

        rlForm = findViewById(R.id.rlForm)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAge = findViewById(R.id.etAge)
        etCity = findViewById(R.id.etCity)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            validation()
        }

        pbForm = findViewById(R.id.pbForm)
        pbForm.indeterminateDrawable.setColorFilter(resources.getColor(R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY)
        pbForm.visibility = View.GONE

        val awsProvider = AWSProvider()
        dynamoDBClient = AmazonDynamoDBClient(awsProvider.getCredentialsProvider(baseContext))
        dynamoDBClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1))
        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .build()

    }

    private fun validation(){
        val validatedResult = validateUserDetails()
        if (validatedResult){
            RegisterUser().execute()
        }
    }

    private fun validateUserDetails(): Boolean {
        if (TextUtils.isEmpty(etFullName.text.toString()) ||
            TextUtils.isEmpty(etEmail.text.toString()) ||
            TextUtils.isEmpty(etPhone.text.toString()) ||
            TextUtils.isEmpty(etAge.text.toString()) ||
            TextUtils.isEmpty(etCity.text.toString()) ||
                TextUtils.isEmpty(etPassword.text.toString())) {
            basicSnackBar("Please enter your details")
            return false
        }

        if (etPassword.length() < 7){
            basicSnackBar("Enter 7 or more characters for password")
            return false
        }

        return if (isValidEmail()){
            if (etPhone.length()==10){
                true
            } else {
                basicSnackBar("Enter valid 10 digit phone number")
                false
            }
        } else {
            basicSnackBar("Enter your valid email address")
            false
        }
    }

    private fun isValidEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val patternEmail = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE)
        val matcherEmail = patternEmail.matcher(etEmail.text.toString())
        return matcherEmail.find()
    }

    fun basicSnackBar(message: String) {
        val snackbar = Snackbar.make(rlForm, message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorWhite))
        snackbar.show()
    }

    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(rlForm, message, Snackbar.LENGTH_SHORT)
                .setAction("Try Again") { validation() }
        snackbar.setActionTextColor(ContextCompat.getColor(baseContext, R.color.colorAccent))

        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(baseContext, R.color.colorWhite))
        snackbar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
        snackbar.show()
    }

    @SuppressLint("StaticFieldLeak")
    inner class RegisterUser : AsyncTask<Void, Void, String>() {

        private var brillaVMUsersDo = BrillaVMUsersDO()

        override fun onPreExecute() {
            pbForm.visibility = View.VISIBLE
            brillaVMUsersDo.phone(etPhone.text.toString())
            brillaVMUsersDo.email(etEmail.text.toString())
            brillaVMUsersDo.name(etFullName.text.toString())
            brillaVMUsersDo.age(etAge.text.toString())
            brillaVMUsersDo.city(etCity.text.toString())
            brillaVMUsersDo.password(etPassword.text.toString())
            brillaVMUsersDo.category("sprouts")
        }

        override fun doInBackground(vararg p0: Void?): String {
            try {
                var result: ScanResult? = null
                do {
                    val req = ScanRequest()
                    req.tableName = Config.USERTABLE
                    if (result != null) {
                        req.exclusiveStartKey = result.lastEvaluatedKey
                    }
                    result = dynamoDBClient.scan(req)
                    val rows = result.items
                    for (map in rows) {
                        try {
                            if (map["phone"]!!.s == brillaVMUsersDo.phone) {
                                return "failed"
                            }
                            if (map["email"]!!.s == brillaVMUsersDo.email) {
                                return "failed"
                            }
                        } catch (e: NumberFormatException) {
                            println(e.message)
                        }
                    }
                } while (result!!.lastEvaluatedKey != null)

                dynamoDBMapper.save(brillaVMUsersDo)
                return "success"
            } catch (e : AmazonClientException){
                showSnackBar("Network connection error!!")
                return "error"
            }
        }

        override fun onPostExecute(result: String?) {
            pbForm.visibility = View.GONE
            if (result == "success") {
                val editor = userPreferences.edit()
                editor.putString("LOGIN", "login")
                editor.putString("EMAIL", etEmail.text.toString())
                editor.putString("NAME", etFullName.text.toString())
                editor.putString("PHONE", etPhone.text.toString())
                editor.putString("AGE", etAge.text.toString())
                editor.putString("CITY", etCity.text.toString())
                editor.putString("PASSWORD", etPassword.text.toString())
                editor.putString("CATEGORY", "sprouts")
                editor.apply()
                startActivity(Intent(applicationContext, HomePageActivity::class.java))
                val loginActivity = LoginActivity()
                loginActivity.finish()
                finish()
            } else if (result == "failed") {
                basicSnackBar("User with this details is already registered")
            }
        }

    }
}
