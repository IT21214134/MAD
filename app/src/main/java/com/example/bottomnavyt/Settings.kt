package com.example.bottomnavyt

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment(),View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseDatabase: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recycleView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var myAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_settings, container, false)
        val btn: FloatingActionButton = view.findViewById(R.id.add_income_button)
        btn.setOnClickListener(this)
        firebaseDatabase= FirebaseFirestore.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()
        loadIncome()
        recycleView=view.findViewById(R.id.incomeRecyclerView)
        recycleView.layoutManager= LinearLayoutManager(container?.context)
        userArrayList= arrayListOf()
        myAdapter= MyAdapter(userArrayList)
        recycleView.adapter=myAdapter
        myAdapter.setOnClickListener(object :MyAdapter.OnClickListener{
            override fun onClick(position: Int, model: User) {

                val dialog = container?.context?.let { BottomSheetDialog(it) }

                val view = layoutInflater.inflate(R.layout.delete_modify_sheet, null)
                val deleteButton=view.findViewById<Button>(R.id.delete_button)
                val updateButton=view.findViewById<Button>(R.id.update_button)
                deleteButton.setOnClickListener{
                    if (model.id!=null){
                        firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("income").collection("income").document(model.id!!).delete()
                        Toast.makeText(context,"Successful Deleted", Toast.LENGTH_SHORT).show()
                        userArrayList.removeAt(position)
                        myAdapter.notifyDataSetChanged()

                    }

                    dialog?.cancel()

                }
                updateButton.setOnClickListener{
                    val view = layoutInflater.inflate(R.layout.activity_bottom_sheet, null)

                    val saveButton=view.findViewById<Button>(R.id.expenses_save_button)
                    val date=view.findViewById<EditText>(R.id.expenses_date_field)
                    val reason=view.findViewById<EditText>(R.id.expenses_reason_field)
                    val amount=view.findViewById<EditText>(R.id.expenses_amount_field)

                   date.setText(model.date).toString()
                    reason.setText(model.reason).toString()
                    amount.setText(model.amount.toString()).toString()

                    saveButton.setOnClickListener{
                        val updates = hashMapOf<String, Any>(
                            "date" to date.text.toString(),
                            "reason" to reason.text.toString(),
                            "amount" to amount.text.toString().toLong()
                        )
                      if (model.id!=null){
                          firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("income").collection("income").document(
                              model.id!!
                          ).update(updates)
                          Toast.makeText(context,"Successful Updated",Toast.LENGTH_SHORT).show()
                          userArrayList[position].amount=amount.text.toString().toLong()
                          userArrayList[position].date=date.text.toString()
                          userArrayList[position].reason=reason.text.toString()
                          myAdapter.notifyDataSetChanged()

                          dialog?.cancel()
                      }
                    }

                    dialog?.setCancelable(true)
                    dialog?.setContentView(view)
                    dialog?.show()


                }


                dialog?.setCancelable(true)
                dialog?.setContentView(view)
                dialog?.show()

            }})




        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("MissingInflatedId")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_income_button -> {

                val dialog = BottomSheetDialog(v.context)
                val view = layoutInflater.inflate(R.layout.activity_bottom_sheet, null)
                val saveButton=view.findViewById<Button>(R.id.expenses_save_button)
                val date=view.findViewById<EditText>(R.id.expenses_date_field)
                val reason=view.findViewById<EditText>(R.id.expenses_reason_field)
                val amount=view.findViewById<EditText>(R.id.expenses_amount_field)
                val cancelButton=view.findViewById<Button>(R.id.expenses_cancel_button)
                cancelButton.setOnClickListener {
                    dialog.cancel()
                }
                saveButton.setOnClickListener {
                    if (date.text.isNotEmpty()&&reason.text.isNotEmpty()&&amount.text.isNotEmpty()){
                        val ref=  firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("income").collection("income")
                        val user = hashMapOf(
                            "date" to date.text.toString(),
                            "reason" to reason.text.toString(),
                            "amount" to amount.text.toString().toLong()
                        )
                        ref.add(user).addOnSuccessListener { documentReference ->
                            ref.document(documentReference.id).update("id",documentReference.id)
                            Toast.makeText(context,"Successful Added",Toast.LENGTH_SHORT).show()
                            dialog.cancel()
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error adding document", e)
                                Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
                            }
                    }
                    else{
                        Toast.makeText(context,"Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
            }


            else -> {
            }
        }
    }
    private fun loadIncome(){

        firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("income").collection("income").addSnapshotListener(object :
            EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error!=null){
                    Log.e("err",error.message.toString())
                    Toast.makeText(context,"No Data Found",Toast.LENGTH_SHORT).show()
                    return
                }
                for (doc : DocumentChange in value?.documentChanges!! ){
                  println(  doc.document.id)
                    if (doc.type== DocumentChange.Type.ADDED){
                        userArrayList.add(doc.document.toObject(User::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()

            }

        })

    }
}