package com.jk.share

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jk.share.databinding.RvExpenseItemBinding
import com.jk.share.models.Expense
import java.math.RoundingMode
import java.text.DecimalFormat

class ExpenseAdapter(
    private val context: Context,
    private var expenses: ArrayList<Expense>,
    private val clickListener: OnExpenseClickListener
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder( RvExpenseItemBinding.inflate( LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position], clickListener)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    class ExpenseViewHolder(var binding: RvExpenseItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense, clickListener: OnExpenseClickListener) {
            binding.tvCheckAmountValue.setText(expense.checkAmount.toString())
            binding.tvPersonsValue.setText(expense.persons.toString())
//            binding.tvAmountValue.setText(expense.getAmountPerPerson().toString())
            binding.tvAmountValue.setText(roundTo(expense.getAmountPerPerson()))

            itemView.setOnClickListener { clickListener.onExpenseSelected(expense) }
        }
        fun roundTo(number: Double): String? {
            val df = DecimalFormat("#,###,###.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number)
        }
    }
}