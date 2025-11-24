package controller; // ★ 패키지 선언

import model.Schedule;
import model.ScheduleRepository;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;

public class Main {

    private static TrayIcon trayIcon;
    private static SystemTray tray;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ★ Model (저장소) 연결
    private static final ScheduleRepository repository = new ScheduleRepository();

    public static void main(String[] args) {

        if (!SystemTray.isSupported()) {
            System.out.println("시스템 트레이를 지원하지 않습니다.");
            return;
        }

        // 아이콘 설정
        URL iconUrl = Main.class.getResource("/icon.png");
        Image image;
        if (iconUrl != null) {
            image = new ImageIcon(iconUrl).getImage();
        } else {
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        }

        tray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(image, "윈도우 일정 알리미");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("트레이 아이콘을 추가할 수 없습니다.");
            return;
        }

        showNotification("🚀 일정 알리미 시작", "프로그램이 백그라운드에서 실행 중입니다.");

        LocalDateTime now = LocalDateTime.now();
        int secondsToNextMinute = 60 - now.getSecond();
        long initialDelay = secondsToNextMinute * 1000L;

        System.out.println("타이머 : " + secondsToNextMinute + "초 후에 첫 실행 시작...");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkTime();
            }
        }, initialDelay, 60000L);
    }

    private static void showNotification(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

    private static void checkTime() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        System.out.println("현재 시간: " + now.format(FORMATTER) + " (체크 중...)");

        // ★ Repository 사용
        List<Schedule> schedules = repository.loadAllSchedules();

        for (Schedule schedule : schedules) {
            if (now.isEqual(schedule.getDateTime())) {
                System.out.println("일정 발견! 알림 발송: " + schedule.getTitle());
                showNotification("🔔 " + schedule.getTitle(), schedule.getMessage());
            }
        }
    }
}