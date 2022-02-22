import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {
        //инициализация API
        ApiContextInitializer.init();

        //создаем объект Telegram API
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        //регистрация бота
        try{
            telegramBotsApi.registerBot(new Bot());
        }catch (TelegramApiRequestException e){
            e.getStackTrace();
        }
    }

    public void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try{
            setButtons(sendMessage);
            sendMessage(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    //метод для приёма сообщений
    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();
        if (message != null && message.hasText()){
            switch (message.getText()){
                case "/start":
                    sendMsg(message, "Welcome! Вас приветствует Weather Bot!");
                    break;
                case "/help":
                    sendMsg(message, "Хочешь узнать погоду? Просто введи название города.");
                    break;
                default:
                    try{
                        sendMsg(message, Weather.getWeather(message.getText(), model));
                    }catch (IOException e){
                        sendMsg(message, "Sorry, город не найден! Попробуй ещё.");
                    }
            }
        }
    }

    //метод для отображения текстовых кнопок
    public void setButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); //инициализация клавиатуры
        sendMessage.setReplyMarkup(replyKeyboardMarkup); //разметка клавиатуры, связь сообщения и клавиатуры
        replyKeyboardMarkup.setSelective(true); //вывод клавиатуры всем пользователям
        replyKeyboardMarkup.setResizeKeyboard(true); //подгонка клавиатуры
        replyKeyboardMarkup.setOneTimeKeyboard(false); //сокрытие клавиатуры после использования

        List<KeyboardRow> keyboardRowList = new ArrayList<>();// коллекция кнопок
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/help"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    //метод, возвращающий имя бота, указанного прирегистрации
    public String getBotUsername() {
        return "ssk1984_bot";
    }

    public String getBotToken() {
        return "5023819987:AAFhaFvoReA-l5bEQou04ggLsDnmDqzSnIQ";
    }
}