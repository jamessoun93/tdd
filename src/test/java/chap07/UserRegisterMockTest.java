package chap07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserRegisterMockTest {
    private UserRegister userRegister;
//    private StubWeakPasswordChecker stubWeakPasswordChecker = new StubWeakPasswordChecker();
    private WeakPasswordChecker mockPasswordChecker = Mockito.mock(WeakPasswordChecker.class);
    private MemoryUserRepository fakeRepository = new MemoryUserRepository();
//    private SpyEmailNotifier spyEmailNotifier = new SpyEmailNotifier();
    private EmailNotifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

    @BeforeEach
    void setUp() {
//        userRegister = new UserRegister(stubWeakPasswordChecker, fakeRepository, spyEmailNotifier);
        userRegister = new UserRegister(mockPasswordChecker, fakeRepository, mockEmailNotifier);
    }

    @Test
    void 약한_암호면_가입_실패() {
//        stubWeakPasswordChecker.setWeak(true);
        BDDMockito.given(mockPasswordChecker.checkPasswordWeak("pw")).willReturn(true);

        assertThrows(WeakPasswordException.class, () -> {
            userRegister.register("id", "pw", "email");
        });
    }

    @Test
    void 이미_같은_ID가_존재하면_가입_실패() {
        // 이미 아이디가 존재하는 상황 생성 (repository.save())
        fakeRepository.save(new User("id", "pw1", "email@email.com"));

        assertThrows(DuplicateIdException.class, () -> {
            userRegister.register("id", "pw1", "email");
        });
    }

    @Test
    void 회원_가입시_암호_검사_수행함() {
        userRegister.register("id", "pw", "email");

        BDDMockito.then(mockPasswordChecker)
                .should()
                .checkPasswordWeak(BDDMockito.anyString());
    }

    @Test
    void 회원가입_성공() {
        userRegister.register("id", "pw", "email");

        User savedUser = fakeRepository.findById("id");
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());
    }

//    @Test
//    void 회원가입_하면_메일_전송() {
//        userRegister.register("id", "pw", "email@email.com");
//
//        assertTrue(spyEmailNotifier.isCalled());
//        assertEquals("email@email.com", spyEmailNotifier.getEmail());
//    }

    @Test
    void 회원가입_하면_메일_전송_argCaptor() {
        userRegister.register("id", "pw", "email@email.com");

        // 모의 객체의 메서드를 호출할 때 전달한 인자를 구하는 코드
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        BDDMockito.then(mockEmailNotifier)
                .should().sendRegisterEmail(captor.capture());

        String realEmail = captor.getValue();
        assertEquals("email@email.com", realEmail);
    }
}
