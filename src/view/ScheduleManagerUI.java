package view; // ★ 1. 패키지 선언 필수!

import model.Schedule;
import model.ScheduleRepository;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleManagerUI {

    private static JTextArea scheduleTextArea;

    // ★ Model (저장소) 연결
    private static final ScheduleRepository repository = new ScheduleRepository();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("일정 관리자");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // --- 아이콘 설정 ---
        // (주의: 클래스가 패키지 안에 있어도 /icon.png는 src 루트를 가리킵니다)
        URL iconUrl = ScheduleManagerUI.class.getResource("/icon.png");
        if (iconUrl != null) {
            Image icon = new ImageIcon(iconUrl).getImage();
            frame.setIconImage(icon);
        }

        // --- 1. 상단 패널 (입력용) ---
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("새 일정 (예: 2025-11-17 14:30,제목,내용)");
        JTextField textField = new JTextField(25);
        JButton addButton = new JButton("추가하기");
        inputPanel.add(label, BorderLayout.NORTH);
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // --- 2. 중앙 패널 (목록 표시용) ---
        scheduleTextArea = new JTextArea(20, 35);
        scheduleTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(scheduleTextArea);

        // --- 3. 하단 패널 (삭제용) ---
        JPanel deletePanel = new JPanel(new FlowLayout());
        JLabel deleteLabel = new JLabel("삭제할 시간 (예: 2025-11-17 14:30)");
        JTextField deleteTextField = new JTextField(15);
        JButton deleteButton = new JButton("선택한 시간 삭제");
        deletePanel.add(deleteLabel);
        deletePanel.add(deleteTextField);
        deletePanel.add(deleteButton);

        // --- 4. 전체 레이아웃 설정 ---
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(deletePanel, BorderLayout.SOUTH);

        // --- 5. 초기 데이터 로드 ---
        loadSchedules();

        // --- 6. "추가하기" 버튼 클릭 이벤트 (Controller 로직) ---
        addButton.addActionListener(e -> {
            try {
                String newScheduleLine = textField.getText();

                // 유효성 검사 및 객체 생성
                String[] parts = newScheduleLine.split(",", 3);
                if (parts.length < 3) throw new IllegalArgumentException("형식이 다릅니다.");

                LocalDateTime dateTime = LocalDateTime.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                Schedule newSchedule = new Schedule(dateTime, parts[1], parts[2]);

                // Model에 저장 요청
                List<Schedule> schedules = repository.loadAllSchedules();
                schedules.add(newSchedule);
                repository.saveAllSchedules(schedules);

                // View 갱신
                loadSchedules();
                textField.setText("");
                JOptionPane.showMessageDialog(frame, "일정이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "날짜/시간 형식이 잘못되었습니다.\n(예: yyyy-MM-dd HH:mm)", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "일정 추가 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- 7. "삭제하기" 버튼 클릭 이벤트 (Controller 로직) ---
        deleteButton.addActionListener(e -> {
            try {
                String timePrefixToDelete = deleteTextField.getText().trim();
                if (timePrefixToDelete.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "삭제할 시간을 입력해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Model에서 로드
                List<Schedule> schedules = repository.loadAllSchedules();

                // 삭제 로직 (필터링)
                List<Schedule> remainingSchedules = schedules.stream()
                        .filter(schedule -> !schedule.getTimePrefix().equals(timePrefixToDelete))
                        .collect(Collectors.toList());

                // 변경사항 저장
                repository.saveAllSchedules(remainingSchedules);

                // View 갱신
                loadSchedules();
                deleteTextField.setText("");

                if (schedules.size() == remainingSchedules.size()) {
                    JOptionPane.showMessageDialog(frame, "해당 시간의 일정을 찾을 수 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "일정이 삭제되었습니다.", "삭제 성공", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "삭제 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * View 갱신 메소드
     */
    private static void loadSchedules() {
        List<Schedule> schedules = repository.loadAllSchedules();

        if (schedules.isEmpty()) {
            scheduleTextArea.setText("저장된 일정이 없습니다.");
        } else {
            String sortedContent = schedules.stream()
                    .map(Schedule::toFileFormat)
                    .collect(Collectors.joining(System.lineSeparator()));
            scheduleTextArea.setText(sortedContent);
        }
    }
}