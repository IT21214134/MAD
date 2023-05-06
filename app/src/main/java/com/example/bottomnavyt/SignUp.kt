package com.example.bottomnavyt

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignUp : AppCompatActivity() {
    private  lateinit var firebaseAuth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth=FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser!=null){
            val intent= Intent(this@SignUp,MainActivity::class.java)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        val email=findViewById<EditText>(R.id.signUp_email_field)
        val password=findViewById<EditText>(R.id.signUp_password_field)
        val confirmPassword=findViewById<EditText>(R.id.signUp_confirm_password_field)
        val signUpButton=findViewById<Button>(R.id.signUp_button_field)
        val signInButton=findViewById<Button>(R.id.get_signIn_button);
        val name=findViewById<EditText>(R.id.name_field)

        signUpButton.setOnClickListener {
            if (email.text.toString().isNotEmpty()&&password.text.toString().isNotEmpty()&&confirmPassword.text.toString().isNotEmpty()){
                if (password.text.toString().equals(confirmPassword.text.toString())){
                    firebaseAuth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){

                            Toast.makeText(this,"Account Created Successful", Toast.LENGTH_SHORT).show()
                            updateUser(  it.result.user,name.text.toString())
                            val intent= Intent(this@SignUp,SignIn::class.java)
                            startActivity(intent)


                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    Toast.makeText(this,"Passwords not matched", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Enter All Fields", Toast.LENGTH_SHORT).show()
            }
        }

        signInButton.setOnClickListener {
            val intent= Intent(this@SignUp,SignIn::class.java)
            startActivity(intent)
        }


    }
    private fun updateUser(user: FirebaseUser?, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder().apply {
            displayName = name
        }.build()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

            }
        }
    }
}