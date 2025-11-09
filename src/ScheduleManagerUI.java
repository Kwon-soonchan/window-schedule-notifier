import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections; // ★★★ 정렬(Sort)을 위해 추가
import java.util.List;

public class ScheduleManagerUI {

    private static JTextArea scheduleTextArea;
    private static final Path schedulePath = Paths.get("schedule.txt");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    /**
     * UI를 생성하고 화면에 표시하는 메소드
     */
    /**
     * UI를 생성하고 화면에 표시하는 메소드
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500); // 창 크기
        frame.setLocationRelativeTo(null); // 화면 중앙
        frame.setLayout(new BorderLayout()); // 혹시 모르니 남겨둡니다.

        // --- 1. ★★★ (진단용 테스트) 상단 패널 레이아웃 변경 ★★★
        JPanel inputPanel = new JPanel(new BorderLayout()); // FlowLayout 대신 BorderLayout 사용

        JLabel label = new JLabel("새 일정 (예: 14:30,제목,내용)");
        JTextField textField = new JTextField(25);
        JButton addButton = new JButton("추가하기");

        // inputPanel에 컴포넌트 추가 (BorderLayout 방식으로)
        inputPanel.add(label, BorderLayout.NORTH);     // 라벨을 패널의 '북쪽'
        inputPanel.add(textField, BorderLayout.CENTER); // 텍스트필드를 '중앙'
        inputPanel.add(addButton, BorderLayout.EAST);    // 버튼을 '동쪽'
        // (모양은 조금 달라질 겁니다)

        // --- 2. 중앙 패널 (목록 표시용) ---
        scheduleTextArea = new JTextArea(20, 35); // 텍스트 영역
        scheduleTextArea.setEditable(false); // 수정 불가
        JScrollPane scrollPane = new JScrollPane(scheduleTextArea); // 스크롤바 감싸기

        // --- 3. 핵심: 전체 레이아웃 설정 ---
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // --- 4. UI 켜질 때 파일 내용 불러오기 ---
        loadSchedules(); // (정렬 기능이 포함된 메소드)

        // --- 5. "추가하기" 버튼 클릭 이벤트 ---
        addButton.addActionListener(e -> {
            String newSchedule = textField.getText();
            if (newSchedule == null || newSchedule.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "내용을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            writeToFile(newSchedule); // 파일 쓰기
            loadSchedules(); // 화면 갱신 (정렬 포함)

            JOptionPane.showMessageDialog(frame, "일정이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            textField.setText(""); // 입력창 비우기
        });

        // --- 6. 창 표시 ---
        frame.setVisible(true);
    }

    /**
     * ★★★ (수정됨) schedule.txt 파일 내용을 '정렬해서' JTextArea에 불러오는 메소드
     */
    private static void loadSchedules() {
        try {
            if (!Files.exists(schedulePath)) {
                scheduleTextArea.setText("schedule.txt 파일이 없습니다.");
                return;
            }

            // 1. 파일의 모든 라인을 List<String>으로 읽어옵니다.
            List<String> lines = Files.readAllLines(schedulePath);

            // 2. ★★★ 라인을 알파벳순(시간순)으로 정렬합니다. ★★★
            Collections.sort(lines);

            // 3. 정렬된 라인들을 하나의 문자열로 합칩니다. (줄바꿈 포함)
            String sortedContent = String.join(System.lineSeparator(), lines);

            // 4. JTextArea에 텍스트 설정
            scheduleTextArea.setText(sortedContent);

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