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
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.runBlocking
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.qontoapigraphqlbridge.graphql.context.Context
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.label.qontoLabelToLabel
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.qontoClient
import org.jraf.qontoapigraphqlbridge.graphql.model.label.Label
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.Transaction

typealias QontoLabel = org.jraf.klibqonto.model.labels.Label

const val DATA_FETCHER_TRANSACTION_LABELS_NAME = "labels"

val DATA_FETCHER_TRANSACTION_LABELS = DataFetcher { env ->
    val transaction: Transaction = env.getSource()
    if (transaction.labelIds.isEmpty()) {
        emptyList<Label>()
    } else {
        runBlocking {
            val allLabelList = env.getAllLabelList()
            transaction.labelIds.map { allLabelList[it] }
        }
    }
}

private suspend fun DataFetchingEnvironment.getAllLabelList(): Map<String, Label> {
    val context = getContext<Context>()
    if (context.allLabels == null) {
        val allQontoLabelList = qontoClient.labels.getAllQontoLabelList()
        val allLabelList = allQontoLabelList.map(::qontoLabelToLabel)
        context.allLabels = allLabelList.associateBy { it.id }
    }
    return context.allLabels!!
}

private suspend fun QontoClient.Labels.getAllQontoLabelList(): List<QontoLabel> {
    val allLabelList = mutableListOf<QontoLabel>()
    var pagination: Pagination? = Pagination()
    var page: Page<QontoLabel>
    while (pagination != null) {
        page = getLabelList(pagination)
        allLabelList.addAll(page.items)
        pagination = page.nextPagination
    }
    return allLabelList
}