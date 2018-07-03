# ReaderK

[![jcenter](https://api.bintray.com/packages/mercari-inc/maven/ReaderK/images/download.svg)](https://bintray.com/mercari-inc/maven/ReaderK/_latestVersion) 
[![Build Status](https://circleci.com/gh/mercari/ReaderK.svg?style=svg)](https://circleci.com/gh/mercari/ReaderK)
[![codecov](https://codecov.io/gh/mercari/ReaderK/branch/master/graph/badge.svg)](https://codecov.io/gh/mercari/ReaderK)

A Reader monad implemented in Kotlin

## Ideology

`Reader<In, Out>` is to provide higher abstraction of operation requires access to the external/shared environment in order to produce the desired output.

It helps you abstract away the computation into the very last moment (until `runReader` function is called). One of the benefit for Reader monad is to make the DI (dependency injection) easy, safe and straightforward.

## Installation

### Gradle 

The latest version is [![jcenter](https://api.bintray.com/packages/mercari-inc/maven/ReaderK/images/download.svg)](https://bintray.com/mercari-inc/maven/ReaderK/_latestVersion) 

``` Groovy
repositories {
  jcenter()
}

dependencies {
  compile 'com.mercari.readerk:readerk:<latest-version>' 
}

```

## Explanation

`Reader` helps us perform the same computation with different values.

The simplest use-case for Reader monad is to use as a DI (Dependency injection). Let's assume that we have a class that interacts with user account.

```` Kotlin
interface AccountService {
  fun getAccount(accoundId: String, session: Session): Future<Account>
  fun getBalance(account: Account, session: Session): Future<Amount>
  fun getStatement(account: Account, session: Session): Future<Statement>
}
````

And the concrete implementation like the following

```` Kotlin
object Accounts : AccountService {

  override fun getAccount(accoundId: String, session: Session): Future<Account> {
    //real implementation
  }
  
  override fun getBalance(account: Account, session: Session): Future<Amount> {
    //real implementation
  }
  
  override fun getStatement(account: Account, session: Session): Future<Statement> {
    //real implementation
  }
}
````

It looks a bit annoying as we need to provide `session` for all of the methods. One can provide a DI at the constructor but it has 2 drawbacks.

Firstly, your class becomes stateful and it is start to be tangled with the construction of your dependency. 

Secondly, once you have multiple things to pass into constructor, the constructor became bloated and un-scalable.

With Reader monad, you can improve above implementation to take advantage of `Reader` like this.

```` Kotlin
typealias SessionReader<T> = Reader<Session, T>

interface AccountService {
  fun getAccount(accoundId: String): SessionReader<Future<Account>>
  fun getBalance(account: Account): SessionsReader<Future<Amount>>
  fun getStatement(account: Account): SessionReader<Future<Statement>>
}

object ReaderAccounts : AccountService {
  
  override fun getAccount(accoundId: String): SessionReader<Future<Account>> = 
    Reader { session: Session -> 
      session.doSomething() //do something before returning account, check authetication, status etc.
      session.fetchAccount(accountId)
    }
  
  override fun getBalance(account: Account): SessionsReader<Future<Amount>> =
    Reader { session: Session ->
      session.doSomething()
      session.fetchAmount(account)
    }
  
  override fun getStatement(account: Account): SessionReader<Future<Statement>> = 
    Reader { session: Session ->
      session.doSomething()
      session.fetchStatement(account)
    }
}
````

As you can see now, we are using `Reader` to abstract away our dependency usage. `Reader` is meant to be composable so you can do something really cool like chaining a series of steps in one nice operation.

```` Kotlin
//somewhere in your application

fun getStatementReader(accountId: String) = 
  ReaderAccounts.getAccount(accountId).flatMap { ReaderAccounts.getStatement(it) }
  
fun getBalanceReader(accountId: String) =
  ReaderAccounts.getBalance(accountId).flatMap { ReaderAccounts.getBalance(it) } 
````

Nice thing about this is that we are not returning the result (yet), however we are returning the gist of steps in our operation as a descriptive `Reader`. The expression up until this point is pure, as it has no execution happening yet. 
It waits for the `session` object to be filled then produce the desired result.

```` Kotlin
val statement = getStatementReader("1234").runReader(Session(UserConfig("xxx"))

//use statement

val balance = getBalanceReader("1234").runReader(Session(UserConfig("xxx"))

//use balance
````

One can use this technique to provide a different dependency at will. For example, you might want to provide session that always expired. (it can be useful in testing, as you wanna see that your program behaves correctly in different scenarios.)

```` Kotlin
//in Test context

val balanceReader = getBalanceReader("1234")

@Test
fun test_expiredSession() {

  object AlwaysExpiredSession : Session {
    // implementation detail that makes this session expired
  }

  val balance = balanceReader.runReader(AlwaysExpiredSession)
  
  assertFalse(balance.valid) //balance is not valid because Session is expired
}

@Test
fun test_normalSession() {
  val balance = balanceReader.runReader(ValidSession())
  
  assertTrue(balance.valid)
}
````

This allows us to have the same (yet pure) operation that is reusable, and simple to understand and easy to test. Plus, there is no `mock`, no clunky setup. You just provide different environments at the different time. 

## Features

#### [Map](https://github.com/mercari/ReaderK/blob/master/readerk/src/main/java/com/merpay/readerk/Reader.kt#L21) & [Flatmap](https://github.com/mercari/ReaderK/blob/master/readerk/src/main/java/com/merpay/readerk/Reader.kt#L17)

`map` and `flatMap` are _common_ transformation function. `map` transform Reader of one output to another type of output.

E.g. from the previous example, you want to get name of the account. You can use `map` to transform Reader<Session, Account> to Reader<Session, String> where `String` represents name of account.

```` Kotlin
val accountName = ReaderAccounts.getAccount("1234").map { it.name }.runReader(Session())
````

`flatMap` is handling the transformation in a similar fashion but the allows to accept `(U) -> Reader<T, Another>` instead.

#### [Pure](https://github.com/mercari/ReaderK/blob/master/readerk/src/main/java/com/merpay/readerk/Reader.kt#L14)

`pure` is a bridge into the `Reader`, it basically accept one value with any type and then return that type as an output.

#### [Ask](https://github.com/mercari/ReaderK/blob/master/readerk/src/main/java/com/merpay/readerk/Reader.kt#L7)

`ask` is an identity Reader, `ask` will return the environment that it passed on so it can be useful, if you want to pass along the input in the chain.

## Licenses

ReaderK is released under the [MIT](http://opensource.org/licenses/MIT) license.
