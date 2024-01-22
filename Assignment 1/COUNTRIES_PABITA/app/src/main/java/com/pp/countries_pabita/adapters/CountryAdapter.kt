package com.pp.countries_pabita.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pp.countries_pabita.R
import com.pp.countries_pabita.models.Country

// Update to accept a list of countries
class CountryAdapter(
    private val countriesList: MutableList<Country>,
    private val btnClickHandler: (Int) -> Unit,
    private val isFavScreen: Boolean = false,
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {
            Log.d("TAG", "This is fav screen ${isFavScreen}")
            if (!!isFavScreen) {
                itemView.findViewById<Button>(R.id.btn_delete).setOnClickListener {
                    btnClickHandler(adapterPosition)
                }
                Log.d("TAG", "This is home screen")
            }
            else {
                itemView.findViewById<Button>(R.id.btn_favourite).setOnClickListener {
                    btnClickHandler(adapterPosition)
                }
                Log.d("TAG", "This is fav screen")
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        var view: View

        if (!!isFavScreen) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_country_row_layout, parent, false)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.country_row_layout, parent, false)
        }
        return CountryViewHolder(view)
    }

    override fun getItemCount(): Int {
       return countriesList.size
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        // Get the current country
        val currCountry:Country = countriesList.get(position)

        // Populate the UI with the country details
        // Get the tv_country with country's name
        val tvCountry = holder.itemView.findViewById<TextView>(R.id.tv_country)
        tvCountry.text = currCountry.name.common

    }

}