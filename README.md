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

### Explanation

A simplest use-case for Reader monad is to use as DI (Dependency injection). Let's assume that we have a class that interacts with user account.

````
interface AccountService {
  fun getAccount(accoundId: String, session: Session): Future<Account>
  fun getBalance(account: Account, session: Session): Future<Amount>
  fun getStatement(account: Account, session: Session): Future<Statement>
}
````

And the concrete implementation like so

````
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

It looks a bit annoying as we need to provide `session` for all of the functions. One can provide a DI at the constructor but it has 2 drawbacks.
Firstly, your class becomes stateful and it is start to tangled with the construction of your dependency. Secondly, if you have multiple things to pass into constructor,
the constructor becomes bloated and un-scalable.

With Reader monad, you can improve above implementation to take advantage of `Reader`

````
typealias SessionReader<T> = Reader<Session, T>

interface AccountService {
  fun getAccount(accoundId: String, session: Session): SessionReader<Future<Account>>
  fun getBalance(account: Account, session: Session): SessionsReader<Future<Amount>>
  fun getStatement(account: Account, session: Session): SessionReader<Future<Statement>>
}

object ReaderAccounts : AccountService {
  
  override fun getAccount(accoundId: String, session: Session): SessionReader<Future<Account>> = 
    Reader { session: Session -> 
      session.doSomething() //do something before returning account, check authetication, status etc.
      session.fetchAccount(accountId)
    }
  
  override fun getBalance(account: Account, session: Session): SessionsReader<Future<Amount>> =
    Reader { session: Session ->
      session.doSomething()
      session.fetchAmount(account)
    }
  
  override fun getStatement(account: Account, session: Session): SessionReader<Future<Statement>> = 
    Reader { session: Session ->
      session.doSomething()
      session.fetchStatement(account)
    }
}
````

As you can see now, we are using `Reader` to abstract away our dependency usage. `Reader` is meant to be composable so you can do something really cool like chaining a series of steps in one nice operation.

````
fun getStatementReader(accountId: String) = 
  ReaderAccounts.getAccount(accountId).flatMap { ReaderAccounts.getStatement(it) }
  
fun getBalanceReader(accountId: String) =
  ReaderAccounts.getBalance(accountId).flatMap { ReaderAccounts.getBalance(it) } 
````

Nice thing about this is that we are not returning the result (yet), however we are returning the gist of steps in our operation as a descriptive `Reader`. The expression up until this point is pure, as it has no execution happening yet. 
It waits for the `session` object to be filled then produce the desired result.

````
val statement = getStatementReader("1234").runReader(Session(UserConfig("xxx"))

//use statement

val balance = getBalanceReader("1234").runReader(Session(UserConfig("xxx"))

//use balance
````

One can use this technique to provide a different dependency at will. For example, you might want to provide session that always expired. (it can be useful in testing, as you wanna see that your program behaves correctly in different scenarios.)

````
//in Test context

val balanceReader = getBalanceReader("1234")

@Test
fun test_expiredSession() {

  object AlwaysExpiredSession : Session {
    // implementation detail that makes this session always expired
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

This allows us to have operation that is reusable, and simple to understand and easy to test. Plus, there is no `mock`, no clunky setup.

