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

package common.web.client;

import common.web.KeyAndTrustStoreInfo;
import common.web.server.DefaultEmbeddedJettyServer;
import common.web.server.EchoServlet;
import common.web.server.EmbeddedJettyServer;
import common.web.server.HostNetUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.meanbean.lang.Factory;
import org.meanbean.test.BeanTester;
import org.meanbean.test.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.mockito.Mockito.*;

/**
 * @author djnorth
 *         <p/>
 *         Unit test for {@link X509HttpClientFactoryBean}
 */
public class X509HttpClientFactoryBeanTests {

    /**
     * Path for echo servlet
     */
    private static final String ECHO_PATH = "/echo";

    /**
     * Full URL for deployed echo server
     */
    private static String echoUrl;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger("common.web");

    /**
     * Embedded server
     */
    private static EmbeddedJettyServer server;

    /**
     * Object under test
     */
    private X509HttpClientFactoryBean x509HttpClientFactoryBean;

    /**
     * Set up embedded server
     */
    @BeforeClass
    public static void setUpServer() {
        final int httpsPort = HostNetUtils.getFreePort(8443);
        server = new DefaultEmbeddedJettyServer(0, httpsPort,
                                                KeyAndTrustStoreInfo.getServer1Ks(),
                                                KeyAndTrustStoreInfo.SERVER1_KS_PWD);
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.getServer().setHandler(context);
        context.addServlet(new ServletHolder(new EchoServlet()), ECHO_PATH);
        Assert.assertTrue("Server not started", server.startWait());

        String localhost = HostNetUtils.getLocalHostname();

        echoUrl = "https://" + localhost + ':' + httpsPort + ECHO_PATH;
        logger.info("echoUrl=" + echoUrl);
    }


    /**
     * Test afterPropertiesSet for undefined info
     */
    @Test
    public void testAfterPropertiesSetDefaultConstructorNoDefaultInfoInSysProps() throws Exception {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();

        x509HttpClientFactoryBean = new X509HttpClientFactoryBean();
        logger.info("x509HttpClientFactoryBean=" + x509HttpClientFactoryBean);

        try {
            x509HttpClientFactoryBean.afterPropertiesSet();
            Assert.fail("expected exception for missing properties");
        } catch (IllegalStateException ise) {
            logger.info("expected exception", ise);
        }
    }


    /**
     * Test afterPropertiesSet for incomplete {@link ClientKeyStoreParameters}
     */
    @Test
    public void testAfterPropertiesSetDefaultConstructorIncompleteClientKSP() throws Exception {

        ClientKeyStoreParameters clientKeyStoreParameters = mock(ClientKeyStoreParameters.class);
        when(clientKeyStoreParameters.hasValidConfiguration()).thenReturn(false);
        when(clientKeyStoreParameters.toString()).thenReturn("empty " + ClientKeyStoreParameters.class);

        x509HttpClientFactoryBean = new X509HttpClientFactoryBean(clientKeyStoreParameters);
        logger.info("x509HttpClientFactoryBean=" + x509HttpClientFactoryBean);

        try {
            x509HttpClientFactoryBean.afterPropertiesSet();
            Assert.fail("expected exception for missing properties");
        } catch (IllegalStateException ise) {
            logger.info("expected exception", ise);

            verify(clientKeyStoreParameters, times(1)).hasValidConfiguration();
        }
    }


    /**
     * Test all setters and getters
     */
    @Test
    public void testProperties() {
        BeanTester beanTester = new BeanTester();

        final HttpParams httpParams = mock(HttpParams.class);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.overrideFactory("httpParams", new Factory<HttpParams>() {

            @Override public HttpParams create() {
                return httpParams;
            }
        });

        beanTester.testBean(X509HttpClientFactoryBean.class, configurationBuilder.build());
    }


    /**
     * Test object creation with default values, using system properties
     */
    @Test
    public void testGetObjectWithDefaults() throws Exception {

        KeyAndTrustStoreInfo.setupKeyInfoSystemProperties();

        x509HttpClientFactoryBean = new X509HttpClientFactoryBean();
        logger.info("x509HttpClientFactoryBean=" + x509HttpClientFactoryBean);

        x509HttpClientFactoryBean.afterPropertiesSet();

        Assert.assertEquals("getObjectType", HttpClient.class,
                            x509HttpClientFactoryBean.getObjectType());
        Assert.assertEquals("isSingleton", true,
                            x509HttpClientFactoryBean.isSingleton());

        HttpClient httpClient = x509HttpClientFactoryBean.getObject();

        logger.info("httpClient=" + httpClient);
        Assert.assertNotNull(httpClient);

        checkConnection(httpClient);
    }


