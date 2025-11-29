# Budget Tool

### Key Features:

*   **Data Input & Validation**:
    *   Provides text fields for users to enter amounts for different categories (e.g., Loans, Food, Other).
    *   Includes real-time input validation that checks for valid numerical inputs and provides immediate feedback on errors.
    *   Features a robust time input field that validates the `yyyy/MM/dd HH:mm` format.
*   **Transaction Recording**:
    *   Users can record multiple transactions at once.
    *   Each transaction is added to a history table, which displays the date, frequency, type, value, and the cumulative total at that point.
*   **Financial Summary**:
    *   A "Total (per month)" area shows a preview of the monthly impact of the current inputs.
    *   A running "Total Balance" is maintained and displayed, along with a status label ("Surplus", "Deficit", or "Balanced") that changes color for better visibility.
*   **History Table**:
    *   All recorded transactions are stored in a `JTable`.
    *   Users can clear the entire transaction history.
*   **Undo**:
    *   A single-level "Undo" feature allows the user to revert the most recent recording action.
    *   This restores the application's state by removing the last transaction(s) from the history, reverting the total balance, and repopulating the input fields with the undone values.


### Completed Features:
*  **Basic system (30 pts)**:
    *   ✅	User enters three (or more) income fields (wages, loan, other) and the system computes total income when a Calculate button is pressed.
    *   ✅	User enters three (or more) expenditure fields (food, rent, other) and the system computes total expenditure when a Calculate button is pressed.
    *   ✅	When Calculate is pressed, the system also shows surplus/deficit (income minus spending). This is black if positive or zero, and red if negative.
    *   ✅	System checks user input (numbers for validity), and produces an appropriate error message if input is not valid. Empty fields are treated as 0 (no error message).

*  **Extensions (20 pts)**:
    *   ✅	Allow users to specify income/expenditure numbers per week, per month, or per year, for each input field. This requires both adding appropriate choice widgets (such as combo boxes), and also modifying the way you calculate totals. You can assume that there are 52 weeks in a year, 12 months in a year, and 4.3333333 weeks in a month.
    *   ✅	Implement “spreadsheet” behaviour, that is totals are updated whenever the user changes a number or time-period, with no need to press a calculate button. You should update whenever the focus shifts, as well as whenever an action is performed.

*   **Undo (30 pts)**:
    *   ✅	Implement a single-level of Undo, so the user can “undo” his or her most recent action. This require saving the state of the system (ie, all numbers) when a change is made.
    *   ✅	Implement multiple Undo, so the user can undo multiple actions. We recommend that you create a class to hold state information numbers, and then maintain a stack of states.
    *   ❌   Good collection of JUnit tests for the Undo feature.

*   **Programming style (20 pts)**:
    *   Java code follows good coding style, including good code comments and variable names
    *   Java code is well structured and decomposed into methods