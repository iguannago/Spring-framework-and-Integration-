package common.web.client;

import common.web.server.EmbeddedJettyServer;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Integration tests for use of {@link ClientHttpConnectionParameters} in a secured configuration,
 * with multiple {@link ClientKeyStoreParameters} configurations, using {@link X509HttpClientFactoryBean} to create
 * HttpClient instances by configuration
 * <p/>
 * To keep test deployment simple, we will copnfigure a {@link common.web.server.DefaultEmbeddedJettyServer} with the
 * <code>jetty</code> profile, rather than deploying to the Jetty plug-in
 * <p/>
 * User: djnorth
 * Date: 18/10/2012
 * Time: 08:38
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("jetty")
@ContextConfiguration
public class SecuredHttpsMultiClientTests {

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger("common.web");

    /**
     * Embedded server
     */
    @Autowired
    private EmbeddedJettyServer server;

    /**
     * ClientHttpConnectionParameters
     */
    @Autowired
    private ClientHttpConnectionParameters clientHttpConnectionParameters;

    /**
     * HttpClient for first client
     */
    @Autowired
    private HttpClient httpClient;

    /**
     * HttpClient for second client
     */
    @Autowired
    private HttpClient httpClient2;

    /**
     * HttpClient for third (untrusted) client
     */
    @Autowired
    private HttpClient httpUntrustedClient;

    /**
     * Servlet paths to test
     */
    @Value("#{servletMap.keySet()}")
    private Set<String> servletPaths;

    /**
     * Static initialization of system properties
     */
    @BeforeClass
    public static void setSysProps() {
        if (logger.isTraceEnabled()) {
            System.setProperty("javax.net.debug", "ssl");
        }
    }


    /**
     * Test sending for 1st httpClient
     */
    @Test
    public void testSecureConnectionClient() throws Exception {
        verifyRequestResponse(httpClient, HttpStatus.SC_OK, "test");
    }


    /**
     * Test sending for 2nd httpClient
     */
    @Test
    public void testSecureConnectionClient2() throws Exception {
        verifyRequestResponse(httpClient2, HttpStatus.SC_OK, "test");
    }


    /**
     * Test sending for 3rd httpClient
     */
    @Test
    public void testSecureConnectionUntrustedClient() throws Exception {
        verifyRequestResponse(httpUntrustedClient, HttpStatus.SC_OK, "test");
    }


    /**
     * Helper testing send request/receive response
     *
     * @param httpClientUnderTest
     * @param expectedStatus
     * @param textToSend
     * @throws IOException
     */
    private void verifyRequestResponse(HttpClient httpClientUnderTest, int expectedStatus, String textToSend) throws IOException {
        for (String servletPath : servletPaths) {
            UriComponentsBuilder uriComponentsBuilder =
                    UriComponentsBuilder.fromUriString(clientHttpConnectionParameters.getUriString());
            final String requestUrl = uriComponentsBuilder.path(servletPath).build().toUriString();
            logger.debug("requestUrl:" + requestUrl);
            HttpPost req = new HttpPost(requestUrl);
            req.setEntity(new StringEntity(textToSend));
            HttpResponse resp = httpClientUnderTest.execute(req);
            StatusLine status = resp.getStatusLine();
            logger.debug("status:" + status);
            for (Header header : resp.getAllHeaders()) {
                logger.debug(" [" + header.getName() + "]=" + header.getValue());
            }
            logger.debug("headers:" + resp.getAllHeaders());
            Assert.assertEquals("status should be OK:" + status + ":requestUrl=" + requestUrl, expectedStatus,
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

                Assert.assertEquals("request and response body", textToSend, respbody.toString());

            } finally {

                // Closing the input stream will trigger connection release
                is.close();
            }
        }
    }

}
