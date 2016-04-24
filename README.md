Super Simple Stock Market
=========================

Super Simple Stock Market is an object-oriented system to run trading in a fictitious Global Beverage Corporation
Exchange stock market. As well as recording trades for common and preferred stock, Super Simple Stock Market is
able to calculate both stock specific metrics (such as Dividend Yield and P/E Ratio) and index metrics
(such as Volume Weighted Stock Price and a GBCE specific All Share Index)

Restrictions and assumptions
----------------------------
* Prices will be given in a single unspecified currency with 100 subunits
* Minimum stock price is **0.01**
* No fractional shares
* Timestamps are given in machine time (i.e., [`Instant`][12]). The system is not timezone aware.

Design guidelines
-----------------

### 1. Immutable model

Stocks and Trades are immutable by design. The system at this stage will not be multithreaded but immutability comes
with highly desirable characteristics such as side effect free programming and simple reasoning about the code.

Mutable collections are still used sparingly and isolated (e.g., for the trading history).

### 2. Composition vs Inheritance

TODO: Explain model and include class diagram

### 3. Accuracy trumps speed

Decimal values are represented with [BigDecimal][1] instead of types like `double` and `long`.

#### Precision and rounding policies:

* Internal computations uses a precision of *30* digits
* The fractional part of monetary results are scaled to the second digit
* The fractional part of percentage results are scaled to the fifth digit

[`ROUND_HALF_EVEN`][2] policy is used for rounding.

The original [Geometric Mean][3] formula is subject to underflow and overflow problems (the product of several small or
large products can outgrown the 30 digits precision). This is less likely to occur using the potentially slower
arithmetic mean of logarithms. In order to strike balance we have used [an algorithm][4] that combines both formulas.

### 4. Some libraries, no containers

TODO: Describe libraries

### 5. Testing

A suite of [TestNG][8] unit tests with [AssertJ][9] assertions is provided. [Surefire][10] reports can be generated
with:

    mvn test

While [Spock][11] was initially considered, the project is still small enough that a complex BDD framework would be
overkill. For the reason automatic coverage, load and acceptance tests were not designed for this version of the
software.

How to build
-------------

In order to build the project [Maven 3][5] and [Oracle JDK 8][6] are required. The project can be built with:

    mvn clean install

Super Simple Stock Market itself is IDE agnostic. Some IDEs may require [extra configuration][7] in order to play well
with Lombok.

[1]: https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html
[2]: https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html#HALF_EVEN
[3]: https://en.wikipedia.org/wiki/Geometric_mean
[4]: http://stackoverflow.com/a/19980705/664577
[5]: https://maven.apache.org/
[6]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[7]: https://projectlombok.org/download.html
[8]: http://testng.org/doc/index.html
[9]: http://joel-costigliola.github.io/assertj/
[10]: https://maven.apache.org/surefire/maven-surefire-plugin/
[11]: https://code.google.com/archive/p/spock/
[12]: https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html