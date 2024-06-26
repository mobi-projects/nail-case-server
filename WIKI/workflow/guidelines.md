# 📑 Project Contribution Guidelines

## 🚀 GitHub Flow

GitHub Flow는 단순하면서도 강력한 브랜칭 모델입니다:

1. **main 브랜치는 항상 배포 가능한 상태로 유지**: main 브랜치는 언제든지 배포될 수 있어야 합니다.
2. **기능 브랜치 생성**: 새로운 기능이나 버그 수정을 위해 main 브랜치로부터 기능 브랜치를 생성합니다.
3. **주기적인 커밋**: 작은 단위로 자주 커밋하여 작업 내용을 기록합니다.
4. **풀 리퀘스트 (PR)**: 작업이 완료되면 기능 브랜치에서 main 브랜치로 풀 리퀘스트를 생성하여 코드 리뷰를 요청합니다.
5. **리뷰 및 승인**: 팀원들이 코드를 리뷰하고 승인하면, main 브랜치에 병합합니다.
6. **배포**: main 브랜치에 병합된 코드는 자동화된 테스트를 거쳐 Gitbug Actions로 자동으로 배포합니다.

## 📝 Jira Integration

1. 이슈 트래킹: 모든 작업 항목은 Jira 티켓을 통해 관리됩니다.
2. Jira 티켓 생성: 새로운 기능이나 버그 수정을 위해 작업을 시작하기 전에 Jira 티켓을 생성합니다.
3. 커밋 메시지: 커밋 메시지에 Jira 티켓 번호를 포함하여 커밋과 Jira 티켓을 연동합니다.

## 📝 Branch Naming Rules

- 브랜치 이름은 변경 사항의 목적을 명확히 하기 위해 일관된 형식을 유지합니다.
- 예시: feat/, bug/, hotfix/ 등의 접두사와 지라 티켓 번호를 포함합니다.

```bash
feat/NAILCASE-123-add-login-api
bug/NAILCASE-456-fix-login-error
hotfix/NAILCASE-789-critical-bug-fix
```

## 🧩 Coding Convention

프로젝트의 일관된 코딩 스타일을 유지하기 위해 코딩 표준을 준수합니다. 네이버의 자바 코딩 컨벤션을 따릅니다:

