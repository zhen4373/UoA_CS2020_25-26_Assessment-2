package main.java;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//all "calculate" chnage to "record"

public class BudgetTool extends JPanel { 
    private JFrame topLevelFrame;
    private GridBagConstraints layouConstraints = new GridBagConstraints();

    private boolean isUndoing = false;

    private JComboBox<String> Selector1;//income or expenditure selector
    //top area
    private JLabel finance_Label,tol_balance_Label,tol_balance_value_Label;
    //input arear
    private JLabel collum1Label,collum2Label;
    private JTextField collum3Label,numInput1,numInput2,numInput3,numInput4;
    private JLabel errorLable1,errorLable2,errorLable3;
    private JComboBox<String> Selector2; // TIme
    //result arear
    private JLabel resultLabel;
    private JTextArea resultArea;
    private JLabel errorLabel4;// time format error
    //button arear
    private JButton calculateButton,clearButton,exitButton,undoButton,clearHisButton;
    //history arear
    private JLabel his_Label;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JButton his_edit_Button, his_finish_edit_Button;

    private double total_balance=0;
    //freq selector
    private JComboBox<String> freqSelector1,freqSelector2,freqSelector3;
    
    //list for label and seleter
    private final java.util.List<String> n1 = new java.util.ArrayList<>();
    private final java.util.List<String> n2 = new java.util.ArrayList<>();
    private final java.util.List<String> n3 = new java.util.ArrayList<>();
    //
    { // initial list for label and selete, put here can easily change the order
        //lable
        n1.add("Loans"); //0
        n1.add("Wages"); //1
        n1.add("Food");  //2
        n1.add("Rent");  //3
        n1.add("Other"); //4
        //selete
        n2.add("Income");     //0
        n2.add("Expenditure");//1
        n2.add("Now");        //2, for time selector

        //The position of each row in the UI can be easily changed by adjusting the order.
        n3.add("0");//group(1)
        n3.add("1");//group(2)
        n3.add("2");//group(3)
        n3.add("3");//group(4)
        n3.add("4");//group(5)
        n3.add("5");//group(6)
        n3.add("6");//group(7)
        n3.add("7");//group(8)
        n3.add("8");//group(9)
    }
    public BudgetTool(JFrame frame) {
        topLevelFrame = frame;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setupUi();
        initListeners();
    }

