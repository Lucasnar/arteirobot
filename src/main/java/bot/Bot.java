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
        model = new Model();
    }

    public void sendMessage(String text , String chatId){
        bot.execute(
                new SendMessage(chatId, text)
        );
   }
    public void sendPhoto(String chat, String photoLink, Artist artist) throws IOException  {
        Image image = null;
        URL url = new URL(photoLink);
        image = ImageIO.read(url);
        BufferedImage originalImage = (BufferedImage) image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("More from " + artist.getNome()).url(artist.getLink())
                });

        bot.execute(
          new SendPhoto(chat, imageInByte).caption(artist.mountArtist()).replyMarkup(inlineKeyboard)
        );
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

    protected void showKeyboard(){

        KeyboardButton searchArtistByNameOrLocationButton =
                new KeyboardButton("Search artists by name or location");
        KeyboardButton showRandomArtistButton = new KeyboardButton("Show random artist");
        KeyboardButton[] searchArtistAndShowRandomButtons = new KeyboardButton[2];
        searchArtistAndShowRandomButtons[0] = searchArtistByNameOrLocationButton;
        searchArtistAndShowRandomButtons[1] = showRandomArtistButton;
        ReplyKeyboardMarkup searchArtistsKeyboard = new ReplyKeyboardMarkup(searchArtistAndShowRandomButtons);

        bot.execute(
                new SendMessage(getChatId(), "What do you want to do?").replyMarkup(searchArtistsKeyboard)
                );
    }

    protected void read(byte[] bodyRequest) {
        try {
            setUpdate(new String(bodyRequest, "UTF-8"));
            if (getTypeChatCommon()) {

                String message = getMessage();

                if(message.contentEquals("Show random artist")) {
                    Artist artist = model.showRandomArtist();
                    sendPhoto(getChatId(), artist.getArte(), artist);
                }

                else {
                    ArrayList<Artist> artists;
                    artists = model.searchArtistName(message);
                    for (Artist artist : artists) {
                        try {
                            sendPhoto(getChatId(), artist.getArte(), artist);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                showKeyboard();

            } else {
                sendMessage("Hello, you are using inline!", getChatId());
            }

        } catch (Exception e) {
            // Admin notification
            sendMessage(e.getMessage() + "\n" + e.getStackTrace(), "-145562622");
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
