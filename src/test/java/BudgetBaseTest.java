package test.java;

import main.java.BudgetTool;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class BudgetBaseTest {

    private BudgetTool budgetTool;
    private JFrame frame;

    // Helper method to access private fields for testing
    private <T> T getPrivateField(String fieldName) throws Exception {
        Field field = BudgetTool.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(budgetTool);
    }

    // Helper method to call private methods for testing
    private <T> T invokePrivateMethod(String methodName, Object... args) throws Exception {
        Class<?>[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argClasses[i] = args[i].getClass();
        }
        Method method = BudgetTool.class.getDeclaredMethod(methodName, argClasses);
        method.setAccessible(true);
        return (T) method.invoke(budgetTool, args);
    }

    @Before
    public void setUp() {
        // We need a frame to pass to the constructor
        frame = new JFrame();
        budgetTool = new BudgetTool(frame);
    }

    @Test
    public void testUndoFunctionality() throws Exception {
        // Get access to private UI components and data structures
        JTextField numInput1 = getPrivateField("numInput1");
        JTextField numInput2 = getPrivateField("numInput2");
        JComboBox<String> selector1 = getPrivateField("Selector1");
        DefaultTableModel historyTableModel = getPrivateField("historyTableModel");
        List<Integer> run_timeList = getPrivateField("run_timeList");

        // --- Step 1: Simulate first record (Income: Wages 1000) ---
        SwingUtilities.invokeAndWait(() -> {
            selector1.setSelectedItem("Income");
            numInput2.setText("1000"); // Wages
        });

        // Manually call the recording logic
        invokePrivateMethod("History_record");

        // --- Assertions after first record ---
        assertEquals("Total balance should be 1000.00", 1000.0, (double) getPrivateField("total_balance"), 0.01);
        assertEquals("History table should have 1 row", 1, historyTableModel.getRowCount());
        assertEquals("Value in table should be +1000.00", "+1000.00", historyTableModel.getValueAt(0, 3));
        assertEquals("run_timeList should have 1 entry", 1, run_timeList.size());
        assertEquals("run_time for first record should be 1", 1, (int) run_timeList.get(0));

        // --- Step 2: Simulate second record (Expenditure: Food 50, Rent 200) ---
        SwingUtilities.invokeAndWait(() -> {
            selector1.setSelectedItem("Expenditure");
            numInput1.setText("50"); // Food
            numInput2.setText("200"); // Rent
        });
        invokePrivateMethod("History_record");

        // --- Assertions after second record ---
        assertEquals("Total balance should be 750.00", 750.0, (double) getPrivateField("total_balance"), 0.01);
        assertEquals("History table should have 3 rows", 3, historyTableModel.getRowCount());
        assertEquals("run_timeList should have 2 entries", 2, run_timeList.size());
        assertEquals("run_time for second record should be 2", 2, (int) run_timeList.get(1));

        // --- Step 3: Perform Undo ---
        invokePrivateMethod("undo");

        // --- Assertions after undo ---
        assertEquals("Total balance should be back to 1000.00", 1000.0, (double) getPrivateField("total_balance"), 0.01);
        assertEquals("History table should have 1 row again", 1, historyTableModel.getRowCount());
        assertEquals("run_timeList should have 1 entry again", 1, run_timeList.size());
        // Check if input fields are restored
        assertEquals("numInput1 (Food) should be restored to 50.00", "50.00", numInput1.getText());
        assertEquals("numInput2 (Rent) should be restored to 200.00", "200.00", numInput2.getText());
        assertEquals("Selector1 should be 'Expenditure'", "Expenditure", selector1.getSelectedItem());

        // --- Step 4: Perform Undo again ---
        invokePrivateMethod("undo");

        // --- Assertions after second undo ---
        assertEquals("Total balance should be 0.00", 0.0, (double) getPrivateField("total_balance"), 0.01);
        assertEquals("History table should be empty", 0, historyTableModel.getRowCount());
        assertTrue("run_timeList should be empty", run_timeList.isEmpty());
        assertEquals("numInput2 (Wages) should be restored to 1000.00", "1000.00", numInput2.getText());
        assertEquals("Selector1 should be 'Income'", "Income", selector1.getSelectedItem());
    }
}