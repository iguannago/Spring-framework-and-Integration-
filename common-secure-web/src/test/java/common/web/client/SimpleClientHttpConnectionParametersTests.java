package common.web.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.meanbean.test.BeanTester;
import org.meanbean.test.EqualsMethodTester;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;

/**
 * Unit tests for {@link SimpleClientHttpConnectionParameters}
 * <p/>
 * User: djnorth
 * Date: 16/10/2012
 * Time: 10:30
 */
public class SimpleClientHttpConnectionParametersTests {

    /**
     * SimpleClientHttpConnectionParameters under test
     */
    private SimpleClientHttpConnectionParameters simpleClientHttpConnectionParameters;


    /**
     * Setup our SimpleClientHttpConnectionParameters for tests
     */
    @Before
    public void setupClientHttpConnectionParameters() {
        simpleClientHttpConnectionParameters = new SimpleClientHttpConnectionParameters();
    }


    /**
     * Test properties
     */
    @Test
    public void testProperties() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(SimpleClientHttpConnectionParameters.class);
    }


    /**
     * Test properties with null serverPort
     */
    @Test
    public void testNullServerPortProperty() {
        configureParameters(simpleClientHttpConnectionParameters, true, "foo.com", 8080, "/context/servlet");
        simpleClientHttpConnectionParameters.setServerPort(null);
        Assert.assertThat("port is null", simpleClientHttpConnectionParameters.getServerPort(), nullValue());
    }


    /**
     * Test equals
     */
    @Test
    public void testEquals() {
        EqualsMethodTester equalsMethodTester = new EqualsMethodTester();
        equalsMethodTester.testEqualsMethod(SimpleClientHttpConnectionParameters.class);
    }


    /**
     * Test equals with both null server port
     */
    @Test
    public void testEqualsEqServerPortBothNull() {
        SimpleClientHttpConnectionParameters otherSimpleClientHttpConnectionParameters =
                new SimpleClientHttpConnectionParameters();
        Assert.assertThat("both null serverPorts eq", simpleClientHttpConnectionParameters,
                          equalTo(otherSimpleClientHttpConnectionParameters));
        Assert.assertThat("both null serverPorts eq inverse", otherSimpleClientHttpConnectionParameters,
                          equalTo(simpleClientHttpConnectionParameters));
    }


    /**
     * Test not equals with one null, one non-null server port
     */
    @Test
    public void testEqualsNeServerPortThisNullOtherNotNull() {
        SimpleClientHttpConnectionParameters otherSimpleClientHttpConnectionParameters =
                new SimpleClientHttpConnectionParameters();
        otherSimpleClientHttpConnectionParameters.setServerPort(8080);
        Assert.assertThat("one null, one non-null serverPorts ne", simpleClientHttpConnectionParameters,
                          not(equalTo(otherSimpleClientHttpConnectionParameters)));
        Assert.assertThat("one null, one non-null serverPorts ne inverse", otherSimpleClientHttpConnectionParameters, not(
                equalTo(simpleClientHttpConnectionParameters)));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#hasValidConfiguration()} with valid config
     * (default)
     */
    @Test
    public void testIsValidConfigDefaultValid() {
        Assert.assertTrue(
                "default config should be valid:simpleClientHttpConnectionParameters=" + simpleClientHttpConnectionParameters,
                simpleClientHttpConnectionParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#validateConfiguration()} with valid config
     * (default)
     */
    @Test
    public void testValidateConfigDefaultValid() {
        simpleClientHttpConnectionParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#hasValidConfiguration()} with valid config
     * (set up)
     */
    @Test
    public void testIsValidConfigNonDefaultValid() {
        configureParameters(simpleClientHttpConnectionParameters, true, "foo.com", 8080, "/app/servlet");
        Assert.assertTrue("correct non-default config should be valid:simpleClientHttpConnectionParameters=" +
                          simpleClientHttpConnectionParameters, simpleClientHttpConnectionParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#validateConfiguration()} with valid config
     * (set up)
     */
    @Test
    public void testValidateConfigNonDefaultValid() {
        configureParameters(simpleClientHttpConnectionParameters, true, "foo.com", 8080, "/app/servlet");
        simpleClientHttpConnectionParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#hasValidConfiguration()} with invalid config
     * (set up, non-null, bad servletPort)
     */
    @Test
    public void testIsValidConfigNonDefaultInvalidServletPort() {
        configureParameters(simpleClientHttpConnectionParameters, true, "foo.com", -1, "/app/servlet");
        Assert.assertFalse("incorrect non-default config should not be valid:simpleClientHttpConnectionParameters=" +
                           simpleClientHttpConnectionParameters, simpleClientHttpConnectionParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#validateConfiguration()} with invalid config
     * (set up, non-null, bad servletPort)
     */
    @Test(expected = IllegalStateException.class)
    public void testValidateConfigNonDefaultInvalidServletPort() {
        configureParameters(simpleClientHttpConnectionParameters, true, "foo.com", -1, "/app/servlet");
        simpleClientHttpConnectionParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#hasValidConfiguration()} with valid config
     * (set up, null serverHostname)
     */
    @Test
    public void testIsValidConfigNonDefaultNullServerName() {
        configureParameters(simpleClientHttpConnectionParameters, true, null, 8080, "/app/servlet");
        Assert.assertTrue("correct non-default config should be valid:simpleClientHttpConnectionParameters=" +
                          simpleClientHttpConnectionParameters, simpleClientHttpConnectionParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#validateConfiguration()} with valid config
     * (set up, null serverHostname)
     */
    @Test
    public void testValidateConfigNonDefaultNullServerName() {
        configureParameters(simpleClientHttpConnectionParameters, true, null, 8080, "/app/servlet");
        simpleClientHttpConnectionParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#hasValidConfiguration()} with valid config
     * (set up, non-null serverHostname, uriTemplate in baseUrlContextPath)
     */
    @Test
    public void testIsValidConfigNonDefaultUriTemplateContextPath() {
        configureParameters(simpleClientHttpConnectionParameters, true, null, 8080, "/app/servlet/accounts/{accountId}");
        Assert.assertFalse("incorrect non-default config should be invalid:simpleClientHttpConnectionParameters=" +
                           simpleClientHttpConnectionParameters + ":uri=" + simpleClientHttpConnectionParameters.getUri() +
                          ":uriString=" + simpleClientHttpConnectionParameters.getUriString(),
                          simpleClientHttpConnectionParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#hasValidConfiguration()} with valid config
     * (set up, non-null serverHostname, uriTemplate in baseUrlContextPath)
     */
    @Test(expected = IllegalStateException.class)
    public void testValidateConfigNonDefaultUriTemplateContextPath() {
        configureParameters(simpleClientHttpConnectionParameters, true, null, 8080, "/app/servlet/accounts/{accountId}");
        simpleClientHttpConnectionParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getEffectiveServerPort()} default for
     * unsecured connection config
     */
    @Test
    public void testGetEffectiveServerPortUnsecuredDefault() {
        Assert.assertThat("effective default for unsecured", simpleClientHttpConnectionParameters.getEffectiveServerPort(),
                          equalTo(80));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getEffectiveServerPort()} default for secured
     * connection config
     */
    @Test
    public void testGetEffectiveServerPortSecuredDefault() {
        simpleClientHttpConnectionParameters.setSecureConnection(true);
        Assert.assertThat("effective default for secured", simpleClientHttpConnectionParameters.getEffectiveServerPort(),
                          equalTo(443));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getEffectiveServerPort()} configured for
     * unsecured connection config
     */
    @Test
    public void testGetEffectiveServerPortUnsecuredConfigured() {
        final int serverPort = 8080;
        simpleClientHttpConnectionParameters.setServerPort(serverPort);
        Assert.assertThat("effective default for unsecured", simpleClientHttpConnectionParameters.getEffectiveServerPort(),
                          equalTo(serverPort));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getEffectiveServerPort()} configured for
     * secured connection config
     */
    @Test
    public void testGetEffectiveServerPortSecuredConfigured() {
        final int serverPort = 8443;
        simpleClientHttpConnectionParameters.setSecureConnection(true);
        simpleClientHttpConnectionParameters.setServerPort(serverPort);
        Assert.assertThat("effective default for unsecured", simpleClientHttpConnectionParameters.getEffectiveServerPort(),
                          equalTo(serverPort));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUri()} for default
     * (unsecured, default serverHostname & serverPort, no baseUriContextPath)
     */
    @Test
    public void testGetBaseUriUnsecuredDefault() {
        URI expectedUri = createURI("http://localhost");
        Assert.assertThat(expectedUri, equalTo(simpleClientHttpConnectionParameters.getUri()));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUriString()} for default
     * (unsecured, default serverHostname & serverPort, no baseUriContextPath)
     */
    @Test
    public void testGetBaseUriStringUnsecuredDefault() {
        String expectedUriString = "http://localhost";
        Assert.assertThat(expectedUriString, equalTo(simpleClientHttpConnectionParameters.getUriString()));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUri()}  for default + secured
     * (default serverHostname & serverPort, no baseUriContextPath)
     */
    @Test
    public void testGetBaseUriSecuredDefault() {
        URI expectedUri = createURI("https://localhost");
        simpleClientHttpConnectionParameters.setSecureConnection(true);
        Assert.assertThat(expectedUri, equalTo(simpleClientHttpConnectionParameters.getUri()));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUriString()} for default + secured
     * (default serverHostname & serverPort, no baseUriContextPath)
     */
    @Test
    public void testGetBaseUriStringSecuredDefault() {
        String expectedUriString = "https://localhost";
        simpleClientHttpConnectionParameters.setSecureConnection(true);
        Assert.assertThat(expectedUriString, equalTo(simpleClientHttpConnectionParameters.getUriString()));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUri()} for
     * unsecured, configured serverHostname, default serverPort, configured baseUriContextPath)
     */
    @Test
    public void testGetBaseUriUnsecuredConfiguredServerHostBaseUrlContextPath() {
        final String serverHostname = "foo.com";
        final String baseUriContextPath = "application";
        URI expectedUri = createURI("http://" + serverHostname + '/' + baseUriContextPath);

        configureParameters(simpleClientHttpConnectionParameters, false, serverHostname, null, baseUriContextPath);
        Assert.assertThat(expectedUri, equalTo(simpleClientHttpConnectionParameters.getUri()));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUriString()} for
     * secured, configured serverHostname, default serverPort, configured baseUriContextPath)
     */
    @Test
    public void testGetBaseUriStringSecuredConfiguredServerHostBaseUrlContextPath() {
        final String serverHostname = "foo.com";
        final String baseUriContextPath = "app/servlet/blahblah";
        String expectedUriString = "https://" + serverHostname + '/' + baseUriContextPath;

        configureParameters(simpleClientHttpConnectionParameters, true, serverHostname, null, baseUriContextPath);
        Assert.assertThat(expectedUriString, equalTo(simpleClientHttpConnectionParameters.getUriString()));
    }


    /**
     * Test {@link SimpleClientHttpConnectionParameters#getUri()} for
     * secured, default serverHostname, configured serverPort, null baseUriContextPath)
     */
    @Test
    public void testGetBaseUriSecuredConfiguredServerHostBaseUrlContextPath() {
        final int serverPort = 8443;
        URI expectedUri = createURI("https://localhost:" + serverPort);

        configureParameters(simpleClientHttpConnectionParameters, true, null, serverPort, null);
        Assert.assertThat(expectedUri, equalTo(simpleClientHttpConnectionParameters.getUri()));
    }


    /**
     * Helper to configure parameters object
     *
     * @param simpleClientHttpConnectionParameters
     * @param secureConnection
     * @param serverHostname
     * @param serverPort
     * @param baseUriContextPath
     */
    private void configureParameters(SimpleClientHttpConnectionParameters simpleClientHttpConnectionParameters,
                                     boolean secureConnection, String serverHostname, Integer serverPort,
                                     String baseUriContextPath) {
        simpleClientHttpConnectionParameters.setSecureConnection(secureConnection);
        simpleClientHttpConnectionParameters.setServerHostname(serverHostname);
        simpleClientHttpConnectionParameters.setServerPort(serverPort);
        simpleClientHttpConnectionParameters.setBaseUriContextPath(baseUriContextPath);
    }


    /**
     * Helper to create URI from string
     */
    private URI createURI(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
