/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util.format;

import org.apache.calcite.util.format.postgresql.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for {@link DateTimeFormatter}.
 */
@Isolated
public class PostgresqlDateTimeFormatterTest {
  private static final Object LOCALE_LOCK = new Object();

  @ParameterizedTest
  @ValueSource(strings = {"HH12", "HH"})
  void testHH12(String pattern) {
    final ZonedDateTime midnight = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime morning = createDateTime(2024, 1, 1, 6, 0, 0, 0);
    final ZonedDateTime noon = createDateTime(2024, 1, 1, 12, 0, 0, 0);
    final ZonedDateTime evening = createDateTime(2024, 1, 1, 18, 0, 0, 0);

    assertEquals("12", DateTimeFormatter.toChar(pattern, midnight));
    assertEquals("06", DateTimeFormatter.toChar(pattern, morning));
    assertEquals("12", DateTimeFormatter.toChar(pattern, noon));
    assertEquals("06", DateTimeFormatter.toChar(pattern, evening));
    assertEquals(
        "12", DateTimeFormatter.toChar("FM" + pattern,
        midnight));
    assertEquals(
        "6", DateTimeFormatter.toChar("FM" + pattern,
        morning));
    assertEquals(
        "12", DateTimeFormatter.toChar("FM" + pattern,
        noon));
    assertEquals(
        "6", DateTimeFormatter.toChar("FM" + pattern,
        evening));

    final ZonedDateTime hourOne = createDateTime(2024, 1, 1, 1, 0, 0, 0);
    final ZonedDateTime hourTwo = createDateTime(2024, 1, 1, 2, 0, 0, 0);
    final ZonedDateTime hourThree = createDateTime(2024, 1, 1, 3, 0, 0, 0);
    assertEquals(
        "12TH", DateTimeFormatter.toChar(pattern + "TH",
        midnight));
    assertEquals(
        "01ST", DateTimeFormatter.toChar(pattern + "TH",
        hourOne));
    assertEquals(
        "02ND", DateTimeFormatter.toChar(pattern + "TH",
        hourTwo));
    assertEquals(
        "03RD", DateTimeFormatter.toChar(pattern + "TH",
        hourThree));
    assertEquals(
        "12th", DateTimeFormatter.toChar(pattern + "th",
        midnight));
    assertEquals(
        "01st", DateTimeFormatter.toChar(pattern + "th",
        hourOne));
    assertEquals(
        "02nd", DateTimeFormatter.toChar(pattern + "th",
        hourTwo));
    assertEquals(
        "03rd", DateTimeFormatter.toChar(pattern + "th",
        hourThree));

    assertEquals(
        "2nd", DateTimeFormatter.toChar(
        "FM" + pattern + "th", hourTwo));
  }

  @Test void testHH24() {
    final ZonedDateTime midnight = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime morning = createDateTime(2024, 1, 1, 6, 0, 0, 0);
    final ZonedDateTime noon = createDateTime(2024, 1, 1, 12, 0, 0, 0);
    final ZonedDateTime evening = createDateTime(2024, 1, 1, 18, 0, 0, 0);

    assertEquals("00", DateTimeFormatter.toChar("HH24", midnight));
    assertEquals("06", DateTimeFormatter.toChar("HH24", morning));
    assertEquals("12", DateTimeFormatter.toChar("HH24", noon));
    assertEquals("18", DateTimeFormatter.toChar("HH24", evening));
    assertEquals("0", DateTimeFormatter.toChar("FMHH24", midnight));
    assertEquals("6", DateTimeFormatter.toChar("FMHH24", morning));
    assertEquals("12", DateTimeFormatter.toChar("FMHH24", noon));
    assertEquals("18", DateTimeFormatter.toChar("FMHH24", evening));

    final ZonedDateTime hourOne = createDateTime(2024, 1, 1, 1, 0, 0, 0);
    final ZonedDateTime hourTwo = createDateTime(2024, 1, 1, 2, 0, 0, 0);
    final ZonedDateTime hourThree = createDateTime(2024, 1, 1, 3, 0, 0, 0);
    assertEquals("00TH", DateTimeFormatter.toChar("HH24TH", midnight));
    assertEquals("01ST", DateTimeFormatter.toChar("HH24TH", hourOne));
    assertEquals("02ND", DateTimeFormatter.toChar("HH24TH", hourTwo));
    assertEquals("03RD", DateTimeFormatter.toChar("HH24TH", hourThree));
    assertEquals("00th", DateTimeFormatter.toChar("HH24th", midnight));
    assertEquals("01st", DateTimeFormatter.toChar("HH24th", hourOne));
    assertEquals("02nd", DateTimeFormatter.toChar("HH24th", hourTwo));
    assertEquals("03rd", DateTimeFormatter.toChar("HH24th", hourThree));

    assertEquals("2nd", DateTimeFormatter.toChar("FMHH24th", hourTwo));
  }

  @Test void testMI() {
    final ZonedDateTime minute0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime minute2 = createDateTime(2024, 1, 1, 0, 2, 0, 0);
    final ZonedDateTime minute15 = createDateTime(2024, 1, 1, 0, 15, 0, 0);

    assertEquals("00", DateTimeFormatter.toChar("MI", minute0));
    assertEquals("02", DateTimeFormatter.toChar("MI", minute2));
    assertEquals("15", DateTimeFormatter.toChar("MI", minute15));

    assertEquals("0", DateTimeFormatter.toChar("FMMI", minute0));
    assertEquals("2", DateTimeFormatter.toChar("FMMI", minute2));
    assertEquals("15", DateTimeFormatter.toChar("FMMI", minute15));

    assertEquals("00TH", DateTimeFormatter.toChar("MITH", minute0));
    assertEquals("02ND", DateTimeFormatter.toChar("MITH", minute2));
    assertEquals("15TH", DateTimeFormatter.toChar("MITH", minute15));
    assertEquals("00th", DateTimeFormatter.toChar("MIth", minute0));
    assertEquals("02nd", DateTimeFormatter.toChar("MIth", minute2));
    assertEquals("15th", DateTimeFormatter.toChar("MIth", minute15));

    assertEquals("2nd", DateTimeFormatter.toChar("FMMIth", minute2));
    assertEquals("2nd", DateTimeFormatter.toChar("FMMInd", minute2));
  }

  @ParameterizedTest
  @ValueSource(strings = {"SSSSS", "SSSS"})
  void testSSSSS(String pattern) {
    final ZonedDateTime second0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime second1001 = createDateTime(2024, 1, 1, 0, 16, 41, 0);
    final ZonedDateTime endOfDay = createDateTime(2024, 1, 1, 23, 59, 59, 0);

    assertEquals("0", DateTimeFormatter.toChar(pattern, second0));
    assertEquals("1001", DateTimeFormatter.toChar(pattern, second1001));
    assertEquals("86399", DateTimeFormatter.toChar(pattern, endOfDay));

    assertEquals("0", DateTimeFormatter.toChar("FM" + pattern, second0));
    assertEquals("1001", DateTimeFormatter.toChar("FM" + pattern, second1001));
    assertEquals("86399", DateTimeFormatter.toChar("FM" + pattern, endOfDay));

    assertEquals("0TH", DateTimeFormatter.toChar(pattern + "TH", second0));
    assertEquals("1001ST", DateTimeFormatter.toChar(pattern + "TH", second1001));
    assertEquals("86399TH", DateTimeFormatter.toChar(pattern + "TH", endOfDay));
    assertEquals("0th", DateTimeFormatter.toChar(pattern + "th", second0));
    assertEquals("1001st", DateTimeFormatter.toChar(pattern + "th", second1001));
    assertEquals("86399th", DateTimeFormatter.toChar(pattern + "th", endOfDay));

    assertEquals("1001st", DateTimeFormatter.toChar("FM" + pattern + "th", second1001));
    assertEquals("1001nd", DateTimeFormatter.toChar("FM" + pattern + "nd", second1001));
  }

  @Test void testSS() {
    final ZonedDateTime second0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime second2 = createDateTime(2024, 1, 1, 0, 0, 2, 0);
    final ZonedDateTime second15 = createDateTime(2024, 1, 1, 0, 0, 15, 0);

    assertEquals("00", DateTimeFormatter.toChar("SS", second0));
    assertEquals("02", DateTimeFormatter.toChar("SS", second2));
    assertEquals("15", DateTimeFormatter.toChar("SS", second15));

    assertEquals("0", DateTimeFormatter.toChar("FMSS", second0));
    assertEquals("2", DateTimeFormatter.toChar("FMSS", second2));
    assertEquals("15", DateTimeFormatter.toChar("FMSS", second15));

    assertEquals("00TH", DateTimeFormatter.toChar("SSTH", second0));
    assertEquals("02ND", DateTimeFormatter.toChar("SSTH", second2));
    assertEquals("15TH", DateTimeFormatter.toChar("SSTH", second15));
    assertEquals("00th", DateTimeFormatter.toChar("SSth", second0));
    assertEquals("02nd", DateTimeFormatter.toChar("SSth", second2));
    assertEquals("15th", DateTimeFormatter.toChar("SSth", second15));

    assertEquals("2nd", DateTimeFormatter.toChar("FMSSth", second2));
    assertEquals("2nd", DateTimeFormatter.toChar("FMSSnd", second2));
  }

