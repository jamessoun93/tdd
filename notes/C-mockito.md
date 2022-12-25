# Mockito

## 모의 객체 생성

`Mockito.mock()` 메서드를 이용하면 특정 타입의 모의 객체를 생성할 수 있다.

```java
import static org.mockito.Mockito.mock;

public class GameGenMockTest {
    @Test
    void mockTest() {
        GameNumGen genMock = mock(GameNumGen.class);
    }
}
```

## 스텁 설정

모의 객체를 생성한 뒤에는 `BDDMockito` 클래스를 이용해서 모의 객체에 스텁을 구성할 수 있다.

`BDDMockito.given()` 을 이용하면 모의 객체의 메서드가 특정 값을 리턴하도록 설정할 수 있다.

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GameGenMockTest {
    @Test
    void mockTest() {
        GameNumGen genMock = mock(GameNumGen.class);
        given(genMock.generate(GameLevel.EASY)).willReturn("123");

        String num = genMock.generate(GameLevel.EASY);
        assertEquals("123", num);
    }
}
```

`BDDMockito.given()` 메서드는 스텁을 정의할 모의 객체의 메서드 호출을 전달하고 `willReturn()` 메서드는 스텁을 정의한 메서드가 리턴할 값을 지정한다.

`genMock.generate(GameLevel.EASY)` 가 호출되면 "123" 을 리턴하게끔 설정되어 있다.

1. 모의 객체 생성
```java
GameNumGen genMock = mock(GameNumGen.class);
```
2. 스텁 설정
```java
given(genMock.generate(GameLevel.EASY)).willReturn("123");
```
3. 스텀 성정에 매칭되는 메서드 실행
```java
String num = genMock.generate(GameLevel.EASY);
```

이렇게 지정한 값을 리턴하는 대신 익셉션을 발생하게 설정할 수도 있다.

```java
given(genMock.generate(null).willThrow(IllegalArgumentException.class);
```

리턴 타입이 void 인 메서드에 대해 익셉션을 발생시키려면 `BDDMockito.willThrow()` 메서드로 시작하면 된다.

```java
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

public class VoidMethodStubTest {
    @Test
    void voidMethodWillThrowTest() {
        List<String> mockList = mock(List.class);
        willThrow(UnsupportedOperationException.class)
                .given(mockList)
                .clear();

        assertThrows(UnsupportedOperationException.class, () -> mockList.clear());
    }
}
```

`BDDMockito.willThrow()` 메서는 발생할 익셉션 타입이나 익셉션 객체를 인자로 받고, `given()` 메서드는 모의 객체를 인자로 받는다. (** 모의 객체의 메서드 실행이 아닌 모의 객체 **)

## 인자 매칭 처리

```java
given(genMock.generate(GameLevel.EASY)).willReturn("123");

String num = genMock.generate(GameLevel.NORMAL);
```

위 코드에서는 스텁을 설정할 때 `genMock.generate()` 에 `GameLevel.EASY` 를 전달하고 있는데 실제 호출할 때는 `GameLevel.NORMAL` 을 인자로 전달하고 있다.

Mockito 는 이런식으로 일치하는 스텁 설정이 없을 경우 리턴 타입의 기본 값을 리턴한다.

리턴 타입이 int 면 0을, boolean 이면 false를, 기본 데이터 타입이 아닌 참조 타입이면 null을 리턴한다.

`org.mockito.ArgumentMatchers` 클래스를 사용하면 정확하게 일치하는 값 대신 임의의 값에 일치하도록 설정할 수 있다.

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class AnyMatcherTest {
@Test
void anyMatchTest() {
GameNumGen genMock = mock(GameNumGen.class);
given(genMock.generate(any())).willReturn("456");

        String num = genMock.generate(GameLevel.EASY);
        assertEquals("456", num);

        String num2 = genMock.generate(GameLevel.NORMAL);
        assertEquals("456", num2);
    }
}
```

`ArgumentMatchers.any()` 대신 `Mockito.any()` 나 `BDDMockito.any()` 를 사용해도 된다.

`ArgumentMatchers` 클래스는 `any()` 외에도 다음 메서드를 제공한다.

- anyInt(), anyShort(), anyLong(), anyByte(), anyChar(), anyDouble(), anyFloat(), anyBoolean(): 기본 데이터 타입에 대한 임의 값 일치
- anyString(): 문자열에 대한 임의 값 일치
- any(): 임의 타입에 대한 일치
- anyList(), anySet(), anyMap(), anyCollection(): 임의 컬렉션에 대한 일치
- matches(String), matches(Pattern): 정규표현식을 이용한 String 값 일치 여부
- eq(값): 특정 값과 일치 여부

스텁을 설정할 메서드의 인자가 두 개 이상인 경우 주의할 점이 있다.

```java
List<String> mockList = mock(List.class);
given(mockList.set(anyInt(), "123")).willReturn("456");
String old = mockList.set(5, "123");
```

아래와 같은 설명과 함께 익셉션이 발생한다.

```
This exception may occur if matchers are combined with raw values:
    //incorrect:
    someMethod(any(), "raw String");
When using matchers, all arguments have to be provided by matchers.
For example:
    //correct:
    someMethod(any(), eq("String by matcher"));
```

Mockito를 사용할 때는 하나의 인자라도 `ArgumentMatcher` 를 사용하는 경우 모든 인자를 `ArgumentMatcher` 를 이용해서 설정해야 한다.

위 예시처럼 특정 값을 사용하고 싶다면 `ArgumentMatchers.eq()` 를 사용해야 한다.

```java
List<String> mockList = mock(List.class);
given(mockList.set(anyInt(), eq("123"))).willReturn("456");
String old = mockList.set(5, "123");
```

## 행위 검증

mock 객체의 역할 중 하나는 실제로 mock 객체가 불렸는지 검증하는 것이다.

```java
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class GameTest {
    @Test
    void init() {
        GameNumGen genMock = mock(GameNumGen.class);
        Game game = new Game(genMock);
        game.init(GameLevel.EASY);
        
        then(genMock).should().generate(GameLevel.EASY);
    }
}
```

`BDDMockito.then()` 은 메서드 호출 여부를 검증할 모의 객체를 전달받는다.

`should()` 메서드는 모의 객체의 매서드가 불려야 한다는 것을 설정하고 `should()` 메서드 다음에 실제로 불려야 할 메서드를 지정한다.

만약 정확한 값이 아니라 메서드 호출 여부만 중요하다면 `any()`, `anyInt()` 등을 사용하여 지정할 수 있다. 

```java
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class GameTest {
    @Test
    void init() {
        GameNumGen genMock = mock(GameNumGen.class);
        Game game = new Game(genMock);
        game.init(GameLevel.EASY);

//        then(genMock).should().generate(GameLevel.EASY);
        then(genMock).should().generate(any());
    }
}
```

정확하게 한 번만 호출된 것을 검증하고 싶은 경우 `should()` 메서드에 `Mockito.only()` 를 인자로 전달하면 된다.

```java
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;

public class GameTest {
    @Test
    void init() {
        GameNumGen genMock = mock(GameNumGen.class);
        Game game = new Game(genMock);
        game.init(GameLevel.EASY);

//        then(genMock).should().generate(GameLevel.EASY);
        then(genMock).should(only()).generate(any());
    }
}
```

메서드 호출 횟수를 검증하기 위해 Mockito 가 제공하는 메서드

- `only()`: 한 번만
- `times(int)`: 지정한 횟수만큼
- `never()`: 호출 안함
- `atLeast(int)`: 적어도 지정한 횟수만큼
- `atLeastOnce()`: 적어도 한 번 / atLeast(1) 과 동일
- `atMost(int)`: 최대 지정한 횟수만큼

## 인자 캡쳐

유닛 테스트를 하다보면 모의 객체를 호출할 때 사용한 인자를 검증해야 할 때가 있다.

String, int 같은 경우 쉽지만 많은 속성을 가진 객체는 쉽게 검증하기 어렵다.

이럴 때 인자 캡쳐를 활용할 수 있다.

```java
@Test
void 회원가입_하면_메일_전송_argCaptor() {
    userRegister.register("id", "pw", "email@email.com");

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    then(mockEmailNotifier).should().sendRegisterEmail(captor.capture());

    String realEmail = captor.getValue();
    assertEquals("email@email.com", realEmail);
}
```

Mockito 의 ArgumentCaptor 를 사용하면 메서드 호출 여부를 검증하는 과정에서 실제 호출할 때 전달한 인자를 보관할 수 있다.

## JUnit 5 확장 설정

`mockito-junit-jupiter` 의존을 추가하면 `MockitoExtension` 확장을 사용하여 `@Mock` 애노테이션을 붙인 필드에 대해 자동으로 모의 객체를 생성할 수 있다.

```
testImplementation 'org.mockito:mockito-junit-jupiter:3.6.28'
```

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JUnit5ExtensionTest {
    @Mock
    private GameNumGen getMock;
}
```