- 네이버 자바 코딩 컨벤션: [네이버 자바 코딩 컨벤션](https://naver.github.io/hackday-conventions-java)
- 코딩 스타일 가이드, 네이밍 규칙, 주석 작성법 등을 포함합니다.
- 코딩 표준을 준수하여 코드의 가독성과 유지보수성을 높입니다.

### 세팅 방법

1. 해당 파일들을 다운로드합니다.

- [naver-intellij-formatter.xml](https://github.com/naver/hackday-conventions-java/blob/master/rule-config/naver-intellij-formatter.xml
  )
- [naver-checkstyle-rules.xml](https://github.com/naver/hackday-conventions-java/blob/master/rule-config/naver-checkstyle-rules.xml)
- [naver-checkstyle-suppressions.xml](https://github.com/naver/hackday-conventions-java/blob/master/rule-config/naver-checkstyle-suppressions.xml)

2. [setting-code-convention.md](setting-code-convention.md)에 설명을 따라서 세팅을 합니다.

## ✉️ Commit Message Style Guide

좋은 커밋 메시지는 협업을 원활하게 하고 프로젝트의 히스토리를 추적하는 데 도움이 됩니다. 아래와 같은 구조로 커밋 메시지를 작성해주세요:

### Format

```bash
:emoji: type(scope): title (JIRA-ID)
\n
body(option)
\n
footer(option)
```

### Components

- **emoji**: (optional) 커밋 메시지의 시작 부분에 이모지를 추가하여 커밋의 의도나 성격을 시각적으로 구분할 수 있습니다. 이는 변경 사항의 목적이나 영향을 한눈에 빠르게 식별하는 데 도움이
  됩니다.
- **type**: 커밋하는 변경 사항의 유형별로 정리하는 데 도움이 됩니다.
- **scope** (optional): 변경 사항이 영향을 미치는 코드베이스의 부분을 지정하는 키워드
- **title**: 변경 사항에 대한 간결한 설명. 대문자로 시작하고, 마침표 없이, 50자 이내로 요점을 담습니다.
- **body** (optional): 변경 사항과 그 이유에 대한 상세 설명. 복잡한 변경 사항일수록 더 자세한 본문을 작성합니다.
- **footer** (optional): 지라 티켓 번호 또는 기타 관련 정보에 대한 참조.

### Emojis & Types

- ✨ feat: 새로운 기능을 도입합니다.
- 🐛 fix: 버그를 수정합니다.
- 💥 !BREAKING CHANGE: 이전 버전과 호환되지 않는 변경 사항을 도입합니다.
- 🚑 !HOTFIX: 중요한 버그를 수정합니다.
- 💄 style: 코드 형식을 변경합니다.
- ♻️ refactor: 버그 수정이나 기능 추가가 아닌 코드 변경입니다.
- ⚗️ perf: 성능을 향상합니다.
- 📝 docs: 문서를 추가하거나 업데이트합니다.
- ✅ test: 테스트를 추가하거나 업데이트합니다.
- 🧪 test: 실패한 테스트를 추가하거나 업데이트합니다.
- 🧹 chore: 소스나 테스트 파일을 수정하지 않는 작업입니다.
- 🔀 rename: 파일, 변수, 함수 등의 이름을 변경합니다.
- 🔥 remove: 코드, 파일, 기능을 제거합니다.
- ➕ add: 새로운 의존성을 추가합니다.
- ➖ remove: 의존성을 제거합니다.
- 💡 postComment: 소스 코드에 주석을 추가하거나 업데이트합니다.
- 🔖 release: 릴리스하거나 버전 태그를 추가합니다.
- 🚀 deploy: 배포합니다 :)
- 🗃️ schema: 엔티티나 데이터베이스 관련 파일을 추가하거나 업데이트합니다.

### Footer

- resolving: 커밋으로 해결 중인 이슈를 나타냅니다.
- resolves: 커밋으로 해결된 이슈를 나타냅니다.
- ref: 추가적인 맥락이나 참고 정보를 제공합니다.

### Example

```bash
:sparkles: feat(login): Add login API (NAILCASE-123)

- 로그인 API 추가
- 사용자가 이메일과 비밀번호로 로그인할 수 있도록 구현
- 성공 및 실패 응답 처리
- JWT 토큰 발급 기능 포함

resolve: [NAILCASE-456](https://nailcase.atlassian.net/browse/NAILCASE-456)
ref: [NAILCASE-123](https://nailcase.atlassian.net/browse/NAILCASE-123)
```

## 🔀 Merge 전략

프로젝트의 일관성을 유지하기 위해 기본적으로 `Create a merge commit` 전략을 사용합니다. 하지만 상황에 따라 아래의 다른 전략을 사용할 수도 있습니다:

1. **Create a merge commit**: 모든 커밋 히스토리를 유지하면서 병합 커밋을 생성합니다.
2. **Squash and merge**: 여러 커밋을 하나의 커밋으로 합쳐서 병합합니다. 이는 브랜치 히스토리를 단순화하는 데 유용합니다.
3. **Rebase and merge**: 병합 대상 브랜치의 커밋을 리베이스하여 커밋 히스토리를 재정렬한 후 병합합니다. 이는 직선적인 커밋 히스토리를 유지하는 데 유용합니다.

## 🔄 Pull Request

- PR은 작은 단위로 자주 생성하여 변경 사항을 쉽게 리뷰할 수 있도록 합니다.
- PR 제목과 설명은 변경 사항을 명확히 설명합니다.
- PR 템플릿을 사용하여 일관된 형식으로 PR을 작성합니다.
- [PR Template](../../.github/PULL_REQUEST_TEMPLATE.md)

## 🕵️ Code Review Conventions 🕵️

코드 리뷰는 개발 과정에서 중요한 부분으로, 고품질 코드를 보장하고 협력이 중요합니다. 효과적인 코드 리뷰를 위해 다음 규칙을 준수해주세요:

- **존중하기**: 친절하고 건설적인 방식으로 피드백을 제공하세요. 목표는 팀의 긍정적인 환경을 조성하면서 코드를 개선하는 것입니다.
- **구체적으로**: 변경을 제안하는 특정 코드 라인을 참조하세요. 제안의 맥락과 이유를 제공하여 실행 가능하도록 만드세요.
- **질문하기**: 변경을 지시하기보다는 개발자의 관점을 이해하기 위해 명확한 질문을 하세요. 이러한 접근법은 통찰력 있는 토론과 더 나은 결정을 이끌어낼 수 있습니다.
- **예시 제공하기**: 변경을 제안할 때, 코드 조각이나 관련 리소스 링크를 포함하여 명확하게 설명하세요.
- **신속히 응답하기**: 지정된 시간 내(예: 24-48시간) 리뷰를 완료하도록 노력하세요. 신속한 피드백은 개발 과정을 원활하게 하고 동료의 시간을 존중하는 것을 보여줍니다.
- **일관성 확인하기**: 코드가 프로젝트의 코딩 표준과 규칙을 준수하는지 확인하세요. 일관성은 유지보수성과 가독성의 핵심입니다.
- **로컬 테스트**: 가능하다면 변경 사항을 로컬에서 테스트하여 기대한 대로 작동하는지 확인하세요. 이를 통해 메인 브랜치에 병합되는 잠재적인 문제를 방지할 수 있습니다.
- **좋은 작업 인정하기**: 긍정적인 피드백은 건설적인 피드백만큼 중요합니다. 코드 리뷰 과정에서 좋은 관행과 해결책을 인식하고 칭찬하세요.
- **모든 팀원의 리뷰 받기**: 모든 팀원의 리뷰를 받아야만 병합이 가능합니다.
