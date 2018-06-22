package ru.spbau.gbarto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.spbau.gbarto.exceptions.TooManyAnnotationsException;
import ru.spbau.gbarto.exceptions.UnexpectedAnnotationsException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {
    private Method tester;
    private ByteArrayOutputStream output;

    @BeforeEach
    void init() throws NoSuchMethodException {
        Class myTester = Main.class;
        tester = myTester.getDeclaredMethod("runTests", Class.class);
        tester.setAccessible(true);

        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @Test
    void testCorrectTest() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class test = ClassLoader.getSystemClassLoader().loadClass("ru.spbau.gbarto.calsses.CorrectTest");
        assertEquals(null, tester.invoke(null, test));
    }

    @Test
    void testWrongTest() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class test = ClassLoader.getSystemClassLoader().loadClass("ru.spbau.gbarto.calsses.WrongTest");
        assertEquals(null, tester.invoke(null, test));

        assertTrue(output.toString().contains("Passed: 0 of 1"));
    }

    @Test
    void testTooManyAnnotations() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class test = ClassLoader.getSystemClassLoader().loadClass("ru.spbau.gbarto.calsses.TooManyAnnotations");
        assertThrows(TooManyAnnotationsException.class, () -> {
            try {
                tester.invoke(null, test);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void testIncorrectAnnotations() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class test = ClassLoader.getSystemClassLoader().loadClass("ru.spbau.gbarto.calsses.IncorrectAnnotations");
        assertThrows(UnexpectedAnnotationsException.class, () -> {
            try {
                tester.invoke(null, test);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }
}