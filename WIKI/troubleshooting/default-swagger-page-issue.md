## 문제 상황 1: 스웨거 페이지에 예상치 못한 API가 표시됨

<br>
<br>

<img width="1211" alt="스크린샷 2024-07-03 00 07 33" src="https://github.com/mobi-projects/nail-case-server/assets/96242198/53261155-5d55-43f6-aaf7-25ecddd50e92">

<br>

로컬 환경에서 서버를 작동시킨 후, Spring Docs로 생성된 스웨거 페이지에 접속했을 때, 컨트롤러에서 생성하지 않은 'petstore' 관련 API가 표시되는 문제가 발생했습니다.

----

## 원인

Spring Docs는 기본적으로 제공하는 디폴트 페이지가 설정되어 있습니다. 이 때문에 사용자가 설정한 페이지 대신 기본 페이지가 표시되었습니다.

## 해결 방안

<br>
<br>

![image](https://github.com/mobi-projects/nail-case-server/assets/96242198/788a45b7-a1e4-4034-aa58-241d26558f79)

위와 같은 설정을 통해서 petstore와 같은 기본 페이지가 표시되지 않도록 설정하였습니다.

---

## 문제 상황 2: API 문서가 제대로 표시되지 않음

<br>
<br>

![image](https://github.com/mobi-projects/nail-case-server/assets/96242198/c6a1574f-2042-41d8-b314-4292adb2e1ca)

<br>

첫 번째 문제를 해결한 후, 기존에 생성했던 API 문서들이 표시되지 않았습니다. 또한 GitHub Pages와 연결한 API 문서도 빌드 과정에서 제대로 작동하지 않았습니다.

---

## 원인

프로젝트에서 ResponseEntityWrapperAdvice를 사용해 모든 컨트롤러의 응답을 가로채어 REST API 응답 형식을 통일하게 설정하였습니다. 하지만, 이 로직이 스웨거의 자동 문서 생성 기능에 영향을
미쳐, 예상과 다르게 응답 객체가 감싸져 반환되었습니다. 이로 인해 스웨거가 단일 객체가 아닌 감싸진 응답 객체를 올바르게 해석하지 못하고 문서를 생성하지 못했습니다.

## 해결방안

스웨거와 관련된 경로의 응답을 조작하지 않도록 설정하여 스웨거 UI가 제대로 렌더링되지 않는 문제를 예방하기 위해 다음과 같이 경로를 예외 처리하였습니다.

<br>
<br>
``
if (pathMatcher.match("/**", path)) {
return body;
}
``
<br>
<br>
이 설정은 스웨거 관련 요청에 대해서는 ResponseEntityWrapperAdvice의 로직이 적용되지 않도록 하여, API 문서가 정상적으로 렌더링되도록 합니다.