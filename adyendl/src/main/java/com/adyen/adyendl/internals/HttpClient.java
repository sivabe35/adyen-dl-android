package com.adyen.adyendl.internals;

import com.adyen.adyendl.exceptions.HttpAuthenticationException;
import com.adyen.adyendl.exceptions.HttpAuthorizationException;
import com.adyen.adyendl.exceptions.HttpDownForMaintenanceException;
import com.adyen.adyendl.exceptions.HttpServerException;
import com.adyen.adyendl.exceptions.UnexpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

/**
 * Created by andrei on 10/24/16.
 */
public class HttpClient<T extends HttpClient> {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String UTF_8 = "UTF-8";

    private SSLSocketFactory mSSLSocketFactory;
    private int mConnectTimeout;
    private int mReadTimeout;


    public HttpClient() {
        mConnectTimeout = (int) TimeUnit.SECONDS.toMillis(60);
        mReadTimeout = (int) TimeUnit.SECONDS.toMillis(60);

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, null);
            mSSLSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            mSSLSocketFactory = null;
        } catch (KeyManagementException e) {
            mSSLSocketFactory = null;
        }
    }

    public T setConnectTimeout(int timeout) {
        mConnectTimeout = timeout;
        return (T) this;
    }

    public T setReadTimeout(int timeout) {
        mReadTimeout = timeout;
        return (T) this;
    }

    /**
     * Synchronous POST request
     *
     * @param url server URL
     * @param data the body of the POST request
     * @return the HTTP body of the response
     * @throws Exception
     */
    public String post(String url, String data) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = initHttpConnection(url);

            connection.setRequestMethod(METHOD_POST);
            connection.setDoOutput(true);

            writeOutputStream(connection.getOutputStream(), data);

            return parseResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Synchronous GET request
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String get(String path) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = initHttpConnection(path);

            connection.setRequestMethod(METHOD_GET);

            return parseResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpURLConnection initHttpConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        if (connection instanceof HttpsURLConnection) {
            if (mSSLSocketFactory == null) {
                throw new SSLException("SSLSocketFactory failed to initialize");
            }

            ((HttpsURLConnection) connection).setSSLSocketFactory(mSSLSocketFactory);
        }

        connection.setConnectTimeout(mConnectTimeout);
        connection.setReadTimeout(mReadTimeout);

        return connection;
    }

    private void writeOutputStream(OutputStream outputStream, String data) throws IOException {
        outputStream.write(data.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    protected String parseResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        switch(responseCode) {
            case HTTP_OK:
            case HTTP_CREATED:
            case HTTP_ACCEPTED:
                return readStream(connection.getInputStream());
            case HTTP_UNAUTHORIZED:
                throw new HttpAuthenticationException(readStream(connection.getErrorStream()));
            case HTTP_FORBIDDEN:
                throw new HttpAuthorizationException(readStream(connection.getErrorStream()));
            case HTTP_INTERNAL_ERROR:
                throw new HttpServerException(readStream(connection.getErrorStream()));
            case HTTP_UNAVAILABLE:
                throw new HttpDownForMaintenanceException(readStream(connection.getErrorStream()));
            default:
                throw new UnexpectedException(readStream(connection.getErrorStream()));
        }
    }

    private String readStream(InputStream in) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();

        if (in == null) {
            return null;
        }

        BufferedReader connectionInputStream = new BufferedReader(new InputStreamReader(in));

        String inputLine;
        while((inputLine = connectionInputStream.readLine()) != null) {
            responseBuilder.append(inputLine);
        }
        connectionInputStream.close();

        return responseBuilder.toString();
    }

}
