package com.mbl.controllinecompanion;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed; // Import necesario
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.mbl.controllinecompanion.EspressoTestUtils.waitForView;

import com.mbl.controllinecompanion.model.connection.Connection;

@RunWith(AndroidJUnit4.class)
public class MainActivityConnectionTest {
    private Connection connection;

    @Before
    public void setUp() {
        connection = Connection.getInstance();
        if (connection.getStatus()) {
            connection.shutDown();
        }
    }

    @Test
    public void testOnConnected_UpdatesUI() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.button_connect)).perform(click());
        onView(withId(R.id.status_text)).check(matches(withText("Connecting...")));

        // Trigger connection event
        scenario.onActivity(activity -> {
            connection.notifyConnected(); // La instancia 'connection' notificará a la 'activity'
        });

        // Check state of text and buttons
        onView(withId(R.id.status_text)).check(matches(withText("Connected")));
        onView(withId(R.id.button_connect)).check(matches(withText("Disconnect")));
    }

    @Test
    public void testOnError_UpdatesUIAndShowsDialog() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.button_connect)).perform(click());

        String errorMessage = "Unable to connect to receiver. Check WIFI is active and connected to the right network";
        String testTriggerMessage = "Test connection failed";

        // Trigger connection event
        scenario.onActivity(activity -> {
            connection.notifyError(testTriggerMessage);
        });

        // Check if dialog is shown
        onView(withText("Connection error")).check(matches(isDisplayed()));
        onView(withText(errorMessage)).check(matches(isDisplayed()));
        onView(ViewMatchers.isRoot()).inRoot(isDialog()).perform(waitForView(withText("OK"), 5000));
        // Dismiss dialog
        onView(withText("OK")).inRoot(isDialog()).perform(click());

        // Check state of text and buttons
        onView(withId(R.id.status_text)).check(matches(withText("Disconnected")));
        onView(withId(R.id.button_connect)).check(matches(withText("Connect")));
    }
}
