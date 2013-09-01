package common.web.client;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Base class for http and https implementations of ClientHttpConnectionParameters
 * <p/>
 * User: djnorth
 * Date: 15/10/2012
 * Time: 16:59
 */
public class SimpleClientHttpConnectionParameters implements ClientHttpConnectionParameters {

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger("common.web");

    /**
     * System default for unsecured port
     */
    public static final int DEFAULT_HTTP_PORT = 80;

    /**
     * System default for secure port
     */
    public static final int DEFAULT_HTTPS_PORT = 443;

    /**
     * Secure connection indication
     */
    private boolean secureConnection = false;

    /**
     * Hostname (default will be localhost)
     */
    private String serverHostname = null;

    /**
     * Port
     */
    private Integer serverPort = null;

    /**
     * Base context path
     */
    private String baseUriContextPath = null;

    /**
     * Ensure valid configuration i.e.
     * <ul>
     * <li>serverPort is null or positive</li>
     * <li>URI(getUriString()) is valid (so that UriTemplates will not be allowed)</li>
     * </ul>
     *
     * @return true or false as appropriate
     */
    @Override
    public boolean hasValidConfiguration() {
        if (serverPort != null && serverPort <= 0) {
            return false;
        } else {
            try {
                new URI(getUriString());
                return true;
            } catch (URISyntaxException e) {
                return false;
            }
        }
    }


    /**
     * Validate configuration, throwing illegalStateException if not complete. This is suitable for use as an
     * <code>init-method</code>, which we support in preference to InitializingBean interface,
     * to give user the choice of when to validate
     * <p/>
     *
     * @see SimpleClientHttpConnectionParameters#hasValidConfiguration()
     */
    @Override
    public void validateConfiguration() {
        Assert.state(hasValidConfiguration(), "invalid configuration:this=" + this);
    }


    /**
     * Set secureConnection, which determines controls scheme and default port values
     * <ul>
     * <li><code>true</code> for https</li>
     * <li><code>false</code> for http (default)</li>
     * </ul>
     *
     * @param secureConnection
     */
    @Override
    public void setSecureConnection(boolean secureConnection) {
        this.secureConnection = secureConnection;
    }


    /**
     * Defines whether connection is secure:
     * <ul>
     * <li><code>true</code> for https</li>
     * <li><code>false</code> (default) for http</li>
     * </ul>
     *
     * @return true or false
     */
    @Override
    public boolean isSecureConnection() {
        return secureConnection;
    }


