# **Pin-Pong**

## Preview

![PinPongIcon](https://github.com/user-attachments/assets/faf63719-26a9-4b5b-afca-7ef776841a33)

### **정녕 이게 당신의 최선입니까…?**

**질문하고 싶을 땐 핀 꽂으러 가기**

여러분들은 코딩하면서 내가 작성한 코드가
정말 최고의 성능을 갖고, 리팩터링이 필요없는 코드일까 궁금하신 적 있지 않으신가요?

Github에 올린 내 코드를 다른 사람들에게 공유하고 질문하며 이를 통해 코드 질문을 할 수 있도록 도와주는 웹 서비스인 ***PinPong***을 이용해보세요!

## Team

문재혁 https://github.com/Kiriiin

김진영 https://github.com/KimJinYeongZ

## TechStack

### FrontEnd

- React

### BackEnd

- Spring Boot
- Cloud : AWS

## Details

**로그인 / 로그아웃**
![1](https://github.com/user-attachments/assets/57c4d460-e88e-44ed-8152-d3599beb5ec4)

- Github API를 이용해 사용자의 Github 계정으로 로그인
- Github Id 와 Github Image를 불러옴
- 첫 로그인 시 기본 핀 5개 지급
- 매일 출석 핀 1개 지급


**메인 / 질문**
![2](https://github.com/user-attachments/assets/359c99fc-3904-4b4e-9cdb-951e6638d4c1)

- 사용자들이 올린 질문 글 목록
- 작성자 Github ID, Github Image, 질문 글에 사용된 기술 스택, 채택 여부, 좋아요 개수도 함께 표시
- “내 태그로 글 보기” 클릭 시 내가 설정한 기술 스택을 가진 질문 글들만 표시되도록 필터링 가능


**핀 꽂으러 가기**
![3](https://github.com/user-attachments/assets/55024b7c-0595-45c8-a924-549358f85d5e)

- 사용자의 핀을 1개 소모하여 질문 글을 작성할 수 있음
- 질문 글에 맞는 기술 스택을 추가할 수 있음
- 질문하고 싶은 사용자의 Pull Request주소를 입력하면 해당하는 Commit 들의 코드를 불러옴
- 질문하고 싶은 내용을 작성한 후 핀 꽂기 클릭
- 좋은 질문이라고 생각하는 글에 좋아요를 누를 수 있음


**댓글 작성**
![4](https://github.com/user-attachments/assets/9debaa58-2eb3-431e-8569-fa54f3db96b3)

- 댓글 작성을 통해 질문 글에 댓글을 달 수 있음
- 작성자가 사용자의 댓글이 가장 유익했다고 생각하는 댓글 하나를 채택할 수 있음
- 채택된 댓글의 작성자는 채택이 되면 핀 10개 지급
- 질문 글에 채택된 댓글이 있으면 채택된 글로 변경됨


**랭킹**
![5](https://github.com/user-attachments/assets/0d55c1c8-0910-4e06-bf9d-800f55e30794)

- 핀 개수가 많은 순으로 랭킹 표시
- 랭킹을 표시함으로써 사용자들의 답변 장려


**마이페이지**
![6](https://github.com/user-attachments/assets/108df2b7-26c6-4ce4-9f4e-93d1e481ebbf)

- 사용자 Github ID와 Image, 사용자가 쓴 질문 글 표시
- 사용자가 관심 있거나 사용 가능한 기술 스택 지정 가능
- 사용자가 현재 갖고 있는 핀 개수 표시

