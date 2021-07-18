package ua.kiev.prog;

import com.google.gson.Gson;
import ua.kiev.prog.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private String login;
    private String password;
    private boolean online;
    private List<ChatRoom> userChats = new ArrayList<>();

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void sendMessage(String receiver){
        Scanner scanner = new Scanner(System.in);
        System.out.println("You entered chat! To exit, send \"!exit\".");
        try{
            Thread th = new Thread(new MessageThread(this.login));
            th.setDaemon(true);
            th.start();

            System.out.println("Enter your message: ");
            while (true) {
                String text = scanner.nextLine();
                if (text.equals("!exit")) break;

                Message m = new Message(this.login, receiver, text);
                int res = m.send(Utils.getURL() + "/send");

                if (res != 200) { // 200 OK
                    System.out.println("HTTP error occured: " + res);
                    return;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String action(String action){
        try{
            URL obj = new URL(Utils.getURL() + "/" + action);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            String json = this.toJSON();
            os.write(json.getBytes(StandardCharsets.UTF_8));

            InputStream is = conn.getInputStream();
            byte[] buf = Utils.responseBodyToArray(is);
            String result = new String(buf, StandardCharsets.UTF_8);

            if(result.equals(Utils.LOGGED_IN)){
                this.online = true;
            }
            return result;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void logout(){
        try {
            URL obj = new URL(Utils.getURL() + "/logout?login=" + this.login);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("GET");

            conn.getResponseCode();
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Successfully logged out!");
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<ChatRoom> getUserChats() {
        return userChats;
    }

    public void setUserChats(List<ChatRoom> userChats) {
        this.userChats = userChats;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
