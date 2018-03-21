package sagsaguz.brillavedicmaths

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import sagsaguz.brillavedicmaths.adapter.VideosListAdapter
import java.util.ArrayList

class DetailsActivity : AppCompatActivity() {

    lateinit var lvDetails : ListView

    lateinit var btnPayNow : Button

    var list1 = ArrayList<String>()
    var list2 = ArrayList<String>()
    var list3 = ArrayList<String>()
    var list4 = ArrayList<String>()
    var list5 = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_layout)

        lvDetails = findViewById(R.id.lvDetails)

        btnPayNow = findViewById(R.id.btnPayNow)

        list1.add("Square of 2 & 3 digit numbers ending with 5")
        list1.add("Square of 2 digit numbers starting with 5")
        list1.add("Multiplying numbers with 11")
        list1.add("Cube root of a Perfect Cube")

        list2.addAll(list1)
        list2.add("Square of any 2 & 3 digit number")
        list2.add("Multiplications: Digits in unit places are adding to 10 and other digits are same")
        list2.add("Multiplying numbers with 111,1111 (series of 1)")
        list2.add("Multiplying 9 to any number having consequitive digits")
        list2.add("Multiplying any number with series of 9 where equal or less digits are there in on the other side")
        list2.add("Criss-Cross Technique - Multiplying any 2 digit numbers")
        list2.add("Doubling")
        list2.add("Doubling 3digit")
        list2.add("Multiplying with 5,25,50")
        list2.add("Halving")
        list2.add("Doubling and Halving together")
        list2.add("Digit Sum")
        list2.add("Circle of 9")
        list2.add("Digit sum group adding to 9")
        list2.add("Fun with 9 point circle")
        list2.add("Digit Sum addition Check")
        list2.add("Digit sum subtraction check")
        list2.add("Digit Sum Checking")
        list2.add("Divisibility Checking")
        list2.add("Deficiency from 10. Deficiency & Completion together")
        list2.add("Mental additions")
        list2.add("Metal addition by Column")
        list2.add("Addition, Subtraction both together (only Voice to be added)")
        list2.add("Quick addition of series of single digit numbers")
        list2.add("Fast Additions/subtraction in a line")

        list3.addAll(list2)
        list3.add("Algebric Proof of Technique 1")
        list3.add("Algebric Proof of Technique 4")
        list3.add("Multiplications: Digits in unit places are same and 10th place digits  adding to 10")
        list3.add("Mutiplying any two numbers between 11-19")
        list3.add("Algebric Proof of Technique 7")
        list3.add("Multiplying any number with series of 9 where more digits are on the otherside")
        list3.add("Square root of a perfect square")
        list3.add("Square roots of  imperfect squares")
        list3.add("Square roots of  imperfect squares")
        list3.add("Quick Division by 9")
        list3.add("Base Method of Division")
        list3.add("One line division - Upto 2 digit divisor")
        list3.add("One line division - 3 or more digits")

        list4.addAll(list3)
        list4.add("Criss-Cross Technique - Multiplying any two 3-digit numbers")
        list4.add("Square of 3-digit numbers")
        list4.add("Square of 4 digit numbers")
        list4.add("Cube of any 2-digit number")
        list4.add("Base multiplication of numbers near the base 100, 200 etc.( 2 digit base multiplication)")
        list4.add("Base Method Division with 2, 3 or more digits in Divisor (but lesser than base)")
        list4.add("Base Method Division with 2, 3 or more digits in Divisor (but greater than base)")
        list4.add("Percentage Reciprocal")
        list4.add("Percentage Compounding")
        list4.add("General Equations")
        list4.add("Factorisation - Vedic Math Technique")
        list4.add("Simultaneous Equations")

        list5.addAll(list4)
        list5.add("Extending Table")
        list5.add("Algebraic Proof of Base method of Multiplication")
        list5.add("Multiplying Numbers near different bases")
        list5.add("Base Multiplication of 3 numbers near the base")
        list5.add("Base multiplication of numbers near 10 base (70 base multiplication)")
        list5.add("Base multiplication of numbers near the base 100, 200 etc.( 2 digit base multiplication)")

        //val listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list5)
        val listAdapter = VideosListAdapter(this, list1)
        lvDetails.adapter = listAdapter

    }

    inner class NotesAdapter : BaseAdapter {

        private var notesList = ArrayList<String>()
        private var context: Context? = null

        constructor(context: Context, notesList: ArrayList<String>) : super() {
            this.notesList = notesList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.list_items, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvTitle.text = notesList[position].title
            vh.tvContent.text = notesList[position].content

            return view
        }

        override fun getItem(position: Int): Any {
            return notesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notesList.size
        }
    }

    private class ViewHolder(view: View?) {
        lateinit var tvSLno: TextView
        lateinit var tvVideoName: TextView

        init {
            this.tvSLno = view?.findViewById(R.id.tvSLno) as TextView
            this.tvVideoName = view?.findViewById(R.id.tvVideoName) as TextView
        }
    }

}
