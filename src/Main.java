import java.awt.*;
import java.awt.image.BufferedImage; // 아이콘을 위한 빈 이미지 생성용

public class Main {
    public static void main(String[] args) {

        // 1. 내 PC가 시스템 트레이(알림) 기능을 지원하는지 확인
        if (!SystemTray.isSupported()) {
            System.out.println("시스템 트레이를 지원하지 않습니다.");
            return; // 지원 안 하면 프로그램 종료
        }

        // 2. 트레이 아이콘에 표시할 이미지 생성
        //    TrayIcon은 이미지가 필수입니다.
        //    (일단은 아무 의미 없는 16x16짜리 빈 이미지를 만들어 씁니다)
        Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);

        // 3. 윈도우의 시스템 트레이(시계 옆 아이콘 영역)에 접근
        SystemTray tray = SystemTray.getSystemTray();

        // 4. 트레이에 등록할 아이콘 생성
        //    파라미터: (아이콘 이미지, 마우스 올렸을 때 뜨는 글자(툴팁))
        TrayIcon trayIcon = new TrayIcon(image, "윈도우 일정 알리미");
        trayIcon.setImageAutoSize(true); // 아이콘 크기 자동 조절

        try {
            // 5. (필수) 시스템 트레이에 이 아이콘을 추가
            //    이걸 해야 알림을 띄울 수 있습니다.
            tray.add(trayIcon);

            // 6. ★★★ 드디어 알림 표시! ★★★
            //    파라미터: (알림 제목, 알림 내용, 메시지 종류)
            trayIcon.displayMessage("🎉 테스트 알림",
                    "Hello, World! 알림 띄우기 성공!",
                    TrayIcon.MessageType.INFO);

        } catch (AWTException e) {
            // 아이콘 추가(add) 실패 시
            System.out.println("트레이 아이콘을 추가할 수 없습니다.");
        }

        // 7. (중요!) 알림이 표시될 시간을 벌어주기
        //    main 스레드가 바로 끝나버리면 알림이 뜨기 전에 프로그램이 종료될 수 있습니다.
        //    AWT 스레드가 알림을 띄울 시간을 주기 위해 5초 정도 기다립니다.
        try {
            Thread.sleep(5000); // 5초 (5000 밀리초) 대기
        } catch (InterruptedException e) {
            // 스레드 예외 처리
        }

        // 8. 프로그램 깔끔하게 종료
        tray.remove(trayIcon); // 트레이에 추가했던 아이콘 제거
        System.exit(0);      // 프로그램 강제 종료 (AWT 스레드까지 모두)
    }
}