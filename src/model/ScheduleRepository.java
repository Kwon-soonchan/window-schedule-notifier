package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// (Model) 파일 입출력을 전담하는 저장소 클래스
public class ScheduleRepository {

    // 공용 파일 경로 (Main과 UI가 동일하게 바라봄)
    private static final Path schedulePath = Paths.get(
            System.getProperty("user.home"), "window_schedule_notifier.txt");

    /**
     * 파일에서 모든 일정을 읽어와 Schedule 객체 리스트로 반환
     */
    public List<Schedule> loadAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        if (!Files.exists(schedulePath)) {
            return schedules; // 빈 리스트 반환
        }

        try {
            List<String> lines = Files.readAllLines(schedulePath);
            for (String line : lines) {
                try {
                    // (파싱 실패한 라인은 무시하고 로깅)
                    schedules.add(new Schedule(line));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    System.out.println("Invalid schedule line skipped: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading schedules: " + e.getMessage());
        }

        Collections.sort(schedules); // (Schedule 클래스의 compareTo 기준 정렬)
        return schedules;
    }

    /**
     * Schedule 객체 리스트를 받아 파일 전체를 덮어쓰기
     */
    public void saveAllSchedules(List<Schedule> schedules) throws IOException {
        // Schedule 객체를 파일 포맷(String)으로 변환
        List<String> lines = schedules.stream()
                .map(Schedule::toFileFormat) // ( s -> s.toFileFormat() )
                .collect(Collectors.toList());

        // 파일 덮어쓰기
        Files.write(schedulePath, lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }
}