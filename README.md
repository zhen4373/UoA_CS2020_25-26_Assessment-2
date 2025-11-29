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
*   **History Management**:
    *   All recorded transactions are stored in a `JTable`.
    *   Users can clear the entire transaction history.
*   **Undo Functionality**:
    *   A single-level "Undo" feature allows the user to revert the most recent recording action.
    *   This restores the application's state by removing the last transaction(s) from the history, reverting the total balance, and repopulating the input fields with the undone values.


### Completed Features:
*   **Basic UI & Layout (20 pts)**: The application has a functional UI with all the required input fields, labels, buttons, and selectors laid out.
*   **Income vs Expenditure (10 pts)**: The UI correctly switches between "Income" and "Expenditure" views, updating labels accordingly.
*   **Error Checking (10 pts)**: The application validates numerical inputs and prevents recording if the data is invalid. The time input field also has robust format and date validity checking.
*   **Clear Button (5 pts)**: The "Clear" button successfully resets all input fields to their default state.
*   **History Log (10 pts)**: A history table correctly logs all recorded transactions.
*   **Total Balance (10 pts)**: The application accurately calculates and displays a running total balance.
*   **Single-Level Undo (10 pts)**: The "Undo" button correctly reverts the last transaction, restoring the previous state of the application, including the total balance and input fields.
*   **Frequency Calculation (10 pts)**: The logic to normalize values based on frequency ("Per Week", "Per Year") is implemented in the `getNormalizedAmount` method, although its integration into the main calculation preview (`calculate` method) is partial.
*   **Custom Time Entry (5 pts)**: Users can select "Other" for time and enter a custom date and time, which is validated.