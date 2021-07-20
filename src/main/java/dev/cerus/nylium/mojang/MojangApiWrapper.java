package dev.cerus.nylium.mojang;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangApiWrapper {

    public static JsonObject hasJoined(final String username, final String hash) throws IOException {
        final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + username + "&serverId=" + hash);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);

        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (final Exception ignored) {
            inputStream = connection.getErrorStream();
        }

        final StringBuilder stringBuffer = new StringBuilder();
        int i;
        while ((i = inputStream.read()) != -1) {
            stringBuffer.append((char) i);
        }

        System.out.println(connection.getResponseCode());
        return JsonParser.parseString(stringBuffer.toString()).getAsJsonObject();
    }

}
