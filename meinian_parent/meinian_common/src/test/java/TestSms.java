import com.atguigu.utils.SMSUtils;
import com.atguigu.utils.ValidateCodeUtils;
import org.junit.Test;

public class TestSms {
    @Test
    public void test() throws Exception {
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        SMSUtils.sendShortMessage("18278132549",String.valueOf(code));
    }
}
