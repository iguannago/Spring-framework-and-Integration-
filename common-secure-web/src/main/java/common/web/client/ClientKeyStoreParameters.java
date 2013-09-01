package common.web.client;

/**
 * Interface for configuring key and trust store parameters where required
 *
 * User: djnorth
 * Date: 14/10/2012
 * Time: 12:46
 */
public interface ClientKeyStoreParameters {


    /**
     * Set keyStore filename (default system property <code>javax.net.ssl.keyStore</code>)
     *
     * @param keyStoreFilename
     */
    void setKeyStoreFilename(String keyStoreFilename);


    /**
     * @return keyStoreFilename
     */
    String getKeyStoreFilename();


    /**
     * Set the keyStore type (default <code>"jks"</code>)
     *
     * @param keyStoreType
     */
    void setKeyStoreType(String keyStoreType);


    /**
     * @return the keyStoreType
     */
    String getKeyStoreType();


    /**
     * Set keyStorePassword.
     * <p/>
     * Default is:
     * <ul><li>>system property
     * <code>javax.net.ssl.keyStorePassword</code> if defined.</li>
     * <li><code>"changeit"</code> if system property not defined</li>
     * </ul>
     * <p/>
     * N.B. This must equal the (private) key password
     *
     * @param keyStorePassword
     */
    void setKeyStorePassword(String keyStorePassword);


    /**
     * @return the keyStorePassword
     */
    String getKeyStorePassword();


    /**
     * Set trustStoreFilename filename (default system property
     * <code>javax.net.ssl.trustStoreFilename</code>)
     *
     * @param trustStoreFilename
     */
    void setTrustStoreFilename(String trustStoreFilename);


    /**
     * @return the trustStoreFilename
     */
    String getTrustStoreFilename();


    /**
     * Set the trustStore type (default <code>"jks"</code>)
     *
     * @param trustStoreType
     */
    void setTrustStoreType(String trustStoreType);


    /**
     * @return the trustStoreType
     */
    String getTrustStoreType();


    /**
     * Set trustStorePassword.
     * <p/>
     * Default is:
     * <ul><li>>system property
     * <code>javax.net.ssl.trustStorePassword</code> if defined</li>
     * <li><code>"changeit"</code> if system property not defined</li>
     * </ul>
     * <p/>
     *
     * @param trustStorePassword
     */
    void setTrustStorePassword(String trustStorePassword);


    /**
     * @return the trustStorePassword
     */
    String getTrustStorePassword();


    /**
     * Check that all information fields correctly provided
     *
     * @return true or false accordingly
     */
    boolean hasValidConfiguration();


    /**
     * Validate configuration, throwing illegalStateException if not complete. This is suitable for use as an
     * <code>init-method</code>, which we support in preference to InitializingBean interface, to give user the choice of when to
     * validate
     */
    void validateConfiguration();
}
