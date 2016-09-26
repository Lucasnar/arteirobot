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
    }


    protected void read(byte[] bodyRequest) {
        try {
            String response = new String(bodyRequest, "UTF-8");
            setUpdate(response);

            if (isCommonChat(response)) {
                setCommonChat();
                String message = getMessage();

                if(message.contentEquals("Show random artist")) {
                    showRandomArtist();
                    showKeyboard();
                } else if(message.contentEquals("Search artists by name")) {
                    sendMessage("Please type an artist name to search for", getChatId());
                } else if(message.contentEquals("getChatId()"))
                    sendMessage(getChatId(), getChatId());
                else {
                    searchArtistName(message);
                    showKeyboard();
                }

            } else {
                setInlineQuery();
                String message = inlineQuery.query();

                ArrayList<Artist> artists = getArtistsByName(message);

                if(artists.size() > 0) {
                    InlineQueryResult[] result = new InlineQueryResultArticle[5];
                    for (int i = 0; i < 5; ++i) {
                        Artist artist = artists.get(i);
                        result[i] = new InlineQueryResultArticle(String.valueOf(i),
                                artist.getNome(), artist.getNome()).thumbUrl(artist.getArte());
                    }

                    bot.execute(new AnswerInlineQuery(inlineQuery.id(), result));
                } else{
                    InlineQueryResult result = new InlineQueryResultArticle("0",
                            "No artists found.", "No results for the query.");
                    bot.execute(new AnswerInlineQuery(inlineQuery.id(), result));
                }

            }

        } catch (Exception e) {
            // Admin notification
            sendMessage(e.getMessage() + "\n" + e.getStackTrace(), "136505761"); // Antigo "-145562622"
        }
    }

    private void showRandomArtist() throws IOException {
        Artist artist = model.showRandomArtist();
        sendPhoto(getChatId(), artist.getArte(), artist);
    }

    private ArrayList<Artist> getArtistsByName(String name){
        ArrayList<Artist> artists = model.searchArtistName(name);
        return artists;
    }

    private void searchArtistName(String name) {
        ArrayList<Artist> artists = getArtistsByName(name);
        if (artists.size() != 0){
            for (Artist artist : artists) {
                try {
                    sendPhoto(getChatId(), artist.getArte(), artist);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else{
            noArtistsFound();
        }

    }

    private void noArtistsFound(){
        sendMessage("No artists found.", getChatId());
    }

    private boolean isCommonChat(String response){
        return response.contains("chat");
    }

    private void setInlineQuery(){
        inlineQuery = update.inlineQuery();
    }

    private void setCommonChat() {
        msg = update.message();
        chat = msg.chat();
    }

    private void showKeyboard(){

        ReplyKeyboardMarkup searchArtistsKeyboard = new ReplyKeyboardMarkup(
               new KeyboardButton[] {
                       new KeyboardButton("Search artists by name"),
                       new KeyboardButton("Show random artist")
               }
        );

        bot.execute(
                new SendMessage(getChatId(), "What do you want to do?").replyMarkup(searchArtistsKeyboard)
                );
    }

    protected String getChatId(){
        return Long.toString(chat.id());
    }

    protected String getMessage(){
        return msg.text();
    }
}
