package com.example.bottomnavyt

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bottomnavyt.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment(),View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseDatabase: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recycleView: RecyclerView
    private lateinit var recycleViewIncome: RecyclerView
    private lateinit var incomeArrayList: ArrayList<User>
    private lateinit var expensesArrayList: ArrayList<User>
    private lateinit var myAdapterExpenses: MyAdapter
    private lateinit var myAdapterIncome: MyAdapter
    private var _binding: FragmentHomeBinding? = null
    private var income=0
   private var expenses=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        } }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        firebaseDatabase= FirebaseFirestore.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()
        loadIncome()
        loadExpenses()
        expensesAmount()
        incomeAmount()
        recycleView=view.findViewById(R.id.listMode)
        var profileButton=view.findViewById<FloatingActionButton>(R.id.personButton)
        profileButton.setOnClickListener(this)
        var addIncomeOrExpensesButton=view.findViewById<FloatingActionButton>(R.id.add_expenses_income_button)
        addIncomeOrExpensesButton.setOnClickListener(this)
        recycleView.layoutManager=LinearLayoutManager(container?.context)
        recycleViewIncome=view.findViewById(R.id.listModeIncome)
        recycleViewIncome.layoutManager=LinearLayoutManager(container?.context)
        incomeArrayList= arrayListOf()
        expensesArrayList= arrayListOf()
        myAdapterExpenses= MyAdapter(expensesArrayList)
        myAdapterIncome= MyAdapter(incomeArrayList)
        recycleView.adapter=myAdapterExpenses
        recycleViewIncome.adapter=myAdapterIncome

        myAdapterExpenses.setOnClickListener(object :MyAdapter.OnClickListener{
            override fun onClick(position: Int, model: User) {

                val dialog = container?.context?.let { BottomSheetDialog(it) }

                val view = layoutInflater.inflate(R.layout.delete_modify_sheet, null)
                val deleteButton=view.findViewById<Button>(R.id.delete_button)
                val updateButton=view.findViewById<Button>(R.id.update_button)
                deleteButton.setOnClickListener{
                    if (model.id!=null){
                        firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("expenses").collection("expenses").document(model.id!!).delete()
                        Toast.makeText(context,"Successful Deleted", Toast.LENGTH_SHORT).show()
                        expensesArrayList.removeAt(position)
                        myAdapterExpenses.notifyDataSetChanged()


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
                             firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("expenses").collection("expenses").document(
                                model.id!!).update(updates)
                            Toast.makeText(context,"Successful Updated",Toast.LENGTH_SHORT).show()
                            expensesArrayList[position].amount=amount.text.toString().toLong()
                            expensesArrayList[position].date=date.text.toString()
                            expensesArrayList[position].reason=reason.text.toString()
                            myAdapterExpenses.notifyDataSetChanged()
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

        myAdapterIncome.setOnClickListener(object :MyAdapter.OnClickListener{
            override fun onClick(position: Int, model: User) {

                val dialog = container?.context?.let { BottomSheetDialog(it) }

                val view = layoutInflater.inflate(R.layout.delete_modify_sheet, null)
                val deleteButton=view.findViewById<Button>(R.id.delete_button)
                val updateButton=view.findViewById<Button>(R.id.update_button)
                deleteButton.setOnClickListener{
                    if (model.id!=null){
                        firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("income").collection("income").document(model.id!!).delete()
                        Toast.makeText(context,"Successful Deleted", Toast.LENGTH_SHORT).show()
                        incomeArrayList.removeAt(position)
                        myAdapterIncome.notifyDataSetChanged()

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
                            incomeArrayList[position].amount=amount.text.toString().toLong()
                            incomeArrayList[position].date=date.text.toString()
                            incomeArrayList[position].reason=reason.text.toString()
                            myAdapterIncome.notifyDataSetChanged()

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
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
    @SuppressLint("MissingInflatedId")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.personButton->{
                val intent= Intent(v.context,UserProfile::class.java)
                intent.putExtra("income", income.toString())
                intent.putExtra("expenses", expenses.toString())
                startActivity(intent)

            }
            R.id.add_expenses_income_button->{
                val dialog = BottomSheetDialog(v.context)

                val view = layoutInflater.inflate(R.layout.add_income_expenses_sheet, null)
                val expensesButton=view.findViewById<Button>(R.id.add_expenses_button_dash)
                val incomeButton=view.findViewById<Button>(R.id.add_income_button_dash)
                expensesButton.setOnClickListener {
                    dialog.cancel()
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

                            val ref=
                                firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("expenses").collection("expenses")
                            val user = hashMapOf(
                                "date" to date.text.toString(),
                                "reason" to reason.text.toString(),
                                "amount" to amount.text.toString().toLong()
                            )
                            ref.add(user).addOnSuccessListener { documentReference ->
                                ref.document(documentReference.id).update("id",documentReference.id)
                                Toast.makeText(context,"Successful Added",Toast.LENGTH_SHORT).show()
                               // expensesArrayList.add(User(date.text.toString(),reason.text.toString(),amount.text.toString().toLong(),documentReference.id))
                                myAdapterExpenses.notifyDataSetChanged()

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
                incomeButton.setOnClickListener {
                    dialog.cancel()
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
                              //  incomeArrayList.add(User(date.text.toString(),reason.text.toString(),amount.text.toString().toLong(),documentReference.id))
                                myAdapterIncome.notifyDataSetChanged()

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
                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()

            }
        }

    }
private fun loadIncome(){

    firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("income").collection("income").addSnapshotListener(object :EventListener<QuerySnapshot>{
        override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
            if (error!=null){
                Log.e("err",error.message.toString())
                Toast.makeText(context,"No Data Found", Toast.LENGTH_SHORT).show()
                return
            }
            for (doc : DocumentChange in value?.documentChanges!! ){
                if (doc.type==DocumentChange.Type.ADDED){
                    incomeArrayList.add(doc.document.toObject(User::class.java))
                }
            }
            myAdapterExpenses.notifyDataSetChanged()

        }

    })

}
    private fun loadExpenses(){

        firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("expenses").collection("expenses").addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error!=null){
                    Log.e("err",error.message.toString())
                    Toast.makeText(context,"No Data Found", Toast.LENGTH_SHORT).show()
                    return
                }
                for (doc : DocumentChange in value?.documentChanges!! ){
                    if (doc.type==DocumentChange.Type.ADDED){
                        expensesArrayList.add(doc.document.toObject(User::class.java))
                    }
                }
                myAdapterIncome.notifyDataSetChanged()
            }

        })

    }

    private fun expensesAmount(){

         firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).document("expenses")
            .collection("expenses").get().addOnSuccessListener { result ->
                for (document in result) {
                    expenses+=document.data["amount"].toString().toInt()


                    Log.d(ContentValues.TAG, "${document.data["amount"]} => ${document.data}")
                }

            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }

    }
    private fun incomeAmount(){

          firebaseDatabase.collection(firebaseAuth.currentUser!!.uid).
        document("income").collection("income").get().addOnSuccessListener { result ->
                for (document in result) {
                    income+=document.data["amount"].toString().toInt()
                    Log.d(ContentValues.TAG, "${document.data["amount"]} => ${document.data}")
                }

            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }

    }

}