  @ParameterizedTest
  @ValueSource(strings = {"MS", "FF3"})
  void testMS(String pattern) {
    final ZonedDateTime ms0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime ms2 = createDateTime(2024, 1, 1, 0, 0, 2, 2000000);
    final ZonedDateTime ms15 = createDateTime(2024, 1, 1, 0, 0, 15, 15000000);

    assertEquals("000", DateTimeFormatter.toChar(pattern, ms0));
    assertEquals("002", DateTimeFormatter.toChar(pattern, ms2));
    assertEquals("015", DateTimeFormatter.toChar(pattern, ms15));

    assertEquals("0", DateTimeFormatter.toChar("FM" + pattern, ms0));
    assertEquals("2", DateTimeFormatter.toChar("FM" + pattern, ms2));
    assertEquals("15", DateTimeFormatter.toChar("FM" + pattern, ms15));

    assertEquals("000TH", DateTimeFormatter.toChar(pattern + "TH", ms0));
    assertEquals("002ND", DateTimeFormatter.toChar(pattern + "TH", ms2));
    assertEquals("015TH", DateTimeFormatter.toChar(pattern + "TH", ms15));
    assertEquals("000th", DateTimeFormatter.toChar(pattern + "th", ms0));
    assertEquals("002nd", DateTimeFormatter.toChar(pattern + "th", ms2));
    assertEquals("015th", DateTimeFormatter.toChar(pattern + "th", ms15));

    assertEquals("2nd", DateTimeFormatter.toChar("FM" + pattern + "th", ms2));
    assertEquals("2nd", DateTimeFormatter.toChar("FM" + pattern + "nd", ms2));
  }

  @Test void testUS() {
    final ZonedDateTime us0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime us2 = createDateTime(2024, 1, 1, 0, 0, 0, 2000);
    final ZonedDateTime us15 = createDateTime(2024, 1, 1, 0, 0, 0, 15000);
    final ZonedDateTime usWithMs = createDateTime(2024, 1, 1, 0, 0, 0, 2015000);

    assertEquals("000000", DateTimeFormatter.toChar("US", us0));
    assertEquals("000002", DateTimeFormatter.toChar("US", us2));
    assertEquals("000015", DateTimeFormatter.toChar("US", us15));
    assertEquals("002015", DateTimeFormatter.toChar("US", usWithMs));

    assertEquals("0", DateTimeFormatter.toChar("FMUS", us0));
    assertEquals("2", DateTimeFormatter.toChar("FMUS", us2));
    assertEquals("15", DateTimeFormatter.toChar("FMUS", us15));
    assertEquals("2015", DateTimeFormatter.toChar("FMUS", usWithMs));

    assertEquals("000000TH", DateTimeFormatter.toChar("USTH", us0));
    assertEquals("000002ND", DateTimeFormatter.toChar("USTH", us2));
    assertEquals("000015TH", DateTimeFormatter.toChar("USTH", us15));
    assertEquals("002015TH", DateTimeFormatter.toChar("USTH", usWithMs));
    assertEquals("000000th", DateTimeFormatter.toChar("USth", us0));
    assertEquals("000002nd", DateTimeFormatter.toChar("USth", us2));
    assertEquals("000015th", DateTimeFormatter.toChar("USth", us15));
    assertEquals("002015th", DateTimeFormatter.toChar("USth", usWithMs));

    assertEquals("2nd", DateTimeFormatter.toChar("FMUSth", us2));
    assertEquals("2nd", DateTimeFormatter.toChar("FMUSnd", us2));
  }

  @Test void testFF1() {
    final ZonedDateTime ms0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime ms200 = createDateTime(2024, 1, 1, 0, 0, 0, 200_000_000);
    final ZonedDateTime ms150 = createDateTime(2024, 1, 1, 0, 0, 0, 150_000_000);

    assertEquals("0", DateTimeFormatter.toChar("FF1", ms0));
    assertEquals("2", DateTimeFormatter.toChar("FF1", ms200));
    assertEquals("1", DateTimeFormatter.toChar("FF1", ms150));

    assertEquals("0", DateTimeFormatter.toChar("FMFF1", ms0));
    assertEquals("2", DateTimeFormatter.toChar("FMFF1", ms200));
    assertEquals("1", DateTimeFormatter.toChar("FMFF1", ms150));

    assertEquals("0TH", DateTimeFormatter.toChar("FF1TH", ms0));
    assertEquals("2ND", DateTimeFormatter.toChar("FF1TH", ms200));
    assertEquals("1ST", DateTimeFormatter.toChar("FF1TH", ms150));
    assertEquals("0th", DateTimeFormatter.toChar("FF1th", ms0));
    assertEquals("2nd", DateTimeFormatter.toChar("FF1th", ms200));
    assertEquals("1st", DateTimeFormatter.toChar("FF1th", ms150));

    assertEquals("2nd", DateTimeFormatter.toChar("FMFF1th", ms200));
    assertEquals("2nd", DateTimeFormatter.toChar("FMFF1nd", ms200));
  }

  @Test void testFF2() {
    final ZonedDateTime ms0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime ms20 = createDateTime(2024, 1, 1, 0, 0, 0, 20_000_000);
    final ZonedDateTime ms150 = createDateTime(2024, 1, 1, 0, 0, 0, 150_000_000);

    assertEquals("00", DateTimeFormatter.toChar("FF2", ms0));
    assertEquals("02", DateTimeFormatter.toChar("FF2", ms20));
    assertEquals("15", DateTimeFormatter.toChar("FF2", ms150));

    assertEquals("0", DateTimeFormatter.toChar("FMFF2", ms0));
    assertEquals("2", DateTimeFormatter.toChar("FMFF2", ms20));
    assertEquals("15", DateTimeFormatter.toChar("FMFF2", ms150));

    assertEquals("00TH", DateTimeFormatter.toChar("FF2TH", ms0));
    assertEquals("02ND", DateTimeFormatter.toChar("FF2TH", ms20));
    assertEquals("15TH", DateTimeFormatter.toChar("FF2TH", ms150));
    assertEquals("00th", DateTimeFormatter.toChar("FF2th", ms0));
    assertEquals("02nd", DateTimeFormatter.toChar("FF2th", ms20));
    assertEquals("15th", DateTimeFormatter.toChar("FF2th", ms150));

    assertEquals("2nd", DateTimeFormatter.toChar("FMFF2th", ms20));
    assertEquals("2nd", DateTimeFormatter.toChar("FMFF2nd", ms20));
  }

  @Test void testFF4() {
    final ZonedDateTime us0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime us200 = createDateTime(2024, 1, 1, 0, 0, 0, 200_000);
    final ZonedDateTime ms150 = createDateTime(2024, 1, 1, 0, 0, 0, 150_000_000);

    assertEquals("0000", DateTimeFormatter.toChar("FF4", us0));
    assertEquals("0002", DateTimeFormatter.toChar("FF4", us200));
    assertEquals("1500", DateTimeFormatter.toChar("FF4", ms150));

    assertEquals("0", DateTimeFormatter.toChar("FMFF4", us0));
    assertEquals("2", DateTimeFormatter.toChar("FMFF4", us200));
    assertEquals("1500", DateTimeFormatter.toChar("FMFF4", ms150));

    assertEquals("0000TH", DateTimeFormatter.toChar("FF4TH", us0));
    assertEquals("0002ND", DateTimeFormatter.toChar("FF4TH", us200));
    assertEquals("1500TH", DateTimeFormatter.toChar("FF4TH", ms150));
    assertEquals("0000th", DateTimeFormatter.toChar("FF4th", us0));
    assertEquals("0002nd", DateTimeFormatter.toChar("FF4th", us200));
    assertEquals("1500th", DateTimeFormatter.toChar("FF4th", ms150));

    assertEquals("2nd", DateTimeFormatter.toChar("FMFF4th", us200));
    assertEquals("2nd", DateTimeFormatter.toChar("FMFF4nd", us200));
  }

  @Test void testFF5() {
    final ZonedDateTime us0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime us20 = createDateTime(2024, 1, 1, 0, 0, 0, 20_000);
    final ZonedDateTime ms150 = createDateTime(2024, 1, 1, 0, 0, 0, 150_000_000);

    assertEquals("00000", DateTimeFormatter.toChar("FF5", us0));
    assertEquals("00002", DateTimeFormatter.toChar("FF5", us20));
    assertEquals("15000", DateTimeFormatter.toChar("FF5", ms150));

    assertEquals("0", DateTimeFormatter.toChar("FMFF5", us0));
    assertEquals("2", DateTimeFormatter.toChar("FMFF5", us20));
    assertEquals("15000", DateTimeFormatter.toChar("FMFF5", ms150));

    assertEquals("00000TH", DateTimeFormatter.toChar("FF5TH", us0));
    assertEquals("00002ND", DateTimeFormatter.toChar("FF5TH", us20));
    assertEquals("15000TH", DateTimeFormatter.toChar("FF5TH", ms150));
    assertEquals("00000th", DateTimeFormatter.toChar("FF5th", us0));
    assertEquals("00002nd", DateTimeFormatter.toChar("FF5th", us20));
    assertEquals("15000th", DateTimeFormatter.toChar("FF5th", ms150));

    assertEquals("2nd", DateTimeFormatter.toChar("FMFF5th", us20));
    assertEquals("2nd", DateTimeFormatter.toChar("FMFF5nd", us20));
  }

