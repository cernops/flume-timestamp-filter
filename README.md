flume-timestamp-filter
======================
Flume interceptor that filters events selectively based on a configured passedTime checking timestamp field in the event header. This supperts either include- or exclude-based filtering.

## Getting Started
- - -
1. Clone the repository
2. Build source
~~~ sh
$ mvn clean package
~~~
3. Create interceptor directory and deploy
~~~ sh
$ mkdir -p /usr/lib/flume-ng/plugins.d/flume-timestamp-filtering-interceptor/lib
$ chown -R flume:flume /usr/lib/flume-ng/plugins.d/
$ cp /path/to/flume-timestamp-filter/target/flume-timestamp-filtering-interceptor-1.0.jar /usr/lib/flume-ng/plugins.d/flume-timestamp-filtering-interceptor/lib/
~~~
4. Configure flume.conf according to **Configuration**
~~~ sh
...
agent.sources.seqGenSrc.interceptors = i1
agent.sources.seqGenSrc.interceptors.i1.type = org.cern.flume.interceptor.TimestampFilteringInterceptor$Builder
agent.sources.seqGenSrc.interceptors.i1.passedTime = 172800
agent.sources.seqGenSrc.interceptors.i1.excludeEvents = true
...
~~~
5. Restart flume

## Configuration
- - -
        type: org.cern.flume.interceptor.TimestampFilteringInterceptor$Builder
        passedTime: Max age in seconds [86400]
        excludeEvents: If true, events that passedTime have passed are excluded, otherwise events that passedTime have passed are included [false]
        fieldName: Field name which contains millisecond timestamp [timestamp]

