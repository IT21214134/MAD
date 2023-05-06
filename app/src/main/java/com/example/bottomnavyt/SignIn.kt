package com.example.bottomnavyt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignIn : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        firebaseAuth= FirebaseAuth.getInstance()

        val email=findViewById<EditText>(R.id.signIn_email_field)
        val password=findViewById<EditText>(R.id.signIn_password_field)
        val signInButton=findViewById<Button>(R.id.signIn_button)
        val signUpButton=findViewById<Button>(R.id.get_signUp_button)


        signInButton.setOnClickListener {
            if(email.text.toString().isNotEmpty()&&password.text.toString().isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Login Successful", Toast.LENGTH_SHORT).show()
                        val intent= Intent(this@SignIn,MainActivity::class.java)
                        startActivity(intent)

                    }
                    else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this,"Fill All Fields", Toast.LENGTH_SHORT).show()
            }
        }

        signUpButton.setOnClickListener {
            val intent= Intent(this@SignIn,SignUp::class.java)
            startActivity(intent)
        }
    }

}