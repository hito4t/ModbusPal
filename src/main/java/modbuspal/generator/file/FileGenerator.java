package modbuspal.generator.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import modbuspal.generator.Generator;
import modbuspal.toolkit.XMLTools;

public class FileGenerator extends Generator {
    
    private static Logger logger = Logger.getLogger(FileGenerator.class.getName());

    private FileControlPanel panel;
    String fileName = "";
    String propertyName = "";
    
    private double lastValue = 0;
    
    public FileGenerator() {
        panel = new FileControlPanel(this);
    }

    @Override
    public double getValue(double time) {
        double value;
        try {
            value = getValue();
            
        } catch (Exception e1) {
            try {
                // retry once
                value = getValue();
                
            } catch (Exception e2) {
                e2.printStackTrace();
                logger.log(Level.WARNING, e2.getMessage(), e2);
                return lastValue;
            }
        }
        
        lastValue = value;
        return value;
    }
    
    private double getValue() throws Exception {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(fileName)) {
            properties.load(in);
        }
        String value = properties.getProperty(propertyName);
        return Double.parseDouble(value);
    }

    @Override
    public void loadGeneratorSettings(NodeList list) {
        Node fileNode = XMLTools.getNode(list, "file");
        fileName = XMLTools.getAttribute("name", fileNode);

        Node propertyNode = XMLTools.getNode(list, "property");
        propertyName = XMLTools.getAttribute("name", propertyNode);
        
        panel.refresh();
    }

    @Override
    public void saveGeneratorSettings(OutputStream out) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("<file name=\"" + fileName + "\"/>\r\n");
        builder.append("<property name=\"" + propertyName + "\"/>\r\n");
        out.write(builder.toString().getBytes());
    }
    
    @Override
    public JPanel getControlPanel() {
        return panel;
    }

}
