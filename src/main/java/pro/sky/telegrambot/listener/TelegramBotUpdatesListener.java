package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationsRepository;
import pro.sky.telegrambot.model.Notifications;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationsRepository notificationRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
//Задаем условие-реакцию на приветственное сообщение /start
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            //Выделяем в отдельную переменную ID чата
            Long chatID = update.message().chat().id();
            if (update.message().text().equals("/start")) {
                String userName = update.message().from().firstName();
                //Создаем структуру приветственного сообщения юзеру
                SendMessage sendMessage = new SendMessage(chatID, "Привет " + userName + ", пора позаботиться о Ваших делах!" + "\n" + "Введите событие и дату в формате -> 01.01.2022 20:00 Сделать домашнюю работу");
                //Отправление ответа юзеру в чат
                SendResponse response = telegramBot.execute(sendMessage);
            }
            //Создание регулярного выражения
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(update.message().text());
            String task = null;
            String date = null;
            //Выборка и распределение составляющих входящего обращения юзера
            if (matcher.matches()) {
                date = matcher.group(1);
                task = matcher.group(3);
                System.out.println(date);
                System.out.println(task);
            }
            //Задаем форматирование даты и времени уведомления для сохранения в БД
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            if (date != null) {
                //Приведение даты и времени к установленному формату
                LocalDateTime dateTime = LocalDateTime.parse(date, dateTimeFormatter);
                //Сохранение уведомления в БД
                notificationRepository.save(new Notifications(chatID, task, dateTime));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
