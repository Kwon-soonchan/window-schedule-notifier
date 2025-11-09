import javax.swing.*;
import java.awt.*;
import java.io.IOException; // ★★★ 파일 쓰기를 위해 추가
import java.nio.file.Files; // ★★★ 파일 쓰기를 위해 추가
import java.nio.file.Path; // ★★★ 파일 쓰기를 위해 추가
import java.nio.file.Paths; // ★★★ 파일 쓰기를 위해 추가
import java.nio.file.StandardOpenOption; // ★★★ 파일 쓰기를 위해 추가

public class ScheduleManagerUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // --- 1~5번까지는 이전 코드와 동일 ---
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("새 일정 (예: 14:30,제목,내용)");
        JTextField textField = new JTextField(25);
        JButton addButton = new JButton("추가하기");

        panel.add(label);
        panel.add(textField);
        panel.add(addButton);

        frame.add(panel);
        frame.setVisible(true);

        // --- 7. ★★★ "추가하기" 버튼 클릭 이벤트 (활성화) ★★★ ---
        addButton.addActionListener(e -> {
            String newSchedule = textField.getText(); // 1. 텍스트필드 내용 가져오기

            // 2. 내용이 비어있지 않은지 간단히 확인
            if (newSchedule == null || newSchedule.trim().isEmpty()) {
                // 사용자에게 경고창 띄우기
                JOptionPane.showMessageDialog(frame,
                        "내용을 입력해주세요!",
                        "경고",
                        JOptionPane.WARNING_MESSAGE);
                return; // 아무것도 안 함
            }

            // 3. 파일에 쓰기
            writeToFile(newSchedule);

            // 4. 사용자에게 성공 알림창 띄우기
            JOptionPane.showMessageDialog(frame,
                    "'" + newSchedule + "'\n일정이 성공적으로 추가되었습니다.",
                    "성공",
                    JOptionPane.INFORMATION_MESSAGE);

            // 5. 입력창 비우기
            textField.setText("");
        });
    }

    /**
     * 입력받은 텍스트(String)를 schedule.txt 파일 맨 끝에 추가하는 메소드
     */
    private static void writeToFile(String text) {
        Path schedulePath = Paths.get("schedule.txt");

        try {
            // ★★★ 파일에 내용을 추가(APPEND)합니다 ★★★
            // StandardOpenOption.CREATE: 파일이 없으면 새로 만들기
            // StandardOpenOption.APPEND: 파일의 맨 끝에 내용 추가하기 (덮어쓰지 않음)
            Files.writeString(schedulePath,
                    text + System.lineSeparator(), // 텍스트 + 줄바꿈
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

        } catch (IOException e) {
            // 실제 앱이라면 여기서 사용자에게 에러를 알려줘야 합니다.
            System.out.println("파일 쓰기 중 오류 발생: " + e.getMessage());
            // (e.printStackTrace();) // 디버깅용
        }
    }
}