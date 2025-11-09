import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalTime; // ★★★ 시간 비교를 위해 추가 ★★★
import java.util.Timer; // ★★★ 주기적인 작업을 위해 추가 ★★★
import java.util.TimerTask; // ★★★ 주기적인 작업을 위해 추가 ★★★

public class Main {

    // TrayIcon과 SystemTray를 다른 메소드에서도 쓸 수 있게 필드로 뺍니다.
    private static TrayIcon trayIcon;
    private static SystemTray tray;

    public static void main(String[] args) {

        if (!SystemTray.isSupported()) {
            System.out.println("시스템 트레이를 지원하지 않습니다.");
            return;
        }

        // --- 1. 트레이 아이콘 설정 (이전 코드와 거의 동일) ---
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

        // --- 2. 알림 메시지 표시 (첫 실행 환영) ---
        // (프로그램이 시작되었다는 것을 알려주기 위해)
        showNotification("🚀 일정 알리미 시작",
                "프로그램이 백그라운드에서 실행 중입니다.");


        // --- 3. ★★★ 핵심: 스케줄러 설정 ★★★ ---
        //    "10초마다 checkTime() 함수를 실행시켜줘"
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkTime(); // 10초마다 이 함수를 실행
            }
        }, 5000, 10000); // 5초 후에 시작해서, 10초(10000ms)마다 반복

        // main 스레드가 종료되면 안 되므로,
        // 이전의 sleep이나 exit(0) 코드는 모두 삭제합니다.
        // 이제 이 프로그램은 Timer 스레드가 돌고 있어서 종료되지 않습니다.
    }

    /**
     * 알림을 띄우는 역할을 하는 별도 메소드
     */
    private static void showNotification(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

    /**
     * 10초마다 호출되어 시간을 체크하는 메소드
     */
    private static void checkTime() {
        // 1. 현재 시간 가져오기 (시, 분)
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        // 2. ★★★ 테스트할 알림 시간 설정 ★★★
        //    (나중에는 이 부분을 파일이나 UI에서 읽어오도록 수정)
        int targetHour = 20; // 오후 7시
        int targetMinute = 40; // 55분

        System.out.println("현재 시간: " + currentHour + ":" + currentMinute + " (체크 중...)"); // 로그

        // 3. 시간 비교
        if (currentHour == targetHour && currentMinute == targetMinute) {
            System.out.println("시간 일치! 알림 발송!"); // 로그
            showNotification("🔔 일정 알림",
                    "지금 " + targetHour + "시 " + targetMinute + "분입니다!");

            // (참고: 이대로 두면 1분 동안 10초마다 알림이 계속 울립니다.
            //  한 번만 울리게 하는 로직은 나중에 추가합시다.)
        }
    }
}