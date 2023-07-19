package gov.miloverada.telegram_bot.util;

import gov.miloverada.telegram_bot.exceptions.HttpGetException;
import gov.miloverada.telegram_bot.exceptions.RepositoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Util class for http requests
 */
public class HttpHelper {

    private static final Logger logger = LogManager.getLogger(HttpHelper.class);

    public static final String HOST = "http://localhost";

    /**
     * Builds query string
     * @param host host
     * @param endpoint endpoint
     * @param params parameters
     * @return built query
     * @throws UnsupportedEncodingException throws exception if encoding is not utf-8
     */
    public static String httpQueryBuilder(String host, String endpoint, Map<String, String> params) throws UnsupportedEncodingException {
        return host + endpoint + "?" + getParamsString(params);
    }

    /**
     * Converts map of query parameters to string with format {@example ?key=value&key=value}
     * @param params map of parameters
     * @return query string
     * @throws UnsupportedEncodingException throws if encoding is not utf-8
     */
    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static String getResponse(InputStream inputStream) {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJson(String host, String endpoint, Map<String, String> params) throws HttpGetException {
        try {
            URL url = new URL(host + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (params != null) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(HttpHelper.getParamsString(params));
                out.flush();
                out.close();
            }

            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if (status == 500) {
                logger.error("status " + status);
                return null;
            }
            else logger.debug("status " + status);

            String resp = getResponse(con.getInputStream());
            con.disconnect();

            return resp;
        } catch (IOException e) {
            throw new HttpGetException(e);
        }
    }

    public static URI buildQuery(String endpoint, String...params) {
        try {
            if (params.length == 0) {
                return new URI(HOST + endpoint);
            } else {

                if (params.length % 2 != 0) throw new IllegalArgumentException("The number of keys does not match the number of values");
                StringBuilder builder = new StringBuilder(HOST + endpoint + "?");
                for (int i = 0; i < params.length; i = i +2) {
                    if (i != 0) builder.append("&");
                    builder
                            .append(params[i])
                            .append("=")
                            .append(params[i+1]);
                }
                return new URI(builder.toString());
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse<String> sendAndGetStringAsResp(HttpRequest request) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            logger.debug(response);
            if (status >= 500) throw new RepositoryException("status code is: " + status);
            else return response;
        } catch (IOException | InterruptedException e) {
            throw new HttpGetException(e);
        }
    }

    public static String getJson(String host, String endpoint) throws HttpGetException  {
        return getJson(host, endpoint, null);
    }

}
