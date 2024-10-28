package io.github.jasonxqh.middleware.test;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Test
    public void test() {
        System.out.println(Integer.parseInt("1234"));
        System.out.println(Integer.parseInt("1236"));
        System.out.println(Integer.parseInt("1235"));
    }

}
