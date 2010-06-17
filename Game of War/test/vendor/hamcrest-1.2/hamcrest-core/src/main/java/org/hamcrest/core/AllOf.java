package org.hamcrest.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Calculates the logical conjunction of multiple matchers. Evaluation is shortcut, so
 * subsequent matchers are not called if an earlier matcher returns <code>false</code>.
 */
public class AllOf<T> extends DiagnosingMatcher<T> {

	private final Iterable<Matcher<? super T>> matchers;

    public AllOf(Iterable<Matcher<? super T>> matchers) {
    	this.matchers = matchers;
    }

    @Override
    public boolean matches(Object o, Description mismatchDescription) {
    	for (Matcher<? super T> matcher : matchers) {
            if (!matcher.matches(o)) {
            	mismatchDescription.appendDescriptionOf(matcher).appendText(" ");
            	matcher.describeMismatch(o, mismatchDescription);
              return false;
            }
        }
    	return true;
    }

    public void describeTo(Description description) {
    	description.appendList("(", " " + "and" + " ", ")", matchers);
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Iterable<Matcher<? super T>> matchers) {
        return new AllOf<T>(matchers);
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
        return allOf(Arrays.asList(matchers));
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Matcher<T> first, Matcher<? super T> second) {
        List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(2);
        matchers.add(first);
        matchers.add(second);
        return allOf(matchers);
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third) {
        List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(3);
        matchers.add(first);
        matchers.add(second);
        matchers.add(third);
        return allOf(matchers);
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth) {
        List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(4);
        matchers.add(first);
        matchers.add(second);
        matchers.add(third);
        matchers.add(fourth);
        return allOf(matchers);
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth) {
        List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(5);
        matchers.add(first);
        matchers.add(second);
        matchers.add(third);
        matchers.add(fourth);
        matchers.add(fifth);
        return allOf(matchers);
    }

    /**
     * Evaluates to true only if ALL of the passed in matchers evaluate to true.
     */
    @Factory
    public static <T> Matcher<T> allOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth, Matcher<? super T> sixth) {
        List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(6);
        matchers.add(first);
        matchers.add(second);
        matchers.add(third);
        matchers.add(fourth);
        matchers.add(sixth);
        return allOf(matchers);
    }
}
