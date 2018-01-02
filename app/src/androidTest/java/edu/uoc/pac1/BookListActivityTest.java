package edu.uoc.pac1;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jordiborrasvivo on 2/1/18.
 */
@RunWith(AndroidJUnit4.class)
public class BookListActivityTest {

    @Rule
    public ActivityTestRule<BookListActivity> mActivityRule = new ActivityTestRule<>(BookListActivity.class);

    //La llista de llibres es mostra a l’aplicació.
    @Test
    public void veureLlistaLlibres() {
        onView(withId(R.id.book_list)).check(matches(isDisplayed()));
    }

    //Al prémer el botó de menú, aquest s’obri.
    @Test
    public void veureMenu(){
        //onView(withId(R.id.material_drawer_layout)).perform(DrawerActions.open()).check(withId(2));
    }

    //Al prémer el botó de detall, s’obri el WebView
    @Test
    public void veureDetall(){
        //onView(withId())
    }
}