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

package org.jraf.qontoapigraphqlbridge.graphql.datafetchers.bankaccount

import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.centsToMonetaryAmount
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.qontoClient
import org.jraf.qontoapigraphqlbridge.graphql.model.bankaccount.BankAccount
import org.jraf.qontoapigraphqlbridge.graphql.model.money.Currency

const val DATA_FETCHER_BANK_ACCOUNTS_NAME = "bankAccounts"

val DATA_FETCHER_BANK_ACCOUNTS = DataFetcher { env ->
    runBlocking {
        env.qontoClient.organizations.getOrganization().bankAccounts.map { qontoBankAccount ->
            BankAccount(
                id = qontoBankAccount.slug,
                iban = qontoBankAccount.iban,
                bic = qontoBankAccount.bic,
                currency = Currency.valueOf(qontoBankAccount.currency),
                balance = centsToMonetaryAmount(qontoBankAccount.balanceCents, Currency.EUR),
                authorizedBalance = centsToMonetaryAmount(qontoBankAccount.authorizedBalanceCents, Currency.EUR)
            )
        }
    }
}