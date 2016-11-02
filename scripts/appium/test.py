import os
import collections
from time import sleep

import unittest

from appium import webdriver

class CategoryOptionComboIDs:
    CASES_LESS_THAN_5_YEARS_OLD = 'e079rjYqlMH'
    DEATHS_LESS_THAN_5_YEARS_OLD = 'C5qLGpw4uet'
    CASES_GREATER_THAN_5_YEARS_OLD = 'jpmVF8rhBNA'
    DEATHS_GREATER_THAN_5_YEARS_OLD = 'Ciavs7v4qYa'

class AggregateReportPickers:
    ORG_UNIT = 0
    DATA_SET = 1
    PERIOD = 2

class LoginTest(unittest.TestCase):

    def setUp(self):
        desired_caps = {}
        desired_caps['appWaitActivity'] = '.ui.activities.LoginActivity'
        desired_caps['platformName'] = 'Android'
        desired_caps['platformVersion'] = '6.0'
        desired_caps['app'] = os.getenv('apk_path')
        desired_caps['deviceName'] = 'Android Emulator'
        self.driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

    def tearDown(self):
        # end the session
        self.driver.quit()

    def scroll_to_by_text(self, element_text):
        template = 'new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().text("{value}").instance(0));'
        return self.driver.find_element_by_android_uiautomator(
            template.format(value=element_text),
        )

    def scroll_to_by_content_description(self, description):
        template = 'new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().description("{description}").instance(0));'
        return self.driver.find_element_by_android_uiautomator(
            template.format(description=description),
        )

    def get_picker_menu(self, picker_id):
        picker_app_id = "org.dhis2.mobile:id/linearlayout_picker"
        return self.driver.find_elements_by_id(picker_app_id)[picker_id]

    def test_form_submission(self):
        self.driver.implicitly_wait(20)

        server_url = os.getenv('dhis2_dev_server_url')
        username = 'admin'
        password = os.getenv('dhis2_dev_password')

        org_unit = 'Bombali'
        year, week = (2016, 40)
        dataset_name = 'IDSR Weekly Disease Report(WDR)'
        form_entries = [
            {
                'data_element_id': 'nEt6PdS2tPR',
                'values': (
                    (CategoryOptionComboIDs.CASES_LESS_THAN_5_YEARS_OLD, 4),
                    (CategoryOptionComboIDs.DEATHS_LESS_THAN_5_YEARS_OLD, 3),
                    (CategoryOptionComboIDs.CASES_GREATER_THAN_5_YEARS_OLD, 2),
                    (CategoryOptionComboIDs.DEATHS_GREATER_THAN_5_YEARS_OLD, 1),
                )
            },
        ]

        # TODO: POST to API here; make sure that the dataValueSet corresponding
        # to this period and organisation unit is empty

        # Login Activity
        el = self.driver.find_element_by_id("org.dhis2.mobile:id/server_url")
        el.set_text(server_url)
        el = self.driver.find_element_by_id("org.dhis2.mobile:id/username")
        el.set_text(username)
        el = self.driver.find_element_by_id("org.dhis2.mobile:id/password")
        el.set_text(password)
        el = self.driver.find_element_by_id("org.dhis2.mobile:id/login_button")
        el.click()

        # Menu Activity
        self.get_picker_menu(AggregateReportPickers.ORG_UNIT).click()
        self.scroll_to_by_text(org_unit).click()

        self.get_picker_menu(AggregateReportPickers.DATA_SET).click()
        self.scroll_to_by_text(dataset_name).click()

        self.get_picker_menu(AggregateReportPickers.PERIOD).click()
        # TODO: get from e.g. isoweek.Week
        period_text = "W40 2016-10-03 - 2016-10-10"
        self.scroll_to_by_text(period_text).click()

        el = self.driver.find_element_by_id("org.dhis2.mobile:id/user_data_entry")
        el.click()

        # Data Entry Activity
        for entry in form_entries:
            el = self.scroll_to_by_content_description(
                entry['data_element_id'],
            )
            for value_pair in entry['values']:
                category, value = value_pair
                child = el.find_element_by_accessibility_id(category)
                child.set_text(str(value))

        # Upload values
        el = self.driver.find_element_by_id("org.dhis2.mobile:id/upload_button")
        el.click()

        # TODO: Query the API here, check that values were submitted


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(LoginTest)
    unittest.TextTestRunner(verbosity=2).run(suite)
