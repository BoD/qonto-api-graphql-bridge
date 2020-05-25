# Qonto API GraphQL Bridge

This is a [GraphQL](https://graphql.org/) bridge to the [Qonto API](https://api-doc.qonto.eu/2.0/welcome/).

Written in Kotlin, using [Ktor](https://github.com/ktorio/ktor), [GraphQL Java](https://www.graphql-java.com/) and [klibqonto](https://github.com/BoD/klibqonto).

## Why
- This project was mainly created for me to learn more about GraphQL
- But this may _actually_ be useful to people as this bridge improves the original REST API (IMHO),
with all the benefits brought by GraphQL (strong typing, documentation, discoverability,
tooling, etc.).

## Schema
The schema is available [here](https://raw.githubusercontent.com/BoD/qonto-api-graphql-bridge/master/src/main/resources/schema.graphqls).

## Authentication
Authentication is header based, as documented [here](https://api-doc.qonto.eu/2.0/welcome/authentication).

I recommend using the [ModHeader extension](https://bewisse.com/modheader/) to easily play with the API within a browser. 

## Demo instance
An instance is running [here](https://qonto-api-graphql-bridge.herokuapp.com/) for your convenience. Note that this is hosted on the
free tier of Heroku so of course, do not use it for any kind of serious project, it's
here for demonstration purposes only.

## Example query
Here's an example query demonstrating the service:

```graphql
fragment allPageInfo on PageInfo {
  pageIndex
  nextPageIndex
  previousPageIndex
}

query {
  organization {
    id
  }

  bankAccounts {
    id
    iban
    bic
    currency
    authorizedBalance {
      currency
      amount
    }
    balance {
      currency
      amount
    }
  }

  labels(pageIndex: 1, itemsPerPage: 4) {
    totalCount
    nodes {
      id
      name
      parentLabelId
    }
    pageInfo {
      ...allPageInfo
    }
  }

  members(pageIndex: 1, itemsPerPage: 4) {
    totalCount
    nodes {
      firstName
      lastName
    }
    pageInfo {
      ...allPageInfo
    }
  }

  transactions(
    bankAccountId: "zgluteks-inc-4242-bank-account-1",
    pageIndex: 1,
    itemsPerPage: 4,
    statusFilter: [PENDING, REVERSED],
    orderBy: {field: UPDATED_DATE, direction: ASC},
    updatedDateFrom: "2020-01-01T00:00:00.000Z"
    updatedDateTo: "2020-01-08T00:00:00.000Z"
  ) {
    totalCount
    nodes {
      id
      status
      counterparty
      emittedDate
      updatedDate
      status
      localAmount {
        amount
        currency
      }
      initiator {
        firstName
        lastName
      }
      labels {
        name
      }
      attachments {
        id
        creationDate
        fileName
        size
        contentType
        url
      }
      vatRate
      vatAmount {
        amount
        currency
      }
    }
    pageInfo {
      ...allPageInfo
    }
  }
}
```

## A word on pagination
Ideally, I would have preferred to implement [Relay cursor based pagination](https://relay.dev/graphql/connections.htm), but unfortunately
this is not really practical as the underlying API uses page based pagination.

Therefore, this bridge uses page based pagination as well.


## Author and licence
_Note: this project is not officially related to or endorsed by Qonto or Olinda SAS._

Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
