/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.qontoapigraphqlbridge.graphql.model.transaction

import org.jraf.klibqonto.model.attachments.Attachment
import org.jraf.qontoapigraphqlbridge.graphql.model.money.MonetaryAmount
import java.util.Date

data class Transaction(
    val id: String,
    val amount: MonetaryAmount,
    val localAmount: MonetaryAmount,
    val attachments: List<Attachment>,
    val side: TransactionSide,
    val operationType: TransactionOperationType,
    val counterparty: String,
    val settledDate: Date?,
    val emittedDate: Date,
    val updatedDate: Date,
    val status: TransactionStatus,
    val note: String?,
    val reference: String?,
    val vatAmount: MonetaryAmount?,
    val vatRate: TransactionVatRate?,
    val initiatorId: String?,
    val labelIds: List<String>,
    val isAttachmentLost: Boolean,
    val isAttachmentRequired: Boolean
)