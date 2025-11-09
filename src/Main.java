import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException; // ★★★ 파일 읽기를 위해 추가
import java.nio.file.Files; // ★★★ 파일 읽기를 위해 추가
import java.nio.file.Path; // ★★★ 파일 읽기를 위해 추가
import java.nio.file.Paths; // ★★★ 파일 읽기를 위해 추가
import java.time.LocalTime;
import java.util.List; // ★★★ 파일 읽기를 위해 추가
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static TrayIcon trayIcon;
    private static SystemTray tray;

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
                "프로그램이 백그라운드에서 실행 중입니다.");


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkTime(); // 10초마다 이 함수를 실행
            }
        }, 5000, 10000); // 5초 후에 시작해서, 10초(10000ms)마다 반복
    }

    private static void showNotification(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

    /**
     * 10초마다 호출되어 schedule.txt 파일을 읽고 시간을 체크하는 메소드
     */
    private static void checkTime() {
        // 1. 현재 시간 (시, 분) 가져오기
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        System.out.println("현재 시간: " + currentHour + ":" + currentMinute + " (파일 체크 중...)"); // 로그

        // 2. schedule.txt 파일 경로 설정
        Path schedulePath = Paths.get("schedule.txt");

        // 3. 파일 읽기
        try {
            // (파일이 없으면 오류 대신 빈 리스트 반환)
            if (!Files.exists(schedulePath)) {
                System.out.println("schedule.txt 파일이 없습니다.");
                return;
            }

            // 파일의 모든 라인을 읽어온다
            List<String> allLines = Files.readAllLines(schedulePath);

            // 4. 한 줄씩 검사
            for (String line : allLines) {
                // 형식: "시간,제목,내용"
                String[] parts = line.split(",", 3); // 콤마로 쪼개기 (최대 3조각)

                if (parts.length < 3) continue; // 형식이 안 맞으면 무시

                // 5. 파일에서 시간 파싱
                String[] timeParts = parts[0].split(":"); // "HH:mm"
                if (timeParts.length < 2) continue; // 시간 형식이 안 맞으면 무시

                int targetHour = Integer.parseInt(timeParts[0]); // 시
                int targetMinute = Integer.parseInt(timeParts[1]); // 분
                String title = parts[1];
                String message = parts[2];

                // 6. 시간 비교
                if (currentHour == targetHour && currentMinute == targetMinute) {
                    System.out.println("일정 발견! 알림 발송: " + title); // 로그
                    showNotification("🔔 " + title, message);
                }
            }

        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류 발생: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("시간 형식이 잘못되었습니다 (HH:mm): " + e.getMessage());
        }
    }
}