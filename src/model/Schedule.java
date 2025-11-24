package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// (Model) 일정 데이터 하나를 표현하는 클래스 (DTO 또는 VO)
public class Schedule implements Comparable<Schedule> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime dateTime;
    private final String title;
    private final String message;

    // "2025-11-17 14:30,제목,내용" 형식의 문자열로 생성
    public Schedule(String line) {
        String[] parts = line.split(",", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid schedule line format: " + line);
        }
        this.dateTime = LocalDateTime.parse(parts[0], FORMATTER);
        this.title = parts[1];
        this.message = parts[2];
    }

    // (UI에서 사용할 생성자)
    public Schedule(LocalDateTime dateTime, String title, String message) {
        this.dateTime = dateTime;
        this.title = title;
        this.message = message;
    }

    // 파일에 저장할 형식 (예: "2025-11-17 14:30,제목,내용")
    public String toFileFormat() {
        return String.format("%s,%s,%s", dateTime.format(FORMATTER), title, message);
    }

    // (Getter 메소드들)
    public LocalDateTime getDateTime() { return dateTime; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimePrefix() { return dateTime.format(FORMATTER); } // 삭제를 위해

    // Collections.sort()를 위한 비교 로직
    @Override
    public int compareTo(Schedule other) {
        return this.dateTime.compareTo(other.getDateTime());
    }
}