package com.example.mobiletpa.activities.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.mobiletpa.R
import com.example.mobiletpa.activities.HomeActivity
import com.example.mobiletpa.activities.SignInActivity
import com.example.mobiletpa.activities.UpdateProfileActivity
import com.example.mobiletpa.models.CacheRemover
import com.example.mobiletpa.models.User
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    lateinit var email: TextView
    lateinit var name: TextView
    lateinit var phone: TextView
    lateinit var progressBar: LinearLayout
    lateinit var profile: LinearLayout
    lateinit var signoutBtn: TextView
    lateinit var mAuth: FirebaseAuth
    lateinit var updateBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
            profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        signoutBtn = root.findViewById(R.id.signoutBtn)
        updateBtn = root.findViewById(R.id.updateBtn)

        signoutBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val sharedPreferences = activity?.getSharedPreferences("userSession", Context.MODE_PRIVATE)
                sharedPreferences?.edit()?.remove("userRole")?.commit()
                CacheRemover.clearCache()

                mAuth.signOut()
                val toSignIn = Intent(activity, SignInActivity::class.java)
                startActivity(toSignIn)
                activity?.finish()
            }
        })

        updateBtn.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent (activity, UpdateProfileActivity::class.java)
                startActivity(intent)
            }
        })

        email = root.findViewById(R.id.emailEt)
        name = root.findViewById(R.id.nameEt)
        phone = root.findViewById(R.id.phoneEt)
        progressBar = root.findViewById(R.id.progressBarLl)
        profile = root.findViewById(R.id.profileLl)
//        val textView: TextView = root.findViewById(R.id.text_profile)
//        profileViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        profileViewModel.getProfile().observe(this, object: Observer<User> {
            override fun onChanged(t: User?) {
                email.text = checkNull(t?.email)
                name.text = checkNull(t?.name)
                phone.text = checkNull(t?.phone)
                progressBar.visibility = View.GONE
                profile.visibility = View.VISIBLE
            }
        })
        return root
    }

    fun checkNull(str: String?): String {
        if (str.equals("null")) {
            return "No Data"
        }
        return str.toString()
    }

}
