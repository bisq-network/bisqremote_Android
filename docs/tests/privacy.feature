Feature: Privacy
    Privacy is of utmost importance. It is vital that no unnecessary data is retained.

    Scenario: App data is erased when deleting the app
        Given the app has been paired
        And the app has received notifications
        When deleting the app
        And reinstalling the app
        Then the app will load the Welcome view
        And the previous pairing and notifications will have been deleted
