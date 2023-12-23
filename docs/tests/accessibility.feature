Feature: Accessibility
    Accessibility options alter the presentation of components within the UI. As a result, it is
    important that the app behaves appropriately when using accessibility options.

    Scenario: Changing system font settings is reflected within the app
        Given the system font settings have been changed
        When navigating the app
        Then the app uses the updated font settings and behaves appropriately

    Scenario: Increasing system text/zoom settings is reflected within the app
        Given the system text/zoom settings have been increased
        When navigating the app
        Then the app uses the updated text/zoom settings and behaves appropriately

    Scenario: Decreasing system text/zoom settings is reflected within the app
        Given the system text/zoom settings have been decreased
        When navigating the app
        Then the app uses the updated text/zoom settings and behaves appropriately

    Scenario: Changing system color settings is reflected within the app
        Given the system color settings have been changed
        When navigating the app
        Then the app uses the updated color settings and behaves appropriately