    /**
     * Test object creation with client0 specific files and passwords directly set
     */
    @Test
    public void testGetObjectWithFilesAndPasswords0() throws Exception {
        x509HttpClientFactoryBean = new X509HttpClientFactoryBean();
        x509HttpClientFactoryBean.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        x509HttpClientFactoryBean
                .setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        x509HttpClientFactoryBean.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ts());
        x509HttpClientFactoryBean
                .setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);
        logger.info("x509HttpClientFactoryBean=" + x509HttpClientFactoryBean);

        x509HttpClientFactoryBean.afterPropertiesSet();

        Assert.assertEquals("getObjectType", HttpClient.class,
                            x509HttpClientFactoryBean.getObjectType());
        Assert.assertEquals("isSingleton", true,
                            x509HttpClientFactoryBean.isSingleton());

        HttpClient httpClient = x509HttpClientFactoryBean.getObject();

        logger.info("httpClient=" + httpClient);
        Assert.assertNotNull(httpClient);

        checkConnection(httpClient);
    }


    /**
     * Test object creation with client1 specific files and passwords set through supplied {@link ClientKeyStoreParameters}
     */
    @Test
    public void testGetObjectWithFilesAndPasswords1() throws Exception {
        ClientKeyStoreParameters clientKeyStoreParameters = mock(ClientKeyStoreParameters.class);
        when(clientKeyStoreParameters.getKeyStoreFilename()).thenReturn(KeyAndTrustStoreInfo.getClient1Ks());
        when(clientKeyStoreParameters.getKeyStorePassword()).thenReturn(KeyAndTrustStoreInfo.CLIENT1_KS_PWD);
        when(clientKeyStoreParameters.getKeyStoreType()).thenReturn("jks");
        when(clientKeyStoreParameters.getTrustStoreFilename()).thenReturn(KeyAndTrustStoreInfo.getClient1Ts());
        when(clientKeyStoreParameters.getTrustStorePassword()).thenReturn(KeyAndTrustStoreInfo.CLIENT1_TS_PWD);
        when(clientKeyStoreParameters.getTrustStoreType()).thenReturn("jks");
        when(clientKeyStoreParameters.hasValidConfiguration()).thenReturn(true);

        x509HttpClientFactoryBean = new X509HttpClientFactoryBean(clientKeyStoreParameters);
        logger.info("x509HttpClientFactoryBean=" + x509HttpClientFactoryBean);
        x509HttpClientFactoryBean.afterPropertiesSet();

        Assert.assertEquals("getObjectType", HttpClient.class,
                            x509HttpClientFactoryBean.getObjectType());
        Assert.assertEquals("isSingleton", true,
                            x509HttpClientFactoryBean.isSingleton());

        HttpClient httpClient = x509HttpClientFactoryBean.getObject();

        logger.info("httpClient=" + httpClient);
        Assert.assertNotNull(httpClient);

        checkConnection(httpClient);
        verify(clientKeyStoreParameters, times(1)).getKeyStoreFilename();
        verify(clientKeyStoreParameters, times(2)).getKeyStorePassword();
        verify(clientKeyStoreParameters, times(1)).getKeyStoreType();
        verify(clientKeyStoreParameters, times(1)).getTrustStoreFilename();
        verify(clientKeyStoreParameters, times(1)).getTrustStorePassword();
        verify(clientKeyStoreParameters, times(1)).getTrustStoreType();
        verify(clientKeyStoreParameters, times(1)).hasValidConfiguration();
    }


    /**
     * Tear down embedded server
     */
    @AfterClass
    public static void tearDownServer() {
        Assert.assertTrue("Server not stopped", server.stopWait());
    }


    /**
     * Common method for testing client works
     */
    private void checkConnection(HttpClient httpClient) throws Exception {
        Assert.assertTrue("Server not started", server.getServer().isStarted());

        HttpPost req = new HttpPost(echoUrl);
        req.setEntity(new StringEntity("test"));
        HttpResponse resp = httpClient.execute(req);

        StatusLine status = resp.getStatusLine();
        logger.debug("status:" + status);
        for (Header header : resp.getAllHeaders()) {
            logger.debug(" [" + header.getName() + "]=" + header.getValue());
        }
        logger.debug("headers:" + resp.getAllHeaders());
        Assert.assertEquals("status should be OK:" + status, HttpStatus.SC_OK,
                            status.getStatusCode());

        HttpEntity entity = resp.getEntity();
        Assert.assertNotNull("response entity", entity);
        logger.debug("entity=" + entity);

        InputStream is = null;
        StringBuffer respbody = new StringBuffer();
        try {

            is = entity.getContent();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String inputLine = null;
            int linei = 0;
            while ((inputLine = reader.readLine()) != null) {
                logger.debug("response line[" + linei + "]:" + inputLine);
                if (respbody.length() > 0) {
                    respbody.append('\n');
                }
                respbody.append(inputLine);
                ++linei;
            }

            Assert.assertEquals("request and response body", "test", respbody.toString());

        } finally {

            // Closing the input stream will trigger connection release
            is.close();
        }

    }
}
