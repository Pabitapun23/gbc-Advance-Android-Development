package com.pp.share.repositories

import android.content.Context
import android.content.SharedPreferences
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.firestore
import com.jk.share.models.Expense
import java.lang.Exception

// Controller
class ExpenseRepository(
    private val context : Context
) {
    private val TAG = this.toString()
    // get an instance of firestore database
    private val db = Firebase.firestore

    // here, the field name must be same as the class variable
    private val COLLECTION_EXPENSES = "Expenses"
    private val COLLECTION_USERS = "Users"
    private val FIELD_CHECK_AMOUNT = "checkAmount"
    private val FIELD_PERSONS = "persons"
    private val FIELD_TIP_PERCENTAGE = "tipPercentage"
    private val FIELD_DONATION_AMOUNT = "donationAmount"
    private val FIELD_ID = "id"

    private var loggedInUserEmail = ""
    private lateinit var sharedPrefs : SharedPreferences

    // expecting all docs from our collection, that's why we use List<> here.
    var allExpenses : MutableLiveData<List<Expense>> = MutableLiveData<List<Expense>>()

    init {
        sharedPrefs = context.getSharedPreferences("com.pp.share", Context.MODE_PRIVATE)

        if (sharedPrefs.contains("USER_EMAIL")) {
            loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
        }
    }

    fun addExpenseToDB(newExpense : Expense) {

        if (loggedInUserEmail.isNotEmpty()) {
            try {
                // MutableMap<String, Any> - key, value
                val data : MutableMap<String, Any> = HashMap()

                data[FIELD_ID] = newExpense.id
                data[FIELD_CHECK_AMOUNT] = newExpense.checkAmount
                data[FIELD_PERSONS] = newExpense.persons
                data[FIELD_DONATION_AMOUNT] = newExpense.donationAmount
                data[FIELD_TIP_PERCENTAGE] = newExpense.tipPercentage

                // for adding document to nested collection
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_EXPENSES)
                    .add(data)
                    .addOnSuccessListener { docRef ->
                        Log.d(TAG, "addExpenseToDB: Document successfully added with ID : ${docRef.id} ")
                    }
                    .addOnFailureListener { docRef ->
                        Log.d(TAG, "addExpenseToDB: Exception ocurred while adding a document")
                    }


                // .add() - only to create new doc with randomly generated id
                // .set() - create and update doc. Checks if the doc is already exist, if no then create a new one
                // For adding document to root level collection
//            db
//                .collection(COLLECTION_EXPENSES)
//                .add(data)
//                .addOnSuccessListener { docRef ->
//                    Log.d(TAG, "addExpenseToDB: Document successfully added with ID : ${docRef.id} ")
//                }
//                .addOnFailureListener { docRef ->
//                    Log.d(TAG, "addExpenseToDB: Exception ocurred while adding a document ")
//                }
            } catch (ex : java.lang.Exception) {
                Log.d(TAG, "addExpenseToDB: Couldn't perform insert on Expenses collection due to exception $ex")
            }
        } else {
            Log.d(TAG, "addExpenseToDB: Cannot create expense without user's email address. You must create the account first.")
        }

    }

    fun retrieveAllExpenses() {

        if (loggedInUserEmail.isNotEmpty()) {
            try {
                // Snapshot - result of our query
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_EXPENSES)
                    .addSnapshotListener(EventListener { result, error ->
                        if (error != null) {
                            Log.d(TAG, "retrieveAllExpenses: Listening to Expenses collection failed due to error : $error")
                            return@EventListener
                        }

                        if (result != null) {
                            Log.d(TAG, "retrieveAllExpenses: Number of documents retrieved: ${result.size()}")

                            val tempList: MutableList<Expense> = ArrayList<Expense>()

                            for (docChanges in result.documentChanges) {

                                val currentDocument : Expense = docChanges.document.toObject(Expense::class.java)
                                currentDocument.id = docChanges.document.id
                                Log.d(TAG, "retrieveAllExpenses: currentDocument : $currentDocument ")

                                when(docChanges.type) {
                                    DocumentChange.Type.ADDED -> {
                                        // do necessary changes to your local list of objects
                                        tempList.add(currentDocument)
                                    }
                                    DocumentChange.Type.MODIFIED -> {
                                        tempList.add(currentDocument)
                                    }
                                    DocumentChange.Type.REMOVED -> {

                                    }
                                }
                            } // for

                            Log.d(TAG, "retrieveAllExpenses: tempList : $tempList")
                            // replace the value in allExpenses
                            allExpenses.postValue(tempList)

                        } else {
                            Log.d(TAG, "retrieveAllExpenses: No data in the result after retrieving")
                        }
                    })

            } catch (ex : java.lang.Exception) {
                Log.d(TAG, "retrieveAllExpenses: Unable to retrieve all expenses : $ex")
            }
        } else {
            Log.d(TAG, "retrieveAllExpenses: Cannot retrieve expenses without user's email address. You must sign in first.")
        }

    }

    fun filterExpenses(amount : Double, persons : Int){
        if (loggedInUserEmail.isNotEmpty()) {
            try{
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_EXPENSES)
                    .whereGreaterThan(FIELD_CHECK_AMOUNT, amount)
                    .whereLessThan(FIELD_PERSONS, persons)
                    .addSnapshotListener(EventListener { result, error ->
                        //check for result or errors and update UI accordingly
                        if (error != null){
                            Log.e(TAG,
                                "filterExpenses: Listening to Expenses collection failed due to error : $error", )
                            return@EventListener
                        }

                        if (result != null){
                            Log.d(TAG, "filterExpenses: Number of documents retrieved : ${result.size()}")

                            val tempList : MutableList<Expense> = ArrayList<Expense>()

                            for (docChanges in result.documentChanges){

                                val currentDocument : Expense = docChanges.document.toObject(Expense::class.java)
                                Log.d(TAG, "filterExpenses: currentDocument : $currentDocument")

                                //do necessary changes to your local list of objects
                                tempList.add(currentDocument)
                            }//for
                            Log.d(TAG, "filterExpenses: tempList : $tempList")
                            //replace the value in allExpenses
                            allExpenses.postValue(tempList)

                        }else{
                            Log.d(TAG, "filterExpenses: No data in the result after retrieving")
                        }
                    })
            }
            catch (ex : java.lang.Exception){
                Log.e(TAG, "filterExpenses: Unable to filter expenses : $ex", )
            }
        }
    }


    fun updateExpense(expenseToUpdate : Expense) {
        if (loggedInUserEmail.isNotEmpty()) {
            val data: MutableMap<String, Any> = HashMap();

            data[FIELD_CHECK_AMOUNT] = expenseToUpdate.checkAmount;
            data[FIELD_PERSONS] = expenseToUpdate.persons
            data[FIELD_DONATION_AMOUNT] = expenseToUpdate.donationAmount
            data[FIELD_TIP_PERCENTAGE] = expenseToUpdate.tipPercentage

            Log.d("TAG", "let's see ${expenseToUpdate.id}")

            try{
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_EXPENSES)
                    .document(expenseToUpdate.id)
                    .update(data)
//                    .whereEqualTo("id", expenseToUpdate.id)
//                    .get()

//                    .addOnSuccessListener { docRef ->
//                        Log.d(TAG, "updateExpense: Document updated successfully : $docRef")
//
//                        if (!docRef.isEmpty) {
//                            val document = docRef.documents[0]
//                            // Update the document
//                            document.reference.update(data)
//                                .addOnSuccessListener {
//                                    // Update data successfully
//                                    Log.d(TAG, "updateExpense: Updated successfully")
////                                    // Set the value of LiveData to true to indicate expense update
////                                    expenseUpdated.value = true
//                                }
//                                .addOnFailureListener { e ->
//                                    // Handle failure
//                                    Log.d(TAG,"Error deleting document: $e")
//                                }
//
//
//                        } else {
//                            Log.d(TAG,"Document not found")
//                        }
//d
//                    }

                    .addOnSuccessListener { docRef ->
                        Log.d(TAG, "updateExpense: Document updated successfully : $docRef")
                    }
                    .addOnFailureListener { ex ->
                        Log.e(TAG, "updateExpense: Failed to update document : $ex", )
                    }
            }
            catch (ex : Exception){
                Log.e(TAG, "updateExpense: Unable to update expense due to exception : $ex", )
            }
        }


    }


    fun deleteExpense(expenseToDelete : Expense){
        try {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_EXPENSES)
                .document(expenseToDelete.id)
                .delete()
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "deleteExpense: Document deleted successfully : $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "deleteExpense: Failed to delete document : $ex")
                }

        } catch (ex: Exception) {
            Log.e(TAG, "deleteExpense: Unable to delete expense due to exception: $ex")
        }
    }
}