    /**
     * Set server serverHostname.
     * <p/>
     * The default/result of null is <code>localhost</code>, but, even for testing,
     * <code>localhost</code> may not suffice if https
     * is used,
     * as the serverHostname must then correspond to that used on the host certificate.
     *
     * @param serverHostname
     */
    @Override
    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }


    /**
     * @return serverHostname
     */
    @Override
    public String getServerHostname() {
        return serverHostname;
    }


    /**
     * Set server serverPort.
     * <p/>
     * The default will be determined by the value of <code>securedConnection</code>:
     * <ul>
     * <li><code>443</code> for secured (https) connection</li>
     * <li><code>80</code> for normal http connection</li>
     * </ul>
     *
     * @param serverPort
     */
    @Override
    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }


    /**
     * @return port
     */
    @Override
    public Integer getServerPort() {
        return serverPort;
    }


    /**
     * Return port as int, using default values if it is null
     *
     * @return effective port value
     */
    @Override
    public int getEffectiveServerPort() {
        if (serverPort != null) {
            return serverPort;
        } else if (isSecureConnection()) {
            return DEFAULT_HTTPS_PORT;
        } else {
            return DEFAULT_HTTP_PORT;
        }
    }


    /**
     * Set base context path(s) (application and maybe servlet) required to build URI. Exactly how much of the path
     * should be set here depends on the variations the client may require. An empty/null path is also allowed.
     * <p/>
     * N.B. This will replace any existing path.
     *
     * @param baseUriContextPath
     */
    @Override
    public void setBaseUriContextPath(String baseUriContextPath) {
        this.baseUriContextPath = baseUriContextPath;
    }


    /**
     * @return baseUriContextPath
     */
    @Override
    public String getBaseUriContextPath() {
        return baseUriContextPath;
    }


    /**
     * Create UriComponentBuilder for base URI, set up with supplied components i.e.
     * <ul>
     * <li>scheme for <code>securedConnection</code></li>
     * <li>serverHostname for <code>serverHostname</code></li>
     * <li>serverPort for <code>serverPort</code> (if supplied)</li>
     * <li>context paths for <code>baseUriContextPath</code></li>
     * </ul>
     * <p/>
     *
     * @return UriComponentBuilder
     */
    @Override
    public String getUriString() {
        return getUriComponents().toUriString();
    }


    /**
     * Create URI for baseUri using component parts as supplied (@see
     * SimpleClientHttpConnectionParameters#getUriString).
     *
     * @return baseUri
     */
    @Override
    public URI getUri() {
        return getUriComponents().toUri();
    }


    /**
     * Equals based on fields only i.e. not treating explicit default port values as being equivalent to null values
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

        SimpleClientHttpConnectionParameters other = (SimpleClientHttpConnectionParameters) otherObj;

        if (secureConnection != other.secureConnection) {
            return false;
        }

        if (baseUriContextPath != null ? !baseUriContextPath.equals(other.baseUriContextPath) :
            other.baseUriContextPath != null) {
            return false;
        }

        if (serverHostname != null ? !serverHostname.equals(other.serverHostname) : other.serverHostname != null) {
            return false;
        }

        if (serverPort != null ? !serverPort.equals(other.serverPort) : other.serverPort != null) {
            return false;
        }

        return true;
    }


    /**
     * hashcode calculated on all fields, so it may change (so be careful with HashSet)!
     *
     * @return calculated hashCode
     */
    @Override
    public int hashCode() {
        int result = (secureConnection ? 1 : 0);
        result = 31 * result + (serverHostname != null ? serverHostname.hashCode() : 0);
        result = 31 * result + (serverPort != null ? serverPort.hashCode() : 0);
        result = 31 * result + (baseUriContextPath != null ? baseUriContextPath.hashCode() : 0);
        return result;
    }


    /**
     * Create readable representation of configuration
     *
     * @return state as string
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("{secureConnection=").append(secureConnection);
        sb.append(", serverHostname='").append(serverHostname).append('\'').append(" (effective value='")
          .append(getEffectiveServerHostname())
          .append("')");
        sb.append(", serverPort=").append(serverPort).append(" (effective value=").append(getEffectiveServerPort())
          .append(')');
        sb.append(", baseUriContextPath='").append(baseUriContextPath).append('\'');
        sb.append('}');
        return sb.toString();
    }


    /**
     * Help[er to return hostname, using default value if it is null. We don't make this public,
     * a s the hostname is visible in the resulting URI
     *
     * @return effective hostname
     */
    private String getEffectiveServerHostname() {
        return (serverHostname == null ? "localhost" : serverHostname);
    }


    /**
     * Helper creating UriComponents from the configuration:
     * <ul>
     * <li>scheme for <code>securedConnection</code></li>
     * <li>serverHostname for <code>serverHostname</code></li>
     * <li>serverPort for <code>serverPort</code> (if supplied)</li>
     * <li>context paths for <code>baseUriContextPath</code></li>
     * </ul>
     *
     * @return uriComponents
     */
    private UriComponents getUriComponents() {
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.scheme(isSecureConnection() ? "https" : "http");
        uriComponentsBuilder.host(getEffectiveServerHostname()).port(serverPort == null ? -1 : serverPort);
        uriComponentsBuilder.replacePath(baseUriContextPath);
        return uriComponentsBuilder.build();
    }
}
