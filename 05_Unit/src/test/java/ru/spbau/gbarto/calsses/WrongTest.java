package ru.spbau.gbarto.calsses;

import ru.spbau.gbarto.annotations.*;

public class WrongTest {
    private int x = 0;

    @BeforeClass
    void beforeClass() {
        x += 1;
    }

    @Test
    void test() {
        x += 2;
        if (x != 5) {
            throw new AssertionError();
        }
    }

    @AfterClass
    void afterClass() {
        x += 3;
    }
}
