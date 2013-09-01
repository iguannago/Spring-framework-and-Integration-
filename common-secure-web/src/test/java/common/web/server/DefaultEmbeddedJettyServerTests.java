/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.web.server;

import common.web.KeyAndTrustStoreInfo;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ssl.SslConnector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.net.ssl.*;
import javax.servlet.Servlet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author djnorth
 */
@RunWith(Parameterized.class)
public class DefaultEmbeddedJettyServerTests {

    private static final String CONTENT_TYPE = "text/plain";

    private static final String HOSTNAME = HostNetUtils.getLocalHostname();

    /**
     * The tests will check what happens when the key-store is the trust-store
     * by using the short constructor defaulting trust-store to key-store. Also,
     * both server trust-stores are set up to work with both clients, and
     * vice-versa.
     * <p/>
     * The tests will also artificially create invalid scenarios, so make the
     * parameters below valid.
     *
     * @return parameters
     */
    @Parameters
    public static List<Object[]> getParameters() {

        if (logger.isDebugEnabled()) {
            System.setProperty("javax.net.debug", "ssl");
        }

        Object[][] parameters = {
                {18280, 0, null, null, null, null, null, null, null, null, "/app", new String[]{"/echo1", "/echo2"}},
                {
                        0, 8643, KeyAndTrustStoreInfo.getServer0Ks(),
                        KeyAndTrustStoreInfo.SERVER0_KS_PWD,
                        KeyAndTrustStoreInfo.getServer0Ts(),
                        KeyAndTrustStoreInfo.SERVER0_TS_PWD,
                        KeyAndTrustStoreInfo.getClient0Ks(),
                        KeyAndTrustStoreInfo.CLIENT0_KS_PWD,
                        KeyAndTrustStoreInfo.getClient0Ts(),
                        KeyAndTrustStoreInfo.CLIENT0_TS_PWD,
                        null,
                        new String[]{"/echo1", "/echo2", "/echo3"}
                },
                {
                        18380, 8743, KeyAndTrustStoreInfo.getServer1Ks(),
                        KeyAndTrustStoreInfo.SERVER1_KS_PWD,
                        KeyAndTrustStoreInfo.getServer1Ts(),
                        KeyAndTrustStoreInfo.SERVER1_TS_PWD,
                        KeyAndTrustStoreInfo.getClient1Ks(),
                        KeyAndTrustStoreInfo.CLIENT1_KS_PWD,
                        KeyAndTrustStoreInfo.getClient1Ts(),
                        KeyAndTrustStoreInfo.CLIENT1_TS_PWD,
                        "/app1",
                        new String[]{"/echo1"}
                }
        };

        return Arrays.asList(parameters);
    }

    private static Logger logger = Logger.getLogger("common.web");

    /**
     * Supplied end expected parms
     */
    private final int                  suppliedHttpPort;
    private final int                  suppliedHttpsPort;
    private final String               suppliedKeyStore;
    private final String               suppliedKeyStorePassword;
    private final String               suppliedTrustStore;
    private final String               suppliedTrustStorePassword;
    private final String               clientKeyStore;
    private final String               clientKeyStorePassword;
    private final String               clientTrustStore;
    private final String               clientTrustStorePassword;
    private final String               applicationContextPath;
    private final Map<String, Servlet> servletMap;

    /**
     * Server under test
     */
    private EmbeddedJettyServer embeddedJettyServer = null;

