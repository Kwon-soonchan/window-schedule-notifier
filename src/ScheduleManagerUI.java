import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List; // ★★★ 파일 읽기를 위해 추가

public class ScheduleManagerUI {

    // JTextArea를 다른 메소드에서도 접근할 수 있게 필드로 뺍니다.
    private static JTextArea scheduleTextArea;
    private static final Path schedulePath = Paths.get("schedule.txt"); // 파일 경로도 필드로

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500); // ★★★ 창 크기를 더 크게 조정
        frame.setLocationRelativeTo(null);

        // --- 1. 상단 패널 (입력용) ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("새 일정 (예: 14:30,제목,내용)");
        JTextField textField = new JTextField(25);
        JButton addButton = new JButton("추가하기");

        inputPanel.add(label);
        inputPanel.add(textField);
        inputPanel.add(addButton);

        // --- 2. ★★★ 중앙 패널 (목록 표시용) ★★★ ---
        scheduleTextArea = new JTextArea(20, 35); // 20줄, 35 글자 크기
        scheduleTextArea.setEditable(false); // ★ 사용자가 수정 못하게 잠그기
        JScrollPane scrollPane = new JScrollPane(scheduleTextArea); // 스크롤바 추가

        // --- 3. 전체 레이아웃 설정 ---
        // 창의 '북쪽'에 inputPanel, '중앙'에 scrollPane을 배치
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // --- 4. (필수) UI가 켜질 때 파일 내용을 불러오는 메소드 실행 ---
        loadSchedules();

        // --- 5. "추가하기" 버튼 클릭 이벤트 ---
        addButton.addActionListener(e -> {
            String newSchedule = textField.getText();
            if (newSchedule == null || newSchedule.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "내용을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            writeToFile(newSchedule); // 파일에 쓰기

            // ★★★ (중요) 파일에 쓴 후, 화면 갱신 ★★★
            loadSchedules();

            JOptionPane.showMessageDialog(frame, "일정이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            textField.setText("");
        });

        // --- 6. 창 표시 ---
        frame.setVisible(true);
    }

    /**
     * schedule.txt 파일 내용을 JTextArea에 불러오는(load) 메소드
     */
    private static void loadSchedules() {
        try {
            if (!Files.exists(schedulePath)) {
                scheduleTextArea.setText("schedule.txt 파일이 없습니다.");
                return;
            }

            // 파일의 모든 내용을 String으로 한 번에 읽어오기
            String content = Files.readString(schedulePath);
            scheduleTextArea.setText(content); // JTextArea에 텍스트 설정

        } catch (IOException e) {
            scheduleTextArea.setText("일정을 불러오는 중 오류 발생:\n" + e.getMessage());
        }
    }

    /**
     * 파일에 쓰는 메소드 (이전과 동일)
     */
    private static void writeToFile(String text) {
        try {
            Files.writeString(schedulePath,
                    text + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("파일 쓰기 중 오류 발생: " + e.getMessage());
        }
    }
}