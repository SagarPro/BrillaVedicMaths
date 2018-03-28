package sagsaguz.brillavedicmaths

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

class HomePageActivity : AppCompatActivity(), View.OnClickListener{

    lateinit var llSprouts : LinearLayout
    lateinit var llBlossoms : LinearLayout
    lateinit var llGarden : LinearLayout
    lateinit var llEstate : LinearLayout
    lateinit var llOrchards : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page_layout)

        llSprouts = findViewById(R.id.llSprouts)
        llBlossoms = findViewById(R.id.llBlossoms)
        llGarden = findViewById(R.id.llGarden)
        llEstate = findViewById(R.id.llEstate)
        llOrchards = findViewById(R.id.llOrchards)

        llSprouts.setOnClickListener(this)
        llBlossoms.setOnClickListener(this)
        llGarden.setOnClickListener(this)
        llEstate.setOnClickListener(this)
        llOrchards.setOnClickListener(this)

    }

    override fun onClick(view: View?) {

        val intent = Intent(applicationContext, DetailsActivity::class.java)

        when(view?.id){
            R.id.llSprouts -> {
                intent.putExtra("category", 0)
            }
            R.id.llBlossoms -> {
                intent.putExtra("category", 1)
            }
            R.id.llGarden -> {
                intent.putExtra("category", 2)
            }
            R.id.llEstate -> {
                intent.putExtra("category", 3)
            }
            R.id.llOrchards -> {
                intent.putExtra("category", 4)
            }
        }

        startActivity(intent)

    }
}
