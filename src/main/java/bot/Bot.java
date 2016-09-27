package bot;
import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.*;

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
    Boolean countrySearch;
    Boolean nameSearch;

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

                if (isCountrySearch()){
                    searchArtistsByCountry(message);
                    showKeyboard();
                    setCountrySearch(false);
                }
                else if(isNameSearch()){
                    searchArtistsByName(message);
                    showKeyboard();
                    setNameSearch(false);
                }
                if(message.contentEquals("Show random artist")) {
                    showRandomArtist();
                    showKeyboard();
                } else if(message.contentEquals("Search artists by name")) {
                    sendMessage("Please type an artist name for me to search for", getChatId());
                    setNameSearch(true);
                }
                else if(message.contentEquals("Search artists by country")) {
                    sendMessage("Please type an artist country for me to search for", getChatId());
                    setCountrySearch(true);
                }

            } else {
                setInlineQuery();
                String message = inlineQuery.query();
                inlineSearch(message);
            }

        } catch (Exception e) {
            // Admin notification
            sendMessage(e.getMessage() + "\n" + e.getStackTrace(), "136505761"); // Antigo "-145562622"
        }
    }

    private void searchArtistsByCountry(String country) {
        ArrayList<Artist> artists = getArtistsByCountry(country);
        if (artists.size() != 0){
            for (Artist artist : artists) {
                try {
                    sendPhoto(getChatId(), artist.getArte(), artist);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else{
            sendMessage("No artists found.", getChatId());
        }
    }

    private void showRandomArtist() throws IOException {
        Artist artist = model.showRandomArtist();
        sendPhoto(getChatId(), artist.getArte(), artist);
    }

    private ArrayList<Artist> getArtistsByCountry(String country){
        ArrayList<Artist> artists = model.searchArtistByCountry(country);
        return artists;
    }
    private ArrayList<Artist> getArtistsByName(String name){
        ArrayList<Artist> artists = model.searchArtistName(name);
        return artists;
    }

    private void inlineSearch(String name){

        ArrayList<Artist> artists = getArtistsByName(name);

        if(artists.size() != 0) {
            InlineQueryResult[] result = new InlineQueryResultArticle[artists.size()];
            int i = 0;
            for (Artist artist : artists) {
                result[i++] = new InlineQueryResultArticle(String.valueOf(i),
                        artist.getNome(), artist.getNome()).thumbUrl(artist.getArte());
            }

            bot.execute(new AnswerInlineQuery(inlineQuery.id(), result));
        }
    }

    private void searchArtistsByName(String name) {
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
            sendMessage("No artists found.", getChatId());
        }

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
                       new KeyboardButton("Search artists by country"),
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

    public void setCountrySearch(boolean isCountrySearch) {
        this.countrySearch = isCountrySearch;
    }

    public void setNameSearch(boolean isNameSearch) {
        this.nameSearch = isNameSearch;
    }

    public boolean isNameSearch() {
        return nameSearch;
    }

    public boolean isCountrySearch() {
        return countrySearch;
    }
}
