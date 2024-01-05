package com.jk.share.models

import java.io.Serializable
import java.util.UUID

// Model
data class Expense(
    // Universal Unique Identifier
    var id : String = UUID.randomUUID().toString(),
    var checkAmount : Double = 0.0,
    var persons : Int = 2,
    var tipPercentage : Double = 0.0,
    var donationAmount : Double = 0.0
) : Serializable {
    fun getAmountPerPerson(): Double {
        val tax = (checkAmount * 0.13).toFloat();
        val tipAmount = ((checkAmount + tax) * tipPercentage / 100).toDouble();
        val totalAmount = ((checkAmount + tax) + tipAmount + donationAmount).toDouble()
        val amountPerPerson = totalAmount / persons

        return amountPerPerson
    }
}