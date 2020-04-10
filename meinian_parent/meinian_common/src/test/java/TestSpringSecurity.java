import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestSpringSecurity {
    @Test
    public void testSpringSecurity() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String abc = encoder.encode("admin");
        System.out.println(abc);
        String abc1 = encoder.encode("admin");
        System.out.println(abc1);
//        $2a$10$ItLyd8mVRoeMZ7BF7ehExOhPtpVjJe7S7WL5Sbf.O/Cc2F4uk2rri
//        $2a$10$Z6S1vGCcIsz/yluoL3Lpb.JqL81kbalZ6TcKyqKL01a8JV1PS/zH.
        boolean matches = encoder.matches("abc", "$2a$10$Z6S1vGCcIsz/yluoL3Lpb.JqL81kbalZ6TcKyqKL01a8JV1PS/zH.");
        System.out.println(matches);
    }
}
