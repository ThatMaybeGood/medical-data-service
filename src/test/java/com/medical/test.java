package com.medical;

import com.medical.service.MockTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Mine
 * @version 1.0
 * 描述:
 * @date 2025/10/15 13:28
 */
@SpringBootTest
public class test {
    @Autowired
    private MockTokenService mockTokenService;
    @Test
    void  test(){
        String token = mockTokenService.getToken();
        System.out.println(token);

    }
}
