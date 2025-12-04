# Budget Tool

### Features:

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
    *   Users can clear the entire transaction history.
*   **Undo**:
    *  `Undo`feature allows the user to revert the most recent recording action.

### Methods:

*   **List**:
    *   `n1`: to store lable
    *   `n2`: to store selection for Time and finance lable
    *   `n3`: to store the position of each row in the UI

*   **Function**:
    *   `update_calculate_button_state()`: to update the state of the calculate button based on the validity of the input fields.
    *   `error_check()`: to validate if a given string is a non-zero number.
    *   `error_massage()`: to display error messages for invalid inputs in the UI.
    *   `input_listener()`: to add a document listener to a text field that triggers validation and recalculation on any change.
    *   `collum_label_valid()`: to validate the label for the "Other" category, ensuring it is not empty.
    *   `set_previous_text()`: to restore the input fields with the values from an undone transaction.
    *   `undo_selection_and_time_part()`: to restore the selection components (like income/expenditure and time selectors) during an undo operation.
    *   `undo_input_part()`: to restore the numerical input fields and remove the last transaction from the history table during an undo operation.
    *   `undo()`: to orchestrate the entire undo process by calling helper methods to restore the previous state.
    *   `normalize()`: to convert an input value to a monthly equivalent based on its selected frequency.
    *   `anti_normalize()`: to convert a monthly value back to its original value based on its frequency.
    *   `clear()`: to clear all input fields and reset the result area.
    *   `calculate()`: to calculate and display the total monthly impact of the current inputs in the preview area.
    *   `differenc()`: to update the total balance by adding or subtracting a given amount.
    *   `History_record()`: to record the current input(s) as one or more transactions in the history table.
    *   `History_record_template()`: a helper method to create and add a new row to the history table.
    *   `Clear_History()`: to clear all records from the history table and reset the total balance.
    *   `update_CHB_states()`: to enable or disable the "Clear History" and "Undo" buttons based on whether the history table is empty.
    *   `Now_time()`: to get the current time formatted as a string.
    *   `edited_time()`: to get the user-entered time as a string.
    *   `input_time_check()`: to validate the format of the user-entered time.
    *   `tinp_state()`: to manage the state of the time input field, enabling or disabling it based on the user's selection.
    *   `finance_update()`: to update the total balance display and the "Surplus/Deficit" status label.