## ERD

---

![image](https://github.com/user-attachments/assets/6043fc94-f392-41b5-b856-ac7fdd54b9c4)

![image](https://github.com/user-attachments/assets/9f3fbce5-c770-4e64-a8de-58b8049fe9b4)

아래 2가지 요구 사항을 바탕으로 각각의 테이블을 만들었습니다.
- 특강 조회 -> 특강(Lecture) 
- 특강 신청 -> 특강신청(Lecture Application)

특강(Lecture)은 제목과 시작 시간을 통해 최소한의 구분이 된다고 생각하였고 'step3의 제한인원'을 고려하여 최대 정원도 컬럼으로 넣게 되었습니다.

특강 신청(Lecture Application)은 특강의 index와 신청자의 id를 통해서도 최소한의 기록이 된다고 판단했습니다.  
'step4 중복 신청 제한'을 대비하여 중복된 레코드의 저장을 방지하고자 유니크 키도 설정하게 되었습니다.
