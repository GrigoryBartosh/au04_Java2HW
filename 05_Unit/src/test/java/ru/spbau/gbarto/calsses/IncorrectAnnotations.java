package ru.spbau.gbarto.calsses;

import ru.spbau.gbarto.annotations.*;

public class IncorrectAnnotations {
    @Before
    @After
    void test() {}
}