    /**
     * Constructor to build tests from parameters
     *
     * @param suppliedHttpPort
     * @param suppliedHttpsPort
     * @param suppliedKeyStore
     * @param suppliedKeyStorePassword
     * @param suppliedTrustStore
     * @param suppliedTrustStorePassword
     * @param clientKeyStore
     * @param clientKeyStorePassword
     * @param clientTrustStore
     * @param clientTrustStorePassword
     * @param applicationContextPath
     * @param servletPathsToDeploy
     */
    public DefaultEmbeddedJettyServerTests(int suppliedHttpPort,
                                           int suppliedHttpsPort, String suppliedKeyStore,
                                           String suppliedKeyStorePassword, String suppliedTrustStore,
                                           String suppliedTrustStorePassword, String clientKeyStore,
                                           String clientKeyStorePassword, String clientTrustStore,
                                           String clientTrustStorePassword, String applicationContextPath,
                                           String[] servletPathsToDeploy) {

        this.suppliedHttpPort = suppliedHttpPort;
        this.suppliedHttpsPort = suppliedHttpsPort;
        this.suppliedKeyStore = suppliedKeyStore;
        this.suppliedKeyStorePassword = suppliedKeyStorePassword;
        this.suppliedTrustStore = suppliedTrustStore;
        this.suppliedTrustStorePassword = suppliedTrustStorePassword;
        this.clientKeyStore = clientKeyStore;
        this.clientKeyStorePassword = clientKeyStorePassword;
        this.clientTrustStore = clientTrustStore;
        this.clientTrustStorePassword = clientTrustStorePassword;
        this.applicationContextPath = applicationContextPath;
        this.servletMap = new HashMap<String, Servlet>();
        for (String servletPath : servletPathsToDeploy) {
            this.servletMap.put(servletPath, new EchoServlet());
        }

        logger.info("test with this=" + this);
    }

    /**
     * Test constructor with all arguments, assuming they are good
     */
    @Test
    public void testContructorAllArgsGood() throws Exception {
        logger.debug("test:all args good:" + this);
        this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                suppliedHttpPort, suppliedHttpsPort, suppliedKeyStore,
                suppliedKeyStorePassword, suppliedTrustStore,
                suppliedTrustStorePassword);

