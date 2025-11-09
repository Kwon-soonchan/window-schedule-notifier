# 윈도우 일정 알리미 (Window Schedule Notifier)

## 📅 1. 프로젝트 소개

이 프로젝트는 우테코 8기 프리코스 4주부터 시작된 오픈 미션입니다.
대학 생활과 프리코스 과제를 병행하며 수동으로 '스티커 메모' 앱에 일정을 관리하던 불편함을 해결하기 위해 시작했습니다.

지정한 시간이 되면 윈도우 알림을 띄워주는 간단한 데스크탑 일정 관리 프로그램입니다.

## ✨ 2. 핵심 기능

* **백그라운드 알림:** `Main.java`가 `schedule.txt` 파일을 1분마다 감시하여, 현재 시간과 일치하는 일정이 있으면 윈도우 알림을 띄웁니다.
* **일정 관리 UI:** `ScheduleManagerUI.java`가 `schedule.txt` 파일을 직접 관리합니다.
    * 새로운 일정 추가 (예: `14:30,과제 제출,오늘까지`)
    * 기존 일정 목록 조회 (시간순으로 자동 정렬)

## ⚙️ 3. 사용 기술

* **Language:** Java 21
* **UI:** Java `Swing` (`java.awt` 포함)
* **Core Logic:**
    * `java.nio.file`: `schedule.txt` 파일 읽기/쓰기
    * `java.awt.SystemTray`: 윈도우 시스템 트레이 및 알림 표시
    * `java.util.Timer`: 1분마다 알림 시간 체크
    * `java.util.Collections`: 일정 목록 정렬

## ⚠️ 4. 개발 과정 및 어려웠던 점 (Troubleshooting)

이 프로젝트를 진행하며 처음 다루어보는 기술들이 많았습니다.

1.  **첫 `SystemTray` 사용:**
    * 윈도우 OS의 네이티브 알림 기능을 Java 코드로 제어하는 `java.awt.SystemTray`를 처음 사용해 보았습니다.
    * `displayMessage()`를 호출하기 위해 `TrayIcon`을 먼저 `tray.add()` 해야 한다는 점, `main` 스레드가 종료되면 알림이 뜨지 않는 문제 등을 해결하며 백그라운드 작업의 생명 주기를 이해했습니다.

2.  **첫 `Swing` UI 개발:**
    * 백엔드 개발 위주로 학습하다가 Java `Swing`을 이용해 처음으로 데스크탑 UI를 구현해 보았습니다.
    * `JFrame`, `JPanel`, `JButton` 등 컴포넌트의 개념과 `addActionListener`를 이용한 이벤트 처리 방식을 익혔습니다.

3.  **원인 불명의 레이아웃 버그:**
    * UI 개발 중 가장 많은 시간을 쏟은 문제입니다. `BorderLayout.NORTH`에 `JPanel(FlowLayout)`을 배치했을 때, `JButton`이 `JTextField`와 겹쳐 보이는 '유령 버그'가 발생했습니다.
    * `Rebuild Project`, `Invalidate Caches` 등 IntelliJ의 캐시를 모두 삭제해도 해결되지 않았습니다.
    * 결국에는, 상단 `JPanel`의 레이아웃 자체를 `FlowLayout`에서 `BorderLayout`으로 강제로 변경하여(`EAST` 영역에 버튼 배치) 레이아웃 충돌 문제를 해결했습니다.

## 🚀 5. 실행 방법

1.  이 프로젝트를 Clone 받습니다.
2.  `schedule.txt` 파일을 프로젝트 루트에 생성합니다. (없으면 자동 생성)
3.  **알림 기능 실행:** `Main.a`를 실행합니다. (백그라운드에서 상시 실행)
4.  **일정 관리:** `ScheduleManagerUI.java`를 실행하여 일정을 추가/조회합니다.