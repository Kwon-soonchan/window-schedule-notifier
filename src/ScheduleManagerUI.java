import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList; // ★★★ 삭제 로직을 위해 추가
import java.util.Collections;
import java.util.List;
import java.net.URL;

public class ScheduleManagerUI {

    private static JTextArea scheduleTextArea;
    private static final Path schedulePath = Paths.get("schedule.txt");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 550); // ★★★ 창 세로 크기를 550으로 늘림
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // --- ★★★ 아이콘 설정 (신규) ★★★ ---
        URL iconUrl = ScheduleManagerUI.class.getResource("/icon.png"); // ★★★ "/resource" 삭제 ★★★
        Image icon = new ImageIcon(iconUrl).getImage();
        frame.setIconImage(icon);

        // --- 1. 상단 패널 (입력용) ---
        // (이전과 동일)
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("새 일정 (예: 14:30,제목,내용)");
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
        JPanel deletePanel = new JPanel(new FlowLayout()); // 왼쪽->오른쪽
        JLabel deleteLabel = new JLabel("삭제할 시간 (예: 21:55)");
        JTextField deleteTextField = new JTextField(10); // 10글자 크기
        JButton deleteButton = new JButton("선택한 시간 삭제");

        deletePanel.add(deleteLabel);
        deletePanel.add(deleteTextField);
        deletePanel.add(deleteButton);

        // --- 4. 전체 레이아웃 설정 (수정) ---
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH); // 상단
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER); // 중단
        frame.getContentPane().add(deletePanel, BorderLayout.SOUTH); // ★ 하단

        // --- 5. UI 켜질 때 파일 내용 불러오기 ---
        // (이전과 동일)
        loadSchedules();

        // --- 6. "추가하기" 버튼 클릭 이벤트 ---
        // (이전과 동일)
        addButton.addActionListener(e -> {
            String newSchedule = textField.getText();
            if (newSchedule == null || newSchedule.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "내용을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            writeToFile(newSchedule);
            loadSchedules();
            JOptionPane.showMessageDialog(frame, "일정이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            textField.setText("");
        });

        // --- 7. "삭제하기" 버튼 클릭 이벤트 ---
        deleteButton.addActionListener(e -> {
            String timeToDelete = deleteTextField.getText(); // 1. 삭제할 시간 가져오기

            if (timeToDelete == null || timeToDelete.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "삭제할 시간을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. 파일에서 해당 시간의 라인 삭제
            deleteSchedule(timeToDelete, frame);

            // 3. UI 갱신
            loadSchedules();
            deleteTextField.setText(""); // 입력창 비우기
        });

        // --- 8. 창 표시 ---
        frame.setVisible(true);
    }

    /**
     * ★★★ (신규) 파일에서 특정 시간으로 시작하는 라인을 삭제하는 메소드 ★★★
     */
    private static void deleteSchedule(String timeToDelete, Component parent) {
        // (예: "21:55" 같이 시간만 입력해도 "21:55,..." 라인을 찾기 위함)
        String timePrefix = timeToDelete.trim();
        List<String> remainingLines = new ArrayList<>(); // 살아남을 라인들
        boolean deleted = false; // 한 줄이라도 지웠는지 확인

        try {
            if (!Files.exists(schedulePath)) return; // 파일 없으면 종료

            // 1. 모든 라인을 읽어온다
            List<String> allLines = Files.readAllLines(schedulePath);

            // 2. "살아남을" 라인만 골라서 newList에 추가한다
            for (String line : allLines) {
                if (line.trim().startsWith(timePrefix)) {
                    // ★ 삭제할 라인 ( newList에 추가 안 함 )
                    deleted = true;
                } else {
                    // ★ 살아남을 라인
                    remainingLines.add(line);
                }
            }

            // 3. "살아남은" 라인들로 파일을 덮어쓴다
            // StandardOpenOption.TRUNCATE_EXISTING: 기존 내용을 다 지우고 새로 쓰기
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


    /**
     * (수정됨) schedule.txt 파일 내용을 '정렬해서' JTextArea에 불러오는 메소드
     */
    private static void loadSchedules() {
        try {
            if (!Files.exists(schedulePath)) {
                scheduleTextArea.setText("schedule.txt 파일이 없습니다.");
                return;
            }
            List<String> lines = Files.readAllLines(schedulePath);
            Collections.sort(lines);

            // ★ (개선) 리스트가 비어있을 때 처리
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

    /**
     * (이전과 동일) 파일에 쓰는 메소드
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