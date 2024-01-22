package com.pp.countries_pabita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pp.countries_pabita.adapters.CountryAdapter
import com.pp.countries_pabita.databinding.ActivityFavouriteBinding
import com.pp.countries_pabita.models.Country
import com.pp.countries_pabita.repositories.CountryRepository

// Favorite Country List Screen
class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var countryArrayList: ArrayList<Country>
    private lateinit var countryRepository: CountryRepository
    private lateinit var adapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_favourite)

        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        countryArrayList = ArrayList()

        adapter = CountryAdapter( countryArrayList, {pos->deleteBtnClicked(pos)}, true)
        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this.applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )


        countryRepository = CountryRepository(applicationContext)

        countryRepository.retrieveAllCountries()

        countryRepository.allFavouriteCountries.observe(this, androidx.lifecycle.Observer { countriesList ->
            if(countryArrayList != null){
                //clear the existing list to avoid duplicate records
                countryArrayList.clear()
                countryArrayList.addAll(countriesList)
                this.adapter.notifyDataSetChanged()

            }
        })


    }


    // Click handler for the fav btn
    fun deleteBtnClicked(position: Int) {
        val snackbar = Snackbar.make(binding.rootLayout, "Favourite country of row: ${position} deleted!", Snackbar.LENGTH_SHORT)
        snackbar.show()

        var favCountryToDelete = countryArrayList.get(position)
        countryRepository.deleteFavCountry(favCountryToDelete)
    }

}