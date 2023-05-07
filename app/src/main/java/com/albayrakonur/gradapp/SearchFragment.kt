package com.albayrakonur.gradapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.albayrakonur.gradapp.model.UserModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var queryResult: ArrayList<UserModel>
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var userList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        db = Firebase.firestore
        queryResult = ArrayList()
        userList = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val listview = view.findViewById<ListView>(R.id.searchViewResultList)

        // access the listView from xml file
        arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1, userList
        )
        listview.adapter = arrayAdapter

        arrayAdapter.notifyDataSetChanged()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getAllUser(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun getAllUser(text: String) {
        db.collection("Users").whereArrayContains("nameArr", text).get()
            .addOnCompleteListener {
                if (it.result.documents.isNotEmpty()) {
                    for (i in it.result.documents) {
                        queryResult.add(convertToUserModel(i))
                    }
                    updateListView(queryResult)
                }
            }
    }

    private fun updateListView(list: ArrayList<UserModel>) {
        userList.clear()
        for (i in list) {
            userList.add(i.fullName)
        }
        arrayAdapter.notifyDataSetChanged()
    }

    private fun convertToUserModel(snapshot: DocumentSnapshot): UserModel {

        return UserModel(
            snapshot["uid"].toString(),
            snapshot["fullName"].toString(),
            snapshot["email"].toString(),
            snapshot["entryYear"].toString(),
            snapshot["gradYear"].toString(),
            snapshot["number"].toString(),
            snapshot["photo"].toString(),
            snapshot["education"].toString(),
            snapshot["workPlace"].toString(),
            snapshot["nameArr"] as List<String>,
            snapshot["isAdmin"] as Boolean
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = SearchFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}