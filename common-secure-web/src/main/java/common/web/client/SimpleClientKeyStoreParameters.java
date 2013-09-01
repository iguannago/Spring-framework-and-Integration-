package common.web.client;

import org.springframework.util.Assert;

/**
 * Class implementing key store parameters interface, for secure web client configuration
 * <p/>
 * User: djnorth
 * Date: 14/10/2012
 * Time: 21:40
 */
public class SimpleClientKeyStoreParameters implements ClientKeyStoreParameters {

    /**
     * KeyStore filename
     */
    private String keyStoreFilename = System.getProperty("javax.net.ssl.keyStore");

    /**
     * KeyStore type
     */
    private String keyStoreType = "jks";

    /**
     * KeyStore password
     */
    private String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword", "changeit");

    /**
     * TrustStore for client
     */
    private String trustStoreFilename = System.getProperty("javax.net.ssl.trustStore");

    /**
     * TrustStore type
     */
    private String trustStoreType = "jks";

    /**
     * TrustStore password
     */
    private String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword", "changeit");


    /**
     * Set keyStoreFilename filename (default system property javax.net.ssl.keyStoreFilename)
     *
     * @param keyStoreFilename the keyStoreFilename to set
     */
    @Override
    public void setKeyStoreFilename(String keyStoreFilename) {
        this.keyStoreFilename = keyStoreFilename;
    }


    /**
     * @return the keyStoreFilename
     */
    @Override
    public String getKeyStoreFilename() {
        return keyStoreFilename;
    }


    /**
     * Set the keyStoreFilename type (default "jks")
     *
     * @param keyStoreType the keyStoreType to set
     */
    @Override
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }


    /**
     * @return the keyStoreType
     */
    @Override
    public String getKeyStoreType() {
        return keyStoreType;
    }


    /**
     * Set keyStorePassword (default system property
     * javax.net.ssl.keyStorePassword, or changeit) N.B. This must equal the
     * (private) key password
     *
     * @param keyStorePassword the keyStorePassword to set
     */
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }


    /**
     * @return the keyStorePassword
     */
    @Override
    public String getKeyStorePassword() {
        return keyStorePassword;
    }


    /**
     * Set trustStoreFilename filename (default system property
     * javax.net.ssl.trustStoreFilename)
     *
     * @param trustStoreFilename the trustStoreFilename to set
     */
    @Override
    public void setTrustStoreFilename(String trustStoreFilename) {
        this.trustStoreFilename = trustStoreFilename;
    }


    /**
     * @return the trustStoreFilename
     */
    @Override
    public String getTrustStoreFilename() {
        return trustStoreFilename;
    }


    /**
     * Set the trustStoreFilename type (default "jks")
     *
     * @param trustStoreType the trustStoreType to set
     */
    @Override
    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }


    /**
     * @return the trustStoreType
     */
    @Override
    public String getTrustStoreType() {
        return trustStoreType;
    }


    /**
     * Set trustStorePassword (default system property
     * javax.net.ssl.trustStorePassword, or changeit)
     *
     * @param trustStorePassword the trustStorePassword to set
     */
    @Override
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }


    /**
     * @return the trustStorePassword
     */
    @Override
    public String getTrustStorePassword() {
        return trustStorePassword;
    }


    /**
     * Check that all information fields correctly provided
     *
     * @return true or false accordingly
     */
    @Override
    public boolean hasValidConfiguration() {
        return (getKeyStoreFilename() != null && getKeyStoreFilename().length() != 0
                && getKeyStoreType() != null && getKeyStoreType().length() != 0
                && getKeyStorePassword() != null
                && getKeyStorePassword().length() != 0
                && getTrustStoreFilename() != null && getTrustStoreFilename().length() != 0
                && getTrustStoreType() != null
                && getTrustStoreType().length() != 0
                && getTrustStorePassword() != null
                && getTrustStorePassword().length() != 0);
    }


    /**
     * Validate configuration, throwing illegalStateException if not complete. This is suitable for use as an
     * <code>init-method</code>, which we support in preference to InitializingBean interface,
     * to give user the choice of if or when to
     * validate
     */
    @Override
    public void validateConfiguration() {
        Assert.state(hasValidConfiguration(), "invalid key info configuration:this=" + this);
    }


    /**
     * Equals based on fields
     *
     * @param otherObj
     * @return
     */
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (otherObj == null || getClass() != otherObj.getClass()) {
            return false;
        }

        SimpleClientKeyStoreParameters other = (SimpleClientKeyStoreParameters) otherObj;

        if (keyStoreFilename != null ? !keyStoreFilename.equals(other.keyStoreFilename) :
            other.keyStoreFilename != null) {
            return false;
        }
        if (keyStorePassword != null ? !keyStorePassword.equals(other.keyStorePassword) :
            other.keyStorePassword != null) {
            return false;
        }
        if (keyStoreType != null ? !keyStoreType.equals(other.keyStoreType) : other.keyStoreType != null) {
            return false;
        }
        if (trustStoreFilename != null ? !trustStoreFilename.equals(other.trustStoreFilename) :
            other.trustStoreFilename != null) {
            return false;
        }
        if (trustStorePassword != null ? !trustStorePassword.equals(other.trustStorePassword) :
            other.trustStorePassword != null) {
            return false;
        }
        if (trustStoreType != null ? !trustStoreType.equals(other.trustStoreType) : other.trustStoreType != null) {
            return false;
        }

        return true;
    }


    /**
     * HashCode will change if properties are changed, but this will normally only be done once,
     * and it is less likely that this class will be used to populate a HashSet, so this should not be an issue.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        int result = keyStoreFilename != null ? keyStoreFilename.hashCode() : 0;
        result = 31 * result + (keyStoreType != null ? keyStoreType.hashCode() : 0);
        result = 31 * result + (keyStorePassword != null ? keyStorePassword.hashCode() : 0);
        result = 31 * result + (trustStoreFilename != null ? trustStoreFilename.hashCode() : 0);
        result = 31 * result + (trustStoreType != null ? trustStoreType.hashCode() : 0);
        result = 31 * result + (trustStorePassword != null ? trustStorePassword.hashCode() : 0);
        return result;
    }


    /**
     * @return readable representation of state
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("{keyStoreFilename='").append(keyStoreFilename).append('\'');
        sb.append(", keyStoreType='").append(keyStoreType).append('\'');
        sb.append(", keyStorePassword='").append(keyStorePassword).append('\'');
        sb.append(", trustStoreFilename='").append(trustStoreFilename).append('\'');
        sb.append(", trustStoreType='").append(trustStoreType).append('\'');
        sb.append(", trustStorePassword='").append(trustStorePassword).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
