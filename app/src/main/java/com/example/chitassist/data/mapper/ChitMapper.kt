package com.example.chitassist.data.mapper

import com.example.chitassist.core.database.entity.ChitEntity
import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.ChitStatus
import com.example.chitassist.domain.model.DividendMode

fun ChitEntity.toDomain(): Chit {
    return Chit(
        id = id,
        userId = userId,
        name = name,
        organizer = organizer,
        chitNumber = chitNumber,
        description = description,
        totalValue = totalValue,
        monthlyContribution = monthlyContribution,
        numberOfInstallments = numberOfInstallments,
        startDate = startDate,
        paymentDueDay = paymentDueDay,
        intervalMonths = intervalMonths,
        status = ChitStatus.valueOf(status),
        dividendMode = DividendMode.valueOf(dividendMode),
        commissionAmount = commissionAmount,
        completedInstallments = completedInstallments,
        isWon = isWon,
        winningInstallmentNumber = winningInstallmentNumber,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Chit.toEntity(): ChitEntity {
    return ChitEntity(
        id = id,
        userId = userId,
        name = name,
        organizer = organizer,
        chitNumber = chitNumber,
        description = description,
        totalValue = totalValue,
        monthlyContribution = monthlyContribution,
        numberOfInstallments = numberOfInstallments,
        startDate = startDate,
        paymentDueDay = paymentDueDay,
        intervalMonths = intervalMonths,
        status = status.name,
        dividendMode = dividendMode.name,
        commissionAmount = commissionAmount,
        completedInstallments = completedInstallments,
        isWon = isWon,
        winningInstallmentNumber = winningInstallmentNumber,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
