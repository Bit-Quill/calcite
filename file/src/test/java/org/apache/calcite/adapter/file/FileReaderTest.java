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
package org.apache.calcite.adapter.file;

import org.apache.calcite.util.Source;
import org.apache.calcite.util.Sources;
import org.apache.calcite.util.TestUtil;

import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import static org.apache.calcite.util.TestUtil.getJavaMajorVersion;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;

/**
 * Unit tests for FileReader.
 */
@ExtendWith(RequiresNetworkExtension.class)
class FileReaderTest {

  private static final Source CITIES_SOURCE =
      Sources.url("http://en.wikipedia.org/wiki/List_of_United_States_cities_by_population");

  private static final Source STATES_SOURCE =
      Sources.url(
          "http://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States");

  private static Source resource(String path) {
    final URL url =
        requireNonNull(FileReaderTest.class.getResource("/" + path), "url");
    return Sources.of(url);
  }

  private static String resourcePath(String path) {
    return resource(path).file().getAbsolutePath();
  }

  /** Tests {@link FileReader} URL instantiation - no path. */
  @Disabled("[CALCITE-3800] FileReaderTest#testFileReaderUrlNoPath() timeout for AppVeyor test")
  @Test @RequiresNetwork public void testFileReaderUrlNoPath() throws FileReaderException {
    // Under OpenJDK, test fails with the following, so skip test:
    //   javax.net.ssl.SSLHandshakeException:
    //   sun.security.validator.ValidatorException: PKIX path building failed:
    //   sun.security.provider.certpath.SunCertPathBuilderException:
    //   unable to find valid certification path to requested target
    final String r = getProperty("java.runtime.name");
    // http://openjdk.java.net/jeps/319 => root certificates are bundled with JEP 10
    assumeTrue(!r.equals("OpenJDK Runtime Environment")
            || getJavaMajorVersion() > 10,
        "Java 10+ should have root certificates (JEP 319). Runtime is "
            + r + ", Jave major version is " + getJavaMajorVersion());

    FileReader t = new FileReader(STATES_SOURCE);
    t.refresh();
  }

  /** Tests {@link FileReader} URL instantiation - with path. */
  @Disabled("[CALCITE-1789] Wikipedia format change breaks file adapter test")
  @Test @RequiresNetwork public void testFileReaderUrlWithPath() throws FileReaderException {
    FileReader t =
        new FileReader(CITIES_SOURCE,
            "#mw-content-text > table.wikitable.sortable", 0);
    t.refresh();
  }

  /** Tests {@link FileReader} URL fetch. */
  @Disabled("[CALCITE-1789] Wikipedia format change breaks file adapter test")
  @Test @RequiresNetwork public void testFileReaderUrlFetch() throws FileReaderException {
    FileReader t =
        new FileReader(STATES_SOURCE,
            "#mw-content-text > table.wikitable.sortable", 0);
    int i = 0;
    for (Elements row : t) {
      i++;
    }
    assertThat(i, is(51));
  }

  /** Tests failed {@link FileReader} instantiation - malformed URL. */
  @Test void testFileReaderMalUrl() {
    try {
      final Source badSource = Sources.url("bad" + CITIES_SOURCE.url());
      fail("expected exception, got " + badSource);
    } catch (RuntimeException e) {
      assertThat(e.getCause(), instanceOf(MalformedURLException.class));
      assertThat(e.getCause().getMessage(), is("unknown protocol: badhttp"));
    }
  }

  /** Tests failed {@link FileReader} instantiation - bad URL. */
  @Test void testFileReaderBadUrl() {
    final String uri =
        "http://ex.wikipedia.org/wiki/List_of_United_States_cities_by_population";
    assertThrows(FileReaderException.class, () -> {
      FileReader t = new FileReader(Sources.url(uri), "table:eq(4)");
      t.refresh();
    });
  }

  /** Tests failed {@link FileReader} instantiation - bad selector. */
  @Test void testFileReaderBadSelector() {
    final Source source = resource("tableOK.html");
    assertThrows(FileReaderException.class, () -> {
      FileReader t = new FileReader(source, "table:eq(1)");
      t.refresh();
    });
  }

  /** Test {@link FileReader} with static file - headings. */
  @Test void testFileReaderHeadings() throws FileReaderException {
    final Source source = resource("tableOK.html");
    FileReader t = new FileReader(source);
    Elements headings = t.getHeadings();
    assertTrue(headings.get(1).text().equals("H1"));
  }

  /** Test {@link FileReader} with static file - data. */
  @Test void testFileReaderData() throws FileReaderException {
    final Source source = resource("tableOK.html");
    FileReader t = new FileReader(source);
    Iterator<Elements> i = t.iterator();
    Elements row = i.next();
    assertTrue(row.get(2).text().equals("R0C2"));
    row = i.next();
    assertTrue(row.get(0).text().equals("R1C0"));
  }

  /** Tests {@link FileReader} with bad static file - headings. */
  @Test void testFileReaderHeadingsBadFile() throws FileReaderException {
    final Source source = resource("tableNoTheadTbody.html");
    FileReader t = new FileReader(source);
    Elements headings = t.getHeadings();
    assertTrue(headings.get(1).text().equals("H1"));
  }