    private void initListeners() {
        exitButton.addActionListener(e -> System.exit(0));
        clearButton.addActionListener(e -> clear());
        undoButton.addActionListener(e -> undo());
        calculateButton.addActionListener(e -> { //record button
            tinp_state();// update time input area, 
            History_record();   //record history
            clear();            //clear input area and result area
            update_CHB_states();//enable clear_history button
        });
        clearHisButton.addActionListener(e->{
            Clear_History();//clear record
            update_CHB_states();//disable clear_history button
            clear();//clear input area and result area
        });
        Selector1.addActionListener(e -> {
            if (Selector1.getSelectedItem().equals(n2.get(0))) {//"Income"
                collum1Label.setText(n1.get(0));//'Loans'
                collum2Label.setText(n1.get(1));//'Wages'
                collum3Label.setText(n1.get(4));//'Other'
            } 
            else if (Selector1.getSelectedItem().equals(n2.get(1))) {//"Expenditure"
                collum1Label.setText(n1.get(2));//'Food'
                collum2Label.setText(n1.get(3));//'Rent'
                collum3Label.setText(n1.get(4));//'Other'
            }
            else {                                             //normally wont happen
                collum1Label.setText(n1.get(4));//'Other'
                collum2Label.setText(n1.get(4));//'Other'
                collum3Label.setText(n1.get(4));//'Other'
            }
        });
        Selector2.addActionListener(e -> {
            numInput4.setText("");//clear input area when select time
            tinp_state();//if pick "now", it will set input area to current time and disable input area
        });

        his_edit_Button.addActionListener(e ->{
            his_finish_edit_Button.setEnabled(true);
            his_edit_Button.setEnabled(false);
        });
        his_finish_edit_Button.addActionListener(e ->{
            his_finish_edit_Button.setEnabled(false);
            his_edit_Button.setEnabled(true);
        });
        // DocumentListener
        input_listener(numInput1, errorLable1);
        input_listener(numInput2, errorLable2);
        input_listener(numInput3, errorLable3);
        input_listener(collum3Label, errorLable3); // 为 "Other" 标签输入框添加监听器
        //
        update_calculate_button_state(); //initial state
        update_CHB_states();          //initial state
        tinp_state();                 //initial state
        //
    }
    private void setupUi() {
        layouConstraints.insets = new Insets(5, 5, 5, 5);
        layouConstraints.fill = GridBagConstraints.HORIZONTAL;
        String[] freqOptions = {"One-Time", "Per Week", "Per Month", "Per Year"};

        //group(0)
        String[] options = {n2.get(0), n2.get(1)};
        Selector1 = new JComboBox<>(options);
        addComponent(Selector1, Integer.parseInt(n3.get(0)), 0, 1);

        //group(1)
        collum1Label = new JLabel(n1.get(0));
        collum1Label.setHorizontalAlignment(JLabel.RIGHT);
        addComponent(collum1Label, Integer.parseInt(n3.get(1)), 0, 1);
        numInput1 = new JTextField(5);
        numInput1.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(numInput1, Integer.parseInt(n3.get(1)), 1, 1);
        freqSelector1 = new JComboBox<>(freqOptions);
        addComponent(freqSelector1, Integer.parseInt(n3.get(1)), 2, 1);
        errorLable1 = new JLabel("");
        addComponent(errorLable1, Integer.parseInt(n3.get(1)), 3, 2);

        //group(2)
        collum2Label = new JLabel(n1.get(1));
        collum2Label.setHorizontalAlignment(JLabel.RIGHT);
        addComponent(collum2Label, Integer.parseInt(n3.get(2)), 0, 1);
        numInput2 = new JTextField(5);
        numInput2.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(numInput2, Integer.parseInt(n3.get(2)), 1, 1);
        freqSelector2 = new JComboBox<>(freqOptions);
        addComponent(freqSelector2, Integer.parseInt(n3.get(2)), 2, 1);
        errorLable2 = new JLabel("");
        addComponent(errorLable2, Integer.parseInt(n3.get(2)), 3, 2);

        //group(3)
        collum3Label = new JTextField (n1.get(4));
        collum3Label.setHorizontalAlignment(JLabel.RIGHT);
        addComponent(collum3Label, Integer.parseInt(n3.get(3)), 0, 1);
        numInput3 = new JTextField(5);
        numInput3.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(numInput3, Integer.parseInt(n3.get(3)), 1, 1);
        freqSelector3 = new JComboBox<>(freqOptions);
        addComponent(freqSelector3, Integer.parseInt(n3.get(3)), 2, 1);
        errorLable3 = new JLabel("");
        addComponent(errorLable3, Integer.parseInt(n3.get(3)), 3, 2);

        //group(4)
        resultLabel = new JLabel("Total (per month):");
        resultLabel.setHorizontalAlignment(JLabel.RIGHT);
        addComponent(resultLabel, Integer.parseInt(n3.get(4)), 0, 1);
        resultArea = new JTextArea(1, 10);
        resultArea.setText("+0.00");
        resultArea.setEditable(false);
        resultArea.setBackground(this.getBackground());
        resultArea.setFont(resultArea.getFont().deriveFont(Font.BOLD, 14f));
        addComponent(resultArea, Integer.parseInt(n3.get(4)), 1, 1);
        finance_Label=new JLabel("Uknown");
        addComponent(finance_Label, Integer.parseInt(n3.get(4)), 2, 1);
        tol_balance_Label=new JLabel("Total Balance: $");
        tol_balance_Label.setHorizontalAlignment(JLabel.RIGHT);
        addComponent(tol_balance_Label, Integer.parseInt(n3.get(4)), 3, 1);
        tol_balance_value_Label=new JLabel("0.00");
        tol_balance_value_Label.setHorizontalAlignment(JLabel.LEFT);
        addComponent(tol_balance_value_Label, Integer.parseInt(n3.get(4)), 4, 1);

        //group(5)
        String[] options2 = {n2.get(2),n1.get(4)};
        Selector2 = new JComboBox<>(options2);
        addComponent(Selector2, Integer.parseInt(n3.get(5)), 0, 1);
        numInput4 = new JTextField(5);
        numInput4.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(numInput4, Integer.parseInt(n3.get(5)), 1, 1);
        errorLabel4 = new JLabel("");
        addComponent(errorLabel4, Integer.parseInt(n3.get(5)), 2, 2);

        //group(6)
        undoButton = new JButton("Undo");
        addComponent(undoButton, Integer.parseInt(n3.get(6)), 0, 1);
        calculateButton = new JButton("Rcord");
        addComponent(calculateButton, Integer.parseInt(n3.get(6)), 1, 1);
        clearButton = new JButton("Clear");
        addComponent(clearButton, Integer.parseInt(n3.get(6)), 2, 1);
        exitButton = new JButton("Exit");
        addComponent(exitButton, Integer.parseInt(n3.get(6)), 3, 1);
        clearHisButton = new JButton("Clear Histry");
        addComponent(clearHisButton, Integer.parseInt(n3.get(6)), 4,1);

        //group(7)
        his_Label = new JLabel("History",JLabel.CENTER);
        addComponent(his_Label, Integer.parseInt(n3.get(7)), 0, 5);
        his_edit_Button = new JButton("Edit");
        addComponent(his_edit_Button, Integer.parseInt(n3.get(7)), 3, 1);
        his_finish_edit_Button = new JButton("Finish");
        addComponent(his_finish_edit_Button, Integer.parseInt(n3.get(7)), 4, 1);

        //group(8)
        String[] col_Names = {"Date", "Frequency", "Type", "Value", "Total"};
        historyTableModel = new DefaultTableModel(col_Names, 0);
        historyTable = new JTable(historyTableModel);
        historyTable.setFillsViewportHeight(true);
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f)); // set font size
        historyTable.setRowHeight(25); // set row height to match the new header size

        JScrollPane scrollPane = new JScrollPane(historyTable);
        addComponent(scrollPane, Integer.parseInt(n3.get(8)), 0, 5, 10);
    }

    private void update_calculate_button_state(){      //run when error_massage() activate
        String text1 = numInput1.getText();
        String text2 = numInput2.getText();
        String text3 = numInput3.getText();
        boolean at_least_one_non_empty = !text1.isEmpty() || !text2.isEmpty() || !text3.isEmpty();
        boolean all_non_empty_are_valid =
                error_check(text1) && error_check(text2) && error_check(text3);
        calculateButton.setEnabled(at_least_one_non_empty && all_non_empty_are_valid && !isUndoing && input_time_check() && collum_label_valid());
    }
    private boolean error_check(String num){        //under error_massage() -> input_checker()
        if (num.isEmpty()){
            return true;
        }
        try {
            Double.valueOf(num);
            return Double.parseDouble(num) != 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void error_massage(JTextField input, JLabel label){ //under input_checker()
        if (label.equals(errorLable3)) {  //specific for row 3  
            if (!collum_label_valid()) {    //check label first
                label.setText("Invalid Label: Cannot be empty");
            } 
            else if (!error_check(numInput3.getText())) { //check input of "other"
                label.setText("Invalid value");
            } 
            else {
                label.setText(""); // both valid, clear error message
            }
        }
        else if (label.equals(errorLabel4)){ //specific for time, output time error message
            if (input_time_check()){
                label.setText("Fromat:yyyy:MM:dd HH:mm");
            }
            else{
                label.setText("Valid Format:yyyy:MM:dd HH:mm");
            }
        }
        else if (error_check(input.getText())) {//clear input1 and 2 error message
            label.setText("");
        } 
        else {
            label.setText("Invalid input");//output input1 and 2 error message
        }
        update_calculate_button_state(); 
    }
    private void input_listener(JTextField numInput, JLabel errorLabel){//DocumentListener for all input area
        numInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            error_massage(numInput, errorLabel);
            calculate();
        }
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            error_massage(numInput, errorLabel);
            calculate();
        }
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            error_massage(numInput, errorLabel);
            calculate();
        }
        });
    }

    private boolean collum_label_valid() {
        if (collum3Label.getText().isEmpty()){  // check if label is empty
            return false;
        }
        // by changing below code, can check if label is a number or not.Therefore keep it for further requierment 
        try {
            Double.valueOf(collum3Label.getText());// check if label is a number
            return true; // accept number 
        } catch (NumberFormatException e) {         // if label is not a number, return false
            return true; 
        }
        //
    }


    //undo
    private void undo(){

    }
    //undo

    private double getNormalizedAmount(JTextField input, JComboBox<String> frequencySelector) {
        String text = input.getText();
        if (text.isEmpty()) {
            return 0;
        }
        try {
            double value = Double.parseDouble(text);
            String frequency = (String) frequencySelector.getSelectedItem();
            switch (frequency) {
                case "Per Week":
                    return value * 4.333333333333333;//
                case "Per Year":
                    return value / 12;
                case "Per Month":
                    return value;
                case "One-Time":
                default:
                    return value;
            }
        } catch (NumberFormatException e) {
            return 0; 
        }
    }

    private void clear(){   // just clear input area and result area. Error lable will auto update when input change, therefore no need to update here
        numInput1.setText("");
        numInput2.setText("");
        numInput3.setText("");
        collum3Label.setText(n1.get(4));
        resultArea.setText("");
        if(Selector2.getSelectedItem().equals(n1.get(4))){ numInput4.setText("");}
        tinp_state();//if pick "now", it will update to current time, otherwise, it will keep the original time
        freqSelector1.setSelectedIndex(0);
        freqSelector2.setSelectedIndex(0);
        freqSelector3.setSelectedIndex(0);
        calculate();//since above code set each input to null, therefore resultArea will be +0.00 or -0.00
    } 

    //calculate
    private void calculate(){
        String seletion = (String) Selector1.getSelectedItem();
        double num1 = getNormalizedAmount(numInput1, freqSelector1);
        double num2 = getNormalizedAmount(numInput2, freqSelector2);
        double num3 = getNormalizedAmount(numInput3, freqSelector3);
        double total = num1 + num2 + num3;
        if (seletion.equals("Income")){
            resultArea.setText(String.format("+%.2f", total));
        } 
        else if (seletion.equals("Expenditure")){
            resultArea.setText(String.format("-%.2f", total));
        }
    }
    private double differenc(double num){//number, return balance
        return total_balance+=num;
    }
    //calculate

    //Histry
    private void History_record(){
        String seletion = (String) Selector1.getSelectedItem();
        double sign = seletion.equals("Expenditure") ? -1 : 1;

        double num1 = getNormalizedAmount(numInput1, freqSelector1);
        String type1 = collum1Label.getText();
        String fre1= (String) freqSelector1.getSelectedItem();

        double num2 = getNormalizedAmount(numInput2, freqSelector2);
        String type2 = collum2Label.getText();
        String fre2= (String) freqSelector2.getSelectedItem();

        double num3 = getNormalizedAmount(numInput3, freqSelector3);
        String type3 = collum3Label.getText();
        String fre3= (String) freqSelector3.getSelectedItem();

        if (num1 != 0){
            double total_1 = differenc(num1*sign);
            History_record_template(type1,num1*sign,total_1,fre1);
        }
        if (num2 != 0){
            double total_2 = differenc(num2*sign);
            History_record_template(type2,num2*sign,total_2,fre2);
        }
        if (num3 != 0){
            double total_3 = differenc(num3*sign);
            History_record_template(type3,num3*sign,total_3,fre3);
        }
    }
    private void History_record_template(String type, double num, double total, String freq){//type, number, balance, frequency
        String time = tinp_state();
        String value = String.format("%s%.2f", num > 0 ? "+" : "", num);
        String totalStr = String.format("%.2f", total);

        historyTableModel.addRow(new Object[]{time, freq, type, value, totalStr});//store history

        tol_balance_value_Label.setText(totalStr);
        if (total>=0){
            finance_Label.setText("Surplus");
            tol_balance_value_Label.setForeground(Color.BLACK);
        }
        else{
            finance_Label.setText("Deficit");
            finance_Label.setForeground(Color.RED);
            tol_balance_value_Label.setForeground(Color.RED);
        }
    }

    private void Clear_History(){ 
        historyTableModel.setRowCount(0);
        total_balance=0;
        tol_balance_value_Label.setText("0.00");
        tol_balance_value_Label.setForeground(Color.BLACK);
        finance_Label.setText("");
    }
    private void update_CHB_states(){clearHisButton.setEnabled(historyTableModel.getRowCount() > 0);}// enable clear history button if history is not empty
    //HIstry
    
    //Time
    private String Now_time(){ // return current time, format: yyyy:MM:dd HH:mm
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        Date date = new Date();
        return formatter.format(date);
    }
    private String edited_time(){ // return input time, no need to check format, since invalid format will be blocked by unable calculate button
        return numInput4.getText();
    }
    private boolean input_time_check(){ //need develop for more precise check
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm");
            formatter.parse(numInput4.getText());
            return true;
        }
        catch(ParseException e){
            return false;
        }
    }
    private String tinp_state(){ // return time input state, if user select "Now", return current time, else return input time, also used for inital state
        if (Selector2.getSelectedItem().equals(n2.get(2))) {
            numInput4.setText(Now_time());
            numInput4.setEditable(false);
            errorLabel4.setText("");
            return Now_time();
        } 
        else if (Selector2.getSelectedItem().equals(n1.get(4))) {
            numInput4.setEditable(true);
            errorLabel4.setText("Fromat:yyyy:MM:dd HH:mm");
            return edited_time();
        }
        else {
            collum3Label.setText(n1.get(4));
            return Now_time();
        }

    }
    //Time



//  no need to make any chenges
    private void addComponent(Component component, int row, int col, int width) {
        addComponent(component, row, col, width, 1);
    }
    private void addComponent(Component component, int row, int col, int width, int height) {
        layouConstraints.gridx = col;
        layouConstraints.gridy = row;
        layouConstraints.gridwidth = width;
        layouConstraints.gridheight = height;
        add(component, layouConstraints);
    }
    public static void createAndShowGUI(){
        JFrame frame = new JFrame("Budget Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new BudgetTool(frame));
        frame.setPreferredSize(new java.awt.Dimension(700, 800));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {createAndShowGUI(); });
    }
//
}