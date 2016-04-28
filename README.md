Super Simple Stock Market
=========================

Super Simple Stock Market (SSSM) is an object-oriented system designed to run trading in a fictitious Global Beverage
Corporation Exchange stock market. As well as recording trades for common and preferred stock, SSSM is able to calculate
both stock specific metrics (such as Dividend Yield and P/E Ratio) and index metrics (such as Volume Weighted Stock
Price and a GBCE specific All Share Index).

Restrictions and assumptions
----------------------------
* Prices will be given in a single unspecified currency with 100 subunits
* Minimum stock price is **0.01**
* No fractional shares
* Timestamps are given in machine time (i.e., [`Instant`][1]). The system is not timezone aware
* Trades represents committed orders and all prices are final
* Trades will not be evicted from the history (i.e., history can contain trades older than 5 minutes)
* Trades are written to the history more often than metrics are computed (i.e., system is optimized for writes)
* Since no trades are evicted and data is held in memory the system will not be submitted to unreasonable load
* SSSM is a single threaded system

Design guidelines
-----------------

### 1. Immutable model

Stocks and Trades are immutable by design. The system at this stage will not be multithreaded but immutability comes
with highly desirable characteristics such as side effect free programming and simple reasoning about the code.

Mutable collections are still used sparingly and isolated (e.g., for the trades history).

### 2. Composition vs Inheritance

Preferred and common stocks are types of stock (IS-A relationship). Fixed dividend is exclusive to preferred stocks and
dividend yield is dependent on the stock type. It would be possible to design the model relying in a type discriminator
(e.g, `StockType` enum). A mix of composition and delegation could be used to model dynamic swappable behaviour. In the
lack of such a requirement inheritance seems like a natural way to design the model respecting [SOLID][2]
[Open/Closed principle][3]. Special care is taken to follow the [Liskov Substitution Principle][4], plus encapsulation
and patterns such as static factory methods are used to hide implementation details from the API.

On the other end of the spectrum it would not make much sense to apply inheritance for different types of `Trade` since
in SSSM all trades behave the same. A `TradeType` enum discriminator is used instead.

> TODO: include class diagram

### 3. Services and Data

In accordance with [Domain-driven design][5] practices a `StockMarketService` class provides the required methods for
calculating Volume Weighted Stock price and the GBCE specific All Share Index.

The data itself is written and retrieved using a in memory implementation of `StockRepository`. Internally a
[MultiMap][6] stores trades for each kind of stock.

> TODO: include class diagram

Finally, a geometric mean [`Collector`][7] is implemented as an alternative for a private or public static utility
method in order to comply with the [Simple responsibility principle][8].

### 4. Accuracy trumps speed

Decimal values are represented with [BigDecimal][9] instead of types like `double` and `long`.

#### Precision and rounding policies:

* Internal computations uses a precision of *30* digits
* The fractional part of monetary results are scaled to the second digit
* The fractional part of percentage results are scaled to the fifth digit

[`ROUND_HALF_EVEN`][10] policy is used for rounding.

The original [Geometric Mean][11] formula is subject to underflow and overflow problems (the product of several small or
large products can outgrown the 30 digits precision). This is less likely to occur using the potentially slower
arithmetic mean of logarithms. In order to strike balance SSSM uses [an algorithm][12] that combines both formulas.

### 5. Some libraries, no containers

It is certainly possible to design a production quality toy project. On the other hand it is hard to ignore that a large
enterprise project may call for a different toolset than a self contained exercise.

Tools such as [Spring Boot][13] are able to bootstrap a production grade application with all the bells and whistles in
a matter of minutes. Still, I feel that overengineering a toy project to emulate enterprise architecture defeats the
purpose of the exercise. Thus, while I'm fully aware of the benefits that a IoC container, bean validation framework etc
could bring to the project I've chosen to stick with a basic clean design.

This does not mean ignoring tools altogether:

* [Lombok][14] is used to reduce boilerplate code
* [Guava][15] bring us that extra level of expressiveness with custom collections, preconditons, etc.
* [SLF4J][16] is used as lightweight logging facade loosely coupled to a [`java.util.logging`][17] implementation.


### 6. Testing

A suite of [TestNG][18] unit tests with [AssertJ][19] assertions is provided. [Surefire][20] reports can be generated
with:

    mvn test

While [Spock][21] was initially considered, the project is still small enough that a complex BDD framework would be
overkill. For the same reason automatic coverage, load and acceptance tests were not implemented for this version of the
software.

How to build
-------------

In order to build the project [Maven 3][22] and [Oracle JDK 8][24] are required. The project can be built with:

    mvn clean install

Super Simple Stock Market itself is IDE agnostic. Some IDEs may require [extra configuration][24] in order to play well
with Lombok.

[1]: https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
[2]: https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)
[3]: https://en.wikipedia.org/wiki/Open/closed_principle
[4]: https://en.wikipedia.org/wiki/Liskov_substitution_principle
[5]: https://en.wikipedia.org/wiki/Domain-driven_design
[6]: http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/Multimap.html
[7]: https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collector.html
[8]: https://en.wikipedia.org/wiki/Single_responsibility_principle
[9]: https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html
[10]: https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html#HALF_EVEN
[11]: https://en.wikipedia.org/wiki/Geometric_mean
[12]: http://stackoverflow.com/a/19980705/664577
[13]: http://projects.spring.io/spring-boot/
[14]: https://projectlombok.org/
[15]: https://github.com/google/guava
[16]: http://www.slf4j.org/
[17]: https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html
[18]: http://testng.org/doc/index.html
[19]: http://joel-costigliola.github.io/assertj/
[20]: https://maven.apache.org/surefire/maven-surefire-plugin/
[21]: https://code.google.com/archive/p/spock/
[22]: https://maven.apache.org/
[23]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[24]: https://projectlombok.org/download.html





