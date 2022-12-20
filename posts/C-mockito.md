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
