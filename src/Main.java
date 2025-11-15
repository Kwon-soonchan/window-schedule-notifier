import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static TrayIcon trayIcon;
    private static SystemTray tray;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ★★★ (수정) 사용자 홈 디렉토리에 공용 파일 경로 생성 ★★★
    // (예: C:\Users\clove\window_schedule_notifier.txt)
    private static final Path schedulePath = Paths.get(
            System.getProperty("user.home"), "window_schedule_notifier.txt");

    public static void main(String[] args) {

        if (!SystemTray.isSupported()) {
            System.out.println("시스템 트레이를 지원하지 않습니다.");
            return;
        }

        Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        tray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(image, "윈도우 일정 알리미");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("트레이 아이콘을 추가할 수 없습니다.");
            return;
        }

        showNotification("🚀 일정 알리미 시작",
                "프로그램이 백그라운드에서 실행 중입니다.\n파일 위치: " + schedulePath.toString()); // ★ 파일 위치 로그 추가

        LocalDateTime now = LocalDateTime.now();
        int secondsToNextMinute = 60 - now.getSecond();
        long initialDelay = secondsToNextMinute * 1000L;

        System.out.println("타이머 : " + secondsToNextMinute + "초 후에 첫 실행 시작...");
        System.out.println("감시 중인 파일: " + schedulePath.toString()); // ★ 파일 위치 로그 추가

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
        System.out.println("현재 시간: " + now.format(FORMATTER) + " (파일 체크 중...)");

        // ★ (수정) 전역 변수 schedulePath 사용 (로컬 변수 삭제)
        try {
            if (!Files.exists(schedulePath)) {
                // (파일이 없는 것은 정상이므로 로그 삭제)
                return;
            }

            List<String> allLines = Files.readAllLines(schedulePath);

            for (String line : allLines) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;

                try {
                    LocalDateTime targetDateTime = LocalDateTime.parse(parts[0], FORMATTER);
                    String title = parts[1];
                    String message = parts[2];

                    if (now.isEqual(targetDateTime)) {
                        System.out.println("일정 발견! 알림 발송: " + title);
                        showNotification("🔔 " + title, message);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("잘못된 날짜/시간 형식의 라인 발견: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류 발생: " + e.getMessage());
        }
    }
}