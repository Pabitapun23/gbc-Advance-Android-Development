package com.jk.share

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.jk.share.databinding.ActivityHomeBinding
import com.jk.share.models.Expense
import com.pp.share.repositories.ExpenseRepository
import java.io.Serializable

class HomeActivity : AppCompatActivity() , OnExpenseClickListener{

    private val TAG = this.toString()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var expenseRepository : ExpenseRepository

    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseArrayList: ArrayList<Expense>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseArrayList = ArrayList()
        expenseAdapter = ExpenseAdapter(this, expenseArrayList, this)
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.addItemDecoration(
            DividerItemDecoration(
                this.applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )

        this.binding.rvExpenses.adapter = expenseAdapter

        expenseRepository = ExpenseRepository(applicationContext)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_add_expense -> {
                Log.d(TAG, "onOptionsItemSelected: Add Expense option is selected")

                val mainIntent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(mainIntent)

                return true
            }
            R.id.action_profile -> {
                Log.d(TAG, "onOptionsItemSelected: Profile option is selected")

                val mainIntent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(mainIntent)
                return true
            }
            R.id.action_sign_out -> {
                Log.d(TAG, "onOptionsItemSelected: Sign Out option is selected")
                FirebaseAuth.getInstance().signOut()
                this@HomeActivity.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onResume() {
        super.onResume()

        expenseRepository.retrieveAllExpenses()

        expenseRepository.allExpenses.observe(this, androidx.lifecycle.Observer { expenseList ->
            Log.d(TAG, "expenseList - ${expenseList}")

            if(expenseList != null){
//                clear the existing list to avoid duplicate records
                expenseArrayList.clear()
                expenseArrayList.addAll(expenseList)
                expenseAdapter.notifyDataSetChanged()

                //                for (expense in expenseList){
//                    Log.e(TAG, "onResume: Expense : ${expense}", )
//                    if (!expenseArrayList.contains(expense)) {
//                        expenseArrayList.add(expense)
//                        expenseAdapter.notifyDataSetChanged()
//                    }
//                }

            }
        })


    }

    override fun onExpenseSelected(expense: Expense) {

        val mainIntent = Intent(this, ExpenseDetailsActivity::class.java)
        mainIntent.putExtra("EXTRA_EXPENSE", expense as Serializable)
//        Log.d("CHECK", "$expenseArrayList")
        startActivity(mainIntent)
    }
}