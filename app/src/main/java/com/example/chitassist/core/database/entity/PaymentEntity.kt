package com.example.chitassist.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = ChitEntity::class,
            parentColumns = ["id"],
            childColumns = ["chitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chitId"])]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chitId: Long,
    val chitName: String = "",
    val installmentNumber: Int,
    val dueDate: Long,
    val amount: Double,
    val dividend: Double = 0.0,
    val bidAmount: Double = 0.0,
    val isWinner: Boolean = false,
    val paidDate: Long? = null,
    val status: String, // PENDING, PAID, OVERDUE
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
