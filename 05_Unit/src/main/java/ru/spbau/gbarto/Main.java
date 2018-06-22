package ru.spbau.gbarto;

import ru.spbau.gbarto.annotations.*;
import ru.spbau.gbarto.exceptions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that allows you to test programs on java.
 */
public class Main {
    /**
     * Class that describes method annotated as @Test.
     */
    private static class TestMethod {
        Method test;
        Class expected;
        String ignore;

        TestMethod(Method test, Class expected, String ignore) {
            this.test = test;
            this.expected = expected;
            this.ignore = ignore;
        }
    }

    /**
     * Class that contains annotated methods.
     */
    private static class TestClassMethods {
        Object instance = null;
        List<TestMethod> tests = new ArrayList<>();
        Method before = null;
        Method after = null;
        Method beforeClass = null;
        Method afterClass = null;

        TestClassMethods() {}
    }

    /**
     * Reads path to the class from input. Also checks input.
     *
     * @param args input arguments
     * @return path to the class
     */
    private static String readPath(String[] args) {
        if (args.length < 1) {
            System.err.println("Not enough arguments");
            System.exit(1);
        }

        return args[0];
    }

    /**
     * Returns annotated methods.
     *
     * @param testClass class with test methods
     * @return annotated methods
     * @throws TooManyAnnotationsException  if class contains two or more not @Test annotations
     * @throws UnexpectedAnnotationsException if any method is annotated with different XUnit annotations
     * @throws IllegalAccessException if class has no default constructor
     * @throws InstantiationException if class has no public default constructor
     */
    private static TestClassMethods getMethods(Class testClass) throws TooManyAnnotationsException,
            UnexpectedAnnotationsException, IllegalAccessException, InstantiationException {
        TestClassMethods methods = new TestClassMethods();

        methods.instance = testClass.newInstance();

        for (Method method : testClass.getDeclaredMethods()) {
            int annotationsNum = 0;

            method.setAccessible(true);

            if (method.getAnnotation(Test.class) != null) {
                Test test = method.getAnnotation(Test.class);
                methods.tests.add(new TestMethod(method, test.expected(), test.ignore()));
                annotationsNum++;
            }
            if (method.getAnnotation(Before.class) != null) {
                if (methods.before != null) {
                    throw new TooManyAnnotationsException("Before");
                }

                methods.before = method;
                annotationsNum++;
            }
            if (method.getAnnotation(After.class) != null) {
                if (methods.after != null) {
                    throw new TooManyAnnotationsException("After");
                }

                methods.after = method;
                annotationsNum++;
            }
            if (method.getAnnotation(BeforeClass.class) != null) {
                if (methods.beforeClass != null) {
                    throw new TooManyAnnotationsException("BeforeClass");
                }

                methods.beforeClass = method;
                annotationsNum++;
            }
            if (method.getAnnotation(AfterClass.class) != null) {
                if (methods.afterClass != null) {
                    throw new TooManyAnnotationsException("AfterClass");
                }

                methods.afterClass = method;
                annotationsNum++;
            }
            if (annotationsNum > 1) {
                throw new UnexpectedAnnotationsException();
            }
        }

        return methods;
    }

    /**
     * Invokes annotated methods.
     *
     * @param methods annotated methods
     */
    private static void invokeTests(TestClassMethods methods) {
        int passedTests = 0;
        int allTests = 0;

        try {
            if (methods.beforeClass != null) {
                methods.beforeClass.invoke(methods.instance);
            }

            for (TestMethod test : methods.tests) {
                if (!test.ignore.equals(Test.EMPTY)) {
                    System.out.print("Test " + test.test.getName() + " ignored. Reason: " + test.ignore);
                    allTests--;
                    continue;
                }

                allTests++;

                long time = System.currentTimeMillis();

                boolean success = true;
                String reason = null;

                try {
                    Throwable exception = null;

                    if (methods.before != null) {
                        methods.before.invoke(methods.instance);
                    }

                    try {
                        test.test.invoke(methods.instance);
                        if (!test.expected.equals(Test.NONE.class)) {
                            exception = new ExceptionNotThrown(test.expected);
                        }
                    } catch (Exception e) {
                        if (!test.expected.equals(e.getCause().getClass())) {
                            exception = e.getCause();
                        }
                    }

                    if (methods.after != null) {
                        methods.after.invoke(methods.instance);
                    }

                    if (exception != null) {
                        throw exception;
                    }
                } catch (Throwable e) {
                    success = false;
                    reason = e.getMessage();
                }

                time = System.currentTimeMillis() - time;

                if (success) {
                    System.out.println("Test " + test.test.getName() + " passed" + ". Time: " + time + "ms");
                    passedTests++;
                } else {
                    System.out.println("Test " + test.test.getName() + " failed. Reason: " + reason + ". Time: " + time + "ms");
                }
            }

            if (methods.afterClass != null) {
                methods.afterClass.invoke(methods.instance);
            }
        } catch (Exception e) {
            System.out.println("failed to complete testing:" + e.getMessage());
        }

        System.out.println("Passed: " + passedTests + " of " + allTests);
    }

    /**
     * Gets annotated methods and invokes them.
     *
     * @param testClass class with test methods
     * @throws TooManyAnnotationsException  if class contains two or more not @Test annotations
     * @throws UnexpectedAnnotationsException if any method is annotated with different XUnit annotations
     * @throws IllegalAccessException if class has no default constructor
     * @throws InstantiationException if class has no public default constructor
     */
    private static void runTests(Class testClass) throws TooManyAnnotationsException,
            UnexpectedAnnotationsException, InstantiationException, IllegalAccessException {
        TestClassMethods methods = getMethods(testClass);
        invokeTests(methods);
    }

    /**
     * takes the path to the input and runs the tests located along this path
     *
     * @param args input arguments
     */
    public static void main(String[] args) {
        String path = readPath(args);

        Class testClass;

        try {
            testClass = Class.forName(path);
        } catch (ClassNotFoundException e) {
            System.out.println("Class was not found");
            return;
        }

        try {
            runTests(testClass);
        } catch (UnexpectedAnnotationsException | TooManyAnnotationsException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException | InstantiationException e) {
            System.out.println("Could not construct a class");
        }
    }
}
