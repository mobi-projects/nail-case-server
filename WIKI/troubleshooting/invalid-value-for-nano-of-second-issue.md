## 문제 상황 : Invalid NanoOfSecond Value Exception 예외 발생

<br>
<br>
12AM과 4AM 사이에 자바 애플리케이션에서 데이터 처리를 시도할 때, NanoOfSecond 값이 유효하지 않아 다음과 같은 예외가 발생하였습니다

```
java.time.DateTimeException: Invalid value for NanoOfSecond (valid values 0 - 999999999): -249000000
at java.base/java.time.temporal.ValueRange.checkValidValue(ValueRange.java:319) ~[na
]
at java.base/java.time.temporal.ChronoField.checkValidValue(ChronoField.java:718) ~[na
]
at java.base/java.time.LocalTime.with(LocalTime.java:867) ~[na
]
at org.hibernate.type.descriptor.java.LocalTimeJavaType.wrap(LocalTimeJavaType.java:143) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.type.d…

```

이 에러는 JPA를 사용하여 데이터를 생성하는 과정에서 발생했습니다.

---

## 원인

문제의 근본 원인은 @EnableJpaAuditing 어노테이션을 사용하여 엔티티의 생성 및 수정 시각을 자동으로 관리하면서 발생했습니다. 특히, 문제는 createdAt과 modifiedAt 필드가
LocalDateTime 타입으로 선언되어 있었지만, 실제 데이터베이스에는 Time 타입으로 저장되어야 하는 상황에서 발생했습니다.

<br>

```
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime modifiedAt;

```

---

## 해결방안

<br>

문제를 해결하기 위해 다음 두 가지 주요 조치를 취했습니다: JPA 컨버터 사용과 데이터베이스 열 정의 수정. 이 두 조치는 LocalDateTime 타입을 데이터베이스의 TIMESTAMP 타입으로 효과적으로
매핑하여, 타입 불일치로 인한 예외를 방지합니다.

<br>

### JPA 컨버터 (@Convert)

@Convert 어노테이션은 엔티티의 속성을 데이터베이스의 열로 변환할 때 사용되는 컨버터를 지정합니다. 이 경우, Jsr310JpaConverters.LocalDateTimeConverter 클래스를 사용하여
LocalDateTime 인스턴스를 데이터베이스 호환 형식으로 변환합니다. 이 컨버터는 자바 8의 날짜 및 시간 API인 JSR-310을 지원하며, LocalDateTime 객체를 데이터베이스의 TIMESTAMP
타입으로 변환할 때 필요한 모든 중간 처리를 수행합니다. 결과적으로, 이는 데이터를 저장하거나 조회할 때 타입의 일관성을 유지하고, 날짜 및 시간 데이터가 정확히 저장되고 검색되도록 보장합니다.

### columnDefinition 속성

columnDefinition은 엔티티의 필드가 데이터베이스에 매핑될 때 사용되는 SQL 열 정의를 직접 지정합니다. "TIMESTAMP" 옵션을 사용함으로써 해당 필드가 데이터베이스에서 TIMESTAMP 타입으로
생성되도록 지시합니다. TIMESTAMP 타입은 날짜와 시간 정보를 모두 포함하며, 시간대 정보 없이 날짜와 시간을 나타냅니다. 이 설정은 LocalDateTime과 자연스럽게 호환되며, 시간 관련 정보(예: 시,
분, 초, 나노초)를 정확하게 데이터베이스에 저장하고 필요할 때 정확히 같은 형태로 검색할 수 있도록 합니다.

<br>

```

    @CreatedDate
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at", nullable = false, columnDefinition = "TIMESTAMP")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime modifiedAt;
    
```

이러한 설정을 통해, 애플리케이션이 생성 및 수정 날짜와 시간을 데이터베이스에 정확하게 저장하고, 타입 불일치로 인한 DateTimeException 예외를 방지할 수 있었습니다.

