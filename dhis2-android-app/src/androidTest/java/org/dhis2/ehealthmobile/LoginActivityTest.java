package org.dhis2.ehealthmobile;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.ui.activities.LoginActivity;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;


public class LoginActivityTest extends BaseInstrumentationTest{

	private static final String SERVER_URL = "http://www.asd.com";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	@Rule
	public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

	@Test
	public void testLogin() throws InterruptedException {

		checkViewDisplayed(R.id.dhis2_logo);
		checkViewDisplayed(R.id.password);
		checkViewDisplayed(R.id.username);
		checkViewDisplayed(R.id.server_url);
		checkViewDisplayed(R.id.login_button);
		checkViewNotDisplayed(R.id.progress_bar);

		typeTextInView(R.id.server_url, SERVER_URL);
		typeTextInView(R.id.username, USERNAME);
		Espresso.closeSoftKeyboard();
		typeTextInView(R.id.password, PASSWORD);

		checkViewWithTextIsDisplayed(SERVER_URL);
		checkViewWithTextIsDisplayed(USERNAME);
		checkViewWithTextIsDisplayed(PASSWORD);

		rotateRigt();
		rotateLeft();
		rotateNatural();

		checkViewWithTextIsDisplayed(SERVER_URL);
		checkViewWithTextIsDisplayed(USERNAME);
		checkViewWithTextIsDisplayed(PASSWORD);

		clickView(R.id.login_button);
		Thread.sleep(2000);
		onView(withText(R.string.wrong_url)).inRoot(withDecorView(not(is(rule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
	}


}

