package com.example.bottomnavyt

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay

private lateinit var firebaseAuth: FirebaseAuth
private var chart: AnyChartView? = null


private val topic = listOf("Income","Expenses")
private lateinit var firebaseDatabase: FirebaseFirestore

class UserProfile : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth=FirebaseAuth.getInstance()
        firebaseDatabase= FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        val incoseniyame = intent.getStringExtra("income")?.toInt()
        val expenses = intent.getStringExtra("expenses")?.toInt()


        val salary = listOf(income!!, expenses!!)
        setContentView(R.layout.activity_user_profile)
        chart = findViewById(R.id.pieChart)
        val email=findViewById<TextView>(R.id.email_text)
        val name=findViewById<TextView>(R.id.name_text)
        name.text = firebaseAuth.currentUser?.displayName
        email.text = firebaseAuth.currentUser?.email
        val logOutButton=findViewById<Button>(R.id.logOut_button)
        val updateButton=findViewById<Button>(R.id.update_button_user_profile)


        updateButton.setOnClickListener{
            var dialog=BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.update_user_name_sheet, null)
            val nameField=view.findViewById<EditText>(R.id.name_edit_field)
            nameField.setText(firebaseAuth.currentUser?.displayName)

            val saveButton=view.findViewById<Button>(R.id.save_name_button)

            saveButton.setOnClickListener {
                updateUser(firebaseAuth.currentUser,nameField.text.toString())
                name.text = nameField.text.toString()
                dialog.cancel()
            }
            val deleteUserButton=view.findViewById<Button>(R.id.delete_account)
            deleteUserButton.setOnClickListener {
                val user = Firebase.auth.currentUser!!
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User account deleted.")
                            Toast.makeText(this,"Delete Successful", Toast.LENGTH_SHORT).show()
                        }
                    }
                val intent=Intent(this,SignIn::class.java)
                startActivity(intent)
            }


            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }
        logOutButton.setOnClickListener {
            firebaseAuth.signOut();
            Toast.makeText(this,"Logout Successful", Toast.LENGTH_SHORT).show()
            val intent=Intent(this@UserProfile,SignIn::class.java)
            startActivity(intent)


        }

        configChartView(salary)


    }
    private fun configChartView(list:List<Int>) {
        val pie : Pie = AnyChart.pie()

        val dataPieChart: MutableList<DataEntry> = mutableListOf()

        for (index in list.indices){
            dataPieChart.add(ValueDataEntry(topic.elementAt(index),list.elementAt(index)))
        }

        pie.data(dataPieChart)
        chart!!.setChart(pie)

    }
    private fun updateUser(user: FirebaseUser?, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder().apply {
            displayName = name
        }.build()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,"Name Changed Successful", Toast.LENGTH_SHORT).show()

            }
        }
    }
}