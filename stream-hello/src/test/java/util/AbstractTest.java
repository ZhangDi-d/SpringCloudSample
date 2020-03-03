package util;

import com.ryze.sample.StreamHelloApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by xueLai on 2020/3/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StreamHelloApplication.class)
@WebAppConfiguration
public abstract class AbstractTest {
}
