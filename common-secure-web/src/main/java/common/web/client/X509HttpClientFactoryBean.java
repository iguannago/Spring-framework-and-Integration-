/*
 * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
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

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Factory creating an HttpClient for use with x.509, using configured key and
 * trust store information.
 *
 * @author djnorth
 */
public class X509HttpClientFactoryBean implements FactoryBean<HttpClient>, InitializingBean {

    /**
     * Default read timeout, used if no HttpParams are provided.
     */
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger("web.client");

    /**
     * https port
     */
    private int httpsPort = 8443;

    /**
     * Optional HttpParams
     */
    private HttpParams httpParams = null;

    /**
     * HttpClient object
     */
    private HttpClient httpClient = null;


    /**
     * Key store parameters object
     */
    private final ClientKeyStoreParameters clientKeyStoreParameters;


    /**
     * Default constructor using default SimpleClientKeyStoreParameters
     */
    public X509HttpClientFactoryBean() {
        this(new SimpleClientKeyStoreParameters());
    }


    /**
     * Constructor taking keyStoreParameters
     *
     * @param clientKeyStoreParameters
     */
    public X509HttpClientFactoryBean(ClientKeyStoreParameters clientKeyStoreParameters) {
        Assert.notNull(clientKeyStoreParameters, "clientKeyStoreParameters may not be null");
        this.clientKeyStoreParameters = clientKeyStoreParameters;
    }


    /**
     * Set the https port (default 8443)
     *
     * @param httpsPort the httpsPort to set
     */
    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }


    /**
     * @return the hppsPort
     */
    public int getHttpsPort() {
        return httpsPort;
    }


    /**
     * Set HttpParams to use. Default is a BasicHttpParams instance with a read
     * timeout value set to DEFAULT_READ_TIMEOUT_MILLISECONDS.
     *
     * @param httpParams the httpParams to set
     */
    public void setHttpParams(HttpParams httpParams) {
        this.httpParams = httpParams;
    }


    /**
     * @return the httpParams
     */
    public HttpParams getHttpParams() {
        return httpParams;
    }

    /**
     * Ensure we have keystores and passwords defined (delagate to clientKeyStoreParameters,
     * if it has not already complained!)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        if (!clientKeyStoreParameters.hasValidConfiguration()) {
            throw new IllegalStateException("key/trust store configuration incomplete:this=" + this);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("afterPropertiesSet:E:this=" + this);
        }

        try {

            final KeyStore keystore = KeyStore.getInstance(getKeyStoreType());
            InputStream keystoreInput = new FileInputStream(ResourceUtils.getFile(getKeyStoreFilename()));
            keystore.load(keystoreInput, getKeyStorePassword().toCharArray());

            KeyStore truststore = KeyStore.getInstance(getTrustStoreType());
            InputStream truststoreInput = new FileInputStream(ResourceUtils.getFile(getTrustStoreFilename()));
            truststore.load(truststoreInput, getTrustStorePassword()
                    .toCharArray());

            final SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", getHttpsPort(),
                                               new SSLSocketFactory(keystore, getKeyStorePassword(),
                                                                    truststore)));

            if (httpParams == null) {
                httpParams = new BasicHttpParams();
                httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                                           DEFAULT_READ_TIMEOUT_MILLISECONDS);
            }

            httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
                    schemeRegistry), httpParams);

            if (logger.isDebugEnabled()) {
                logger.debug("afterPropertiesSet:R:this=" + this);
            }

        } catch (Throwable t) {
            throw new RuntimeException(this.toString(), t);
        }

    }

    /**
     * Return the httpClient created from the properties set
     *
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public HttpClient getObject() {
        return httpClient;
    }

    /**
     * Return the interface type. This should be sufficient information to
     * support @Autowire
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    /**
     * We do cache our client
     *
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }


    /**
     * @param keyStoreFilename
     * @see ClientKeyStoreParameters#setKeyStoreFilename(String)
     */
    public void setKeyStoreFilename(String keyStoreFilename) {
        clientKeyStoreParameters.setKeyStoreFilename(keyStoreFilename);
    }


    /**
     * @return keyStoreFilename
     * @see ClientKeyStoreParameters#getKeyStoreFilename()
     */
    public String getKeyStoreFilename() {
        return clientKeyStoreParameters.getKeyStoreFilename();
    }


    /**
     * @param keyStoreType
     * @see ClientKeyStoreParameters#setKeyStoreType(String)
     */
    public void setKeyStoreType(String keyStoreType) {
        clientKeyStoreParameters.setKeyStoreType(keyStoreType);
    }


    /**
     * @return keyStoreType
     * @see common.web.client.ClientKeyStoreParameters#getKeyStoreType()
     */
    public String getKeyStoreType() {
        return clientKeyStoreParameters.getKeyStoreType();
    }


    /**
     * @param keyStorePassword
     * @see ClientKeyStoreParameters#setKeyStorePassword(String)
     */
    public void setKeyStorePassword(String keyStorePassword) {
        clientKeyStoreParameters.setKeyStorePassword(keyStorePassword);
    }


    /**
     * @return keyStorePassword
     * @see ClientKeyStoreParameters#getKeyStorePassword()
     */
    public String getKeyStorePassword() {
        return clientKeyStoreParameters.getKeyStorePassword();
    }


    /**
     * @param trustStoreFilename
     * @see ClientKeyStoreParameters#setTrustStoreFilename(String)
     */
    public void setTrustStoreFilename(String trustStoreFilename) {
        clientKeyStoreParameters.setTrustStoreFilename(trustStoreFilename);
    }


    /**
     * @return trustStoreFilename
     * @see ClientKeyStoreParameters#getTrustStoreFilename()
     */
    public String getTrustStoreFilename() {
        return clientKeyStoreParameters.getTrustStoreFilename();
    }


    /**
     * @param trustStoreType
     * @see ClientKeyStoreParameters#setTrustStoreType(String)
     */
    public void setTrustStoreType(String trustStoreType) {
        clientKeyStoreParameters.setTrustStoreType(trustStoreType);
    }


    /**
     * @return trustStoreType
     * @see ClientKeyStoreParameters#getTrustStoreType()
     */
    public String getTrustStoreType() {
        return clientKeyStoreParameters.getTrustStoreType();
    }


    /**
     * @param trustStorePassword
     * @see ClientKeyStoreParameters#setTrustStorePassword(String)
     */
    public void setTrustStorePassword(String trustStorePassword) {
        clientKeyStoreParameters.setTrustStorePassword(trustStorePassword);
    }


    /**
     * @return trustStorePassword
     * @see ClientKeyStoreParameters#getTrustStorePassword()
     */
    public String getTrustStorePassword() {
        return clientKeyStoreParameters.getTrustStorePassword();
    }


    /**
     * @return readable representation of state
     */
    public String toString() {
        StringBuffer tos = new StringBuffer(super.toString());
        tos.append(":clientKeyStoreParameters=").append(clientKeyStoreParameters);
        tos.append(":httpsPort=").append(httpsPort);
        tos.append(":httpClient=").append(httpClient);
        tos.append(":httpParams=").append(httpParams);

        return tos.toString();
    }
}
