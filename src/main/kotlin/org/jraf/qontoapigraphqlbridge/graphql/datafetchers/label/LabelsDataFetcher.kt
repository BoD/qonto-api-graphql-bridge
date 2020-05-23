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

package org.jraf.qontoapigraphqlbridge.graphql.datafetchers.label

import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.getItemsPerPage
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.getPageIndex
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.qontoClient
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.toConnection
import org.jraf.qontoapigraphqlbridge.graphql.model.label.Label

const val DATA_FETCHER_LABELS_NAME = "labels"

val DATA_FETCHER_LABELS = DataFetcher { env ->
    val pageIndex = env.getPageIndex()
    val itemsPerPage = env.getItemsPerPage()
    runBlocking {
        env.qontoClient.labels.getLabelList(Pagination(pageIndex, itemsPerPage)).toConnection { qontoLabel ->
            qontoLabelToLabel(qontoLabel)
        }
    }
}

fun qontoLabelToLabel(qontoLabel: org.jraf.klibqonto.model.labels.Label): Label {
    return Label(
        id = qontoLabel.id,
        name = qontoLabel.name,
        parentLabelId = qontoLabel.parentId
    )
}
