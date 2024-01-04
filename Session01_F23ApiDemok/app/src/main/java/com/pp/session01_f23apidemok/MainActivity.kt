package com.pp.session01_f23apidemok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pp.session01_f23apidemok.adapter.UserAdapter
import com.pp.session01_f23apidemok.api.MyInterface
import com.pp.session01_f23apidemok.api.RetrofitInstance
import com.pp.session01_f23apidemok.databinding.ActivityMainBinding
import com.pp.session01_f23apidemok.models.Address
import com.pp.session01_f23apidemok.models.Company
import com.pp.session01_f23apidemok.models.Geo
import com.pp.session01_f23apidemok.models.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // adapter
    lateinit var adapter: UserAdapter

    // data source
    // "hey chatgpt, write me the kotlin code to produce an instance of teh Address class!!!"
    var geo: Geo = Geo("23.5","99.1")
    val userAddress: Address = Address("255 Main Street", "204", "Toronto", "blah", geo)
    val company: Company = Company("My Company", "sdfdsfsdfsd", "use chatgpt now!")


    var datasource:MutableList<User> = mutableListOf<User>(
        User(999, "Peter", "psmith", "peter@gmail.com", userAddress, "555-5555-1234", "http://www.peter.com", company),
        User(100, "Mary", "psmith", "peter@gmail.com", userAddress, "555-5555-1234", "http://www.peter.com", company),
        User(101, "Julie", "psmith", "peter@gmail.com", userAddress, "555-5555-1234", "http://www.peter.com", company)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup the adapter & recycler view
        // if you have a mutable list and you call .toList() on it, it will return a List<> version
        // .toList() -- will convert the <MutableList> to a <List>
        // This way we don't have to update the adapter
        adapter = UserAdapter( datasource, { pos -> rowClicked(pos) } )
        // recycler view
        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        binding.btnGetUser.setOnClickListener {
            // 0. Get the user id from the user interface
            val etUserIdFromUI:String = binding.etUserIdFromUI.text.toString()
            if (etUserIdFromUI.isEmpty() == true) {
                return@setOnClickListener
            } else {
                binding.tvResults.setText("ERROR: User not found")
            }
            val idFromUI:Int = etUserIdFromUI.toInt()


            // 1. Connect to the API endpoint using retrofit
            // a. Get an instance of the RetrofitInstance.kt file
            var api: MyInterface = RetrofitInstance.retrofitService

            // b. launches a background task
            lifecycleScope.launch {
                // the code you want to execute inside the
                // b. Using that instance, call the MyInstance.kt getSingleUser() function
//                val user4: User = api.getSingleUser()
//                Log .d("MYAPP", user4.toString())
//
//                val user3: User = api.getSingleUser3()
//                Log .d("MYAPP", user3.toString())

                // attempt to get /users/8
                val user = api.getUserById(idFromUI)

                // c. The call to the function must occur as a BACKGROUND TASK
//                binding.tvResults.setText(user4.toString())
                val output = "Name: ${user.name}\n" +
                        "Username: ${user.username}\n" +
                        "Email: ${user.email}\n" +
                        "Company: ${user.company.name}\n" +
                        "Company catchphrase: ${user.company.catchPhrase}\n"

                binding.tvResults.setText(output)

            }

        }

        binding.btnGetAllUser.setOnClickListener {
            // connect to the endpoint
            var api: MyInterface = RetrofitInstance.retrofitService
            lifecycleScope.launch {
                val usersList: List<User> = api.getAllUsers()
                // output it to the UI!
//                var output:String = ""
//                for (currUser in usersList) {
//                    output += "${currUser.name}\n"
//                }
//                binding.tvResults.setText(output)

                // update the recycler view with the data from the api
                // - clear the existing data source
                datasource.clear()

                // - add all the data from the API to the data source
                datasource.addAll(usersList)

                // - notify the adapter that the data source has changed
                // & so the adapter will refresh the recycler view with the new data
                adapter.notifyDataSetChanged()

            }
        }

    }

    // click handler for the row
    fun rowClicked(position:Int) {
        val snackbar = Snackbar.make(binding.rootLayout, "Clicked on ${position}", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

}