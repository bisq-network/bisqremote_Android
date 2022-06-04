Feature: Accessibility
    Accessibility options alter the presentation of components within the UI. As a result, it is
    important that the app behaves appropriately when using accessibility options.

    Scenario: Changing font settings is reflected within the app
        Given the font settings have been changed
        When navigating the app
        Then the app uses the updated font settings and behaves appropriately

    Scenario: Increasing text/zoom settings is reflected within the app
        Given the text/zoom settings have been increased
        When navigating the app
        Then the app uses the updated text/zoom settings and behaves appropriately

    Scenario: Decreasing text/zoom settings is reflected within the app
        Given the text/zoom settings have been decreased
        When navigating the app
        Then the app uses the updated text/zoom settings and behaves appropriately

    Scenario: Changing color settings is reflected within the app
        Given the color settings have been changed
        When navigating the app
        Then the app uses the updated color settings and behaves appropriately
