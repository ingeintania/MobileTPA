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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {

    val RC_SIGN_IN = 1

    lateinit var emailEt: TextInputLayout
    lateinit var passwordEt: TextInputLayout
    lateinit var signinBtn: Button
    lateinit var signupBtn: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var mDb: FirebaseDatabase
    lateinit var progressBar: LinearLayout
    lateinit var googleSignUpBtn: Button
    lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var userRef : DatabaseReference
    lateinit var rootRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        createGoogleRequest()
        rootRef = FirebaseDatabase.getInstance().getReference()

        emailEt = findViewById(R.id.emailEt)
        passwordEt = findViewById(R.id.passwordEt)
        progressBar = findViewById(R.id.progressBarLl)
        signinBtn = findViewById(R.id.signinBtn)
        signupBtn = findViewById(R.id.signupBtn)
        googleSignUpBtn = findViewById(R.id.googleSignUpBtn)

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseDatabase.getInstance()

        userRef = FirebaseDatabase.getInstance().getReference().child("Users")

        signinBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val email = emailEt.editText?.text.toString()
                val password = passwordEt.editText?.text.toString()

                if (onValidate(email, password)) {

                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    progressBar.visibility = View.VISIBLE

                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { t0 ->
                            if (t0.isSuccessful) {


                                mDb.getReference("Users").child(mAuth.currentUser?.uid.toString()).addValueEventListener(object: ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        val role = p0.getValue(User::class.java)?.role.toString()
                                        editor.putString("userRole", role)
                                        editor.commit()

                                        var currentUserId : String
                                        currentUserId = mAuth.currentUser!!.uid

                                        var deviceToken : String
                                        deviceToken = FirebaseInstanceId.getInstance().getToken().toString()

                                        userRef.child(currentUserId).child("device_token")
                                            .setValue(deviceToken)
                                            .addOnCompleteListener { object: OnCompleteListener<Void>{
                                                override fun onComplete(p0: Task<Void>) {
                                                    if(p0.isSuccessful){
//                                                        if (role.equals("Customer")) {
//                                                            val toHome = Intent(this@SignInActivity, HomeActivity::class.java)
//                                                            startActivity(toHome)
//                                                            finish()
//                                                        } else if (role.equals("Customer Service")) {
//                                                            val toChat = Intent(this@SignInActivity, ChatActivity::class.java)
//                                                            startActivity(toChat)
//                                                            finish()
//                                                        } else if (role.equals("Owner Shop")) {
//                                                            val toOwnerHome = Intent(this@SignInActivity, OwnerHomeActivity::class.java)
//                                                            startActivity(toOwnerHome)
//                                                            finish()
//                                                        }
                                                    }
                                                }

                                            } }

                                        if (role.equals("Customer")) {
                                            val toHome = Intent(this@SignInActivity, HomeActivity::class.java)
                                            startActivity(toHome)
                                            finish()
                                        } else if (role.equals("Customer Service")) {
                                            val toChat = Intent(this@SignInActivity, ChatActivity::class.java)
                                            startActivity(toChat)
                                            finish()
                                        } else if (role.equals("Owner Shop")) {
                                            val toOwnerHome = Intent(this@SignInActivity, OwnerHomeActivity::class.java)
                                            startActivity(toOwnerHome)
                                            finish()
                                        }

                                    }

                                })

                            } else {
                                progressBar.visibility = View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                Toast.makeText(
                                    this@SignInActivity,
                                    "Account Not Exist",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        })

        signupBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val toSignUp = Intent(this@SignInActivity, SignUpActivity::class.java)
                startActivity(toSignUp)
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
            val toHome = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(toHome)
            finish()
        }
    }

    fun onValidate(email: String,
                   password: String): Boolean {
        var isValid = true
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val phonePattern = "[0-9]{12,}"
        if (email.length == 0) {
            Toast.makeText(this@SignInActivity, "Email Required", Toast.LENGTH_LONG).show()
            isValid = false
        } else if(!Pattern.matches(emailPattern, email)) {
            Toast.makeText(this@SignInActivity, "Email Not Valid", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (password.length == 0) {
            Toast.makeText(this@SignInActivity, "Password Required", Toast.LENGTH_LONG).show()
            isValid = false
        } else if (password.length < 8) {
            Toast.makeText(this@SignInActivity, "Password Length Min 8", Toast.LENGTH_LONG).show()
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

                                                    val toHome = Intent(this@SignInActivity, HomeActivity::class.java)
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

                                val toHome = Intent(this@SignInActivity, HomeActivity::class.java)
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
