package com.example.chitassist.data.mapper

import com.example.chitassist.core.database.entity.PaymentEntity
import com.example.chitassist.domain.model.Payment
import com.example.chitassist.domain.model.PaymentStatus

fun PaymentEntity.toDomain(): Payment {
    return Payment(
        id = id,
        chitId = chitId,
        chitName = chitName,
        installmentNumber = installmentNumber,
        dueDate = dueDate,
        amount = amount,
        dividend = dividend,
        bidAmount = bidAmount,
        isWinner = isWinner,
        paidDate = paidDate,
        status = PaymentStatus.valueOf(status),
        notes = notes
    )
}

fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        id = id,
        chitId = chitId,
        chitName = chitName,
        installmentNumber = installmentNumber,
        dueDate = dueDate,
        amount = amount,
        dividend = dividend,
        bidAmount = bidAmount,
        isWinner = isWinner,
        paidDate = paidDate,
        status = status.name,
        notes = notes
    )
}
