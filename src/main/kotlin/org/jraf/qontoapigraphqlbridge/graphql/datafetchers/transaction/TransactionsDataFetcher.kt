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
import org.jraf.klibqonto.client.QontoClient.Transactions.SortField
import org.jraf.klibqonto.client.QontoClient.Transactions.SortOrder
import org.jraf.klibqonto.model.dates.DateRange
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.klibqonto.model.transactions.Transaction.Status
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.centsToMonetaryAmount
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.get
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.itemsPerPage
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.pageIndex
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.qontoClient
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.toConnection
import org.jraf.qontoapigraphqlbridge.graphql.model.lists.OrderDirection
import org.jraf.qontoapigraphqlbridge.graphql.model.money.Currency
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.Transaction
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionCategory
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionOperationType
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionSide
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionStatus
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionVatRate
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.TransactionsOrder
import java.util.Date

const val DATA_FETCHER_TRANSACTIONS_NAME = "transactions"

val DATA_FETCHER_TRANSACTIONS = DataFetcher { env ->
    val qontoClient = env.qontoClient
    val pageIndex = env.pageIndex
    val itemsPerPage = env.itemsPerPage
    val bankAccountId: String = env["bankAccountId"]
    val statusFilter: List<String> = env["statusFilter"]
    val orderBy: TransactionsOrder = env["orderBy"]
    val updatedDateFrom: Date? = env["updatedDateFrom"]
    val updatedDateTo: Date? = env["updatedDateTo"]
    val settledDateFrom: Date? = env["settledDateFrom"]
    val settledDateTo: Date? = env["settledDateTo"]
    runBlocking {
        try {
            qontoClient.transactions.getTransactionList(
                bankAccountSlug = bankAccountId,
                pagination = Pagination(pageIndex, itemsPerPage),
                status = statusFilter.map { Status.valueOf(it) }.toSet(),
                sortField = SortField.valueOf(orderBy.field.name),
                sortOrder = when (orderBy.direction) {
                    OrderDirection.ASC -> SortOrder.ASCENDING
                    OrderDirection.DESC -> SortOrder.DESCENDING
                },
                updatedDateRange = DateRange(from = updatedDateFrom, to = updatedDateTo),
                settledDateRange = DateRange(from = settledDateFrom, to = settledDateTo)
            ).toConnection { qontoTransaction ->
                Transaction(
                    id = qontoTransaction.id,
                    amount = centsToMonetaryAmount(qontoTransaction.amountCents, Currency.valueOf(qontoTransaction.currency)),
                    localAmount = centsToMonetaryAmount(qontoTransaction.localAmountCents, Currency.valueOf(qontoTransaction.localCurrency)),
                    attachmentIds = qontoTransaction.attachmentIds,
                    side = TransactionSide.valueOf(qontoTransaction.side.name),
                    operationType = TransactionOperationType.valueOf(qontoTransaction.operationType.name),
                    counterparty = qontoTransaction.counterparty,
                    settledDate = qontoTransaction.settledDate,
                    emittedDate = qontoTransaction.emittedDate,
                    updatedDate = qontoTransaction.updatedDate,
                    status = TransactionStatus.valueOf(qontoTransaction.status.name),
                    category = TransactionCategory.valueOf(qontoTransaction.category.name),
                    note = qontoTransaction.note,
                    reference = qontoTransaction.reference,
                    vatAmount = qontoTransaction.vatAmountCents?.let { centsToMonetaryAmount(it, Currency.valueOf(qontoTransaction.currency)) },
                    vatRate = TransactionVatRate.fromFloat(qontoTransaction.vatRate),
                    initiatorId = qontoTransaction.initiatorId,
                    labelIds = qontoTransaction.labelIds,
                    isAttachmentLost = qontoTransaction.attachmentLost,
                    isAttachmentRequired = qontoTransaction.attachmentRequired
                )
            }
        } catch (t: Throwable) {
            // TODO This is a very crude way of analyzing the exception - this can only be improved
            // after this issue is fixed: https://github.com/BoD/klibqonto/issues/1
            if (t.message?.contains("404") == true) {
                throw Exception("Unknown bankAccountId '$bankAccountId'")
            }
            throw t
        }
    }
}
