import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleManagerUI {

    private static JTextArea scheduleTextArea;

    // ★★★ (수정) 사용자 홈 디렉토리에 공용 파일 경로 생성 ★★★
    private static final Path schedulePath = Paths.get(
            System.getProperty("user.home"), "window_schedule_notifier.txt");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // frame.setSize(450, 580); // ★★★ (삭제)

        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // --- 아이콘 설정 ---
        URL iconUrl = ScheduleManagerUI.class.getResource("/icon.png");
        Image icon = new ImageIcon(iconUrl).getImage();
        frame.setIconImage(icon);

        // --- 1. 상단 패널 (입력용) ---
        // (이전과 동일)
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("새 일정 (예: 2025-11-17 14:30,제목,내용)");
        JTextField textField = new JTextField(25);
        JButton addButton = new JButton("추가하기");
        inputPanel.add(label, BorderLayout.NORTH);
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // --- 2. 중앙 패널 (목록 표시용) ---
        // (이전과 동일)
        scheduleTextArea = new JTextArea(20, 35);
        scheduleTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(scheduleTextArea);

        // --- 3. 하단 패널 (삭제용) ---
        // (이전과 동일)
        JPanel deletePanel = new JPanel(new FlowLayout());
        JLabel deleteLabel = new JLabel("삭제할 시간 (예: 2025-11-17 14:30)");
        JTextField deleteTextField = new JTextField(15);
        JButton deleteButton = new JButton("선택한 시간 삭제");
        deletePanel.add(deleteLabel);
        deletePanel.add(deleteTextField);
        deletePanel.add(deleteButton);

        // --- 4. 전체 레이OUT 설정 ---
        // (이전과 동일)
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(deletePanel, BorderLayout.SOUTH);

        // --- 5. UI 켜질 때 파일 내용 불러오기 ---
        loadSchedules();

        // --- 6. "추가하기" 버튼 클릭 이벤트 ---
        // (이전과 동일)
        addButton.addActionListener(e -> {
            String newSchedule = textField.getText();
            if (newSchedule == null || newSchedule.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "내용을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            saveNewSchedule(newSchedule, frame);
            loadSchedules();
            textField.setText("");
        });

        // --- 7. "삭제하기" 버튼 클릭 이벤트 ---
        // (이전과 동일)
        deleteButton.addActionListener(e -> {
            String timeToDelete = deleteTextField.getText();
            if (timeToDelete == null || timeToDelete.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "삭제할 시간을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteSchedule(timeToDelete, frame);
            loadSchedules();
            deleteTextField.setText("");
        });

        // --- ★★★ (수정) pack()으로 크기 자동 조절 ★★★ ---
        frame.pack(); // 컴포넌트 크기에 맞춰 창 크기 자동 조절

        frame.setVisible(true);
    }

    // (saveNewSchedule, deleteSchedule, loadSchedules 메소드는
    //  schedulePath가 전역 변수로 바뀌었으므로 완벽하게 동일하게 작동합니다. 수정X)

    private static void saveNewSchedule(String newSchedule, Component parent) {
        try {
            List<String> allLines;
            if (Files.exists(schedulePath)) {
                allLines = new ArrayList<>(Files.readAllLines(schedulePath));
            } else {
                allLines = new ArrayList<>();
            }
            allLines.add(newSchedule);
            Collections.sort(allLines);
            Files.write(schedulePath, allLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            JOptionPane.showMessageDialog(parent, "일정이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "파일 저장 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            System.out.println("파일 쓰기 중 오류 발생: " + e.getMessage());
        }
    }

    private static void deleteSchedule(String timeToDelete, Component parent) {
        String timePrefix = timeToDelete.trim();
        List<String> remainingLines = new ArrayList<>();
        boolean deleted = false;
        try {
            if (!Files.exists(schedulePath)) return;
            List<String> allLines = Files.readAllLines(schedulePath);
            for (String line : allLines) {
                if (line.trim().startsWith(timePrefix)) {
                    deleted = true;
                } else {
                    remainingLines.add(line);
                }
            }
            Files.write(schedulePath, remainingLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            if (deleted) {
                JOptionPane.showMessageDialog(parent, "'" + timePrefix + "'로 시작하는 일정이 삭제되었습니다.", "삭제 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "'" + timePrefix + "'로 시작하는 일정을 찾을 수 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "파일 삭제 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void loadSchedules() {
        try {
            if (!Files.exists(schedulePath)) {
                scheduleTextArea.setText("저장된 일정이 없습니다.\n(파일 위치: " + schedulePath.toString() + ")");
                return;
            }
            List<String> lines = Files.readAllLines(schedulePath);
            Collections.sort(lines);
            if (lines.isEmpty()) {
                scheduleTextArea.setText("저장된 일정이 없습니다.");
            } else {
                String sortedContent = String.join(System.lineSeparator(), lines);
                scheduleTextArea.setText(sortedContent);
            }
        } catch (IOException e) {
            scheduleTextArea.setText("일정을 불러오는 중 오류 발생:\n" + e.getMessage());
        }
    }
}