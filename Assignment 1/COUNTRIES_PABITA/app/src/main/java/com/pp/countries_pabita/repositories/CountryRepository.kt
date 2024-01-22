package com.pp.countries_pabita.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.firestore
import com.pp.countries_pabita.models.Country
import com.pp.countries_pabita.models.CountryName
import java.lang.Exception

class CountryRepository(
    private var context: Context
) {
    // get an instance of firestore database
    private val db = Firebase.firestore
    private val TAG = this.toString()

    // here, the field name must be same as the class variable
    private val COLLECTION_FAVOURITE_COUNTRIES = "Favourite Countries"
    private val FIELD_COUNTRY_NAME = "common"

    // expecting all docs from our collection, that's why we use List<> here.
    var allFavouriteCountries : MutableLiveData<List<Country>> = MutableLiveData<List<Country>>()


    fun addFavouriteCountryToDB(favCountry: Country ) {
        try {
            // MutableMap<String, Any> - key, value
            val data : MutableMap<String, Any> = HashMap()

            data[FIELD_COUNTRY_NAME] = favCountry.name.common

            db.collection(COLLECTION_FAVOURITE_COUNTRIES)
                .whereEqualTo("common",data[FIELD_COUNTRY_NAME] )
                .get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        println("Already exists")
                    } else {
                        // For adding document to root level collection
                        db
                            .collection(COLLECTION_FAVOURITE_COUNTRIES)
                            .add(data)
                            .addOnSuccessListener { docRef ->
                                Log.d(TAG, "addFavCountryToDB: Document successfully added : ${docRef.id} ")
                            }
                            .addOnFailureListener { docRef ->
                                Log.d(TAG, "addFavCountryToDB: Exception occurred while adding a document ")
                            }
                    }
                }
        } catch (ex : java.lang.Exception) {
            Log.d(TAG, "addFavCountryToDB: Couldn't add country to favourite collection due to exception $ex")
        }

    }
    fun checkCountryExists(name: String, callback: (Boolean) -> Unit) {
        db.collection(COLLECTION_FAVOURITE_COUNTRIES)
            .whereEqualTo("common", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val res = !querySnapshot.isEmpty
                callback.invoke(res)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Couldn't check country existence due to exception $exception")
                callback.invoke(false)
            }
    }

    fun retrieveAllCountries() {

        try {
            db.collection(COLLECTION_FAVOURITE_COUNTRIES)
                .addSnapshotListener(EventListener { result, error ->
                    if (error != null) {
                        Log.d(TAG, "retrieveAllFavouriteCountries: Listening to Favourite Country collection failed due to error : $error")
                        return@EventListener
                    }

                    if (result != null) {
                        Log.d(TAG, "retrieveAllFavouriteCountries: Number of documents retrieved: ${result.size()}")


                        val tempList: MutableList<Country> = ArrayList<Country>()

                        for (docChanges in result.documentChanges) {
                            Log.d(TAG, "docChanges: ${ docChanges.document}")

                            val currCountryName : CountryName = docChanges.document.toObject(CountryName::class.java)
                            Log.d(TAG, "retrieveAllFavouriteCountries: currentDocument : $currCountryName ")
                            val currentDocument : Country = Country(currCountryName)

                            when(docChanges.type) {
                                DocumentChange.Type.ADDED -> {
                                    // do necessary changes to your local list of objects
                                    tempList.add(currentDocument)
                                }
                                DocumentChange.Type.MODIFIED -> {

                                }
                                DocumentChange.Type.REMOVED -> {

                                }
                            }
                        } // for

                        Log.d(TAG, "retrieveAllFavouriteCountries: tempList : $tempList")
                        allFavouriteCountries.postValue(tempList)

                    } else {
                        Log.d(TAG, "retrieveAllFavouriteCountries: No data in the result after retrieving")
                    }
                })

        } catch (ex : java.lang.Exception) {
            Log.d(TAG, "retrieveAllFavouriteCountries: Unable to retrieve all expenses : $ex")
        }

    }

    fun deleteFavCountry(countryToDelete : Country){
        Log.d("TAG", "DELETE DATA : $countryToDelete")
        try {
            Log.d("TAG", "DELETE DATA : ${countryToDelete.name.common}")
            db.collection(COLLECTION_FAVOURITE_COUNTRIES)
                .whereEqualTo("common",countryToDelete.name.common )
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        // Delete the document
                        document.reference.delete()
                            .addOnSuccessListener {
                                // Deletion successful
                                this.retrieveAllCountries()
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                Log.d(TAG, "Error deleting document: $e")
                            }
                    } else {
                        Log.d(TAG,"Document not found")
                    }
                }

        } catch (ex: Exception) {
            Log.e(TAG, "deleteFavCountry: Unable to delete fav country due to exception: $ex")
        }
    }
}