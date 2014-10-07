/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cern.flume.interceptor;

import static org.cern.flume.interceptor.TimestampFilteringInterceptor.Constants.DEFAULT_EXCLUDE_EVENTS;
import static org.cern.flume.interceptor.TimestampFilteringInterceptor.Constants.DEFAULT_PASSED_TIME;
import static org.cern.flume.interceptor.TimestampFilteringInterceptor.Constants.DEFAULT_FIELD_NAME;
import static org.cern.flume.interceptor.TimestampFilteringInterceptor.Constants.EXCLUDE_EVENTS;
import static org.cern.flume.interceptor.TimestampFilteringInterceptor.Constants.PASSED_TIME;
import static org.cern.flume.interceptor.TimestampFilteringInterceptor.Constants.FIELD_NAME;

import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.StaticInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Interceptor that filters events selectively based on a configured passedTime
 * checking timestamp field in the event header.
 *
 * This supperts either include- or exclude-based filtering.
 *
 * Properties:<p>
 *
 *   passedTime: Max age in seconds.
 *              (default is 86400)<p>
 *   excludeEvents: If true, events that passedTime have passed are excluded,
 *                  otherwise events that passedTime have passed are included
 *                  (default is false)<p>
 *   fieldName: Field name which contains millisecond timestamp
 *              (default is timestamp)<p>
 *
 * Sample config:<p>
 *
 * <code>
 *   agent.sources.r1.channels = c1<p>
 *   agent.sources.r1.type = http<p>
 *   agent.sources.r1.port = 9000<p>
 *   agent.sources.r1.interceptors = i1<p>
 *   agent.sources.r1.interceptors.i1.type = org.cern.flume.interceptor.TimestampFilteringInterceptor$Builder<p>
 *   agent.sources.r1.interceptors.i1.passedTime = 172800<p>
 *   agent.sources.r1.intercepotrs.i1.excludeEvents = true<p>
 * </code>
 *
 */
public class TimestampFilteringInterceptor implements Interceptor {

  private static final Logger logger = LoggerFactory
      .getLogger(StaticInterceptor.class);

  private final long timeThreshold;
  private final boolean excludeEvents;
  private final String fieldName;

  /**
   * Only {@link TimestampFilteringInterceptor.Builder} can build me
   */
  private TimestampFilteringInterceptor(long passedTime, boolean excludeEvents, String fieldName) {
    this.timeThreshold = System.currentTimeMillis() - passedTime * 1000;
    this.excludeEvents = excludeEvents;
    this.fieldName = fieldName;
  }

  @Override
  public void initialize() {
    // no-op
  }


  @Override
  /**
   * Returns the event if it passes the timestamp filter and null
   * otherwise.
   */
  public Event intercept(Event event) {
    Map<String, String> headers = event.getHeaders();
    if (headers.containsKey(fieldName)) {
      long timestamp = Long.parseLong(headers.get(fieldName));
      if (!excludeEvents) {
        if (timestamp < timeThreshold) {
          return event;
        } else {
          logger.debug(String.format(
              "TimestampFilteringInterceptor: drop data (%s: %s)", fieldName, timestamp));
          return null;
        }
      } else {
        if (timestamp < timeThreshold) {
          logger.debug(String.format(
              "TimestampFilteringInterceptor: drop data (%s: %s)", fieldName, timestamp));
          return null;
        } else {
          return event;
        }
      }
    } else {
      return event;
    }
  }

  /**
   * Returns the set of events which pass filters, according to
   * {@link #intercept(Event)}.
   * @param events
   * @return
   */
  @Override
  public List<Event> intercept(List<Event> events) {
    List<Event> out = Lists.newArrayList();
    for (Event event : events) {
      Event outEvent = intercept(event);
      if (outEvent != null) { out.add(outEvent); }
    }
    return out;
  }

  @Override
  public void close() {
    // no-op
  }

  /**
   * Builder which builds new instance of the StaticInterceptor.
   */
  public static class Builder implements Interceptor.Builder {

    private long passedTime;
    private boolean excludeEvents;
    private String fieldName;

    @Override
    public void configure(Context context) {
      passedTime = context.getLong(PASSED_TIME, DEFAULT_PASSED_TIME);
      excludeEvents = context.getBoolean(EXCLUDE_EVENTS,
          DEFAULT_EXCLUDE_EVENTS);
      fieldName = context.getString(FIELD_NAME, DEFAULT_FIELD_NAME);
    }

    @Override
    public Interceptor build() {
      logger.info(String.format(
          "Creating TimestampFilteringInterceptor: passedTime=%s,excludeEvents=%s,fieldName=%s",
          passedTime, excludeEvents, fieldName));
      return new TimestampFilteringInterceptor(passedTime, excludeEvents, fieldName);
    }
  }

  public static class Constants {

    public static final String PASSED_TIME = "passedTime";
    public static final long DEFAULT_PASSED_TIME = 86400;

    public static final String EXCLUDE_EVENTS = "excludeEvents";
    public static final boolean DEFAULT_EXCLUDE_EVENTS = false;

    public static final String FIELD_NAME = "fieldName";
    public static final String DEFAULT_FIELD_NAME = "timestamp";
  }

}
