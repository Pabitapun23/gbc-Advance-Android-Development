package com.jk.share

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.jk.share.databinding.ActivityExpenseDetailsBinding
import com.jk.share.models.Expense
import com.pp.share.repositories.ExpenseRepository

class ExpenseDetailsActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ActivityExpenseDetailsBinding
    private lateinit var expenseRepository: ExpenseRepository
    val tipPercentage = intArrayOf(0, 5, 10, 20)
    var tipAmount = 0.0
    var checkAmount = 0.0
    var tax = 0f
    var selectedTipPer = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_expense_details)

        binding = ActivityExpenseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUpdate.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)

        expenseRepository = ExpenseRepository(applicationContext)

        val expense = intent.getSerializableExtra("EXTRA_EXPENSE") as? Expense
        if (expense != null) {
            // Use the 'expense' object to populate the details in the ExpenseDetailsActivity UI

            Log.d("TEST", "$expense")
            binding.etCheckAmount.setText(expense.checkAmount.toString())
            binding.etPersons.setText(expense.persons.toString())
            if (expense.donationAmount != null) {
                if (expense.donationAmount == 5.0) {
                    binding.rbYes.setChecked(true)
                } else {
                    binding.rbNo.setChecked(true)
                }
            }


            //TODO: Spinner data display
            val tipAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.array_tip)
            )

            binding.spTipPercentage.adapter = tipAdapter

            // get selected items
//            for tip percentage
            val tip = expense.tipPercentage.toInt()
            binding.spTipPercentage.setSelection(tipPercentage.indexOf(tip))


            binding.spTipPercentage.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {

                    Log.d("TAG", "onItemSelected: User wants to leave ${tipPercentage[position]} % of tip")
                    Log.d("TAG", "onItemSelected: User selection : ${resources.getStringArray(R.array.array_tip).get(position)}")
                    selectedTipPer = tipPercentage[position]
                    tipAmount = ((checkAmount + tax) * selectedTipPer / 100).toDouble()

//
//                    Log.d("TAG", "${tipPercentage[1]}")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.d("TAG", "onNothingSelected: User has not made any selection. Set the default percentage to 0%")
                }
            }


        }

    }


    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.btn_update -> {
                    Log.d("TAG", "onClick: btnUpdate performed")
                    updateCalculation()
                }
                R.id.btn_delete -> {
                    Log.d("TAG", "onClick: btnDelete performed")
                    deleteExpense()
//                    finish()
                }
            }
        }
    }

    private fun updateCalculation()  {
        val expense = intent.getSerializableExtra("EXTRA_EXPENSE") as? Expense
        if (expense != null) {

            var id = expense.id
            var checkAmount = expense.checkAmount
            var persons = expense.persons
            var donationAmount = expense.donationAmount.toInt()

            // checkAmount
            if (checkAmount.toString().isEmpty()) {
                binding.etCheckAmount.setError(resources.getString(R.string.error_check_amount))
            } else {
                checkAmount = (binding.etCheckAmount.text.toString().toFloatOrNull() ?: 0.0f).toDouble()
            }

            //person
            if(persons.toString().isEmpty()) {
                binding.etPersons.setError(resources.getString(R.string.error_persons))
            } else {
                persons = binding.etPersons.text.toString().toInt()
            }

            // tax
            tax = (checkAmount * 0.13).toFloat();

            val selectedIndex = binding.spTipPercentage.selectedItemPosition
            selectedTipPer = tipPercentage[selectedIndex];
            tipAmount = ((checkAmount + tax) * selectedTipPer / 100).toDouble();

            when(binding.rgDonation.checkedRadioButtonId){
                R.id.rdb_yes -> donationAmount = 5
                R.id.rdb_no -> donationAmount = 0
            }

            var billAmount = ((checkAmount + tax) + tipAmount + donationAmount).toDouble()
            var amountPerPerson = billAmount / persons

            //save expense to database
            val expenseToUpdate = Expense(
                id = id,
                checkAmount = checkAmount.toDouble(),
                persons = persons,
                tipPercentage = selectedTipPer.toDouble(),
                donationAmount = donationAmount.toDouble())

            Log.d("TAG", "Checking $expenseToUpdate")

            this.expenseRepository.updateExpense(expenseToUpdate)

            // Navigate back to HomeActivity and indicate refresh is needed
//            val homeIntent = Intent(this, HomeActivity::class.java)
//            homeIntent.putExtra("EXPENSE_UPDATED", true)
//            startActivity(homeIntent)


            finish()

        }

    }

    private fun deleteExpense() {
        val expense = intent.getSerializableExtra("EXTRA_EXPENSE") as? Expense
        if (expense != null) {
            this.expenseRepository.deleteExpense(expense)

            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
        }

    }


}