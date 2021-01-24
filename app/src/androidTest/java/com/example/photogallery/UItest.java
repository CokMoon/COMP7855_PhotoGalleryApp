package com.example.photogallery;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class UItest {
    @Rule
    public ActivityTestRule<Main> mActivityRule =
            new ActivityTestRule<>(Main.class);

    @Test
    public void DateFilterTest() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_toDate)).perform(typeText("2019/05/01"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("2019/03/01"), closeSoftKeyboard());

        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void LocationFilterTest() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_city)).perform(typeText("Surrey"), closeSoftKeyboard());
        onView(withId(R.id.search_Country)).perform(typeText("Canada"), closeSoftKeyboard());

        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void KeywordFilterTest() {

        onView(withId(R.id.editKeyword)).perform(typeText("RE"), closeSoftKeyboard());
        onView(withId(R.id.confirmKeyword)).perform(click());

        onView(withId(R.id.btnRight)).perform(click());
        onView(withId(R.id.confirmKeyword)).perform(click());

        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_Keyword)).perform(typeText("RE"), closeSoftKeyboard());

        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void FileUpload() {
        onView(withId(R.id.SecureUpload)).perform(click());
        onView(withId(R.id.UploadStatus)).check(matches(withText("Upload Done")));
    }
}

