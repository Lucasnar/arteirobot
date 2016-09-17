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

/**
 * Created by Aluno on 16/09/2016.
 */
public class Bot {
    TelegramBot bot = TelegramBotAdapter.build("225486233:AAGSX_ghHrtXdtlXeyEjyWUKCaTlqb5P4y8");
    Update update;
    InlineQuery inlineQuery;
    Message msg;
    Chat chat;




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

//    protected void setUpdate(String response){
//        update = BotUtils.parseUpdate(response);
//        checkChat(response);
//
//    }

//    private void checkChat(String response){
//        if(response.indexOf("chat") != -1){
//            msg = update.message();
//            chat = msg.chat();
//            typeChatCommon = true;
//        } else {
//            typeChatCommon = false;
//            inlineQuery = update.inlineQuery();
//        }
//    }
//
//    protected  boolean getTypeChatCommon(){
//        return typeChatCommon;
//    }
//    protected String getChatId(){
//        return Long.toString(chat.id());
//    }
//
//    protected String getMessage(){
//        return msg.text();
//    }
}
