package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notifications;
import pro.sky.telegrambot.model.NotificationsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final TelegramBot telegramBot;

    public NotificationsService(NotificationsRepository notificationsRepository, TelegramBot telegramBot) {
        this.notificationsRepository = notificationsRepository;
        this.telegramBot = telegramBot;
    }
    //Создаем метод проверки и отправки уведомлений из БД
    public void sendNotification(LocalDateTime dateTime) {
        //Делаем выборку уведомлений соответствующих запрашиваемой дате и времени
        //Формируем список уведомлений
        List<Notifications> notificationsList = notificationsRepository.findAll().stream()
                        .filter(e -> e.getDate().equals(dateTime))
                                .collect(Collectors.toList());
        //Осуществляем отправку всех уведомлений, которые соответствуют переданной дате и времени
        notificationsList.forEach(e -> {
            SendResponse sendNotify = telegramBot.execute(new SendMessage(e.getChatId(), e.getNotification()));
        });
    }

}
