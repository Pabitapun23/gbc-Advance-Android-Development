package com.jk.share

import com.jk.share.models.Expense

interface OnExpenseClickListener {
    fun onExpenseSelected(expense: Expense)
}