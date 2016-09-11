package models;
import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.*;
/**
 * Created by Samuel on 10/09/2016.
 */
public class Send extends BaseModel{
    TelegramBot bot = TelegramBotAdapter.build("235548784:AAHkS-f8J4D4LTM527TldPFHRKt0DL1ykB4");
    Update update;
    InlineQuery inlineQuery;
    Message msg;
    Chat chat;
    boolean typeChatCommon; // TRUE for common chat / FALSE for inline chat

    public void sendMessage(String text , String chatId){
        SendResponse sendResponse = bot.execute(
                new SendMessage(chatId, text)
        );
    }

    protected void setUpdate(String response){
        update = BotUtils.parseUpdate(response);
        checkChat(response);

    }

    private void checkChat(String response){
        if(response.indexOf("chat") != -1){
            msg = update.message();
            chat = msg.chat();
            typeChatCommon = true;
        } else {
            typeChatCommon = false;
            inlineQuery = update.inlineQuery();
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
