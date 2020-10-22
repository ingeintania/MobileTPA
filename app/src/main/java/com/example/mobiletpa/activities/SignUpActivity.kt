package com.example.mobiletpa.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.mobiletpa.R
import com.example.mobiletpa.models.ChatThumb
import com.example.mobiletpa.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    val RC_SIGN_IN = 1

    lateinit var nameEt: TextInputLayout
    lateinit var emailEt: TextInputLayout
    lateinit var phoneEt: TextInputLayout
    lateinit var passwordEt: TextInputLayout
    lateinit var signupBtn: Button
    lateinit var googleSignUpBtn: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var mDb: FirebaseDatabase
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var progressBar: LinearLayout

    lateinit var rootRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        rootRef = FirebaseDatabase.getInstance().getReference()

        nameEt = findViewById(R.id.nameEt)
        emailEt = findViewById(R.id.emailEt)
        phoneEt = findViewById(R.id.phoneEt)
        passwordEt = findViewById(R.id.passwordEt)
        signupBtn = findViewById(R.id.signupBtn)
        googleSignUpBtn = findViewById(R.id.googleSignUpBtn)
        progressBar = findViewById(R.id.progressBarLl)

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseDatabase.getInstance()

        createGoogleRequest()

        signupBtn.setOnClickListener(object: View.OnClickListener {

            override fun onClick(v: View?) {

                val name = nameEt.editText?.text.toString()
                val email = emailEt.editText?.text.toString()
                val phone = phoneEt.editText?.text.toString()
                val password = passwordEt.editText?.text.toString()

                if (onValidate(name, email, phone, password)) {

                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    progressBar.visibility = View.VISIBLE

                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { t0 ->
                            if (t0.isSuccessful) {
                                val user = User()
                                user.id = mAuth.currentUser?.uid.toString()
                                user.name = name
                                user.email = email
                                user.phone = phone
                                user.role = "Customer"

                                mDb.getReference("Users").child(user.id).setValue(user)
                                    .addOnCompleteListener { t1 ->
                                        if (t1.isSuccessful) {

                                            mAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener { t0 ->
                                                    if (t0.isSuccessful) {

                                                        mDb.getReference("Users").addValueEventListener(object: ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {

                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {

                                                                for (dt in p0.children) {
                                                                    val role = dt.getValue(User::class.java)?.role.toString()
                                                                    val id = dt.getValue(User::class.java)?.id.toString()
                                                                    if (role.equals("Customer Service")) {
                                                                        val myChatThumb = ChatThumb()
                                                                        myChatThumb.id = mAuth.currentUser?.uid.toString()
                                                                        myChatThumb.username = name
                                                                        myChatThumb.lastSentMessage = ""
                                                                        myChatThumb.lastSentTime = ""

                                                                        mDb.getReference("ChatThumb").child(user.id + "X" + id).setValue(myChatThumb)

                                                                        val csChatThumb = ChatThumb()
                                                                        csChatThumb.id = dt.getValue(User::class.java)?.id.toString()
                                                                        csChatThumb.username = dt.getValue(User::class.java)?.name.toString()
                                                                        csChatThumb.lastSentMessage = ""
                                                                        csChatThumb.lastSentTime = ""

                                                                        mDb.getReference("ChatThumb").child(id + "X" + user.id).setValue(csChatThumb)
                                                                    }
                                                                }

                                                                val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
                                                                val editor = sharedPreferences.edit()
                                                                editor.putString("userRole", "Customer")
                                                                editor.commit()

                                                                var currentUserId : String
                                                                currentUserId = mAuth.currentUser!!.uid


                                                                var deviceToken : String
                                                                deviceToken = FirebaseInstanceId.getInstance().getToken().toString()

                                                                rootRef.child("Users").child(currentUserId).child("device_token").setValue(deviceToken)


                                                                val toHome = Intent(this@SignUpActivity, HomeActivity::class.java)
                                                                startActivity(toHome)
                                                                finish()
                                                            }

                                                        })


                                                    } else {
                                                        progressBar.visibility = View.GONE
                                                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                        Toast.makeText(
                                                            this@SignUpActivity,
                                                            "Invalid Account",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }
                            } else {
                                // Error message
                                progressBar.visibility = View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Account Exist",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                }

            }
        })

        googleSignUpBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                googleSignIn();
            }
        })
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser != null) {
            val toHome = Intent(this@SignUpActivity, HomeActivity::class.java)
            startActivity(toHome)
            finish()
        }
    }

    fun onValidate(name: String,
                            email: String,
                            phone: String,
                            password: String): Boolean {
        var isValid = true
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val phonePattern = "[0-9]{12,}"
        if (name.length == 0) {
            Toast.makeText(this@SignUpActivity, "Name Required", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (name.length < 5) {
            Toast.makeText(this@SignUpActivity, "Name Length Min 5", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (email.length == 0) {
            Toast.makeText(this@SignUpActivity, "Email Required", Toast.LENGTH_LONG).show()
            isValid = false
        } else if(!Pattern.matches(emailPattern, email)) {
            Toast.makeText(this@SignUpActivity, "Email Not Valid", Toast.LENGTH_LONG).show()
            isValid = false
        } else if(phone.length == 0) {
            Toast.makeText(this@SignUpActivity, "Phone Required", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (!Pattern.matches(phonePattern, phone)) {
            Toast.makeText(this@SignUpActivity, "Phone Not Valid", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (password.length == 0) {
            Toast.makeText(this@SignUpActivity, "Password Required", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (password.length < 8) {
            Toast.makeText(this@SignUpActivity, "Password Length Min 8", Toast.LENGTH_LONG).show()
            isValid = false
        }
        return isValid
    }

    fun createGoogleRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    fun googleSignIn() {
        val toSignInGoogle = mGoogleSignInClient.signInIntent
        startActivityForResult(toSignInGoogle, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.VISIBLE
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
//                Toast.makeText(
//                    this@SignUpActivity,
//                    "Google Sign Up Failed" + resultCode.toString() + " " + requestCode.toString(),
//                    Toast.LENGTH_LONG
//                ).show()
                // ...
                progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { t0 ->
                if (t0.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    mDb.getReference("Users").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.getValue() == null) {
                                val user = User()
                                user.id = mAuth.currentUser?.uid.toString()
                                user.name = mAuth.currentUser?.displayName.toString()
                                user.email = mAuth.currentUser?.email.toString()
                                user.phone = mAuth.currentUser?.phoneNumber.toString()
                                user.role = "Customer"

                                mDb.getReference("Users").child(user.id).setValue(user)
                                    .addOnCompleteListener { t1 ->
                                        if (t1.isSuccessful) {

                                            mDb.getReference("Users").addValueEventListener(object: ValueEventListener {
                                                override fun onCancelled(p1: DatabaseError) {

                                                }

                                                override fun onDataChange(p1: DataSnapshot) {

                                                    for (dt in p1.children) {
                                                        val role = dt.getValue(User::class.java)?.role.toString()
                                                        val id = dt.getValue(User::class.java)?.id.toString()
                                                        if (role.equals("Customer Service")) {
                                                            val myChatThumb = ChatThumb()
                                                            myChatThumb.id = mAuth.currentUser?.uid.toString()
                                                            myChatThumb.username = mAuth.currentUser?.displayName.toString()
                                                            myChatThumb.lastSentMessage = ""
                                                            myChatThumb.lastSentTime = ""

                                                            mDb.getReference("ChatThumb").child(user.id + "X" + id).setValue(myChatThumb)

                                                            val csChatThumb = ChatThumb()
                                                            csChatThumb.id = dt.getValue(User::class.java)?.id.toString()
                                                            csChatThumb.username = dt.getValue(User::class.java)?.name.toString()
                                                            csChatThumb.lastSentMessage = ""
                                                            csChatThumb.lastSentTime = ""

                                                            mDb.getReference("ChatThumb").child(id + "X" + user.id).setValue(csChatThumb)
                                                        }
                                                    }

                                                    val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
                                                    val editor = sharedPreferences.edit()
                                                    editor.putString("userRole", "Customer")
                                                    editor.commit()

                                                    var currentUserId : String
                                                    currentUserId = mAuth.currentUser!!.uid


                                                    var deviceToken : String
                                                    deviceToken = FirebaseInstanceId.getInstance().getToken().toString()

                                                    rootRef.child("Users").child(currentUserId).child("device_token").setValue(deviceToken)

                                                    val toHome = Intent(this@SignUpActivity, HomeActivity::class.java)
                                                    startActivity(toHome)
                                                    finish()
                                                }

                                            })
                                        }
                                    }
                            } else {
                                val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("userRole", p0.getValue(User::class.java)?.role.toString())
                                editor.commit()

                                var currentUserId : String
                                currentUserId = mAuth.currentUser!!.uid


                                var deviceToken : String
                                deviceToken = FirebaseInstanceId.getInstance().getToken().toString()

                                rootRef.child("Users").child(currentUserId).child("device_token").setValue(deviceToken)

                                val toHome = Intent(this@SignUpActivity, HomeActivity::class.java)
                                startActivity(toHome)
                                finish()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
//                            println(p0!!.message)
                        }
                    })

                } else {
                    // If sign in fails, display a message to the user
                    progressBar.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                // ...
            }
    }

}
