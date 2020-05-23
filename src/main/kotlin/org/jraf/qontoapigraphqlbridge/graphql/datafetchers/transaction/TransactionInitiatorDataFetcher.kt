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
import org.jraf.klibqonto.model.memberships.Membership
import org.jraf.klibqonto.model.pagination.Page
import org.jraf.klibqonto.model.pagination.Pagination
import org.jraf.qontoapigraphqlbridge.graphql.context.Context
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.member.qontoMembershipToMember
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.qontoClient
import org.jraf.qontoapigraphqlbridge.graphql.model.member.Member
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.Transaction

const val DATA_FETCHER_TRANSACTION_INITIATOR_NAME = "initiator"

val DATA_FETCHER_TRANSACTION_INITIATOR = DataFetcher { env ->
    val transaction: Transaction = env.getSource()
    if (transaction.initiatorId == null) {
        null
    } else {
        runBlocking {
            env.getAllMemberList()[transaction.initiatorId]
        }
    }
}

private suspend fun DataFetchingEnvironment.getAllMemberList(): Map<String, Member> {
    val context = getContext<Context>()
    if (context.allMembers == null) {
        val allMembershipList = qontoClient.memberships.getAllMembershipList()
        val allMemberList = allMembershipList.map(::qontoMembershipToMember)
        context.allMembers = allMemberList.associateBy { it.id }
    }
    return context.allMembers!!
}

private suspend fun QontoClient.Memberships.getAllMembershipList(): List<Membership> {
    val allMembershipList = mutableListOf<Membership>()
    var pagination: Pagination? = Pagination()
    var page: Page<Membership>
    while (pagination != null) {
        page = getMembershipList(pagination)
        allMembershipList.addAll(page.items)
        pagination = page.nextPagination
    }
    return allMembershipList
}