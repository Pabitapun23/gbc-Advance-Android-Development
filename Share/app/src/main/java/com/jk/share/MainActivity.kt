package com.jk.share

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.jk.share.databinding.ActivityMainBinding
import com.jk.share.models.Expense
import com.pp.share.repositories.ExpenseRepository
import java.time.LocalDate


/*
User should be presented with Home screen upon successful sign In

Home Screen
- present options menu with 3 options
1. Profile (ProfileActivity)
2. Add Expense (MainActivity)
3. Sign out

Home Screen should show the RecyclerView to list all the expenses for the logged in user
RecyclerView should allow the user to open another activity to show expense details

ExpenseDetails screen should allow the user to update or delete expense.

Profile Screen should allow the user to modify their profile related information.
 */

// View
class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding
    var checkAmount = 0f
    var persons = 2
    var tax = 0f
    var donationAmount = 0
    var tipAmount = 0.0
    var selectedTipPer = 1
    var billAmount = 0.0
    var amountPerPerson = 0.0
    var transactionDate = LocalDate.now()

    val TAG = this@MainActivity.toString()
    val tipPercentage = intArrayOf(0, 5, 10, 20)

    private lateinit var expenseRepository: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener(this)
        binding.edtDate.setOnClickListener(this)
        initializeSpinner()

        this.expenseRepository = ExpenseRepository(applicationContext)
    }

    override fun onResume() {
        super.onResume()

        this.expenseRepository.retrieveAllExpenses()

        this.expenseRepository.allExpenses.observe(this) { expenseList ->
            if (expenseList != null) {
                for (expense in expenseList) {
                    Log.d(TAG, "onResume: Expense : $expense")
                }
            } else {
                Log.d(TAG, "onResume: Observer received null expenseList")
            }
        }
    }

    private fun initializeSpinner(){
        val tipAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.array_tip)
        )

        binding.spnTipPercentage.adapter = tipAdapter
        
        binding.spnTipPercentage.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                Log.d(TAG, "onItemSelected: User wants to leave ${tipPercentage[position]} % of tip")
                Log.d(TAG, "onItemSelected: User selection : ${resources.getStringArray(R.array.array_tip).get(position)}")
                selectedTipPer = tipPercentage[position]
                tipAmount = ((checkAmount + tax) * selectedTipPer / 100).toDouble();
                binding.tvTip.setText(String.format("%.2f", tipAmount))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: User has not made any selection. Set the default percentage to 0%")
            }
        }
    }

    override fun onClick(p0: View?) {
        if(p0 != null){
            when(p0.id){
                R.id.btn_calculate -> {
                    Log.d(TAG, "onClick: btnCalculate performed")
                    performCalculation()
                }
                R.id.edt_date -> {
                    //show date picker
                    val datePicker = DatePickerFragment()
                    datePicker.show(supportFragmentManager, "transaction_date_picker")
                }
            }
        }
    }

    private fun performCalculation(){

        if (binding.edtCheckAmount.text.toString().isEmpty()){
            binding.edtCheckAmount.setError(resources.getString(R.string.error_check_amount))
        }else {
            checkAmount = binding.edtCheckAmount.text.toString().toFloatOrNull() ?: 0.0f
        }

        if(binding.edtPersons.text.toString().isEmpty()){
            binding.edtPersons.setError(resources.getString(R.string.error_persons))
        }else{
            persons = binding.edtPersons.text.toString().toInt()
        }

        tax = (checkAmount * 0.13).toFloat();

        val selectedIndex = binding.spnTipPercentage.selectedItemPosition
        selectedTipPer = tipPercentage[selectedIndex];
        tipAmount = ((checkAmount + tax) * selectedTipPer / 100).toDouble();

        when(binding.rdgDonation.checkedRadioButtonId){
            R.id.rdb_yes -> donationAmount = 5
            R.id.rdb_no -> donationAmount = 0
        }

        billAmount = ((checkAmount + tax) + tipAmount + donationAmount).toDouble()
        amountPerPerson = billAmount / persons

        Log.d(TAG, "performCalculation: checkAmount : ${checkAmount}")
        Log.d(TAG, "performCalculation: Tax @ 13% : ${tax}")
        Log.d(TAG, "performCalculation: selected tip percentage : ${selectedTipPer} %")
        Log.d(TAG, "performCalculation: tip amount : ${tipAmount}")
        Log.d(TAG, "performCalculation: donation amount : ${donationAmount}")
        Log.d(TAG, "performCalculation: total bill amount : ${billAmount}")
        Log.d(TAG, "performCalculation: number of persons : ${persons}")
        Log.d(TAG, "performCalculation: amount per person : ${amountPerPerson}")

        binding.tvTip.setText(String.format("%.2f", tipAmount))
        binding.tvBillAmount.setText(String.format("%.2f", billAmount));
        binding.tvAmountPerPerson.setText(String.format("%.2f", amountPerPerson))

        //save expense to database

        // create new object of Expense class
        val expenseToAdd = Expense(checkAmount = checkAmount.toDouble(),
                                   persons = persons,
                                   tipPercentage = selectedTipPer.toDouble(),
                                   donationAmount = donationAmount.toDouble())

        this.expenseRepository.addExpenseToDB(expenseToAdd)
    }

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener{
        val TAG = "DatePickerFragment"

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return DatePickerDialog(this.requireActivity(), this, 2010, 7, 28)
        }

        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

            val myDate = LocalDate.of(year, month+1, dayOfMonth)

            Log.d(TAG, "onDateSet: Date selection ${myDate}")
            (this.requireActivity() as MainActivity).binding.edtDate.setText(myDate.toString())
            (this.requireActivity() as MainActivity).transactionDate = myDate
        }

    }

}


// NOTE: - unable to store multimedia on the firestore (as a String pauxa)
// - docs size are limited to 1MB


