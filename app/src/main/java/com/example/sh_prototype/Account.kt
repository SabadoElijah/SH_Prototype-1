package com.example.sh_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLogout: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        textView = view.findViewById(R.id.user_details)
        buttonLogout = view.findViewById(R.id.logoutButton)

        if (user == null) {
            Log.d("AccountFragment", "User is null. Redirecting to Login activity.")
            // If user is not authenticated, navigate to Login activity
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
            textView.text = user.email
        }

        buttonLogout.setOnClickListener {
            Log.d("AccountFragment", "Logout button clicked.")
            // Perform logout and navigate to Login activity
            auth.signOut()
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Account.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}