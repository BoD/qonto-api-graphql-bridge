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

package org.jraf.qontoapigraphqlbridge.graphql.datafetchers

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetchingEnvironment
import org.jraf.klibqonto.client.QontoClient
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.qontoapigraphqlbridge.graphql.context.Context
import org.jraf.qontoapigraphqlbridge.graphql.model.lists.pagination.Connection
import org.jraf.qontoapigraphqlbridge.graphql.model.lists.pagination.PageInfo
import org.jraf.qontoapigraphqlbridge.graphql.model.money.Currency
import org.jraf.qontoapigraphqlbridge.graphql.model.money.MonetaryAmount
import org.jraf.qontoapigraphqlbridge.qontoapi.QontoApi

@Suppress("ObjectPropertyName")
val _JACKSON_OBJECT_MAPPER = jacksonObjectMapper()

val DataFetchingEnvironment.qontoClient: QontoClient
    get() {
        val context = getContext<Context>()
        if (context.qontoClient == null) {
            val authenticationInformation = getContext<Context>().authenticationInformation
            context.qontoClient = QontoApi(authenticationInformation).qontoClient
        }
        return context.qontoClient!!
    }

val DataFetchingEnvironment.pageIndex: Int
    get() {
        return getArgument("pageIndex")
    }

val DataFetchingEnvironment.itemsPerPage: Int
    get() {
        return getArgument("itemsPerPage")
    }

inline operator fun <reified T> DataFetchingEnvironment.get(name: String): T = _JACKSON_OBJECT_MAPPER.convertValue(getArgument(name))

fun <QontoType : Any, GraphqlType : Any> Page<QontoType>.toConnection(mapper: (QontoType) -> GraphqlType): Connection<GraphqlType> {
    return Connection(
        nodes = items.map(mapper),
        pageInfo = PageInfo(
            pageIndex = pageIndex,
            nextPageIndex = nextPagination?.pageIndex,
            previousPageIndex = previousPagination?.pageIndex
        ),
        totalCount = totalItems
    )
}

fun centsToMonetaryAmount(cents: Long, currency: Currency): MonetaryAmount {
    return MonetaryAmount((cents.toDouble() / 100.0).toString(), currency)
}
