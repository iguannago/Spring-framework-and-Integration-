package common.web.client;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Interface representing required properties required to configure a web client for communication with a web server.
 * <p/>
 * The URL properties are factored into appropriate components to facilitate modular configuration e.g. Spring bean
 * inheritance.
 * <p/>
 * User: djnorth
 * Date: 14/10/2012
 * Time: 11:53
 */
public interface ClientHttpConnectionParameters {

    /**
     * Set secureConnection, which determines controls scheme and default port values
     * <ul>
     *     <li><code>true</code> for https</li>
     *     <li><code>false</code> for http (default)</li>
     * </ul>
     *
     * @param secureConnection
     */
    void setSecureConnection(boolean secureConnection);


    /**
     * @return secureConnection
     */
    boolean isSecureConnection();


    /**
     * Set server hostname.
     * <p/>
     * The default is <code>localhost</code>, but, even for testing, <code>localhost</code> may not suffice if https
     * is used,
     * as the hostname must then correspond to that used on the host certificate.
     *
     * @param hostname
     */
    void setServerHostname(String hostname);


    /**
     * @return hostname
     */
    String getServerHostname();


    /**
     * Set server port.
     * <p/>
     * The default will be determined by the value of <code>securedConnection</code>:
     * <ul>
     * <li><code>443</code> for secured (https) connection</li>
     * <li><code>80</code> for normal http connection</li>
     * </ul>
     *
     * @param port
     */
    void setServerPort(Integer port);


    /**
     * Set base context path(s) (application and maybe servlet) required to build URI. Exactly how much of the path should be set
     * here depends on the variations the client may require. An empty/null path is also allowed.
     * <p/>
     * N.B. This will replace any existing path.
     *
     * @param baseUriContextPath
     */
    void setBaseUriContextPath(String baseUriContextPath);


    /**
     * Create UriComponentBuilder for base URI, set up with supplied components i.e.
     * <ul>
     * <li>scheme for <code>securedConnection</code></li>
     * <li>serverHostname for <code>serverHostname</code></li>
     * <li>serverPort for <code>serverPort</code> (if supplied)</li>
     * <li>context paths for <code>baseUriContextPath</code></li>
     * </ul>
     * <p/>
     * N.B.No expansion of URI templates is done with this method
     *
     * @return UriComponentBuilder
     */
    String getUriString();


    /**
     * Create URI for baseUri using component parts as supplied (@see
     * SimpleClientHttpConnectionParameters#getUriString).
     * <p/>
     * N.B.No expansion of URI templates is done with this method.
     *
     * @return baseUri
     */
    URI getUri();


    /**
     * @return port
     */
    Integer getServerPort();


    /**
     * Return port as int, using appropriate default value if it is null
     *
     * @return effective port value
     */
    int getEffectiveServerPort();


    /**
     * @return baseUriContextPath
     */
    String getBaseUriContextPath();

    /**
     * Ensure valid configuration i.e.
     * <ul>
     *     <li>serverPort is null or positive</li>
     *     <li>URI(getUriString()) is valid (so that UriTemplates will not be allowed)</li>
     * </ul>
     *
     * @return true or false as appropriate
     */
    boolean hasValidConfiguration();

    /**
     * Validate configuration, throwing illegalStateException if not complete. This is suitable for use as an
     * <code>init-method</code>, which we support in preference to InitializingBean interface,
     * to give user the choice of if or when to
     * validate
     */
    void validateConfiguration();
}
