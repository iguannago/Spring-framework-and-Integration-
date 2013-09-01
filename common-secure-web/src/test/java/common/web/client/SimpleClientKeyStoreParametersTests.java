package common.web.client;

import common.web.KeyAndTrustStoreInfo;
import junit.framework.Assert;
import org.junit.Test;
import org.meanbean.test.BeanTester;
import org.meanbean.test.EqualsMethodTester;

/**
 * Unit tests for {@link SimpleClientKeyStoreParameters}
 * <p/>
 * User: djnorth
 * Date: 14/10/2012
 * Time: 22:36
 */
public class SimpleClientKeyStoreParametersTests {

    /**
     * SimpleClientKeyStoreParameters under test
     */
    private SimpleClientKeyStoreParameters simpleClientKeyStoreParameters;


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with no key info configured,
     * no default info sys props present
     */
    @Test
    public void testHasValidConfigurationNoInfo() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        Assert.assertFalse(
                "should be false with no info configured, no default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters, simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with no key info configured,
     * all default info sys props present
     */
    @Test
    public void testHasValidConfigurationDefaultInfoAllDefined() {

        KeyAndTrustStoreInfo.setupKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        Assert.assertTrue(
                "should be true with no info configured, all default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters,
                simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with no key info configured,
     * no default info sys props present
     */
    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationNoInfo() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with no key info configured,
     * all default info sys props present
     */
    @Test
    public void testValidateConfigurationDefaultInfoAllDefined() {

        KeyAndTrustStoreInfo.setupKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.validateConfiguration();
    }


    /**
     * Test all setters and getters
     */
    @Test
    public void testProperties() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(SimpleClientKeyStoreParameters.class);
    }


    /**
     * Test equals and hashCode
     */
    @Test
    public void testEquals() {
        EqualsMethodTester equalsMethodTester = new EqualsMethodTester();
        equalsMethodTester.testEqualsMethod(SimpleClientKeyStoreParameters.class);
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all key info configured
     * except keyStorePassword, no default info sys props present
     */
    @Test
    public void testHasValidConfigurationAllInfoDefined() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ts());
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);

        Assert.assertTrue(
                "should be true with all info configured, no default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters, simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except keyStorePassword, no default info sys props present
     */
    @Test
    public void testHasValidConfigurationInfoDefinedKsPwdMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient1Ks());
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient1Ts());
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT1_TS_PWD);

        Assert.assertTrue(
                "should be true with all info configured except keyStorePassword, " +
                "no default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters, simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except trustStorePassword, no default info sys props present
     */
    @Test
    public void testHasValidConfigurationInfoDefinedTsPwdMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ts());

        Assert.assertTrue(
                "should be true with all info configured except trustStorePassword, " +
                "no default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters, simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except keyStoreFilename, no default info sys props present
     * except for keyStore filename
     */
    @Test
    public void testHasValidConfigurationInfoDefinedKsFilenameMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT1_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient1Ts());
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT1_TS_PWD);

        Assert.assertFalse(
                "should be false with all info configured except keyStoreFilename, " +
                "no default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters, simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except trustStoreFilename, no default info sys props present
     * except for keyStore filename
     */
    @Test
    public void testHasValidConfigurationInfoDefinedTsFilenameMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);

        Assert.assertFalse(
                "should be false with all info configured except trustStoreFilename, " +
                "no default info sys props present:simpleClientKeyStoreParameters=" +
                simpleClientKeyStoreParameters, simpleClientKeyStoreParameters.hasValidConfiguration());
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all key info configured
     * except keyStorePassword, no default info sys props present
     */
    @Test
    public void testValidateConfigurationAllInfoDefined() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ts());
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);

        simpleClientKeyStoreParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except keyStorePassword, no default info sys props present
     */
    @Test
    public void testValidateConfigurationInfoDefinedKsPwdMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient1Ks());
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient1Ts());
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT1_TS_PWD);

        simpleClientKeyStoreParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except trustStorePassword, no default info sys props present
     */
    @Test
    public void testValidateConfigurationInfoDefinedTsPwdMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ts());

        simpleClientKeyStoreParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except keyStoreFilename, no default info sys props present
     * except for keyStore filename
     */
    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationInfoDefinedKsFilenameMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT1_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient1Ts());
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT1_TS_PWD);

        simpleClientKeyStoreParameters.validateConfiguration();
    }


    /**
     * Test {@link SimpleClientKeyStoreParameters#hasValidConfiguration()} with all info configured
     * except trustStoreFilename, no default info sys props present
     * except for keyStore filename
     */
    @Test(expected = IllegalStateException.class)
    public void testValidateConfigurationInfoDefinedTsFilenameMissing() {

        KeyAndTrustStoreInfo.clearKeyInfoSystemProperties();
        simpleClientKeyStoreParameters = new SimpleClientKeyStoreParameters();

        simpleClientKeyStoreParameters.setTrustStoreFilename(KeyAndTrustStoreInfo.getClient0Ks());
        simpleClientKeyStoreParameters.setKeyStorePassword(KeyAndTrustStoreInfo.CLIENT0_KS_PWD);
        simpleClientKeyStoreParameters.setTrustStorePassword(KeyAndTrustStoreInfo.CLIENT0_TS_PWD);

        simpleClientKeyStoreParameters.validateConfiguration();
    }
}