        verifyConstructedServer(suppliedHttpPort, suppliedHttpsPort,
                                suppliedKeyStore, suppliedKeyStorePassword, suppliedTrustStore,
                                suppliedTrustStorePassword);
    }

    /**
     * Test constructor with all arguments, with inconsistent passwords for
     * matching key-store and trust-store.
     */
    @Test
    public void testContructorAllArgsHttpsKeyEqTrustStorePasswordsInconsistent()
            throws Exception {

        if (suppliedHttpsPort > 0 && suppliedKeyStore != null
            && suppliedKeyStore.equals(suppliedTrustStore)) {
            logger.debug("test:httpsPort > 0 and keyStore eq trustStore:" + this);
            try {
                this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                        suppliedHttpPort, suppliedHttpsPort, suppliedKeyStore,
                        suppliedKeyStorePassword, suppliedKeyStore,
                        suppliedKeyStorePassword + "!!!");

                Assert.fail("expected an exception for mis-matched passwords");
            } catch (IllegalStateException ise) {
                logger.info("expected exception for mis-matched passwords:this=" + this, ise);
            }
        } else {
            logger.debug("skipping test:httpsPort <=0 or keyStore != trustStore:" + this);
        }

    }

    /**
     * Test constructor with all arguments, with missing key-store
     */
    @Test
    public void testContructorAllArgsHttpsMissingKeyStore() throws Exception {

        if (suppliedHttpsPort > 0) {

            logger.debug("test:httpsPort > 0 missing keyStore:" + this);
            try {
                this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                        suppliedHttpPort, suppliedHttpsPort, null,
                        suppliedKeyStorePassword, suppliedTrustStore,
                        suppliedTrustStorePassword);

                Assert.fail("expected an exception for missing keyStore:" + this);

            } catch (IllegalArgumentException iae) {
                logger.debug("expected exception for missing keyStore:this=" + this, iae);
            }
        } else {
            logger.info("skipping test for missing keyStore:httpsPort <=0");
        }

    }

    /**
     * Test constructor with all arguments, with missing key-store pwd
     */
    @Test
    public void testContructorAllArgsHttpsMissingKeyStorePassword()
            throws Exception {

        if (suppliedHttpsPort > 0) {

            logger.debug("test:httpsPort > 0 missing keyStorePassword:" + this);
            try {
                this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                        suppliedHttpPort, suppliedHttpsPort, suppliedKeyStore,
                        null, suppliedTrustStore, suppliedTrustStorePassword);

                Assert.fail("expected an exception for missing keyStorePassword:" + this);

            } catch (IllegalArgumentException iae) {
                logger.info("expected exception for missing keyStorePassword:this=" + this, iae);
            }
        } else {
            logger.info("skipping test for missing keyStorePassword:httpsPort <=0");
        }

    }

    /**
     * Test constructor with all arguments, with missing trust-store
     */
    @Test
    public void testContructorAllArgsHttpsMissingTrustStore() throws Exception {

        if (suppliedHttpsPort > 0) {

            logger.debug("test:httpsPort > 0 missing trustStore:" + this);
            try {
                this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                        suppliedHttpPort, suppliedHttpsPort, suppliedKeyStore,
                        suppliedKeyStorePassword, null,
                        suppliedTrustStorePassword);

                Assert.fail("expected an exception for missing trustStore:" + this);

            } catch (IllegalArgumentException iae) {
                logger.info("expected exception for missing trustStore:this=" + this, iae);
            }
        } else {
            logger.info("skipping test for missing trustStore:httpsPort <=0");
        }

    }

    /**
     * Test constructor with all arguments, with missing trust-store password
     */
    @Test
    public void testContructorAllArgsHttpsMissingTrustStorePassword()
            throws Exception {

        if (suppliedHttpsPort > 0) {

            logger.debug("test:httpsPort > 0 missing trustStorePassword:" + this);
            try {
                this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                        suppliedHttpPort, suppliedHttpsPort, suppliedKeyStore,
                        suppliedKeyStorePassword, suppliedTrustStore, null);

                Assert.fail("expected an exception for missing trustStorePassword:" + this);

            } catch (IllegalArgumentException iae) {
                logger.info("expected exception for missing trustStorePassword:this=" + this, iae);
            }
        } else {
            logger.info("skipping test for missing trustStorePassword:httpsPort <=0");
        }

    }

    /**
     * Test short constructor with keyStore arguments (trustStore -> keyStore)
     */
    @Test
    public void testContructorKeyStoreArgs() throws Exception {
        logger.debug("test:keyStore args only:" + this);
        this.embeddedJettyServer = new DefaultEmbeddedJettyServer(
                suppliedHttpPort, suppliedHttpsPort, suppliedKeyStore,
                suppliedKeyStorePassword);

        verifyConstructedServer(suppliedHttpPort, suppliedHttpsPort,
                                suppliedKeyStore, suppliedKeyStorePassword, suppliedKeyStore,
                                suppliedKeyStorePassword);

    }


    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DefaultEmbeddedJettyServerTests [suppliedHttpPort=");
        builder.append(suppliedHttpPort);
        builder.append(", suppliedHttpsPort=");
        builder.append(suppliedHttpsPort);
        builder.append(", suppliedKeyStore=");
        builder.append(suppliedKeyStore);
        builder.append(", suppliedKeyStorePassword=");
        builder.append(suppliedKeyStorePassword);
        builder.append(", suppliedTrustStore=");
        builder.append(suppliedTrustStore);
        builder.append(", suppliedTrustStorePassword=");
        builder.append(suppliedTrustStorePassword);
        builder.append(", clientKeyStore=");
        builder.append(clientKeyStore);
        builder.append(", clientKeyStorePassword=");
        builder.append(clientKeyStorePassword);
        builder.append(", clientTrustStore=");
        builder.append(clientTrustStore);
        builder.append(", clientTrustStorePassword=");
        builder.append(clientTrustStorePassword);
        builder.append(", embeddedJettyServer=");
        builder.append(embeddedJettyServer);
        builder.append("]");
        return builder.toString();
    }


    /**
     * Helper to verify constructed server.
     * <p/>
     * We've suppressed deprecation warnings given that getPort etc have been
     * deprecated without corresponding update to Javadoc.
     *
     * @param useHttpPort
     * @param useHttpsPort
     * @param useKeyStore
     * @param useKeyStorePassword
     * @param useTrustStore
     * @param useTrustStorePassword
     */
    @SuppressWarnings("deprecation")
    private void verifyConstructedServer(int useHttpPort, int useHttpsPort,
                                         String useKeyStore, String useKeyStorePassword,
                                         String useTrustStore, String useTrustStorePassword)
            throws Exception {

        final String msg = "useHttpPort=" + useHttpPort + ":useHttpsPort="
                           + useHttpsPort + ":useKeyStore=" + useKeyStore
                           + ":useKeyStorePassword=" + useKeyStorePassword
                           + ":useTrustStore=" + useTrustStore + ":useTrustStorePassword="
                           + useTrustStorePassword + ":this=" + this;

        Assert.assertNotNull(msg, embeddedJettyServer);

        Connector connector = embeddedJettyServer.getHttpConnector();
        if (useHttpPort > 0) {
            Assert.assertNotNull("should have httpConnector" + msg, connector);
            Assert.assertEquals("httpPort" + msg, useHttpPort,
                                connector.getPort());
        } else {
            Assert.assertNull("should not have httpConnector" + msg, connector);
        }

        SslConnector sslConnector = embeddedJettyServer.getHttpsConnector();
        if (useHttpsPort > 0) {
            Assert.assertNotNull("should have httpsConnector" + msg,
                                 sslConnector);
            Assert.assertEquals("httpsPort" + msg, useHttpsPort,
                                sslConnector.getPort());
            verifyStoreFile("keyStore", msg, useKeyStore, sslConnector.getKeystore());
            verifyStoreFile("trustStore", msg, useTrustStore, sslConnector.getTruststore());
        } else {
            Assert.assertNull("should not have httpsConnector" + msg, sslConnector);
        }

        if (useHttpPort > 0 || useHttpsPort > 0) {

            if (applicationContextPath != null) {
                embeddedJettyServer.setApplicationContextPath(applicationContextPath);
            }

            embeddedJettyServer.setServlets(servletMap);

            try {

                Assert.assertTrue("Server not started in 120 secs:"
                                  + embeddedJettyServer,
                                  embeddedJettyServer.startWait(120));

                if (useHttpPort > 0) {
                    final String body = "testHttp";
                }

                if (useHttpsPort > 0) {
                    final String body = "testHttps";
                    SSLSocketFactory factory = null;
                    SSLContext ctx;
                    KeyManagerFactory kmf;
                    KeyStore ks;
                    char[] kspwd = clientKeyStorePassword.toCharArray();
                    TrustManagerFactory tmf;
                    KeyStore ts;
                    char[] tspwd = clientTrustStorePassword.toCharArray();

                    ctx = SSLContext.getInstance("TLS");
                    kmf = KeyManagerFactory.getInstance("SunX509");
                    ks = KeyStore.getInstance("JKS");
                    tmf = TrustManagerFactory.getInstance("SunX509");
                    ts = KeyStore.getInstance("JKS");

                    ks.load(new FileInputStream(clientKeyStore), kspwd);
                    ts.load(new FileInputStream(clientTrustStore), tspwd);

                    kmf.init(ks, kspwd);
                    tmf.init(ts);
                    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                    factory = ctx.getSocketFactory();

                    for (String servletPath : servletMap.keySet()) {
                        String paths;
                        if (applicationContextPath == null) {
                            paths = servletPath;
                        } else if (applicationContextPath.endsWith("/") || servletPath.startsWith("/")) {
                            paths = applicationContextPath + servletPath;
                        } else {
                            paths = applicationContextPath + "/" + servletPath;
                        }

                        URL url = new URL("https", HOSTNAME, useHttpsPort, paths);
                        HttpsURLConnection sslConnection = (HttpsURLConnection) url
                                .openConnection();
                        sslConnection.disconnect();
                        sslConnection.setSSLSocketFactory(factory);
                        verifyRequestResponse(useHttpsPort, body, sslConnection);
                    }
                }

            } finally {

                if (embeddedJettyServer != null) {
                    embeddedJettyServer.stopAndJoin();
                    embeddedJettyServer = null;
                }
            }

        }
    }


    /**
     * Helper comparing store files, allowing for path expansion
     *
     * @param storeName
     * @param msg
     * @param expectedStoreFileName
     * @param actualStoreFileName
     */
    private void verifyStoreFile(String storeName, String msg, String expectedStoreFileName,
                                 String actualStoreFileName) {
        final File expectedStoreFile = new File(expectedStoreFileName).getAbsoluteFile();
        final File actualStoreFile = new File(actualStoreFileName).getAbsoluteFile();

        Assert.assertEquals(storeName + ':' + msg, expectedStoreFile, actualStoreFile);
    }


    /**
     * Call this to run common test using socket that's been set up
     * appropriately
     *
     * @param useHttpPort
     * @param body
     * @param connection
     * @throws java.net.ProtocolException
     * @throws java.io.IOException
     */
    private void verifyRequestResponse(int useHttpPort, final String body,
                                       HttpURLConnection connection) throws ProtocolException, IOException {

        final int bodyLen = body.length();
        OutputStream reqOutputStream = null;
        InputStreamReader respReader = null;
        try {
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Host", HOSTNAME + ':' + useHttpPort);
            connection.setRequestProperty("Content-Type", CONTENT_TYPE);
            connection.setRequestProperty("Content-Length",
                                          Integer.toString(bodyLen));
            reqOutputStream = connection.getOutputStream();
            PrintWriter reqWriter = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(reqOutputStream)));
            reqWriter.print(body);
            logger.debug("sent body +---\n" + body + "\nsent body:bodyLen="
                         + bodyLen + " ----");
            reqWriter.flush();
            reqWriter.close();
            reqOutputStream = null;

            int responseCode = connection.getResponseCode();
            String responseMsg = connection.getResponseMessage();
            logger.debug("url=" + connection.getURL() + ":server=" + embeddedJettyServer);
            logger.debug("status:" + responseCode + " " + responseMsg);
            Assert.assertEquals("Should get 200 OK:" + responseMsg,
                                HttpURLConnection.HTTP_OK, responseCode);

            String contentType = connection.getContentType();
            logger.debug("Content-Type:" + contentType);
            int contentLen = connection.getContentLength();
            logger.debug("Content-Length:" + contentLen);

            Assert.assertEquals("Content-Type", CONTENT_TYPE, contentType);

            Map<String, List<String>> headers = connection.getHeaderFields();
            logger.debug("headers:" + headers);

            respReader = new InputStreamReader(connection.getInputStream());
            char[] respBodyBytes = new char[contentLen + 1];

            int readLen = 0;
            while (true) {
                int thisReadLen = respReader.read(respBodyBytes, readLen, respBodyBytes.length-readLen);
                if (thisReadLen < 0) {
                    break;
                }
                logger.debug("thisReadLen=" + thisReadLen);
                readLen += thisReadLen;
                logger.debug("readLen->" + readLen);
            }
            Assert.assertTrue("resp body not empty", readLen >= 0);

            String respBody = new String(respBodyBytes, 0, readLen);
            logger.debug("resp body +---\n>" + respBody + "<\nresp body:readLen="
                         + readLen + " ----");

            Assert.assertEquals("Content-Length", bodyLen, contentLen);
            Assert.assertEquals("Length read", contentLen, readLen);
            Assert.assertEquals("body text", body, respBody);
        } finally {
            if (reqOutputStream != null) {
                reqOutputStream.close();
            }

            if (respReader != null) {
                respReader.close();
            }
        }
    }

}
