package sagsaguz.brillavedicmaths.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask

import java.util.Properties

import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

//Class is extending AsyncTask because this class is going to perform a networking operation
class SendEMail
//ProgressDialog to show while sending email
// private ProgressDialog progressDialog;

//Class Constructor
(//Declaring Variables
        @field:SuppressLint("StaticFieldLeak")
        private val context: Context, //Information to send email
        private val email: String, private val subject: String, private val message: String, private val password: String)//Initializing variables
    : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void): Void? {
        //Creating properties
        val props = Properties()

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")

        //Creating a new session
        val session = Session.getDefaultInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication? {
                        return PasswordAuthentication("you email", "your password")
                    }
                })
        try {
            //Creating MimeMessage object
            val mm = MimeMessage(session)
            //Setting sender address
            mm.setFrom(InternetAddress("your email"))
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            //Adding subject
            mm.subject = subject
            //Adding message and Password
            mm.setText("$message\nLoginId : $email\nPassword : $password")
            //Sending email
            Transport.send(mm)

        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        return null
    }
}
