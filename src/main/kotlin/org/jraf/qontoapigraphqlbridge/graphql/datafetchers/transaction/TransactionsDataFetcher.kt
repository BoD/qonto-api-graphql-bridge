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

package org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction

import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.centsToMonetaryAmount
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.get
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.getItemsPerPage
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.getPageIndex
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.getQontoClient
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.toConnection
import org.jraf.qontoapigraphqlbridge.graphql.model.money.Currency
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.Transaction
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionOperationType
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionSide
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionStatus
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionVatRate

const val DATA_FETCHER_TRANSACTIONS_NAME = "transactions"

val DATA_FETCHER_TRANSACTIONS = DataFetcher { env ->
    val qontoClient = env.getQontoClient()
    val pageIndex = env.getPageIndex()
    val itemsPerPage = env.getItemsPerPage()
    val bankAccountId: String = env["bankAccountId"]
    runBlocking {
        qontoClient.transactions.getTransactionList(
            bankAccountSlug = bankAccountId,
            pagination = Pagination(pageIndex, itemsPerPage)
        ).toConnection { qontoTransaction ->
            Transaction(
                id = qontoTransaction.id,
                amount = centsToMonetaryAmount(qontoTransaction.amountCents, Currency.valueOf(qontoTransaction.currency)),
                localAmount = centsToMonetaryAmount(qontoTransaction.localAmountCents, Currency.valueOf(qontoTransaction.localCurrency)),
                attachments = listOf(), // TODO
                side = TransactionSide.valueOf(qontoTransaction.side.name),
                operationType = TransactionOperationType.valueOf(qontoTransaction.operationType.name),
                counterparty = qontoTransaction.counterparty,
                settledDate = qontoTransaction.settledDate,
                emittedDate = qontoTransaction.emittedDate,
                updatedDate = qontoTransaction.updatedDate,
                status = TransactionStatus.valueOf(qontoTransaction.status.name),
                note = qontoTransaction.note,
                reference = qontoTransaction.reference,
                vatAmount = qontoTransaction.vatAmountCents?.let { centsToMonetaryAmount(it, Currency.valueOf(qontoTransaction.currency)) },
                vatRate = TransactionVatRate.CUSTOM, // TODO
                initiator = null, // TODO
                labels = listOf(), // TODO
                isAttachmentLost = qontoTransaction.attachmentLost,
                isAttachmentRequired = qontoTransaction.attachmentRequired
            )
        }
    }
}


