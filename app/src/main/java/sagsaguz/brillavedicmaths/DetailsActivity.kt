package sagsaguz.brillavedicmaths

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.*
import com.amazonaws.AmazonClientException
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import com.payumoney.core.PayUmoneyConfig
import com.payumoney.core.PayUmoneyConstants
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.ResultModel
import sagsaguz.brillavedicmaths.utils.AWSProvider
import sagsaguz.brillavedicmaths.utils.BrillaVMUsersDO
import sagsaguz.brillavedicmaths.utils.Config
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class DetailsActivity : AppCompatActivity() {

    lateinit var rlDetails : RelativeLayout

    lateinit var category : ImageView
    lateinit var lvDetails : ListView
    lateinit var btnPayNow : Button
    lateinit var btnRegister : Button
    lateinit var btnFree : Button
    lateinit var btnViewCourse : Button
    lateinit var tvText : TextView
    lateinit var btnNumber : Button

    var videoNameList = ArrayList<Int>()
    var imageList = ArrayList<Int>()
    var priceList = ArrayList<Int>()
    var nameList = ArrayList<String>()

    var skillList = ArrayList<String>()

    var pos = 0

    var email : String? = null
    var phone : String? = null
    var name : String? = null
    var age : String? = null
    var city : String? = null
    var password : String? = null

    lateinit var userPreferences: SharedPreferences

    private lateinit var mPaymentParams: PayUmoneySdkInitializer.PaymentParam

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_layout)

        pos = intent.getIntExtra("category", 0)

        rlDetails = findViewById(R.id.rlDetails)

        category = findViewById(R.id.category)
        lvDetails = findViewById(R.id.lvDetails)
        btnPayNow = findViewById(R.id.btnPayNow)
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.visibility = View.GONE
        btnFree = findViewById(R.id.btnFree)
        btnViewCourse = findViewById(R.id.btnViewCourse)
        btnViewCourse.visibility = View.GONE

        skillList.add("5 Skills")
        skillList.add("30 easy to learn skills")
        skillList.add("40 important skills")
        skillList.add("50 valuable skills")
        skillList.add("60 essential skills")

        btnNumber = findViewById(R.id.btnNumber)
        btnNumber.text = skillList[pos]

        userPreferences = getSharedPreferences("USERDETAILS", Context.MODE_PRIVATE)
        /*if (userPreferences.getString("LOGIN", "logout") == "login"){
            btnRegister.visibility = View.GONE
        } else{
            btnPayNow.visibility = View.GONE
        }*/

        email = userPreferences.getString("EMAIL", "null")
        phone = userPreferences.getString("PHONE", "null")
        name = userPreferences.getString("NAME", "null")
        age = userPreferences.getString("AGE", "null")
        city = userPreferences.getString("CITY", "null")
        password = userPreferences.getString("PASSWORD", "null")

        tvText = findViewById(R.id.tvText)
        if (pos == 0) {
            tvText.visibility = View.GONE
            btnPayNow.visibility = View.GONE
            //btnRegister.visibility = View.GONE
            btnFree.visibility = View.VISIBLE
            btnViewCourse.visibility = View.GONE
        } else{
            btnFree.visibility = View.GONE
        }

        btnFree.setOnClickListener {
            if (userPreferences.getString("LOGIN", "logout") == "login"){
                val intent = Intent(baseContext, VideoListActivity::class.java)
                intent.putExtra("category", pos)
                startActivity(intent)
                finish()
            } else{
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }

        btnPayNow.setOnClickListener {
            if (userPreferences.getString("LOGIN", "logout") == "login"){
                launchPayUMoneyFlow()
            } else{
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }

        /*btnRegister.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }*/

        btnViewCourse.setOnClickListener {
            if (userPreferences.getString("LOGIN", "logout") == "login"){
                val intent = Intent(baseContext, VideoListActivity::class.java)
                intent.putExtra("category", pos)
                startActivity(intent)
                finish()
            } else{
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }

        nameList.add("sprouts")
        nameList.add("blossom")
        nameList.add("garden")
        nameList.add("estate")
        nameList.add("orchards")

        val categoryName = userPreferences.getString("CATEGORY", "sprouts")

        val n = nameList.indexOf(categoryName)
        if (pos<=n)
            btnPayNow.visibility = View.GONE

        if (categoryName == nameList[pos]) {
            if (categoryName == "sprouts") {
                btnViewCourse.visibility = View.GONE
            } else {
                btnViewCourse.visibility = View.VISIBLE
            }
        }

        videoNameList.add(R.array.sprouts)
        videoNameList.add(R.array.blossoms)
        videoNameList.add(R.array.garden)
        videoNameList.add(R.array.estate)
        videoNameList.add(R.array.orchard)

        imageList.add(R.drawable.sprouts_d)
        imageList.add(R.drawable.blossoms_d)
        imageList.add(R.drawable.garden_d)
        imageList.add(R.drawable.estate_d)
        imageList.add(R.drawable.orchards_d)

        priceList.add(0)
        priceList.add(299)
        priceList.add(499)
        priceList.add(799)
        priceList.add(999)

        category.setImageResource(imageList[pos])

        val mFacts = resources.getStringArray(videoNameList[pos])
        val list = ArrayList(Arrays.asList(*mFacts))

        val listAdapter = ListAdapter(this, list)
        lvDetails.adapter = listAdapter

    }

    private fun basicSnackBar(message: String) {
        val snackbar = Snackbar.make(rlDetails, message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNavy))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    private fun launchPayUMoneyFlow() {
        val payUmoneyConfig = PayUmoneyConfig.getInstance()

        payUmoneyConfig.doneButtonText = "Done Button"

        payUmoneyConfig.payUmoneyActivityTitle = "Payment"

        val builder = PayUmoneySdkInitializer.PaymentParam.Builder()
        val txnId = System.currentTimeMillis().toString() + ""
        //val amount = java.lang.Double.parseDouble(priceList[pos].toString())
        val amount = java.lang.Double.parseDouble("1")
        val phone = phone
        val productName = "Brilla Vedic Maths - "+ nameList[pos]
        val firstName = name
        val email = email
        val udf1 = ""
        val udf2 = ""
        val udf3 = ""
        val udf4 = ""
        val udf5 = ""
        val udf6 = ""
        val udf7 = ""
        val udf8 = ""
        val udf9 = ""
        val udf10 = ""

        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(true)
                .setKey("7oYrm7US")
                .setMerchantId("5761531")

        try {
            mPaymentParams = builder.build()
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams)

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, this, R.style.AppTheme_default, false)

        } catch (e: Exception) {
            Toast.makeText(this, "Exception : " + e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun calculateServerSideHashAndInitiatePayment1(paymentParam: PayUmoneySdkInitializer.PaymentParam): PayUmoneySdkInitializer.PaymentParam {

        val stringBuilder = StringBuilder()
        val params = paymentParam.params
        stringBuilder.append(params[PayUmoneyConstants.KEY]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.TXNID]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.AMOUNT]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.PRODUCT_INFO]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.FIRSTNAME]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.EMAIL]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF1]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF2]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF3]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF4]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF5]).append("||||||")
        stringBuilder.append("zbk4YZzVY9")

        val hash = hashCal(stringBuilder.toString())
        paymentParam.setMerchantHash(hash)

        return paymentParam
    }

    fun hashCal(str: String): String {
        val hashseq = str.toByteArray()
        val hexString = StringBuilder()
        try {
            val algorithm = MessageDigest.getInstance("SHA-512")
            algorithm.reset()
            algorithm.update(hashseq)
            val messageDigest = algorithm.digest()
            for (aMessageDigest in messageDigest) {
                val hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
                if (hex.length == 1) {
                    hexString.append("0")
                }
                hexString.append(hex)
            }
        } catch (ignored: NoSuchAlgorithmException) {
            Toast.makeText(this, "Exception : " + ignored.message, Toast.LENGTH_SHORT).show()
        }

        return hexString.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code $requestCode resultcode $resultCode")
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK && data != null) {
            val transactionResponse = data.getParcelableExtra<TransactionResponse>(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE)

            val resultModel = data.getParcelableExtra<ResultModel>(PayUmoneyFlowManager.ARG_RESULT)


            if (transactionResponse?.getPayuResponse() != null) {
                if (transactionResponse.transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                    basicSnackBar("Successful Transaction")
                    UpdateCategory().execute()
                } else {
                    basicSnackBar("Failure Transaction")
                }

                // Response from Payumoney
                val payuResponse = transactionResponse.getPayuResponse()

                // Response from SURl and FURL
                val merchantResponse = transactionResponse.transactionDetails

                /*new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/

            } else if (resultModel != null && resultModel.error != null) {
                Toast.makeText(this, "Error : " + resultModel.error.transactionResponse, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Both Objects are Null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class ListAdapter : BaseAdapter {

        private var list = ArrayList<String>()
        private var context: Context? = null
        private var inflater: LayoutInflater? = null

        constructor(context: Context, notesList: ArrayList<String>) : super() {
            this.list = notesList
            this.context = context
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val holder = Holder()
            val rowView = inflater!!.inflate(R.layout.list_items, null)
            holder.tvSLno = rowView.findViewById(R.id.tvSLno)
            holder.tvSLno!!.text = (position+1).toString()
            holder.tvVideoName = rowView.findViewById(R.id.tvVideoName)
            holder.tvVideoName!!.text = list[position]

            return rowView
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list.size
        }

        private inner class Holder {
            internal var tvSLno: TextView? = null
            internal var tvVideoName: TextView? = null
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class UpdateCategory : AsyncTask<Void, Void, String>() {

        private var brillaVMUsersDo = BrillaVMUsersDO()

        override fun onPreExecute() {
            //pbForm.visibility = View.VISIBLE
            brillaVMUsersDo.phone(phone.toString())
            brillaVMUsersDo.email(email.toString())
            brillaVMUsersDo.name(name.toString())
            brillaVMUsersDo.age(age.toString())
            brillaVMUsersDo.city(city.toString())
            brillaVMUsersDo.password(password.toString())
        }

        override fun doInBackground(vararg p0: Void?): String {

            val awsProvider = AWSProvider()
            val dynamoDBClient = AmazonDynamoDBClient(awsProvider.getCredentialsProvider(baseContext))
            dynamoDBClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1))
            val dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .build()

            try {

                dynamoDBMapper.delete(brillaVMUsersDo)

                brillaVMUsersDo.category(nameList[pos])
                dynamoDBMapper.save(brillaVMUsersDo)
                return "success"

            } catch (e : AmazonClientException){
                return "error"
            }
        }

        override fun onPostExecute(result: String?) {
            //pbForm.visibility = View.GONE
            if (result == "success") {
                val editor = userPreferences.edit()
                editor.putString("CATEGORY", nameList[pos])
                editor.apply()
                val intent = Intent(baseContext, VideoListActivity::class.java)
                intent.putExtra("category", pos)
                startActivity(intent)
                finish()
            }
        }

    }

}
