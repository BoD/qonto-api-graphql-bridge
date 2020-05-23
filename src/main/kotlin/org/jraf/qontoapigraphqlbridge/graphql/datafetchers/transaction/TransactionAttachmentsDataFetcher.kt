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
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.qontoClient
import org.jraf.qontoapigraphqlbridge.graphql.model.attachment.Attachment
import org.jraf.qontoapigraphqlbridge.graphql.model.transaction.Transaction

const val DATA_FETCHER_TRANSACTION_ATTACHMENTS_NAME = "attachments"

val DATA_FETCHER_TRANSACTION_ATTACHMENTS = DataFetcher { env ->
    val transaction: Transaction = env.getSource()
    runBlocking {
        transaction.attachmentIds
            .map { id -> env.qontoClient.attachments.getAttachment(id) }
            .map { qontoAttachment ->
                Attachment(
                    id = qontoAttachment.id,
                    creationDate = qontoAttachment.createdDate,
                    fileName = qontoAttachment.fileName,
                    contentType = qontoAttachment.contentType,
                    size = qontoAttachment.size,
                    url = qontoAttachment.url
                )
            }
    }
}