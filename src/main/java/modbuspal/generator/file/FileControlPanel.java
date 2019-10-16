package modbuspal.generator.file;

import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileControlPanel extends JPanel {
    
    private FileGenerator generator;
    
    private JTextField fileNameField;
    private JTextField propertyNameField;

    public FileControlPanel(FileGenerator generator) {
        this.generator = generator;
        initComponents();
    }
    
    private void initComponents() {
        GridLayout layout = new GridLayout(2, 2);
        setLayout(layout);
        
        JLabel label1 = new JLabel("File name");
        add(label1);
        
        fileNameField = new JTextField();
        fileNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                generator.fileName = fileNameField.getText();
            }
        });
        add(fileNameField);
        
        JLabel label2 = new JLabel("Property name");
        add(label2);
        
        propertyNameField = new JTextField();
        propertyNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                generator.propertyName = propertyNameField.getText();
            }
        });
        add(propertyNameField);
    }
    
    void refresh() {
        fileNameField.setText(generator.fileName);
        propertyNameField.setText(generator.propertyName);
    }

}
