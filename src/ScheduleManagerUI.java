import javax.swing.*; // ★★★ Swing UI 라이브러리 ★★★
import java.awt.*;    // "Add" 버튼 클릭 이벤트를 위한 import

public class ScheduleManagerUI {

    public static void main(String[] args) {
        // 1. UI를 "Event Dispatch Thread"라는 별도의 스레드에서
        //    안전하게 실행하도록 보장하는 Swing의 공식 코드입니다.
        //    (일단은 '이렇게 해야 한다'고 넘어가시면 됩니다)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * UI를 생성하고 화면에 표시하는 메소드
     */
    private static void createAndShowGUI() {
        // 1. 가장 바깥의 윈도우(창)을 만듭니다.
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫으면 프로그램 종료
        frame.setSize(400, 300); // 창 크기 설정
        frame.setLocationRelativeTo(null); // 화면 중앙에 띄우기

        // 2. UI 컴포넌트들을 담을 '패널'을 만듭니다.
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout()); // 컴포넌트를 한 줄로 쭉 나열하는 레이아웃

        // 3. UI 컴포넌트들 생성
        JLabel label = new JLabel("새 일정 (예: 14:30,제목,내용)");
        JTextField textField = new JTextField(25); // 25 글자 정도 보이는 텍스트 입력칸
        JButton addButton = new JButton("추가하기");

        // 4. 패널(쟁반)에 컴포넌트들(반찬)을 올립니다.
        panel.add(label);
        panel.add(textField);
        panel.add(addButton);

        // 5. 창(frame)에 패널(panel)을 붙입니다.
        frame.add(panel);

        // 6. ★★★ 마지막에 창을 화면에 보여줍니다 ★★★
        frame.setVisible(true);

        // --- 7. (나중에 추가할 곳) "추가하기" 버튼 클릭 이벤트 ---
        // addButton.addActionListener(e -> {
        //    String newSchedule = textField.getText();
        //    System.out.println("추가할 일정: " + newSchedule);
        //    // 여기에 newSchedule을 schedule.txt 파일에 쓰는 로직을 넣으면 됩니다.
        // });
    }
}