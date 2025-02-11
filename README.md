# 🌐 온 세상이 주식이야 (온세주)

---

## 📌 MVP 1차 개요 (2025.02.10 ~ 2025.02.15)

### 🚀 시스템 아키텍처

#### 클라이언트-서버 구조
- 클라이언트는 한국투자 API WebSocket과 연결하여 실시간 시세 데이터를 수신
- 주문 요청(매수/매도) 및 체결 내역을 서버와 주고받음
- 체결된 주문 결과를 WebSocket을 통해 실시간으로 반영

#### 서버
- 주문을 우선순위 큐에 저장 후 매칭 알고리즘 실행
- 체결 가능 여부 확인 후 주문을 처리하고 DB에 저장
- WebSocket을 통해 클라이언트에 체결된 주문을 전송

---

## 🛠 기능 상세

### 1. 클라이언트-서버 통신 (실시간 데이터)
- 한국투자 API WebSocket을 통해 실시간 주식 가격 및 주문 정보를 가져와 UI에 반영
- 가격 변동 시 주문 요청(매수/매도) 가능

### 2. 주문 처리 및 체결
- 주문 생성 요청 (매수/매도) → 서버 API 호출
- 체결된 주문 결과를 WebSocket을 통해 실시간 반영

### 3. 서버 - 주문 매칭 및 처리
- 우선순위 큐(Priority Queue) 정렬  
  - 매수 주문: 내림차순(Max Heap)  
  - 매도 주문: 오름차순(Min Heap)
- 매칭된 주문을 체결 후 DB 저장 및 클라이언트로 전송

### 4. 체결된 주문 결과 전송 (WebSocket)
- 주문이 체결되면 즉시 클라이언트에 WebSocket 알림

---

## ⚠️ 고려 사항
### 실시간 데이터 반영
- 변동하는 주식 시세 데이터를 빠르고 정확하게 반영
### 다중 클라이언트 동시 처리
- 여러 사용자의 WebSocket 연결을 효율적으로 관리
### 서버 성능 최적화
- 우선순위 큐 + 비동기 I/O를 활용한 빠른 주문 처리

---

## 💡 결론
1. 클라이언트 → 한국투자 API WebSocket으로 실시간 시세 데이터 수신  
2. 서버 → 주문 매칭 & 체결 후 WebSocket 알림 전송  
3. 우선순위 큐 및 비동기 처리로 고속 주문 체결 구현  
