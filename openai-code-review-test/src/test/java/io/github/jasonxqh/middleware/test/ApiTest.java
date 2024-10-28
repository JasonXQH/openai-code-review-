package io.github.jasonxqh.middleware.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Test
    public void test() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("21992e1");
        sb.append(",");
        sb.append("21992e2");
        sb.append(",");
        sb.append(",");
        sb.append(",xqwe");
        System.out.println(sb);
    }

}
