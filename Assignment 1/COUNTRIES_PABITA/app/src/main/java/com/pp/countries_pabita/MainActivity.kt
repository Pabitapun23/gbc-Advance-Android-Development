package com.pp.countries_pabita

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pp.countries_pabita.adapters.CountryAdapter
import com.pp.countries_pabita.api.MyInterface
import com.pp.countries_pabita.api.RetrofitInstance
import com.pp.countries_pabita.databinding.ActivityMainBinding
import com.pp.countries_pabita.models.Country
import com.pp.countries_pabita.repositories.CountryRepository
import kotlinx.coroutines.launch


// List of World Countries Screen
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // adapter
    private lateinit var adapter: CountryAdapter

    var datasource: MutableList<Country> = mutableListOf<Country>(
//        Country("Canada")
    )


    private lateinit var countryRepository: CountryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.menuToolbar)
        // Change the title
        supportActionBar?.title = "CountryApp"

        // setup adapter
        adapter = CountryAdapter( datasource, { pos -> favBtnClicked(pos) }, false )
        // recycler view
        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        // connect to the endpoint
        // Connect to the API endpoint using retrofit
        // Get an instance of the RetrofitInstance.kt file
        var api: MyInterface = RetrofitInstance.retrofitService

        // launches a background task
        lifecycleScope.launch {
            val countriesList: List<Country> = api.getAllCountries()

            // update the recycler view with the data from the api
            // - clear the existing data source
            datasource.clear()

            // - add all the data from the API to the data source
            datasource.addAll(countriesList)

            Log.d("TAG", "data ${datasource}")

            // - notify the adapter that the data source has changed
            // & so the adapter will refresh the recycler view with the new data
            adapter.notifyDataSetChanged()
        }

        // initialize country Repository
        countryRepository = CountryRepository(applicationContext)

    }



    // Click handler for the fav btn
    fun favBtnClicked(position: Int) {
        var snackbar = Snackbar.make(binding.rootLayout, "Clicked on ${position}", Snackbar.LENGTH_SHORT)
        var favCountryToAdd = datasource.get(position)
        countryRepository.checkCountryExists(favCountryToAdd.name.common){ exists ->
            if (exists) {
                snackbar = Snackbar.make(binding.rootLayout, "Already exists", Snackbar.LENGTH_SHORT)
                snackbar.show()
            } else {
                countryRepository.addFavouriteCountryToDB(favCountryToAdd)
                snackbar = Snackbar.make(binding.rootLayout, "Successfully added to favourite list", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }

    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_items_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Add Expense option is selected")

                val mainIntent = Intent(this@MainActivity, FavouriteActivity::class.java)
                startActivity(mainIntent)

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}