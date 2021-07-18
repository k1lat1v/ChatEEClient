package ua.kiev.prog.utils;

import ua.kiev.prog.ChatRoom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final String URL = "http://127.0.0.1";
    private static final int PORT = 8080;
    public static final String LOGGED_IN = "Successfully logged in!";
    public static final String NOT_REGISTERED = "User is not registered!";

    public static String getURL() {
        return URL + ":" + PORT;
    }

    public static byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

    public static String getUsers(String exceptUser){
        try {
            URL obj = new URL(getURL() + "/users?except=" + exceptUser);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            InputStream is = conn.getInputStream();
            byte[] buf = Utils.responseBodyToArray(is);
            return new String(buf, StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getChatRooms(String login){
        try{
            URL obj = new URL(getURL() + "/chatroom?login=" + login);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            InputStream is = conn.getInputStream();
            byte[] buf = Utils.responseBodyToArray(is);
            return new String(buf, StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static void addChatRoom(ChatRoom chatRoom){
        try{
            URL obj = new URL(getURL() + "/addchatroom");
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            String result = chatRoom.toJSON();
            os.write(result.getBytes(StandardCharsets.UTF_8));
            conn.getResponseCode();
        }catch (IOException e){
            e.printStackTrace();;
        }
    }
}