  @Test void testFF6() {
    final ZonedDateTime us0 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime us2 = createDateTime(2024, 1, 1, 0, 0, 0, 2_000);
    final ZonedDateTime ms150 = createDateTime(2024, 1, 1, 0, 0, 0, 150_000_000);

    assertEquals("000000", DateTimeFormatter.toChar("FF6", us0));
    assertEquals("000002", DateTimeFormatter.toChar("FF6", us2));
    assertEquals("150000", DateTimeFormatter.toChar("FF6", ms150));

    assertEquals("0", DateTimeFormatter.toChar("FMFF6", us0));
    assertEquals("2", DateTimeFormatter.toChar("FMFF6", us2));
    assertEquals("150000", DateTimeFormatter.toChar("FMFF6", ms150));

    assertEquals("000000TH", DateTimeFormatter.toChar("FF6TH", us0));
    assertEquals("000002ND", DateTimeFormatter.toChar("FF6TH", us2));
    assertEquals("150000TH", DateTimeFormatter.toChar("FF6TH", ms150));
    assertEquals("000000th", DateTimeFormatter.toChar("FF6th", us0));
    assertEquals("000002nd", DateTimeFormatter.toChar("FF6th", us2));
    assertEquals("150000th", DateTimeFormatter.toChar("FF6th", ms150));

    assertEquals("2nd", DateTimeFormatter.toChar("FMFF6th", us2));
    assertEquals("2nd", DateTimeFormatter.toChar("FMFF6nd", us2));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AM", "PM"})
  void testAMUpperCase(String pattern) {
    final ZonedDateTime midnight = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime morning = createDateTime(2024, 1, 1, 6, 0, 0, 0);
    final ZonedDateTime noon = createDateTime(2024, 1, 1, 12, 0, 0, 0);
    final ZonedDateTime evening = createDateTime(2024, 1, 1, 18, 0, 0, 0);

    assertEquals("AM", DateTimeFormatter.toChar(pattern, midnight));
    assertEquals("AM", DateTimeFormatter.toChar(pattern, morning));
    assertEquals("PM", DateTimeFormatter.toChar(pattern, noon));
    assertEquals("PM", DateTimeFormatter.toChar(pattern, evening));
  }

  @ParameterizedTest
  @ValueSource(strings = {"am", "pm"})
  void testAMLowerCase(String pattern) {
    final ZonedDateTime midnight = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime morning = createDateTime(2024, 1, 1, 6, 0, 0, 0);
    final ZonedDateTime noon = createDateTime(2024, 1, 1, 12, 0, 0, 0);
    final ZonedDateTime evening = createDateTime(2024, 1, 1, 18, 0, 0, 0);

    assertEquals("am", DateTimeFormatter.toChar(pattern, midnight));
    assertEquals("am", DateTimeFormatter.toChar(pattern, morning));
    assertEquals("pm", DateTimeFormatter.toChar(pattern, noon));
    assertEquals("pm", DateTimeFormatter.toChar(pattern, evening));
  }

  @ParameterizedTest
  @ValueSource(strings = {"A.M.", "P.M."})
  void testAMWithDotsUpperCase(String pattern) {
    final ZonedDateTime midnight = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime morning = createDateTime(2024, 1, 1, 6, 0, 0, 0);
    final ZonedDateTime noon = createDateTime(2024, 1, 1, 12, 0, 0, 0);
    final ZonedDateTime evening = createDateTime(2024, 1, 1, 18, 0, 0, 0);

    assertEquals("A.M.", DateTimeFormatter.toChar(pattern, midnight));
    assertEquals("A.M.", DateTimeFormatter.toChar(pattern, morning));
    assertEquals("P.M.", DateTimeFormatter.toChar(pattern, noon));
    assertEquals("P.M.", DateTimeFormatter.toChar(pattern, evening));
  }

  @ParameterizedTest
  @ValueSource(strings = {"a.m.", "p.m."})
  void testAMWithDotsLowerCase(String pattern) {
    final ZonedDateTime midnight = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime morning = createDateTime(2024, 1, 1, 6, 0, 0, 0);
    final ZonedDateTime noon = createDateTime(2024, 1, 1, 12, 0, 0, 0);
    final ZonedDateTime evening = createDateTime(2024, 1, 1, 18, 0, 0, 0);

    assertEquals("a.m.", DateTimeFormatter.toChar(pattern, midnight));
    assertEquals("a.m.", DateTimeFormatter.toChar(pattern, morning));
    assertEquals("p.m.", DateTimeFormatter.toChar(pattern, noon));
    assertEquals("p.m.", DateTimeFormatter.toChar(pattern, evening));
  }

  @Test void testYearWithCommas() {
    final ZonedDateTime year1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year2 = createDateTime(100, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year3 = createDateTime(1, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year4 = createDateTime(32136, 1, 1, 0, 0, 0, 0);

    assertEquals("2,024", DateTimeFormatter.toChar("Y,YYY", year1));
    assertEquals("0,100", DateTimeFormatter.toChar("Y,YYY", year2));
    assertEquals("0,001", DateTimeFormatter.toChar("Y,YYY", year3));
    assertEquals("32,136", DateTimeFormatter.toChar("Y,YYY", year4));
    assertEquals("2,024", DateTimeFormatter.toChar("FMY,YYY", year1));
    assertEquals("0,100", DateTimeFormatter.toChar("FMY,YYY", year2));
    assertEquals("0,001", DateTimeFormatter.toChar("FMY,YYY", year3));
    assertEquals("32,136", DateTimeFormatter.toChar("FMY,YYY", year4));

    assertEquals("2,024TH", DateTimeFormatter.toChar("Y,YYYTH", year1));
    assertEquals("0,100TH", DateTimeFormatter.toChar("Y,YYYTH", year2));
    assertEquals("0,001ST", DateTimeFormatter.toChar("Y,YYYTH", year3));
    assertEquals("32,136TH", DateTimeFormatter.toChar("Y,YYYTH", year4));
    assertEquals("2,024th", DateTimeFormatter.toChar("Y,YYYth", year1));
    assertEquals("0,100th", DateTimeFormatter.toChar("Y,YYYth", year2));
    assertEquals("0,001st", DateTimeFormatter.toChar("Y,YYYth", year3));
    assertEquals("32,136th", DateTimeFormatter.toChar("Y,YYYth", year4));

    assertEquals("2,024th", DateTimeFormatter.toChar("FMY,YYYth", year1));
  }

  @Test void testYYYY() {
    final ZonedDateTime year1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year2 = createDateTime(100, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year3 = createDateTime(1, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year4 = createDateTime(32136, 1, 1, 0, 0, 0, 0);

    assertEquals("2024", DateTimeFormatter.toChar("YYYY", year1));
    assertEquals("0100", DateTimeFormatter.toChar("YYYY", year2));
    assertEquals("0001", DateTimeFormatter.toChar("YYYY", year3));
    assertEquals("32136", DateTimeFormatter.toChar("YYYY", year4));
    assertEquals("2024", DateTimeFormatter.toChar("FMYYYY", year1));
    assertEquals("100", DateTimeFormatter.toChar("FMYYYY", year2));
    assertEquals("1", DateTimeFormatter.toChar("FMYYYY", year3));
    assertEquals("32136", DateTimeFormatter.toChar("FMYYYY", year4));

    assertEquals("2024TH", DateTimeFormatter.toChar("YYYYTH", year1));
    assertEquals("0100TH", DateTimeFormatter.toChar("YYYYTH", year2));
    assertEquals("0001ST", DateTimeFormatter.toChar("YYYYTH", year3));
    assertEquals("32136TH", DateTimeFormatter.toChar("YYYYTH", year4));
    assertEquals("2024th", DateTimeFormatter.toChar("YYYYth", year1));
    assertEquals("0100th", DateTimeFormatter.toChar("YYYYth", year2));
    assertEquals("0001st", DateTimeFormatter.toChar("YYYYth", year3));
    assertEquals("32136th", DateTimeFormatter.toChar("YYYYth", year4));

    assertEquals("2024th", DateTimeFormatter.toChar("FMYYYYth", year1));
  }

  @Test void testYYY() {
    final ZonedDateTime year1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year2 = createDateTime(100, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year3 = createDateTime(1, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year4 = createDateTime(32136, 1, 1, 0, 0, 0, 0);

    assertEquals("024", DateTimeFormatter.toChar("YYY", year1));
    assertEquals("100", DateTimeFormatter.toChar("YYY", year2));
    assertEquals("001", DateTimeFormatter.toChar("YYY", year3));
    assertEquals("136", DateTimeFormatter.toChar("YYY", year4));
    assertEquals("24", DateTimeFormatter.toChar("FMYYY", year1));
    assertEquals("100", DateTimeFormatter.toChar("FMYYY", year2));
    assertEquals("1", DateTimeFormatter.toChar("FMYYY", year3));
    assertEquals("136", DateTimeFormatter.toChar("FMYYY", year4));

    assertEquals("024TH", DateTimeFormatter.toChar("YYYTH", year1));
    assertEquals("100TH", DateTimeFormatter.toChar("YYYTH", year2));
    assertEquals("001ST", DateTimeFormatter.toChar("YYYTH", year3));
    assertEquals("136TH", DateTimeFormatter.toChar("YYYTH", year4));
    assertEquals("024th", DateTimeFormatter.toChar("YYYth", year1));
    assertEquals("100th", DateTimeFormatter.toChar("YYYth", year2));
    assertEquals("001st", DateTimeFormatter.toChar("YYYth", year3));
    assertEquals("136th", DateTimeFormatter.toChar("YYYth", year4));

    assertEquals("24th", DateTimeFormatter.toChar("FMYYYth", year1));
  }

  @Test void testYY() {
    final ZonedDateTime year1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year2 = createDateTime(100, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year3 = createDateTime(1, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year4 = createDateTime(32136, 1, 1, 0, 0, 0, 0);

    assertEquals("24", DateTimeFormatter.toChar("YY", year1));
    assertEquals("00", DateTimeFormatter.toChar("YY", year2));
    assertEquals("01", DateTimeFormatter.toChar("YY", year3));
    assertEquals("36", DateTimeFormatter.toChar("YY", year4));
    assertEquals("24", DateTimeFormatter.toChar("FMYY", year1));
    assertEquals("0", DateTimeFormatter.toChar("FMYY", year2));
    assertEquals("1", DateTimeFormatter.toChar("FMYY", year3));
    assertEquals("36", DateTimeFormatter.toChar("FMYY", year4));

    assertEquals("24TH", DateTimeFormatter.toChar("YYTH", year1));
    assertEquals("00TH", DateTimeFormatter.toChar("YYTH", year2));
    assertEquals("01ST", DateTimeFormatter.toChar("YYTH", year3));
    assertEquals("36TH", DateTimeFormatter.toChar("YYTH", year4));
    assertEquals("24th", DateTimeFormatter.toChar("YYth", year1));
    assertEquals("00th", DateTimeFormatter.toChar("YYth", year2));
    assertEquals("01st", DateTimeFormatter.toChar("YYth", year3));
    assertEquals("36th", DateTimeFormatter.toChar("YYth", year4));

    assertEquals("24th", DateTimeFormatter.toChar("FMYYth", year1));
  }

  @Test void testY() {
    final ZonedDateTime year1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year2 = createDateTime(100, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year3 = createDateTime(1, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime year4 = createDateTime(32136, 1, 1, 0, 0, 0, 0);

    assertEquals("4", DateTimeFormatter.toChar("Y", year1));
    assertEquals("0", DateTimeFormatter.toChar("Y", year2));
    assertEquals("1", DateTimeFormatter.toChar("Y", year3));
    assertEquals("6", DateTimeFormatter.toChar("Y", year4));
    assertEquals("4", DateTimeFormatter.toChar("FMY", year1));
    assertEquals("0", DateTimeFormatter.toChar("FMY", year2));
    assertEquals("1", DateTimeFormatter.toChar("FMY", year3));
    assertEquals("6", DateTimeFormatter.toChar("FMY", year4));

    assertEquals("4TH", DateTimeFormatter.toChar("YTH", year1));
    assertEquals("0TH", DateTimeFormatter.toChar("YTH", year2));
    assertEquals("1ST", DateTimeFormatter.toChar("YTH", year3));
    assertEquals("6TH", DateTimeFormatter.toChar("YTH", year4));
    assertEquals("4th", DateTimeFormatter.toChar("Yth", year1));
    assertEquals("0th", DateTimeFormatter.toChar("Yth", year2));
    assertEquals("1st", DateTimeFormatter.toChar("Yth", year3));
    assertEquals("6th", DateTimeFormatter.toChar("Yth", year4));

    assertEquals("4th", DateTimeFormatter.toChar("FMYth", year1));
  }

  @Test void testIYYY() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(1);
    final ZonedDateTime date4 = date3.plusDays(1);
    final ZonedDateTime date5 = date4.plusDays(1);

    assertEquals("2019", DateTimeFormatter.toChar("IYYY", date1));
    assertEquals("2020", DateTimeFormatter.toChar("IYYY", date2));
    assertEquals("2020", DateTimeFormatter.toChar("IYYY", date3));
    assertEquals("2020", DateTimeFormatter.toChar("IYYY", date4));
    assertEquals("2020", DateTimeFormatter.toChar("IYYY", date5));
    assertEquals("2019", DateTimeFormatter.toChar("FMIYYY", date1));
    assertEquals("2020", DateTimeFormatter.toChar("FMIYYY", date2));
    assertEquals("2020", DateTimeFormatter.toChar("FMIYYY", date3));
    assertEquals("2020", DateTimeFormatter.toChar("FMIYYY", date4));
    assertEquals("2020", DateTimeFormatter.toChar("FMIYYY", date5));

    assertEquals("2019TH", DateTimeFormatter.toChar("IYYYTH", date1));
    assertEquals("2020TH", DateTimeFormatter.toChar("IYYYTH", date2));
    assertEquals("2020TH", DateTimeFormatter.toChar("IYYYTH", date3));
    assertEquals("2020TH", DateTimeFormatter.toChar("IYYYTH", date4));
    assertEquals("2020TH", DateTimeFormatter.toChar("IYYYTH", date5));
    assertEquals("2019th", DateTimeFormatter.toChar("IYYYth", date1));
    assertEquals("2020th", DateTimeFormatter.toChar("IYYYth", date2));
    assertEquals("2020th", DateTimeFormatter.toChar("IYYYth", date3));
    assertEquals("2020th", DateTimeFormatter.toChar("IYYYth", date4));
    assertEquals("2020th", DateTimeFormatter.toChar("IYYYth", date5));

    assertEquals("2020th", DateTimeFormatter.toChar("FMIYYYth", date5));
  }

  @Test void testIYY() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(1);
    final ZonedDateTime date4 = date3.plusDays(1);
    final ZonedDateTime date5 = date4.plusDays(1);

    assertEquals("019", DateTimeFormatter.toChar("IYY", date1));
    assertEquals("020", DateTimeFormatter.toChar("IYY", date2));
    assertEquals("020", DateTimeFormatter.toChar("IYY", date3));
    assertEquals("020", DateTimeFormatter.toChar("IYY", date4));
    assertEquals("020", DateTimeFormatter.toChar("IYY", date5));
    assertEquals("19", DateTimeFormatter.toChar("FMIYY", date1));
    assertEquals("20", DateTimeFormatter.toChar("FMIYY", date2));
    assertEquals("20", DateTimeFormatter.toChar("FMIYY", date3));
    assertEquals("20", DateTimeFormatter.toChar("FMIYY", date4));
    assertEquals("20", DateTimeFormatter.toChar("FMIYY", date5));

    assertEquals("019TH", DateTimeFormatter.toChar("IYYTH", date1));
    assertEquals("020TH", DateTimeFormatter.toChar("IYYTH", date2));
    assertEquals("020TH", DateTimeFormatter.toChar("IYYTH", date3));
    assertEquals("020TH", DateTimeFormatter.toChar("IYYTH", date4));
    assertEquals("020TH", DateTimeFormatter.toChar("IYYTH", date5));
    assertEquals("019th", DateTimeFormatter.toChar("IYYth", date1));
    assertEquals("020th", DateTimeFormatter.toChar("IYYth", date2));
    assertEquals("020th", DateTimeFormatter.toChar("IYYth", date3));
    assertEquals("020th", DateTimeFormatter.toChar("IYYth", date4));
    assertEquals("020th", DateTimeFormatter.toChar("IYYth", date5));

    assertEquals("20th", DateTimeFormatter.toChar("FMIYYth", date5));
  }

  @Test void testIY() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(1);
    final ZonedDateTime date4 = date3.plusDays(1);
    final ZonedDateTime date5 = date4.plusDays(1);

    assertEquals("19", DateTimeFormatter.toChar("IY", date1));
    assertEquals("20", DateTimeFormatter.toChar("IY", date2));
    assertEquals("20", DateTimeFormatter.toChar("IY", date3));
    assertEquals("20", DateTimeFormatter.toChar("IY", date4));
    assertEquals("20", DateTimeFormatter.toChar("IY", date5));
    assertEquals("19", DateTimeFormatter.toChar("FMIY", date1));
    assertEquals("20", DateTimeFormatter.toChar("FMIY", date2));
    assertEquals("20", DateTimeFormatter.toChar("FMIY", date3));
    assertEquals("20", DateTimeFormatter.toChar("FMIY", date4));
    assertEquals("20", DateTimeFormatter.toChar("FMIY", date5));

    assertEquals("19TH", DateTimeFormatter.toChar("IYTH", date1));
    assertEquals("20TH", DateTimeFormatter.toChar("IYTH", date2));
    assertEquals("20TH", DateTimeFormatter.toChar("IYTH", date3));
    assertEquals("20TH", DateTimeFormatter.toChar("IYTH", date4));
    assertEquals("20TH", DateTimeFormatter.toChar("IYTH", date5));
    assertEquals("19th", DateTimeFormatter.toChar("IYth", date1));
    assertEquals("20th", DateTimeFormatter.toChar("IYth", date2));
    assertEquals("20th", DateTimeFormatter.toChar("IYth", date3));
    assertEquals("20th", DateTimeFormatter.toChar("IYth", date4));
    assertEquals("20th", DateTimeFormatter.toChar("IYth", date5));

    assertEquals("20th", DateTimeFormatter.toChar("FMIYth", date5));
  }

  @Test void testI() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(1);
    final ZonedDateTime date4 = date3.plusDays(1);
    final ZonedDateTime date5 = date4.plusDays(1);

    assertEquals("9", DateTimeFormatter.toChar("I", date1));
    assertEquals("0", DateTimeFormatter.toChar("I", date2));
    assertEquals("0", DateTimeFormatter.toChar("I", date3));
    assertEquals("0", DateTimeFormatter.toChar("I", date4));
    assertEquals("0", DateTimeFormatter.toChar("I", date5));
    assertEquals("9", DateTimeFormatter.toChar("FMI", date1));
    assertEquals("0", DateTimeFormatter.toChar("FMI", date2));
    assertEquals("0", DateTimeFormatter.toChar("FMI", date3));
    assertEquals("0", DateTimeFormatter.toChar("FMI", date4));
    assertEquals("0", DateTimeFormatter.toChar("FMI", date5));

    assertEquals("9TH", DateTimeFormatter.toChar("ITH", date1));
    assertEquals("0TH", DateTimeFormatter.toChar("ITH", date2));
    assertEquals("0TH", DateTimeFormatter.toChar("ITH", date3));
    assertEquals("0TH", DateTimeFormatter.toChar("ITH", date4));
    assertEquals("0TH", DateTimeFormatter.toChar("ITH", date5));
    assertEquals("9th", DateTimeFormatter.toChar("Ith", date1));
    assertEquals("0th", DateTimeFormatter.toChar("Ith", date2));
    assertEquals("0th", DateTimeFormatter.toChar("Ith", date3));
    assertEquals("0th", DateTimeFormatter.toChar("Ith", date4));
    assertEquals("0th", DateTimeFormatter.toChar("Ith", date5));

    assertEquals("0th", DateTimeFormatter.toChar("FMIth", date5));
  }

  @Test void testIW() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(186);

    assertEquals("52", DateTimeFormatter.toChar("IW", date1));
    assertEquals("01", DateTimeFormatter.toChar("IW", date2));
    assertEquals("27", DateTimeFormatter.toChar("IW", date3));
    assertEquals("52", DateTimeFormatter.toChar("FMIW", date1));
    assertEquals("1", DateTimeFormatter.toChar("FMIW", date2));
    assertEquals("27", DateTimeFormatter.toChar("FMIW", date3));

    assertEquals("52ND", DateTimeFormatter.toChar("IWTH", date1));
    assertEquals("01ST", DateTimeFormatter.toChar("IWTH", date2));
    assertEquals("27TH", DateTimeFormatter.toChar("IWTH", date3));
    assertEquals("52nd", DateTimeFormatter.toChar("IWth", date1));
    assertEquals("01st", DateTimeFormatter.toChar("IWth", date2));
    assertEquals("27th", DateTimeFormatter.toChar("IWth", date3));

    assertEquals("27th", DateTimeFormatter.toChar("FMIWth", date3));
  }

  @Test void testIDDD() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(186);

    assertEquals("364", DateTimeFormatter.toChar("IDDD", date1));
    assertEquals("001", DateTimeFormatter.toChar("IDDD", date2));
    assertEquals("187", DateTimeFormatter.toChar("IDDD", date3));
    assertEquals("364", DateTimeFormatter.toChar("FMIDDD", date1));
    assertEquals("1", DateTimeFormatter.toChar("FMIDDD", date2));
    assertEquals("187", DateTimeFormatter.toChar("FMIDDD", date3));

    assertEquals("364TH", DateTimeFormatter.toChar("IDDDTH", date1));
    assertEquals("001ST", DateTimeFormatter.toChar("IDDDTH", date2));
    assertEquals("187TH", DateTimeFormatter.toChar("IDDDTH", date3));
    assertEquals("364th", DateTimeFormatter.toChar("IDDDth", date1));
    assertEquals("001st", DateTimeFormatter.toChar("IDDDth", date2));
    assertEquals("187th", DateTimeFormatter.toChar("IDDDth", date3));

    assertEquals("187th", DateTimeFormatter.toChar("FMIDDDth", date3));
  }

  @Test void testID() {
    final ZonedDateTime date1 = createDateTime(2019, 12, 29, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.plusDays(1);
    final ZonedDateTime date3 = date2.plusDays(186);

    assertEquals("7", DateTimeFormatter.toChar("ID", date1));
    assertEquals("1", DateTimeFormatter.toChar("ID", date2));
    assertEquals("5", DateTimeFormatter.toChar("ID", date3));
    assertEquals("7", DateTimeFormatter.toChar("FMID", date1));
    assertEquals("1", DateTimeFormatter.toChar("FMID", date2));
    assertEquals("5", DateTimeFormatter.toChar("FMID", date3));

    assertEquals("7TH", DateTimeFormatter.toChar("IDTH", date1));
    assertEquals("1ST", DateTimeFormatter.toChar("IDTH", date2));
    assertEquals("5TH", DateTimeFormatter.toChar("IDTH", date3));
    assertEquals("7th", DateTimeFormatter.toChar("IDth", date1));
    assertEquals("1st", DateTimeFormatter.toChar("IDth", date2));
    assertEquals("5th", DateTimeFormatter.toChar("IDth", date3));

    assertEquals("5th", DateTimeFormatter.toChar("FMIDth", date3));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AD", "BC"})
  void testEraUpperCaseNoDots(String pattern) {
    final ZonedDateTime date1 = createDateTime(2019, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = date1.minusYears(2018);
    final ZonedDateTime date3 = date2.minusYears(1);
    final ZonedDateTime date4 = date3.minusYears(200);

    assertEquals("AD", DateTimeFormatter.toChar(pattern, date1));
    assertEquals("AD", DateTimeFormatter.toChar(pattern, date2));
    assertEquals("BC", DateTimeFormatter.toChar(pattern, date3));
    assertEquals("BC", DateTimeFormatter.toChar(pattern, date4));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ad", "bc"})
  void testEraLowerCaseNoDots(String pattern) {
    final ZonedDateTime date1 = createDateTime(2019, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = date1.minusYears(2018);
    final ZonedDateTime date3 = date2.minusYears(1);
    final ZonedDateTime date4 = date3.minusYears(200);

    assertEquals("ad", DateTimeFormatter.toChar(pattern, date1));
    assertEquals("ad", DateTimeFormatter.toChar(pattern, date2));
    assertEquals("bc", DateTimeFormatter.toChar(pattern, date3));
    assertEquals("bc", DateTimeFormatter.toChar(pattern, date4));
  }

  @ParameterizedTest
  @ValueSource(strings = {"A.D.", "B.C."})
  void testEraUpperCaseWithDots(String pattern) {
    final ZonedDateTime date1 = createDateTime(2019, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = date1.minusYears(2018);
    final ZonedDateTime date3 = date2.minusYears(1);
    final ZonedDateTime date4 = date3.minusYears(200);

    assertEquals("A.D.", DateTimeFormatter.toChar(pattern, date1));
    assertEquals("A.D.", DateTimeFormatter.toChar(pattern, date2));
    assertEquals("B.C.", DateTimeFormatter.toChar(pattern, date3));
    assertEquals("B.C.", DateTimeFormatter.toChar(pattern, date4));
  }

  @ParameterizedTest
  @ValueSource(strings = {"a.d.", "b.c."})
  void testEraLowerCaseWithDots(String pattern) {
    final ZonedDateTime date1 = createDateTime(2019, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = date1.minusYears(2018);
    final ZonedDateTime date3 = date2.minusYears(1);
    final ZonedDateTime date4 = date3.minusYears(200);

    assertEquals("a.d.", DateTimeFormatter.toChar(pattern, date1));
    assertEquals("a.d.", DateTimeFormatter.toChar(pattern, date2));
    assertEquals("b.c.", DateTimeFormatter.toChar(pattern, date3));
    assertEquals("b.c.", DateTimeFormatter.toChar(pattern, date4));
  }

  @Test void testMonthFullUpperCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("JANUARY  ", DateTimeFormatter.toChar("MONTH", date1));
      assertEquals("MARCH    ", DateTimeFormatter.toChar("MONTH", date2));
      assertEquals("NOVEMBER ", DateTimeFormatter.toChar("MONTH", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthFullUpperCaseNoPadding() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("JANUARY", DateTimeFormatter.toChar("FMMONTH", date1));
      assertEquals("MARCH", DateTimeFormatter.toChar("FMMONTH", date2));
      assertEquals("NOVEMBER", DateTimeFormatter.toChar("FMMONTH", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthFullUpperCaseNoTranslate() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.FRENCH);
      assertEquals("JANUARY  ", DateTimeFormatter.toChar("MONTH", date1));
      assertEquals("MARCH    ", DateTimeFormatter.toChar("MONTH", date2));
      assertEquals("NOVEMBER ", DateTimeFormatter.toChar("MONTH", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthFullUpperCaseTranslate() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.FRENCH);
      assertEquals("JANVIER  ", DateTimeFormatter.toChar("TMMONTH", date1));
      assertEquals("MARS     ", DateTimeFormatter.toChar("TMMONTH", date2));
      assertEquals("NOVEMBRE ", DateTimeFormatter.toChar("TMMONTH", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthFullCapitalized() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("January  ", DateTimeFormatter.toChar("Month", date1));
      assertEquals("March    ", DateTimeFormatter.toChar("Month", date2));
      assertEquals("November ", DateTimeFormatter.toChar("Month", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthFullLowerCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("january  ", DateTimeFormatter.toChar("month", date1));
      assertEquals("march    ", DateTimeFormatter.toChar("month", date2));
      assertEquals("november ", DateTimeFormatter.toChar("month", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthShortUpperCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("JAN", DateTimeFormatter.toChar("MON", date1));
      assertEquals("MAR", DateTimeFormatter.toChar("MON", date2));
      assertEquals("NOV", DateTimeFormatter.toChar("MON", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthShortCapitalized() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("Jan", DateTimeFormatter.toChar("Mon", date1));
      assertEquals("Mar", DateTimeFormatter.toChar("Mon", date2));
      assertEquals("Nov", DateTimeFormatter.toChar("Mon", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMonthShortLowerCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("jan", DateTimeFormatter.toChar("mon", date1));
      assertEquals("mar", DateTimeFormatter.toChar("mon", date2));
      assertEquals("nov", DateTimeFormatter.toChar("mon", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testMM() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    assertEquals("01", DateTimeFormatter.toChar("MM", date1));
    assertEquals("03", DateTimeFormatter.toChar("MM", date2));
    assertEquals("11", DateTimeFormatter.toChar("MM", date3));
    assertEquals("1", DateTimeFormatter.toChar("FMMM", date1));
    assertEquals("3", DateTimeFormatter.toChar("FMMM", date2));
    assertEquals("11", DateTimeFormatter.toChar("FMMM", date3));

    assertEquals("01ST", DateTimeFormatter.toChar("MMTH", date1));
    assertEquals("03RD", DateTimeFormatter.toChar("MMTH", date2));
    assertEquals("11TH", DateTimeFormatter.toChar("MMTH", date3));
    assertEquals("01st", DateTimeFormatter.toChar("MMth", date1));
    assertEquals("03rd", DateTimeFormatter.toChar("MMth", date2));
    assertEquals("11th", DateTimeFormatter.toChar("MMth", date3));

    assertEquals("3rd", DateTimeFormatter.toChar("FMMMth", date2));
  }

  @Test void testDayFullUpperCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("MONDAY   ", DateTimeFormatter.toChar("DAY", date1));
      assertEquals("FRIDAY   ", DateTimeFormatter.toChar("DAY", date2));
      assertEquals("TUESDAY  ", DateTimeFormatter.toChar("DAY", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayFullUpperNoTranslate() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.FRENCH);
      assertEquals("MONDAY   ", DateTimeFormatter.toChar("DAY", date1));
      assertEquals("FRIDAY   ", DateTimeFormatter.toChar("DAY", date2));
      assertEquals("TUESDAY  ", DateTimeFormatter.toChar("DAY", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayFullUpperTranslate() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.FRENCH);
      assertEquals("LUNDI    ", DateTimeFormatter.toChar("TMDAY", date1));
      assertEquals("VENDREDI ", DateTimeFormatter.toChar("TMDAY", date2));
      assertEquals("MARDI    ", DateTimeFormatter.toChar("TMDAY", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayFullCapitalized() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("Monday   ", DateTimeFormatter.toChar("Day", date1));
      assertEquals("Friday   ", DateTimeFormatter.toChar("Day", date2));
      assertEquals("Tuesday  ", DateTimeFormatter.toChar("Day", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayFullLowerCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("monday   ", DateTimeFormatter.toChar("day", date1));
      assertEquals("friday   ", DateTimeFormatter.toChar("day", date2));
      assertEquals("tuesday  ", DateTimeFormatter.toChar("day", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayShortUpperCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("MON", DateTimeFormatter.toChar("DY", date1));
      assertEquals("FRI", DateTimeFormatter.toChar("DY", date2));
      assertEquals("TUE", DateTimeFormatter.toChar("DY", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayShortCapitalized() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("Mon", DateTimeFormatter.toChar("Dy", date1));
      assertEquals("Fri", DateTimeFormatter.toChar("Dy", date2));
      assertEquals("Tue", DateTimeFormatter.toChar("Dy", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDayShortLowerCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 1, 23, 0, 0, 0);

    final Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      assertEquals("mon", DateTimeFormatter.toChar("dy", date1));
      assertEquals("fri", DateTimeFormatter.toChar("dy", date2));
      assertEquals("tue", DateTimeFormatter.toChar("dy", date3));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testDDD() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 11, 1, 23, 0, 0, 0);

    assertEquals("001", DateTimeFormatter.toChar("DDD", date1));
    assertEquals("061", DateTimeFormatter.toChar("DDD", date2));
    assertEquals("306", DateTimeFormatter.toChar("DDD", date3));
    assertEquals("1", DateTimeFormatter.toChar("FMDDD", date1));
    assertEquals("61", DateTimeFormatter.toChar("FMDDD", date2));
    assertEquals("306", DateTimeFormatter.toChar("FMDDD", date3));

    assertEquals("001ST", DateTimeFormatter.toChar("DDDTH", date1));
    assertEquals("061ST", DateTimeFormatter.toChar("DDDTH", date2));
    assertEquals("306TH", DateTimeFormatter.toChar("DDDTH", date3));
    assertEquals("001st", DateTimeFormatter.toChar("DDDth", date1));
    assertEquals("061st", DateTimeFormatter.toChar("DDDth", date2));
    assertEquals("306th", DateTimeFormatter.toChar("DDDth", date3));

    assertEquals("1st", DateTimeFormatter.toChar("FMDDDth", date1));
  }

  @Test void testDD() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 1, 12, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 1, 29, 23, 0, 0, 0);

    assertEquals("01", DateTimeFormatter.toChar("DD", date1));
    assertEquals("12", DateTimeFormatter.toChar("DD", date2));
    assertEquals("29", DateTimeFormatter.toChar("DD", date3));
    assertEquals("1", DateTimeFormatter.toChar("FMDD", date1));
    assertEquals("12", DateTimeFormatter.toChar("FMDD", date2));
    assertEquals("29", DateTimeFormatter.toChar("FMDD", date3));

    assertEquals("01ST", DateTimeFormatter.toChar("DDTH", date1));
    assertEquals("12TH", DateTimeFormatter.toChar("DDTH", date2));
    assertEquals("29TH", DateTimeFormatter.toChar("DDTH", date3));
    assertEquals("01st", DateTimeFormatter.toChar("DDth", date1));
    assertEquals("12th", DateTimeFormatter.toChar("DDth", date2));
    assertEquals("29th", DateTimeFormatter.toChar("DDth", date3));

    assertEquals("1st", DateTimeFormatter.toChar("FMDDth", date1));
  }

  @Test void testD() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 1, 2, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 1, 27, 23, 0, 0, 0);

    assertEquals("2", DateTimeFormatter.toChar("D", date1));
    assertEquals("3", DateTimeFormatter.toChar("D", date2));
    assertEquals("7", DateTimeFormatter.toChar("D", date3));
    assertEquals("2", DateTimeFormatter.toChar("FMD", date1));
    assertEquals("3", DateTimeFormatter.toChar("FMD", date2));
    assertEquals("7", DateTimeFormatter.toChar("FMD", date3));

    assertEquals("2ND", DateTimeFormatter.toChar("DTH", date1));
    assertEquals("3RD", DateTimeFormatter.toChar("DTH", date2));
    assertEquals("7TH", DateTimeFormatter.toChar("DTH", date3));
    assertEquals("2nd", DateTimeFormatter.toChar("Dth", date1));
    assertEquals("3rd", DateTimeFormatter.toChar("Dth", date2));
    assertEquals("7th", DateTimeFormatter.toChar("Dth", date3));

    assertEquals("2nd", DateTimeFormatter.toChar("FMDth", date1));
  }

  @Test void testWW() {
    final ZonedDateTime date1 = createDateTime(2016, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2016, 3, 1, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2016, 10, 1, 23, 0, 0, 0);

    assertEquals("1", DateTimeFormatter.toChar("WW", date1));
    assertEquals("9", DateTimeFormatter.toChar("WW", date2));
    assertEquals("40", DateTimeFormatter.toChar("WW", date3));
    assertEquals("1", DateTimeFormatter.toChar("FMWW", date1));
    assertEquals("9", DateTimeFormatter.toChar("FMWW", date2));
    assertEquals("40", DateTimeFormatter.toChar("FMWW", date3));

    assertEquals("1ST", DateTimeFormatter.toChar("WWTH", date1));
    assertEquals("9TH", DateTimeFormatter.toChar("WWTH", date2));
    assertEquals("40TH", DateTimeFormatter.toChar("WWTH", date3));
    assertEquals("1st", DateTimeFormatter.toChar("WWth", date1));
    assertEquals("9th", DateTimeFormatter.toChar("WWth", date2));
    assertEquals("40th", DateTimeFormatter.toChar("WWth", date3));

    assertEquals("1st", DateTimeFormatter.toChar("FMWWth", date1));
  }

  @Test void testW() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 1, 15, 23, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 10, 31, 23, 0, 0, 0);

    assertEquals("1", DateTimeFormatter.toChar("W", date1));
    assertEquals("3", DateTimeFormatter.toChar("W", date2));
    assertEquals("5", DateTimeFormatter.toChar("W", date3));
    assertEquals("1", DateTimeFormatter.toChar("FMW", date1));
    assertEquals("3", DateTimeFormatter.toChar("FMW", date2));
    assertEquals("5", DateTimeFormatter.toChar("FMW", date3));

    assertEquals("1ST", DateTimeFormatter.toChar("WTH", date1));
    assertEquals("3RD", DateTimeFormatter.toChar("WTH", date2));
    assertEquals("5TH", DateTimeFormatter.toChar("WTH", date3));
    assertEquals("1st", DateTimeFormatter.toChar("Wth", date1));
    assertEquals("3rd", DateTimeFormatter.toChar("Wth", date2));
    assertEquals("5th", DateTimeFormatter.toChar("Wth", date3));

    assertEquals("1st", DateTimeFormatter.toChar("FMWth", date1));
  }

  @Test void testCC() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 23, 0, 0, 0);
    final ZonedDateTime date2 = date1.minusYears(2023);
    final ZonedDateTime date3 = date2.minusYears(1);
    final ZonedDateTime date4 = date3.minusYears(200);

    assertEquals("21", DateTimeFormatter.toChar("CC", date1));
    assertEquals("01", DateTimeFormatter.toChar("CC", date2));
    assertEquals("-01", DateTimeFormatter.toChar("CC", date3));
    assertEquals("-03", DateTimeFormatter.toChar("CC", date4));
    assertEquals("21", DateTimeFormatter.toChar("FMCC", date1));
    assertEquals("1", DateTimeFormatter.toChar("FMCC", date2));
    assertEquals("-1", DateTimeFormatter.toChar("FMCC", date3));
    assertEquals("-3", DateTimeFormatter.toChar("FMCC", date4));

    assertEquals("21ST", DateTimeFormatter.toChar("CCTH", date1));
    assertEquals("01ST", DateTimeFormatter.toChar("CCTH", date2));
    assertEquals("-01ST", DateTimeFormatter.toChar("CCTH", date3));
    assertEquals("-03RD", DateTimeFormatter.toChar("CCTH", date4));
    assertEquals("21st", DateTimeFormatter.toChar("CCth", date1));
    assertEquals("01st", DateTimeFormatter.toChar("CCth", date2));
    assertEquals("-01st", DateTimeFormatter.toChar("CCth", date3));
    assertEquals("-03rd", DateTimeFormatter.toChar("CCth", date4));

    assertEquals("-1st", DateTimeFormatter.toChar("FMCCth", date3));
  }

  @Test void testJ() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime date2 = date1.minusYears(2024);
    final ZonedDateTime date3 = date2.minusYears(1000);

    assertEquals("2460311", DateTimeFormatter.toChar("J", date1));
    assertEquals("1721060", DateTimeFormatter.toChar("J", date2));
    assertEquals("1356183", DateTimeFormatter.toChar("J", date3));
    assertEquals("2460311", DateTimeFormatter.toChar("FMJ", date1));
    assertEquals("1721060", DateTimeFormatter.toChar("FMJ", date2));
    assertEquals("1356183", DateTimeFormatter.toChar("FMJ", date3));

    assertEquals("2460311TH", DateTimeFormatter.toChar("JTH", date1));
    assertEquals("1721060TH", DateTimeFormatter.toChar("JTH", date2));
    assertEquals("1356183RD", DateTimeFormatter.toChar("JTH", date3));
    assertEquals("2460311th", DateTimeFormatter.toChar("Jth", date1));
    assertEquals("1721060th", DateTimeFormatter.toChar("Jth", date2));
    assertEquals("1356183rd", DateTimeFormatter.toChar("Jth", date3));
  }

  @Test void testQ() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 4, 9, 0, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 8, 23, 0, 0, 0, 0);
    final ZonedDateTime date4 = createDateTime(2024, 12, 31, 0, 0, 0, 0);

    assertEquals("1", DateTimeFormatter.toChar("Q", date1));
    assertEquals("2", DateTimeFormatter.toChar("Q", date2));
    assertEquals("3", DateTimeFormatter.toChar("Q", date3));
    assertEquals("4", DateTimeFormatter.toChar("Q", date4));
    assertEquals("1", DateTimeFormatter.toChar("FMQ", date1));
    assertEquals("2", DateTimeFormatter.toChar("FMQ", date2));
    assertEquals("3", DateTimeFormatter.toChar("FMQ", date3));
    assertEquals("4", DateTimeFormatter.toChar("FMQ", date4));

    assertEquals("1ST", DateTimeFormatter.toChar("QTH", date1));
    assertEquals("2ND", DateTimeFormatter.toChar("QTH", date2));
    assertEquals("3RD", DateTimeFormatter.toChar("QTH", date3));
    assertEquals("4TH", DateTimeFormatter.toChar("QTH", date4));
    assertEquals("1st", DateTimeFormatter.toChar("Qth", date1));
    assertEquals("2nd", DateTimeFormatter.toChar("Qth", date2));
    assertEquals("3rd", DateTimeFormatter.toChar("Qth", date3));
    assertEquals("4th", DateTimeFormatter.toChar("Qth", date4));
  }

  @Test void testRMUpperCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 4, 9, 0, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 8, 23, 0, 0, 0, 0);
    final ZonedDateTime date4 = createDateTime(2024, 12, 31, 0, 0, 0, 0);

    assertEquals("I", DateTimeFormatter.toChar("RM", date1));
    assertEquals("IV", DateTimeFormatter.toChar("RM", date2));
    assertEquals("VIII", DateTimeFormatter.toChar("RM", date3));
    assertEquals("XII", DateTimeFormatter.toChar("RM", date4));
  }

  @Test void testRMLowerCase() {
    final ZonedDateTime date1 = createDateTime(2024, 1, 1, 0, 0, 0, 0);
    final ZonedDateTime date2 = createDateTime(2024, 4, 9, 0, 0, 0, 0);
    final ZonedDateTime date3 = createDateTime(2024, 8, 23, 0, 0, 0, 0);
    final ZonedDateTime date4 = createDateTime(2024, 12, 31, 0, 0, 0, 0);

    assertEquals("i", DateTimeFormatter.toChar("rm", date1));
    assertEquals("iv", DateTimeFormatter.toChar("rm", date2));
    assertEquals("viii", DateTimeFormatter.toChar("rm", date3));
    assertEquals("xii", DateTimeFormatter.toChar("rm", date4));
  }

  @Test void testToTimestampHH() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 1, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "HH"));
    assertEquals(
        createDateTime(1, 1, 1, 1, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "HH"));
    assertEquals(
        createDateTime(1, 1, 1, 11, 0, 0, 0),
        DateTimeFormatter.toTimestamp("11", "HH"));

    try {
      DateTimeFormatter.toTimestamp("72", "HH");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "HH");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampHH12() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 1, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "HH12"));
    assertEquals(
        createDateTime(1, 1, 1, 1, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "HH12"));
    assertEquals(
        createDateTime(1, 1, 1, 11, 0, 0, 0),
        DateTimeFormatter.toTimestamp("11", "HH12"));

    try {
      DateTimeFormatter.toTimestamp("72", "HH12");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "HH12");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampHH24() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 1, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "HH24"));
    assertEquals(
        createDateTime(1, 1, 1, 1, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "HH24"));
    assertEquals(
        createDateTime(1, 1, 1, 18, 0, 0, 0),
        DateTimeFormatter.toTimestamp("18", "HH24"));

    try {
      DateTimeFormatter.toTimestamp("72", "HH24");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "HH24");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampMI() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 1, 0, 0),
        DateTimeFormatter.toTimestamp("01", "MI"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 1, 0, 0),
        DateTimeFormatter.toTimestamp("1", "MI"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 57, 0, 0),
        DateTimeFormatter.toTimestamp("57", "MI"));

    try {
      DateTimeFormatter.toTimestamp("72", "MI");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "MI");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampSS() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 1, 0),
        DateTimeFormatter.toTimestamp("01", "SS"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 1, 0),
        DateTimeFormatter.toTimestamp("1", "SS"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 57, 0),
        DateTimeFormatter.toTimestamp("57", "SS"));

    try {
      DateTimeFormatter.toTimestamp("72", "SS");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "SS");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampMS() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000_000),
        DateTimeFormatter.toTimestamp("001", "MS"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000_000),
        DateTimeFormatter.toTimestamp("1", "MS"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 999_000_000),
        DateTimeFormatter.toTimestamp("999", "MS"));

    try {
      DateTimeFormatter.toTimestamp("9999", "MS");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "MS");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampUS() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000),
        DateTimeFormatter.toTimestamp("001", "US"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000),
        DateTimeFormatter.toTimestamp("1", "US"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 999_000),
        DateTimeFormatter.toTimestamp("999", "US"));

    try {
      DateTimeFormatter.toTimestamp("9999999", "US");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "US");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampFF1() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 100_000_000),
        DateTimeFormatter.toTimestamp("1", "FF1"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 900_000_000),
        DateTimeFormatter.toTimestamp("9", "FF1"));

    try {
      DateTimeFormatter.toTimestamp("72", "FF1");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "FF1");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampFF2() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 10_000_000),
        DateTimeFormatter.toTimestamp("01", "FF2"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 10_000_000),
        DateTimeFormatter.toTimestamp("1", "FF2"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 970_000_000),
        DateTimeFormatter.toTimestamp("97", "FF2"));

    try {
      DateTimeFormatter.toTimestamp("999", "FF2");
      fail();
    } catch (Exception e) {
    }

    try {
      DateTimeFormatter.toTimestamp("abc", "FF2");
      fail();
    } catch (Exception e) {
    }
  }

  @Test void testToTimestampFF3() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000_000),
        DateTimeFormatter.toTimestamp("001", "FF3"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000_000),
        DateTimeFormatter.toTimestamp("1", "FF3"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 976_000_000),
        DateTimeFormatter.toTimestamp("976", "FF3"));
  }

  @Test void testToTimestampFF4() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 100_000),
        DateTimeFormatter.toTimestamp("0001", "FF4"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 100_000),
        DateTimeFormatter.toTimestamp("1", "FF4"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 976_200_000),
        DateTimeFormatter.toTimestamp("9762", "FF4"));
  }

  @Test void testToTimestampFF5() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 10_000),
        DateTimeFormatter.toTimestamp("00001", "FF5"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 10_000),
        DateTimeFormatter.toTimestamp("1", "FF5"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 976_210_000),
        DateTimeFormatter.toTimestamp("97621", "FF5"));
  }

  @Test void testToTimestampFF6() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000),
        DateTimeFormatter.toTimestamp("000001", "FF6"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 1_000),
        DateTimeFormatter.toTimestamp("1", "FF6"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 976_214_000),
        DateTimeFormatter.toTimestamp("976214", "FF6"));
  }

  @Test void testToTimestampAMPM() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03AM", "HH12AM"));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03AM", "HH12PM"));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03PM", "HH12AM"));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03PM", "HH12PM"));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03A.M.", "HH12A.M."));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03A.M.", "HH12P.M."));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03P.M.", "HH12A.M."));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03P.M.", "HH12P.M."));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03am", "HH12am"));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03am", "HH12pm"));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03pm", "HH12am"));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03pm", "HH12pm"));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03a.m.", "HH12a.m."));
    assertEquals(
        createDateTime(1, 1, 1, 3, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03a.m.", "HH12p.m."));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03p.m.", "HH12a.m."));
    assertEquals(
        createDateTime(1, 1, 1, 15, 0, 0, 0),
        DateTimeFormatter.toTimestamp("03p.m.", "HH12p.m."));
  }

  @Test void testToTimestampYYYYWithCommas() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("0,001", "Y,YYY"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2,024", "Y,YYY"));
  }

  @Test void testToTimestampYYYY() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("0001", "YYYY"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "YYYY"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024", "YYYY"));
  }

  @Test void testToTimestampYYY() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("001", "YYY"));
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "YYY"));
    assertEquals(
        createDateTime(1987, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("987", "YYY"));
  }

  @Test void testToTimestampYY() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "YY"));
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "YY"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("24", "YY"));
  }

  @Test void testToTimestampY() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "Y"));
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "Y"));
    assertEquals(
        createDateTime(2004, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("4", "Y"));
  }

  @Test void testToTimestampIYYY() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("0001", "IYYY"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "IYYY"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024", "IYYY"));
  }

  @Test void testToTimestampIYY() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("001", "IYY"));
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "IYY"));
    assertEquals(
        createDateTime(1987, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("987", "IYY"));
  }

  @Test void testToTimestampIY() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "IY"));
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "IY"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("24", "IY"));
  }

  @Test void testToTimestampI() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "I"));
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "I"));
    assertEquals(
        createDateTime(2004, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("4", "I"));
  }

  @Test void testToTimestampBCAD() throws Exception {
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920BC", "YYYYBC").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920BC", "YYYYAD").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920AD", "YYYYBC").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920AD", "YYYYAD").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920B.C.", "YYYYB.C.").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920B.C.", "YYYYA.D.").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920A.D.", "YYYYB.C.").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920A.D.", "YYYYA.D.").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920bc", "YYYYbc").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920bc", "YYYYad").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920ad", "YYYYbc").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920ad", "YYYYad").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920b.c.", "YYYYb.c.").get(ChronoField.ERA));
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("1920b.c.", "YYYYa.d.").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920a.d.", "YYYYb.c.").get(ChronoField.ERA));
    assertEquals(
        1,
        DateTimeFormatter.toTimestamp("1920a.d.", "YYYYa.d.").get(ChronoField.ERA));
  }

  @Test void testToTimestampMonthUpperCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1, 1, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("JANUARY", "MONTH"));
      assertEquals(
          createDateTime(1, 3, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("MARCH", "MONTH"));
      assertEquals(
          createDateTime(1, 11, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("NOVEMBER", "MONTH"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampMonthCapitalized() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1, 1, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("January", "Month"));
      assertEquals(
          createDateTime(1, 3, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("March", "Month"));
      assertEquals(
          createDateTime(1, 11, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("November", "Month"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampMonthLowerCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1, 1, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("january", "month"));
      assertEquals(
          createDateTime(1, 3, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("march", "month"));
      assertEquals(
          createDateTime(1, 11, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("november", "month"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampMonUpperCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1, 1, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("JAN", "MON"));
      assertEquals(
          createDateTime(1, 3, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("MAR", "MON"));
      assertEquals(
          createDateTime(1, 11, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("NOV", "MON"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampMonCapitalized() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1, 1, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("Jan", "Mon"));
      assertEquals(
          createDateTime(1, 3, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("Mar", "Mon"));
      assertEquals(
          createDateTime(1, 11, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("Nov", "Mon"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampMonLowerCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1, 1, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("jan", "mon"));
      assertEquals(
          createDateTime(1, 3, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("mar", "mon"));
      assertEquals(
          createDateTime(1, 11, 1, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("nov", "mon"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampMM() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "MM"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "MM"));
    assertEquals(
        createDateTime(1, 11, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("11", "MM"));
  }

  @Test void testToTimestampDayUpperCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1982, 6, 7, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 MONDAY", "IYYY IW DAY"));
      assertEquals(
          createDateTime(1982, 6, 10, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 THURSDAY", "IYYY IW DAY"));
      assertEquals(
          createDateTime(1982, 6, 11, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 FRIDAY", "IYYY IW DAY"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampDayCapitalized() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1982, 6, 7, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 Monday", "IYYY IW Day"));
      assertEquals(
          createDateTime(1982, 6, 10, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 Thursday", "IYYY IW Day"));
      assertEquals(
          createDateTime(1982, 6, 11, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 Friday", "IYYY IW Day"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampDayLowerCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1982, 6, 7, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 monday", "IYYY IW day"));
      assertEquals(
          createDateTime(1982, 6, 10, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 thursday", "IYYY IW day"));
      assertEquals(
          createDateTime(1982, 6, 11, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 friday", "IYYY IW day"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampDyUpperCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1982, 6, 7, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 MON", "IYYY IW DY"));
      assertEquals(
          createDateTime(1982, 6, 10, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 THU", "IYYY IW DY"));
      assertEquals(
          createDateTime(1982, 6, 11, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 FRI", "IYYY IW DY"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampDyCapitalized() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1982, 6, 7, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 Mon", "IYYY IW Dy"));
      assertEquals(
          createDateTime(1982, 6, 10, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 Thu", "IYYY IW Dy"));
      assertEquals(
          createDateTime(1982, 6, 11, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 Fri", "IYYY IW Dy"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampDyLowerCase() throws Exception {
    final Locale originalLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.US);

      assertEquals(
          createDateTime(1982, 6, 7, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 mon", "IYYY IW dy"));
      assertEquals(
          createDateTime(1982, 6, 10, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 thu", "IYYY IW dy"));
      assertEquals(
          createDateTime(1982, 6, 11, 0, 0, 0, 0),
          DateTimeFormatter.toTimestamp("1982 23 fri", "IYYY IW dy"));
    } finally {
      Locale.setDefault(originalLocale);
    }
  }

  @Test void testToTimestampDDD() throws Exception {
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 001", "YYYY DDD"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 1", "YYYY DDD"));
    assertEquals(
        createDateTime(2024, 5, 16, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 137", "YYYY DDD"));
  }

  @Test void testToTimestampDD() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("01", "DD"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "DD"));
    assertEquals(
        createDateTime(1, 1, 23, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("23", "DD"));
  }

  @Test void testToTimestampIDDD() throws Exception {
    assertEquals(
        createDateTime(2019, 12, 30, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2020 001", "IYYY IDDD"));
    assertEquals(
        createDateTime(2019, 12, 30, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2020 1", "IYYY IDDD"));
    assertEquals(
        createDateTime(2020, 5, 14, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2020 137", "IYYY IDDD"));
  }

  @Test void testToTimestampID() throws Exception {
    assertEquals(
        createDateTime(1982, 6, 7, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1982 23 1", "IYYY IW ID"));
    assertEquals(
        createDateTime(1982, 6, 10, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1982 23 4", "IYYY IW ID"));
    assertEquals(
        createDateTime(1982, 6, 11, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1982 23 5", "IYYY IW ID"));
  }

  @Test void testToTimestampW() throws Exception {
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 1 1", "YYYY MM W"));
    assertEquals(
        createDateTime(2024, 4, 8, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 4 2", "YYYY MM W"));
    assertEquals(
        createDateTime(2024, 11, 22, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 11 4", "YYYY MM W"));
  }

  @Test void testToTimestampWW() throws Exception {
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 01", "YYYY WW"));
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 1", "YYYY WW"));
    assertEquals(
        createDateTime(2024, 12, 16, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024 51", "YYYY WW"));
  }

  @Test void testToTimestampIW() throws Exception {
    assertEquals(
        createDateTime(2019, 12, 30, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2020 01", "IYYY IW"));
    assertEquals(
        createDateTime(2019, 12, 30, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2020 1", "IYYY IW"));
    assertEquals(
        createDateTime(2020, 12, 14, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2020 51", "IYYY IW"));
  }

  @Test void testToTimestampCC() throws Exception {
    assertEquals(
        createDateTime(2001, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("21", "CC"));
    assertEquals(
        createDateTime(1501, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("16", "CC"));
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1", "CC"));
  }

  @Test void testToTimestampJ() throws Exception {
    assertEquals(
        createDateTime(2024, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2460311", "J"));
    assertEquals(
        createDateTime(1984, 7, 15, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2445897", "J"));
    assertEquals(
        createDateTime(234, 3, 21, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("1806606", "J"));
  }

  @Test void testToTimestampRMUpperCase() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("I", "RM"));
    assertEquals(
        createDateTime(1, 4, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("IV", "RM"));
    assertEquals(
        createDateTime(1, 9, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("IX", "RM"));
  }

  @Test void testToTimestampRMLowerCase() throws Exception {
    assertEquals(
        createDateTime(1, 1, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("i", "rm"));
    assertEquals(
        createDateTime(1, 4, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("iv", "rm"));
    assertEquals(
        createDateTime(1, 9, 1, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("ix", "rm"));
  }

  @Test void testToTimestampTZH() throws Exception {
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("+00", "TZH").getOffset().get(ChronoField.OFFSET_SECONDS));
    assertEquals(
        3 * 60 * 60,
        DateTimeFormatter.toTimestamp("+03", "TZH").getOffset().get(ChronoField.OFFSET_SECONDS));
    assertEquals(
        -15 * 60 * 60,
        DateTimeFormatter.toTimestamp("-15", "TZH").getOffset().get(ChronoField.OFFSET_SECONDS));
  }

  @Test void testToTimestampTZM() throws Exception {
    assertEquals(
        0,
        DateTimeFormatter.toTimestamp("00", "TZM").getOffset().get(ChronoField.OFFSET_SECONDS));
    assertEquals(
        30 * 60,
        DateTimeFormatter.toTimestamp("30", "TZM").getOffset().get(ChronoField.OFFSET_SECONDS));
    assertEquals(
        55 * 60,
        DateTimeFormatter.toTimestamp("55", "TZM").getOffset().get(ChronoField.OFFSET_SECONDS));
  }

  @Test void testToTimestampDateValidFormats() throws Exception {
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024-04-17", "YYYY-MM-DD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2,024-04-17", "Y,YYY-MM-DD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("24-04-17", "YYY-MM-DD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("24-04-17", "YY-MM-DD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2124-04-17", "CCYY-MM-DD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("20240417", "YYYYMMDD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2,0240417", "Y,YYYMMDD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024-16-3", "IYYY-IW-ID"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024-16 Wednesday", "IYYY-IW Day"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024-108", "IYYY-IDDD"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("April 17, 2024", "Month DD, YYYY"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("IV 17, 2024", "RM DD, YYYY"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("APR 17, 2024", "MON DD, YYYY"));
    assertEquals(
        createDateTime(2024, 4, 15, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024-16", "YYYY-WW"));
    assertEquals(
        createDateTime(2024, 4, 17, 0, 0, 0, 0),
        DateTimeFormatter.toTimestamp("2024-108", "YYYY-DDD"));
  }

  private ZonedDateTime createDateTime(int year, int month, int dayOfMonth, int hour, int minute,
      int seconds, int nanoseconds) {
    return ZonedDateTime.of(
        LocalDateTime.of(year, month, dayOfMonth, hour, minute, seconds, nanoseconds),
        ZoneId.systemDefault());
  }
}
