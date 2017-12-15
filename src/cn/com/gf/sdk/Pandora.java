package cn.com.gf.sdk;

import com.google.gson.Gson;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.log4j.Logger;

import java.util.UUID;
import java.io.IOException;

public final class Pandora {
    private static final String regex = "\\d+";
    private static final String REQUEST_ID_HEADER = "X-DenCode-Request-Id";
    private static final String API_TOKEN_HEADER = "X-DenCode-Token";
    private static final String ENCODE_URL = "http://%s/api/secure/dencode/1.0.0/public/encode?infoType=%s";
    private static final String DECODE_URL = "http://%s/api/secure/dencode/1.0.0/public/decode";
    private static final String ERROR_MESSAGE = "status %s: %s";

    private static final MediaType PLAIN = MediaType.parse("text/plain;charset=UTF-8");
    private static final Object lockObj = new Object();

    private static Pandora instance;

    private OkHttpClient client;
    private Logger logger;
    private ConfigManager configManager;
    private String host;
    private String appId;

    private static String maskString(String strText, int start, int end, char maskChar)
            throws PandoraException {
        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end) {
            throw new PandoraException("End index cannot be greater than start index");
        }

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);
        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    private static void checkPhoneNum(String value) throws PandoraException {
        String number = value.trim();
        if (!number.matches(regex)) {
            throw new PandoraException("Invalid phone number " + value);
        }
    }

    private static void checkNull(String value, String name) throws PandoraException {
        if (value == null) {
            throw new PandoraException(name + " should not be null");
        }
    }

    private static void checkEmpty(String value, String name) throws PandoraException {
        if (value.trim().length() == 0) {
            throw new PandoraException(name + " should not be empty");
        }
    }

    private static void validateNull(Object object, String message) throws PandoraException {
        if (object == null) {
            throw new PandoraException(message);
        }
    }

    private String post(String url, String content) throws PandoraException {
        RequestBody body = RequestBody.create(PLAIN, content);
        Request request = new Request.Builder()
                .url(url)
                .addHeader(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .addHeader(API_TOKEN_HEADER, this.appId)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            validateNull(response, "HTTP Response should not be null");
            if (response.code() == 200) {
                validateNull(response.body(), "Invalid HTTP Response: " + response.toString());
                String result = response.body().string();
                checkNull(result, "Response body should not be null");
                checkEmpty(result, "Response body should not be empty");
                return result;
            }
            throw new PandoraException(response.toString());
        } catch (IOException e) {
            throw new PandoraException("Failed to make HTTP POST", e);
        }
    }

    private String getEncodeEndpoint(String host, String type) {
        return String.format(ENCODE_URL, host, type);
    }

    private String getDecodeEndpoint(String host) {
        return String.format(DECODE_URL, host);
    }

    public String encode(String type, String value) throws PandoraException {
        try {
            checkNull(type, "type");
            checkEmpty(type, "type");
            checkNull(value, "value");
            checkEmpty(value, "value");
            InfoType.check(type);

            if (type.equals(InfoType.PHONE)) {
                checkPhoneNum(value);
            }

            String text = value.trim();
            String url = getEncodeEndpoint(this.host, type);
            logger.debug(url + ", " + maskString(text, 3, 7, '*'));
            String result = post(url, text);
            logger.debug(result);
            Gson gson = new Gson();
            EncodeResult encodeResult = gson.fromJson(result, EncodeResult.class);
            if (encodeResult.errLevel == 0 && encodeResult.status == 10000) {
                return encodeResult.data.hashKey;
            } else {
                throw new PandoraException(String.format(ERROR_MESSAGE, encodeResult.status, encodeResult.message));
            }
        } catch (PandoraException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public String decode(String cipher) throws PandoraException {
        try {
            checkNull(cipher, "cipher");
            checkEmpty(cipher, "cipher");

            String url = getDecodeEndpoint(this.host);
            logger.debug(url + ", " + cipher.trim());
            String result = post(url, cipher.trim());
            Gson gson = new Gson();
            DecodeResult decodeResult = gson.fromJson(result, DecodeResult.class);
            if (decodeResult.errLevel == 0 && decodeResult.status == 10001) {
                logger.debug(maskString(decodeResult.data, 3, 7, '*'));
                return decodeResult.data;
            } else {
                throw new PandoraException(String.format(ERROR_MESSAGE, decodeResult.status, decodeResult.message));
            }
        } catch (PandoraException e) {
            logger.error(e);
            throw e;
        }
    }

    public static Pandora getInstance(String host, String appId) throws PandoraException {
        checkNull(host, "host");
        checkEmpty(host, "host");
        checkNull(appId, "appId");
        checkEmpty(appId, "appId");
        if (instance == null) {
            synchronized (lockObj) {
                if (instance == null) {
                    instance = new Pandora();
                    instance.configManager = ConfigManager.getInstance();
                    instance.logger = Logger.getLogger(Pandora.class);
                    instance.client = new OkHttpClient();
                    instance.appId = appId;
                    instance.host = host;
                }
            }
        }
        return instance;
    }
}
