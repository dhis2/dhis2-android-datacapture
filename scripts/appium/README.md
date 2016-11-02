# Installation

```
$ cd scripts/appium
$ virtualenv -p $(which python3) env
$ source env/bin/activate
$ pip install -r requirements.txt
$ nvm use 6.2.2
$ npm install -g appium
```

# Running the tests

### 1. Start the app in Android Studio.

**NOTE:** If you're making changes to the app itself (and not only to the tests), make sure that "Instant Run" is turned off. Otherwise, Appium might not be able to recognize that the APK has changed.

### 2. Start Appium:

```
$ appium
```

### 3. Run tests

In another terminal:

```
$ dhis2_dev_server_url=<server_url> dhis2_dev_password=<secret_password> apk_path=<path_to_apk> env/bin/py.test -s test.py
```