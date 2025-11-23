package com.mbl.controllinecompanion;

import android.view.View;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import org.hamcrest.Matcher;
import java.util.concurrent.TimeoutException;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class EspressoTestUtils {

    /**
     * ViewAction que espera a que una vista específica aparezca.
     * @param viewMatcher el Matcher para encontrar la vista.
     * @param millis el tiempo máximo de espera.
     */
    public static ViewAction waitForView(final Matcher<View> viewMatcher, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot(); // Actuamos sobre la vista raíz.
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with matcher <" + viewMatcher + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // si la vista se encuentra, la acción termina exitosamente.
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50);
                } while (System.currentTimeMillis() < endTime);

                // Si se acaba el tiempo, lanza una excepción.
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}