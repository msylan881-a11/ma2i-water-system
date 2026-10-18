package com.ma2i.water.data.model

import java.io.Serializable

// Simple data model for a Sale. Adapt fields to your real entity.
data class Sale(
    val id: Long = 0,
    val customerId: Long,
    val amount: Double,
    val note: String? = null
) : Serializable
