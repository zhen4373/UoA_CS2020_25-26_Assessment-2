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

    private boolean Undoing = false; // store state

    private JComboBox<String> Selector1;//income or expenditure selector
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
    //Time format
    private final String time_format = "yyyy/MM/dd HH:mm";
    //list for label and seleter
    private final java.util.List<String> n1 = new java.util.ArrayList<>();
    private final java.util.List<String> n2 = new java.util.ArrayList<>();
    private final java.util.List<String> n3 = new java.util.ArrayList<>();
    private final java.util.List<String> selection_Records = new java.util.ArrayList<>();//store selection records
    private final java.util.List<Integer> his_row_number = new java.util.ArrayList<>();//store history row number, direct relate with history table
    private final java.util.List<Integer> run_timeList = new java.util.ArrayList<>();//store undo index value
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
        freqSelector1.addActionListener(e -> calculate());
        freqSelector2.addActionListener(e -> calculate());
        freqSelector3.addActionListener(e -> calculate());
        // DocumentListener
        input_listener(numInput1, errorLable1);
        input_listener(numInput2, errorLable2);
        input_listener(numInput3, errorLable3);
        input_listener(numInput4, errorLabel4);
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
        String[] options2 = {n2.get(2),n1.get(4)};//"now" and "other
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
        calculateButton = new JButton("Record");
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
    //state change and error check
    private void update_calculate_button_state(){      //run when error_massage() activate, making sure number_input are not empty and valid
        String text1 = numInput1.getText();
        String text2 = numInput2.getText();
        String text3 = numInput3.getText();
        boolean at_least_one_non_empty = !text1.isEmpty() || !text2.isEmpty() || !text3.isEmpty();
        boolean all_non_empty_are_valid =
                error_check(text1) && error_check(text2) && error_check(text3);
        calculateButton.setEnabled(at_least_one_non_empty && all_non_empty_are_valid && !Undoing && input_time_check() && collum_label_valid());
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
                label.setText("Valid");
            }
            else{
                label.setText("Valid Format is "+time_format);
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
    private boolean collum_label_valid() {//for row 3 label, check if label is empty or not
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
    //state change and error check

    //undo
    private void set_previous_text(int row_num, String freq, String type, String value){//need to based on frequency to chnage the value back!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        switch (row_num) {
            case 1 -> {
                numInput1.setText(anti_normalize(Double.parseDouble(value), freq));
                freqSelector1.setSelectedItem(freq);
            }
            case 2 -> {
                numInput2.setText(anti_normalize(Double.parseDouble(value), freq));
                freqSelector2.setSelectedItem(freq);
            }
            case 3 -> {
                numInput3.setText(anti_normalize(Double.parseDouble(value), freq));
                freqSelector3.setSelectedItem(freq);
                collum3Label.setText(type);
            }
            default -> System.out.println("error for undo(set_previous_text)");//only for debug
        }
    }
    private void undo_selection_and_time_part(){ //done recently
        if (selection_Records.size() < 2 && historyTableModel.getRowCount() <=0){// prevent undo when no previous selection
            System.err.println("No previous selection, no need to undo");
            return;
        }
        String time_undo = historyTableModel.getValueAt(historyTableModel.getRowCount() -1, 0).toString(); // time in history table
        System.out.println(time_undo+"---get time done");//only for debug
        String selection_1= selection_Records.get(selection_Records.size() -2);// get previous selection(income or expenditure)
        System.out.println(selection_1+"---get s1 done");//only for debug
        String selection_2= selection_Records.get(selection_Records.size() -1);//get previous selection(now or other)
        System.out.println(selection_2+"---get s2 done");//only for debug
        
        // selection part, include time input
        Selector1.setSelectedItem(selection_1);//set previous selection(income or expenditure)

        if (!selection_2.equals(n2.get(2))){//check previous selection is equal 'now' or 'other'
            Selector2.setSelectedItem(selection_2);// if not equal, set to previous selection
            numInput4.setText(time_undo);          // set to previous time
        }
        else{
            Selector2.setSelectedItem(n2.get(2));// set to 'now', auto update to current time, so no need to update here  
        }
        //remove last 2 item , -1 is time, -2 is selection(income or expenditure)
        selection_Records.remove(selection_Records.size()-1); // remove time
        selection_Records.remove(selection_Records.size()-1); // remove selection(income or expenditure)

    }
    private void undo_input_part(){
        int lastTransactionSize = run_timeList.get(run_timeList.size() - 1);
        for (int i = 0; i < lastTransactionSize; i++) {
            int lastRowIndex = historyTableModel.getRowCount() - 1;
            if (lastRowIndex < 0) break; // Safety break

            String freq_undo = historyTableModel.getValueAt(lastRowIndex, 1).toString();
            String type_undo = historyTableModel.getValueAt(lastRowIndex, 2).toString();
            String value_str_undo = historyTableModel.getValueAt(lastRowIndex, 3).toString();

            double value_to_undo = Double.parseDouble(value_str_undo);
            total_balance -= value_to_undo;

            int row_num_undo = his_row_number.get(his_row_number.size() - 1);

            String absolute_value_str = String.format("%.2f", Math.abs(value_to_undo));
            set_previous_text(row_num_undo, freq_undo, type_undo, absolute_value_str);

            historyTableModel.removeRow(lastRowIndex);
            his_row_number.remove(his_row_number.size() - 1);
        }
        run_timeList.remove(run_timeList.size() - 1);
    }

    private void undo(){
        if (run_timeList.isEmpty()) return;
        Undoing = true;//disable calculate(record) button
        clear();
        undo_selection_and_time_part();
        undo_input_part();//set previous input
        finance_update(total_balance);
        calculate();
        Undoing = false;//enable calculate(record) button
        update_CHB_states();
        update_calculate_button_state();

    }
    //undo

    private double normalize(JTextField input, JComboBox<String> frequencySelector) { // need changes
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

    private String anti_normalize(double value, String frequency) {
        switch (frequency) {
            case "Per Week":
                return String.format("%.2f", value / 4.333333333333333);
            case "Per Year":
                return String.format("%.2f", value * 12);
            case "Per Month":
                return String.format("%.2f", value);
            case "One-Time":
            default:
                return String.format("%.2f", value);
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
        finance_update(total_balance);
    } 

    //calculate
    private void calculate(){ // only involve result area, not involve history
        String seletion = (String) Selector1.getSelectedItem();
        double num1 = normalize(numInput1, freqSelector1);
        double num2 = normalize(numInput2, freqSelector2);
        double num3 = normalize(numInput3, freqSelector3);
        double total = num1 + num2 + num3;
        if (seletion.equals("Income")){
            resultArea.setText(String.format("+%.2f", total));
            double tmep_total_balance = total_balance+total;
            finance_update(tmep_total_balance);
        } 
        else if (seletion.equals("Expenditure")){
            resultArea.setText(String.format("-%.2f", total));
            double tmep_total_balance = total_balance-total;
            finance_update(tmep_total_balance);
        }
    }
    private double differenc(double num){//number, return balance
        return total_balance+=num;
    }
    //calculate

    //Histry
    private void History_record(){
        int run_time=0; // to know how many input has been recorded
        String seletion = (String) Selector1.getSelectedItem();
        double sign = seletion.equals("Expenditure") ? -1 : 1;

        double num1 = normalize(numInput1, freqSelector1);
        String type1 = collum1Label.getText();
        String fre1= (String) freqSelector1.getSelectedItem();

        double num2 = normalize(numInput2, freqSelector2);
        String type2 = collum2Label.getText();
        String fre2= (String) freqSelector2.getSelectedItem();

        double num3 = normalize(numInput3, freqSelector3);
        String type3 = collum3Label.getText();
        String fre3= (String) freqSelector3.getSelectedItem();

        if (num1 != 0){
            double total_1 = differenc(num1*sign);
            History_record_template(type1,num1*sign,total_1,fre1,1);
            run_time++;
        }
        if (num2 != 0){
            double total_2 = differenc(num2*sign);
            History_record_template(type2,num2*sign,total_2,fre2,2);
            run_time++;
        }
        if (num3 != 0){
            double total_3 = differenc(num3*sign);
            History_record_template(type3,num3*sign,total_3,fre3,3);
            run_time++;
        }
        run_timeList.add(run_time);//update how many input has been recorded
    }
    private void History_record_template(String type, double num, double total, String freq, int row_number){//type, number, balance, frequency, row number(to store which row user input)
        String time = tinp_state();
        String value = String.format("%s%.2f", num > 0 ? "+" : "", num);
        String totalStr = String.format("%.2f", total);

        historyTableModel.addRow(new Object[]{time, freq, type, value, totalStr});//store history
        // store slection
        String selection = (String) Selector1.getSelectedItem();//'Income' or 'Expenditure'
        String selection_2 = (String) Selector2.getSelectedItem();// 'now' or 'other'
        selection_Records.add(selection);//1
        selection_Records.add(selection_2);//2
        // store row number
        his_row_number.add(row_number);//direct relate with history table
        //
        finance_update(total);
    }

    private void Clear_History(){ 
        historyTableModel.setRowCount(0);
        total_balance=0;
        tol_balance_value_Label.setText("0.00");
        tol_balance_value_Label.setForeground(Color.BLACK);
        finance_Label.setText("");
    }
    private void update_CHB_states(){//enable clear history button if history is not empty, same for undo button
        clearHisButton.setEnabled(historyTableModel.getRowCount() > 0);
        undoButton.setEnabled(historyTableModel.getRowCount() >0);
    }// enable clear history button if history is not empty, same for undo button

    //HIstry
    
    //Time
    private String Now_time(){ // return current time
        SimpleDateFormat formatter = new SimpleDateFormat(time_format);
        Date date = new Date();
        return formatter.format(date);
    }
    private String edited_time(){ // return input time, no need to check format, since invalid format will be blocked by unable calculate button
        return numInput4.getText();
    }
    private boolean input_time_check(){ //format check for time input
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(time_format);//set accept format
            String input = numInput4.getText();//get input
            formatter.parse(input); //parse input, if invalid format, will throw exception
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
            errorLabel4.setText("Format:"+time_format);
            return edited_time();
        }
        else {
            collum3Label.setText(n1.get(4));
            return Now_time();
        }

    }
    //Time
    private void finance_update(double total){
        String totalStr_temp= String.format("%.2f", total);  
        tol_balance_value_Label.setText(totalStr_temp);  
        if (total>=0){                              // set finance label to surplus
            finance_Label.setText("Surplus");
            finance_Label.setForeground(Color.BLACK);
            tol_balance_value_Label.setForeground(Color.BLACK);
        }
        else{                                      // set finance label to deficit
            finance_Label.setText("Deficit");
            finance_Label.setForeground(Color.RED);
            tol_balance_value_Label.setForeground(Color.RED);
        }
    }

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