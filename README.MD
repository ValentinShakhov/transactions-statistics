**Transaction statistics application**

Technologies stack:
* Java 8
* Spring Framework 5.0.7
* Spring Boot 2.0.3
* Gradle 4.5.1
* Gradle Lombok plugin 1.14
* Gradle Spring Boot plugin 2.0.3
* JUnit 4
* Mockito 2.18.3


_StatisticsData.class_ contains an array where every index(bucket) is a transaction statistics data related to a particular moment in time (second).
Data is distributed among these buckets according to transaction timestamps and is limited by the property "window.size-seconds" (application.yml) which is currently set to 60.
Simply saying: every bucket in the array reflects transaction statistics for that particular second.

Time complexity of read/write operations is _O(1)_ since the size of array is constant and is set in the properties file.

Insertion operation shifts the data in order to keep _StatisticsData_ in actual state.

Insertion operation (and therefore shifting) is synchronized and therefore thread-safe.

While shifting, array is copied with an offset, which means the statistics data is moved to an actual bucket according to the current time, and obsolete statistics data is getting removed.

Read operation shifts data as well, however, this operation is readonly and does not provide side effect.
Read operation returns combined statistics data from all the buckets after shifting.

The application has been test-driven developed and contains necessary validation of input.

BigDecimal is used for amount input deliberately - money must be counted precisely.