package sagsaguz.brillavedicmaths

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import kotlinx.android.synthetic.main.video_item.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import sagsaguz.brillavedicmaths.utils.AWSProvider
import sagsaguz.brillavedicmaths.utils.BrillaVMUsersDO
import sagsaguz.brillavedicmaths.utils.Config
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class VideoListActivity : AppCompatActivity() {

    lateinit var btnUpgradeNow : Button
    lateinit var lvVideosList : ListView
    lateinit var rlVideoList : RelativeLayout
    lateinit var pbVideoList : ProgressBar

    var videoNameList = ArrayList<Int>()
    var videoCodeList = ArrayList<Int>()

    lateinit var progressDialog : ProgressDialog
    var pdfFiles = ArrayList<String>()
    var quizFiles = ArrayList<String>()
    var pos : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_list_layout)

        btnUpgradeNow = findViewById(R.id.btnUpgradeNow)
        lvVideosList = findViewById(R.id.lvVideosList)
        rlVideoList = findViewById(R.id.rlVideoList)
        pbVideoList = findViewById(R.id.pbVideoList)
        pbVideoList.visibility = View.GONE

        progressDialog = ProgressDialog(this, R.style.MyAlertDialogStyle)

        btnUpgradeNow.setOnClickListener { startActivity(Intent(applicationContext, HomePageActivity::class.java)) }

        videoNameList.add(R.array.sprouts)
        videoNameList.add(R.array.blossoms)
        videoNameList.add(R.array.garden)
        videoNameList.add(R.array.estate)
        videoNameList.add(R.array.orchard)

        videoCodeList.add(R.array.cSprouts)
        videoCodeList.add(R.array.cBlossoms)
        videoCodeList.add(R.array.cGarden)
        videoCodeList.add(R.array.cEstate)
        videoCodeList.add(R.array.cOrchard)

        pos = intent.getIntExtra("category", 0)

        if (pos==4)
            btnUpgradeNow.visibility = View.GONE

        PdfQuizList().execute()

    }

    private fun basicSnackBar(message: String) {
        val snackbar = Snackbar.make(rlVideoList, message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNavy))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    private fun showSnackBar(message: String, type: String, fileName: String) {
        val snackbar = Snackbar.make(rlVideoList, message, Snackbar.LENGTH_SHORT)
                .setAction(type) {
                    if (type == "Try Again") {
                        PdfQuizList().execute()
                    } else {
                        openFolder(fileName)
                    }
                }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNavy))
        val textView = sbView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        if (type == "Try Again") {
            snackbar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
        } else {
            snackbar.duration = BaseTransientBottomBar.LENGTH_LONG
        }
        snackbar.show()
    }

    private fun openFolder(fileName: String) {
        val target = Intent(Intent.ACTION_VIEW)
        val file = File(Environment.getExternalStorageDirectory().toString() + "/BrillaVM/pdf/", fileName)
        val uri = Uri.fromFile(file)
        target.setDataAndType(uri,"application/pdf")
        try {
            startActivity(Intent.createChooser(target, "Open File Using"))
        } catch (e: ActivityNotFoundException) {
            basicSnackBar("Please download pdf reader.")
        }
    }

    inner class VideoListAdapter : BaseAdapter {

        private var list = ArrayList<String>()
        private var clist = ArrayList<String>()
        private var context: Context? = null
        private var inflater: LayoutInflater? = null

        constructor(context: Context, listNames: ArrayList<String>, listCodes: ArrayList<String>) : super() {
            this.list = listNames
            this.clist = listCodes
            this.context = context
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val holder = Holder()
            val rowView = inflater!!.inflate(R.layout.video_item, null)
            holder.tvSLno = rowView.findViewById(R.id.tvSLno)
            holder.tvSLno!!.text = (position+1).toString()
            holder.tvVideoName = rowView.findViewById(R.id.tvVideoName)
            holder.tvVideoName!!.text = list[position]

            holder.btnLearn = rowView.findViewById(R.id.btnLearn)
            holder.btnPractise = rowView.findViewById(R.id.btnPractise)
            holder.btnDownloadPDF = rowView.findViewById(R.id.btnDownloadPDF)

            if ((list[position] == "Course Introduction") || (list[position] == "Learning Outcome")){
                holder.btnPractise!!.visibility = View.GONE
                holder.btnDownloadPDF!!.visibility = View.GONE
            }

            if (quizFiles.contains(clist[position])){
                holder.btnPractise!!.visibility = View.VISIBLE
            } else{
                holder.btnPractise!!.visibility = View.GONE
            }

            if (pdfFiles.contains(clist[position])){
                holder.btnDownloadPDF!!.visibility = View.VISIBLE
            } else{
                holder.btnDownloadPDF!!.visibility = View.GONE
            }

            val btn1 = holder.btnLearn as Button
            val btn2 = holder.btnPractise as Button
            val btn3 = holder.btnDownloadPDF as Button

            btn1.setOnClickListener {
                val intent = Intent(context, VideoViewActivity::class.java)
                intent.putExtra("VideoCode", clist[position])
                startActivity(intent)
                /*when {
                    list[position] == "Introduction" -> intent.putExtra("VideoCode", "Introduction")
                    list[position] == "Learning Outcome" -> intent.putExtra("VideoCode", "Learning Outcome")
                    else -> intent.putExtra("VideoCode", clist[position])
                }*/
            }

            btn2.setOnClickListener {
                val intent = Intent(context, QuizActivity::class.java)
                intent.putExtra("QuizCode", clist[position])
                startActivity(intent)
            }

            btn3.setOnClickListener {
                val str =  list[position]
                val cstr =  clist[position]
                storagePermissionCheck(str, cstr)
            }

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
            internal var btnLearn: Button? = null
            internal var btnPractise: Button? = null
            internal var btnDownloadPDF: Button? = null
        }

        @SuppressLint("InflateParams")
        private fun storagePermissionCheck(str: String, cstr: String){

            val permissionCheck = ContextCompat.checkSelfPermission(this@VideoListActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                DownloadPdf().execute(str+".pdf", cstr+".pdf")
            } else {

                val dialogBuilder = AlertDialog.Builder(this@VideoListActivity)
                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.permission_dialog, null)
                dialogBuilder.setView(dialogView)

                val b = dialogBuilder.create()

                val dialog_message = dialogView.findViewById<View>(R.id.dialog_message) as TextView
                dialog_message.text = "This app needs storage permission for downloading PDF files."
                val pCancel = dialogView.findViewById<View>(R.id.pCancel) as TextView
                val pSettings = dialogView.findViewById<View>(R.id.pSettings) as TextView
                val pOk = dialogView.findViewById<View>(R.id.pOk) as TextView
                pCancel.setOnClickListener {
                    b.dismiss()
                }
                pSettings.setOnClickListener {
                    b.dismiss()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                pOk.setOnClickListener {
                    b.dismiss()
                    ActivityCompat.requestPermissions(this@VideoListActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
                }

                b.show()

            }
        }

    }


    @SuppressLint("StaticFieldLeak")
    inner class PdfQuizList : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            pbVideoList.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg str: String?): Boolean {

            val myCredentials = BasicAWSCredentials(Config.ACCESSKEY, Config.SECRETKEY)
            val s3client = AmazonS3Client(myCredentials)

            try {
                val listing = s3client.listObjects(Config.BUCKETNAME, "pdf")
                val summaries = listing.objectSummaries

                for (objectSummary in summaries) {
                    val key = objectSummary.key
                    try {
                        val s = key.substring(key.indexOf("/") + 1, key.indexOf("."))
                        pdfFiles.add(s)
                    } catch (e: Exception) {
                        Log.d("Repeat", "repeated exception")
                    }

                }
                return true
            } catch (e: AmazonClientException) {
                showSnackBar("Network connection error!!", "Try Again", "empty")
                return false
            }

        }

        override fun onPostExecute(result: Boolean?) {
            pbVideoList.visibility = View.GONE
            if (result!!) {
                QuizList().execute()
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class QuizList : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            pbVideoList.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg str: String?): Boolean {

            val myCredentials = BasicAWSCredentials(Config.ACCESSKEY, Config.SECRETKEY)
            val s3client = AmazonS3Client(myCredentials)

            try {
                val pdfListing = s3client.listObjects(Config.BUCKETNAME, "pdf")
                val pdfSummaries = pdfListing.objectSummaries

                for (objectSummary in pdfSummaries) {
                    val key = objectSummary.key
                    try {
                        val s = key.substring(key.indexOf("/") + 1, key.indexOf("."))
                        pdfFiles.add(s)
                    } catch (e: Exception) {
                        Log.d("Repeat", "repeated exception")
                    }

                }

                val quizListing = s3client.listObjects(Config.BUCKETNAME, "quiz")
                val quizSummaries = quizListing.objectSummaries

                for (objectSummary in quizSummaries) {
                    val key = objectSummary.key
                    try {
                        val s = key.substring(key.indexOf("/") + 1, key.indexOf("."))
                        quizFiles.add(s)
                    } catch (e: Exception) {
                        Log.d("Repeat", "repeated exception")
                    }

                }
                return true
            } catch (e: AmazonClientException) {
                showSnackBar("Network connection error!!", "Try Again", "empty")
                return false
            }

        }

        override fun onPostExecute(result: Boolean?) {
            pbVideoList.visibility = View.GONE
            if (result!!) {
                val mNames = resources.getStringArray(videoNameList[pos])
                val nList = ArrayList(Arrays.asList(*mNames))
                val mCodes = resources.getStringArray(videoCodeList[pos])
                val cList = ArrayList(Arrays.asList(*mCodes))
                val videosListAdapter = VideoListAdapter(baseContext, nList, cList)
                lvVideosList.adapter = videosListAdapter
            }
        }

    }


    @SuppressLint("StaticFieldLeak")
    inner class DownloadPdf : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            progressDialog.setMessage("Downloading pdf, please wait...")
            progressDialog.show()
        }

        override fun doInBackground(vararg str: String?): Boolean {

            val path = Environment.getExternalStorageDirectory()
            val dir = File(path.toString() + "/BrillaVM/pdf/")
            dir.mkdirs()
            val filePath = path.toString() + "/BrillaVM/pdf/"+str[0]

            val credentials = BasicAWSCredentials(Config.ACCESSKEY, Config.SECRETKEY)
            val s3Client = AmazonS3Client(credentials)
            s3Client.setRegion(Region.getRegion(Regions.AP_SOUTH_1))

            val transferUtility = TransferUtility.builder().context(applicationContext)
                    .s3Client(s3Client)
                    .build()

            val downloadObserver = transferUtility.download(
                    "brillavm/pdf", str[1],
                    File(filePath))
            downloadObserver.setTransferListener(object : TransferListener {

                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED === state) {
                        progressDialog.dismiss()
                        showSnackBar("Download Successful\nFile path: File Manager/BrillaVM/pdf", "View", str[0].toString())
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()

                    //Log.d(FragmentActivity.TAG, "   ID:$id   bytesCurrent: $bytesCurrent   bytesTotal: $bytesTotal $percentDone%")
                }

                override fun onError(id: Int, ex: Exception) {
                    progressDialog.dismiss()
                    basicSnackBar("Download failed, please try again")
                    ex.printStackTrace()
                }

            })

            return true
        }

        override fun onPostExecute(result: Boolean?) {
        }

    }

}
