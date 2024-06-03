## 🍽️ Naver Code Convention 세팅하기

### 1. 🎿 Scheme 세팅

1️⃣ Intellij IDEA에서 Settings 메뉴 열기

2️⃣ Editor에서 Code Style - Java 클릭

3️⃣ Scheme에서 오른쪽에 위치한 톱니바퀴를 클릭

4️⃣ import Scheme에서 intellij IDEA code style XML 클릭
</br>
</br>
<img width="947" alt="scheme_1" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/753a28af-d5a2-4dfb-861f-9508f615d8af">

5️⃣ naver-intelij-formatter.xml 파일을 선택한 후 확인
</br>
</br>
<img width="291" alt="스크린샷 2024-06-03 23 44 29" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/c45029ac-c8a9-440b-960a-c76d9656594a">

6️⃣ 이름은 그대로 저장해도 상관없음.

### 2. 🚘 Actions on Save(저장시 자동 적용)

1️⃣ Intellij IDEA에서 Settings 메뉴 열기

2️⃣ Tools에 Actions on Save 클릭

3️⃣ Reformat code(저장시 포맷 적용), Optimize imports(저장 시 불필요 import제거) 체크
</br>
</br>
<img width="997" alt="스크린샷 2024-06-03 23 58 15" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/d1d36157-c63d-4db8-82ed-15baa3da7945">

### 3. ✅ Checkstyle 적용

1️⃣ Intellij IDEA에서 Settings 메뉴 열기

2️⃣ Tools에서 Checkstyle 선택

3️⃣ Treat Checkstyle errors as warnings를 체크
</br>
</br>
<img width="1000" alt="스크린샷 2024-06-04 00 01 01" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/83fe3d60-8112-4583-a276-371499c110ca">

4️⃣ Configuration File에서 + 버튼을 클릭하여 Use a Local Checkstyle File을 선택
</br>
</br>
<img width="989" alt="스크린샷 2024-06-04 00 02 21" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/9c9b154a-b83a-44c6-9937-8960a1d1c3a1">

5️⃣ naver-checkstyle-rules.xml를 선택 후 Next클릭
</br>
</br>

<img width="530" alt="스크린샷 2024-06-04 00 01 41" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/5b7f1db5-4451-4bba-bce5-22992b845a28">

6️⃣ suppressionFile 변수를 설정에서는 Value에 naver-checkstyle-suppressions.xml입력
</br>
</br>

<img width="529" alt="스크린샷 2024-06-04 00 03 16" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/800a3ff7-b149-4cbd-8e93-9bc037d38e9d">

7️⃣ 방금 생성한 Configuration File에 Active 체크
</br>
</br>
<img width="710" alt="스크린샷 2024-06-04 00 06 14" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/822c1e17-7d07-4255-94d4-6145eb9386ad">

