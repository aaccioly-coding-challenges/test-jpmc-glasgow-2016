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
* Minimum fixed dividend is **1%**
* No fractional shares
* P/E Ratio is based on past performance (i.e, on last dividend paid)
* Timestamps are given in machine time (i.e., [`Instant`][1]); the system is not timezone aware
* Trades represents committed orders and all prices are final
* Trades will not be evicted from the history (i.e., history can contain trades older than 5 minutes)
* Trades are written to the history more often than metrics are computed (i.e., system is optimized for writes)
* Since no trades are evicted and data is held in memory the system will not be submitted to unreasonable load
* SSSM is a single threaded system

Design guidelines
-----------------

### 1. Immutable model

Stocks and Trades are immutable by design. The system at this stage is not multithreaded but immutability comes with
highly desirable characteristics such as side effect free programming and simple reasoning about the code.

Mutable collections are still used sparingly and isolated (e.g., for the trades history).

### 2. Composition vs Inheritance

Preferred and common stocks are types of stock (IS-A relationship). Fixed dividend is exclusive to preferred stocks and
dividend yield is dependent on the stock type. It would be possible to design the model relying in a type discriminator
(e.g, `StockType` enum). A mix of composition and delegation could be used to model dynamic swappable behaviour. In the
lack of such a requirement inheritance seems like a natural way to design the model respecting [SOLID][2]
[Open/Closed principle][3]. Special care is taken to follow the [Liskov Substitution Principle][4], plus encapsulation
and patterns such as static factory methods are used to hide implementation details from the API.

[![Class Diagram - Stocks][23]][23]
On the other end of the spectrum it would not make much sense to apply inheritance for different types of `Trade` since
in SSSM all trades behave the same. A `TradeIndicator` enum discriminator is used instead.

[![Class Diagram - Trades][24]][24]

### 3. Services and Data

In accordance with [Domain-driven design][5] practices a `StockMarketService` class provides the required methods for
calculating Volume Weighted Stock price and the GBCE specific All Share Index.

The data itself is written and retrieved using a in memory implementation of `StockRepository`. Internally a
[MultiMap][6] stores trades for each kind of stock.

[![Class Diagram - Service and Repository][25]][25]

Finally, a geometric mean summary [`Collector`][7] is implemented as an alternative for a private or public static utility
method in order to comply with the [Simple responsibility principle][8].

### 4. Accuracy trumps speed

Decimal values are represented with [BigDecimal][9] instead of types like `double` and `long`.

#### Precision and rounding policies:

* Internal computations uses a precision of *30* digits
* The fractional part of monetary values are scaled to the second digit
* The fractional part of percentage values are scaled to the fifth digit

[`ROUND_HALF_EVEN`][10] policy is used for rounding.

### 5. Some libraries, no containers

It is certainly possible to design a production quality toy project. On the other hand it is hard to ignore that a large
enterprise project may call for a different toolset than a self contained exercise.

Tools such as [Spring Boot][11] are able to bootstrap a production grade application with all the bells and whistles in
a matter of minutes. Still, I feel that overengineering a toy project to emulate enterprise architecture defeats the
purpose of the exercise. Thus, while I'm fully aware of the benefits that IoC containers, a bean validation framework
etc could bring to the project I've chosen to stick with a basic clean design.

This does not mean ignoring tools altogether:

* [Lombok][12] is used to reduce boilerplate code
* [Guava][13] bring us that extra level of expressiveness with custom collections, preconditions, etc.
* [SLF4J][14] is used as lightweight logging facade loosely coupled to a [`java.util.logging`][15] implementation.


### 6. Testing

A suite of [TestNG][16] unit tests with [AssertJ][17] assertions is provided. [Surefire][18] reports can be generated
with:

    mvn test

While [Spock][19] was initially considered, the project is still small enough that a complex BDD framework would be
overkill. For the same reason automatic coverage, load and acceptance tests were not implemented for this version of the
software.

How to build
-------------

In order to build the project [Maven 3][20] and [Oracle JDK 8][21] are required. The project can be built with:

    mvn clean install

Super Simple Stock Market itself is IDE agnostic. Some IDEs may require [extra configuration][22] in order to play well
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
[11]: http://projects.spring.io/spring-boot/
[12]: https://projectlombok.org/
[13]: https://github.com/google/guava
[14]: http://www.slf4j.org/
[15]: https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html
[16]: http://testng.org/doc/index.html
[17]: http://joel-costigliola.github.io/assertj/
[18]: https://maven.apache.org/surefire/maven-surefire-plugin/
[19]: https://code.google.com/archive/p/spock/
[20]: https://maven.apache.org/
[21]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[22]: https://projectlombok.org/download.html

[23]: src/main/docs/images/stock_class_diagram.png
[24]: src/main/docs/images/trade_class_diagram.png
[25]: src/main/docs/images/service_and_repository_class_diagram.png




