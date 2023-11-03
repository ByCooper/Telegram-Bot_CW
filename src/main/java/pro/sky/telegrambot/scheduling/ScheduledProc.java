package pro.sky.telegrambot.scheduling;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.service.NotificationsService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ScheduledProc {

   private final NotificationsService notificationsService;

    public ScheduledProc(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }
    //Задаем периодичность проверки хранящихся уведомлений в БД
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        //Вызов метода проверки уведомлений в БД для отправки в установленный период
        //В сигнатуру метода передаем дату и время(время ограничено до минут)
        notificationsService.sendNotification(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}
