package com.example.chitassist.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chits",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class ChitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val organizer: String?,
    val chitNumber: String?,
    val description: String?,
    val totalValue: Double,
    val monthlyContribution: Double,
    val numberOfInstallments: Int,
    val startDate: Long,
    val paymentDueDay: Int,
    val intervalMonths: Int,
    val status: String,    // ACTIVE, COMPLETED, CANCELLED
    val dividendMode: String = "EXCLUDING_WINNER",
    val commissionAmount: Double = 0.0,
    val completedInstallments: Int = 0,
    val isWon: Boolean = false,
    val winningInstallmentNumber: Int? = null,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
