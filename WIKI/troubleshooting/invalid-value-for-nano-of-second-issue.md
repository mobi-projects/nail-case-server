## 문제 상황 1:

- 12AM과 4AM 사이에 자바 애플리케이션에서 데이터 처리를 시도할 때, NanoOfSecond 값이 유효하지 않아 다음과 같은 예외가 발생하였습니다
  h2 데이터베이스상황에서 Invalid NanoOfSecond Value Exception 예외 발생
  <br>
  <br>

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

## 문제 상황 2:

- 마찬가지로 postgresql에서도 12AM과 4AM 사이에 자바 애플리케이션에서 데이터 처리를 시도할 때, 예외가 발생하였지만 DataIntegrityViolationException와 같은 다른 예외가 발생
  <br>
  <br>

```
org.springframework.dao.DataIntegrityViolationException: could not execute statement [ERROR: invalid input syntax for type timestamp: "02:21:36.279+09"
  Where: unnamed portal parameter $1 = '...'] [insert into members (created_at,create_by,email,modified_at,modified_by,name,profile_img_url,role,social_id,social_type) values (?,?,?,?,?,?,?,?,?,?) returning member_id]; SQL [insert into members (created_at,create_by,email,modified_at,modified_by,name,profile_img_url,role,social_id,social_type) values (?,?,?,?,?,?,?,?,?,?) returning member_id]
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:293) ~[spring-orm-6.1.8.jar:6.1.8]
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:241) ~[spring-orm-6.1.8.jar:6.1.8]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.translateExceptionIfPossible(AbstractEntityManagerFactoryBean.java:550) ~[spring-orm-6.1.8.jar:6.1.8]
	at org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61) ~[spring-tx-6.1.8.jar:6.1.8]

```

이 에러는 JPA를 사용하여 데이터를 생성하는 과정에서 발생했습니다.


---

## 시도 1 : columnDefinition에 'TIMESTAMP' 추가하기

=> 같은 예외가 발생

````
    @CreatedDate
	@Schema(title = "생성시간")
	@Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Schema(title = "수정시간")
	@Column(name = "modified_at", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime modifiedAt;
	
````

## 시도2: @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class) 어노테이션 사용

=> h2 database와 postgresql 모두 예외 발생 ❌,성공적 ✅

<br>
<br>

```
    @CreatedDate
	@Schema(title = "생성시간")
	@Column(name = "created_at", nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Schema(title = "수정시간")
	@Column(name = "modified_at", nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime modifiedAt;
```

## 시도3: @Temporal(TemporalType.TIMESTAMP) 어노테이션 사용

=> h2 database와 postgresql 모두 예외 발생 ❌,성공적 ✅

<br>
<br>

```
    @CreatedDate
	@Schema(title = "생성시간")
	@Column(name = "created_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Schema(title = "수정시간")
	@Column(name = "modified_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime modifiedAt;
```

---

## 원인찾기

원인을 찾기 위하여 @EntityListeners(AuditingEntityListener.class) 기존 auditing 어노테이션과 config모두 주석 처리후에, 아래와 같은
코드를 명시적으로 넣었다. 결과는 같은 예외가 두 db에 모두 발생.

```
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }
```

<br>

---

## 원인

근본적인 원인은 JPA와 데이터베이스간의 타입변환 문제라고 판단했습니다.
(JPA가 기본적으로 제공하는 LocalDateTime 변환 방식부분에서 예외가 발생)

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

### Temporal 어노테이션

@Temporal 어노테이션은 Java의 날짜 및 시간 타입을 데이터베이스의 날짜/시간 타입으로 매핑할 때 사용됩니다. 이 어노테이션은 javax.persistence 패키지에 속해 있으며, JPA 스펙의
일부입니다.

@Temporal 어노테이션은 TemporalType 열거형 값을 인자로 받습니다. 주요 TemporalType 값은 다음과 같습니다:

TemporalType.TIMESTAMP: 날짜와 시간을 모두 포함합니다. 데이터베이스의 TIMESTAMP 타입에 매핑됩니다.
TemporalType.DATE: 날짜만 포함합니다. 데이터베이스의 DATE 타입에 매핑됩니다.
TemporalType.TIME: 시간만 포함합니다. 데이터베이스의 TIME 타입에 매핑됩니다.


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

이러한 설정을 통해, 애플리케이션이 생성 및 수정 날짜와 시간을 데이터베이스에 정확하게 저장하고, 타입 불일치로 인한 예외를 방지할 수 있었습니다.

