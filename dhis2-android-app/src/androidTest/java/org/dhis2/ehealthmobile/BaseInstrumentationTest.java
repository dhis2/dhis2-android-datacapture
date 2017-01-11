package org.dhis2.ehealthmobile;

import android.os.RemoteException;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class BaseInstrumentationTest {

	protected UiDevice device;

	@Before
	public void setup() throws Exception {
		device = UiDevice.getInstance(getInstrumentation());
	}

	protected void checkViewDisplayed(int id){
		onView(withId(id)).check(matches(isDisplayed()));
	}

	protected void checkViewNotDisplayed(int id){
		onView(withId(id)).check(matches(not(isDisplayed())));
	}

	protected void typeTextInView(int viewId, String text){
		onView(withId(viewId)).perform(typeText(text));
	}

	protected void checkViewWithTextIsDisplayed(String text){
		onView(withText(text)).check(matches(isDisplayed()));
	}

	protected void clickView(int id){
		onView(withId(id)).perform(click());
	}

	protected void rotateLeft(){
		try {
			device.setOrientationLeft();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void rotateRigt(){
		try {
			device.setOrientationRight();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void rotateNatural(){
		try {
			device.setOrientationNatural();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