  /** Tests {@link FileReader} with bad static file - data. */
  @Test void testFileReaderDataBadFile() throws FileReaderException {
    final Source source = resource("tableNoTheadTbody.html");
    FileReader t = new FileReader(source);
    Iterator<Elements> i = t.iterator();
    Elements row = i.next();
    assertTrue(row.get(2).text().equals("R0C2"));
    row = i.next();
    assertTrue(row.get(0).text().equals("R1C0"));
  }

  /** Tests {@link FileReader} with no headings static file - data. */
  @Test void testFileReaderDataNoTh() throws FileReaderException {
    final Source source = resource("tableNoTH.html");
    FileReader t = new FileReader(source);
    Iterator<Elements> i = t.iterator();
    Elements row = i.next();
    assertTrue(row.get(2).text().equals("R0C2"));
  }

  /** Tests {@link FileReader} iterator with a static file. */
  @Test void testFileReaderIterator() throws FileReaderException {
    final Source source = resource("tableOK.html");
    FileReader t = new FileReader(source);
    Elements row = null;
    for (Elements aT : t) {
      row = aT;
    }
    assertFalse(row == null);
    assertTrue(row.get(1).text().equals("R2C1"));
  }

  /** Tests reading a CSV file via the file adapter. Based on the test case for
   * <a href="https://issues.apache.org/jira/browse/CALCITE-1952">[CALCITE-1952]
   * NPE in planner</a>. */
  @Test void testCsvFile() throws Exception {
    Properties info = new Properties();
    final String path = resourcePath("sales-csv");
    final String model = "inline:"
        + "{\n"
        + "  \"version\": \"1.0\",\n"
        + "  \"defaultSchema\": \"XXX\",\n"
        + "  \"schemas\": [\n"
        + "    {\n"
        + "      \"name\": \"FILES\",\n"
        + "      \"type\": \"custom\",\n"
        + "      \"factory\": \"org.apache.calcite.adapter.file.FileSchemaFactory\",\n"
        + "      \"operand\": {\n"
        + "        \"directory\": " + TestUtil.escapeString(path) + "\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}";
    info.put("model", model);
    info.put("lex", "JAVA");

    try (Connection connection =
             DriverManager.getConnection("jdbc:calcite:", info);
         Statement stmt = connection.createStatement()) {
      final String sql = "select * from FILES.DEPTS";
      final ResultSet rs = stmt.executeQuery(sql);
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("10"));
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("20"));
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("30"));
      assertThat(rs.next(), is(false));
      rs.close();
    }
  }

  /**
   * Tests reading a JSON file via the file adapter.
   */
  @Test void testJsonFile() throws Exception {
    Properties info = new Properties();
    final String path = resourcePath("sales-json");
    final String model = "inline:"
        + "{\n"
        + "  \"version\": \"1.0\",\n"
        + "  \"defaultSchema\": \"XXX\",\n"
        + "  \"schemas\": [\n"
        + "    {\n"
        + "      \"name\": \"FILES\",\n"
        + "      \"type\": \"custom\",\n"
        + "      \"factory\": \"org.apache.calcite.adapter.file.FileSchemaFactory\",\n"
        + "      \"operand\": {\n"
        + "        \"directory\": " + TestUtil.escapeString(path) + "\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}";
    info.put("model", model);
    info.put("lex", "JAVA");

    try (Connection connection =
             DriverManager.getConnection("jdbc:calcite:", info);
         Statement stmt = connection.createStatement()) {
      final String sql = "select * from FILES.DEPTS";
      final ResultSet rs = stmt.executeQuery(sql);
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("10"));
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("20"));
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("30"));
      assertThat(rs.next(), is(false));
      rs.close();
    }
  }

  /**
   * Tests reading two JSON file with join via the file adapter.
   */
  @Test void testJsonFileWithJoin() throws Exception {
    Properties info = new Properties();
    final String path = resourcePath("sales-json");
    final String model = "inline:"
        + "{\n"
        + "  \"version\": \"1.0\",\n"
        + "  \"defaultSchema\": \"XXX\",\n"
        + "  \"schemas\": [\n"
        + "    {\n"
        + "      \"name\": \"FILES\",\n"
        + "      \"type\": \"custom\",\n"
        + "      \"factory\": \"org.apache.calcite.adapter.file.FileSchemaFactory\",\n"
        + "      \"operand\": {\n"
        + "        \"directory\": " + TestUtil.escapeString(path) + "\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}";
    info.put("model", model);
    info.put("lex", "JAVA");

    try (Connection connection =
             DriverManager.getConnection("jdbc:calcite:", info);
         Statement stmt = connection.createStatement()) {
      final String sql = "select a.EMPNO,a.NAME,a.CITY,b.DEPTNO "
          + "from FILES.EMPS a, FILES.DEPTS b where a.DEPTNO = b.DEPTNO";
      final ResultSet rs = stmt.executeQuery(sql);
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("100"));
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("110"));
      assertThat(rs.next(), is(true));
      assertThat(rs.getString(1), is("120"));
      assertThat(rs.next(), is(false));
      rs.close();
    }
  }
}
