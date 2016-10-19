package com.adyen.adyendl.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by andrei on 9/6/16.
 */
public class CheckoutHttpRequest<T> {

    private final static String tag = CheckoutHttpRequest.class.getSimpleName();

    private URL serverUrl;
    private T requestObject;

    public CheckoutHttpRequest(URL serverUrl, T requestObject) {
        this.serverUrl = serverUrl;
        this.requestObject = requestObject;
    }

    public String stringPostRequestWithBody() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();

        HttpURLConnection serverConnection = (HttpURLConnection)serverUrl.openConnection();
        serverConnection.setRequestMethod("POST");
        serverConnection.setDoOutput(true);

        OutputStream serverConnectionOutputStream = serverConnection.getOutputStream();
        serverConnectionOutputStream.write(String.valueOf(requestObject).getBytes());
        serverConnectionOutputStream.flush();
        serverConnectionOutputStream.close();

        int responseCode = serverConnection.getResponseCode();
        Log.i(tag, "POST response code: " + responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader connectionInputStream = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));

            String inputLine;
            while((inputLine = connectionInputStream.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            connectionInputStream.close();
        }

        return responseBuilder.toString();
    }

    public String stringPostRequest() {
        StringBuilder responseBuilder = new StringBuilder();

        try {
            HttpURLConnection urlConnection = (HttpURLConnection)serverUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
            while(httpResponseScanner.hasNextLine()) {
                responseBuilder.append(httpResponseScanner.nextLine());
            }
            httpResponseScanner.close();
        } catch (IOException e) {
            Log.e(tag, e.getMessage(), e);
        }
        return responseBuilder.toString();
    }

    public String fetchRedirectUrl() {
        return serverUrl.toString() + "?" + requestObject;
    }

}
