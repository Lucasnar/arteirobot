package bot;
import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Objects;

/**
 * Created by Aluno on 16/09/2016.
 */

public class Bot {
    TelegramBot bot = TelegramBotAdapter.build("225486233:AAGSX_ghHrtXdtlXeyEjyWUKCaTlqb5P4y8");
    Update update;
    InlineQuery inlineQuery;
    Message msg;
    Chat chat;
    Model model;
    boolean typeChatCommon;

    Bot(){
        model = new Model("mongodb://arteiro:abacate@ds019846.mlab.com:19846/arteiro");
    }

    public void sendMessage(String text , String chatId){
        bot.execute(
                new SendMessage(chatId, text)
        );
    }
    protected void sendPhoto(String chat, String photoLink,String caption) throws IOException  {
        Image image = null;
        URL url = new URL(photoLink);
        image = ImageIO.read(url);
        BufferedImage originalImage = (BufferedImage) image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();

        bot.sendPhoto(chat, InputFileBytes.photo(imageInByte), caption, null, new ReplyKeyboardHide());
    }

    protected void setUpdate(String response){
        update = BotUtils.parseUpdate(response);
        checkChat(response);

    }

    private boolean isCommonChat(String response){
        if(response.contains("chat")) {
            typeChatCommon = true;
            return true;
        }
        typeChatCommon = false;
        return false;
    }

    private void checkChat(String response){
        if(isCommonChat(response)){
            msg = update.message();
            chat = msg.chat();
        } else {
            inlineQuery = update.inlineQuery();
        }
    }

    protected void read(byte[] bodyRequest) {
        try {
            setUpdate(new String(bodyRequest, "UTF-8"));
            if (getTypeChatCommon()) {
                String message = getMessage();
                sendMessage(message, getChatId());

                if(message.contentEquals("random")) {
                    Artist artist = model.showRandomArtist();
                    sendMessage("Showing random artist test", getChatId());
                    sendPhoto(getChatId(), artist.getArte(), artist.mountArtist());
                }

                else {
                    ArrayList<Artist> artists;
                    artists = model.searchArtistName(message);
                    for (Artist artist : artists) {
                        try {
                            sendPhoto(getChatId(), artist.getArte(), artist.mountArtist());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    KeyboardButton searchArtistByNameOrLocationButton =
                            new KeyboardButton("Search artist by artist name or location");
                    KeyboardButton showRandomArtistButton = new KeyboardButton("random");
                    KeyboardButton[] searchArtistAndShowRandom = new KeyboardButton[2];
                    searchArtistAndShowRandom[0] = searchArtistByNameOrLocationButton;
                    searchArtistAndShowRandom[1] = showRandomArtistButton;
                    ReplyKeyboardMarkup searchArtists = new ReplyKeyboardMarkup(searchArtistAndShowRandom);

                    //SendResponse sendResponse = bot.execute(
//                        new SendMessage(getChatId(), "Please choose one:").replyMarkup(new ReplyKeyboardMarkup( new KeyboardButton[]{
//                                new KeyboardButton("Search for artist name or location"),
//                                new KeyboardButton("Show random artist"),
//                        }))
//                );

                    bot.execute(
                            new SendMessage(getChatId(), "Please choose one:").replyMarkup(searchArtists)
                    );
                }
            } else {
                sendMessage("Hello, you are using inline!", getChatId());

            }

        } catch (Exception e) {
            // Admin notification
            sendMessage(e.getMessage() + "\n" + e.getStackTrace(), "-145562622");
        }
    }

    String adminId = "-164346147";
    // Chats ID
    // Lucas = 136505761 / Samuel = 153878723 / grupo = -164346147
    protected void testMessage(String text) {
        sendMessage(text, adminId);
    }

    protected void testSearch(String artistName){
        ArrayList< Artist > artistas;
        artistas = model.searchArtistName(artistName);
        for (int i = 0; i < artistas.size(); i++) {
            try {
            sendPhoto(adminId, artistas.get(i).getArte() ,  artistas.get(i).mountArtist());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    protected  boolean getTypeChatCommon(){
        return typeChatCommon;
    }
    protected String getChatId(){
        return Long.toString(chat.id());
    }

    protected String getMessage(){
        return msg.text();
    }
}
