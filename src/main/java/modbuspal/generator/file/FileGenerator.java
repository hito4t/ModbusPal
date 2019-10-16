package modbuspal.generator.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JPanel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import modbuspal.generator.Generator;
import modbuspal.toolkit.XMLTools;

public class FileGenerator extends Generator {
    
    private FileControlPanel panel;
    String fileName = "";
    String propertyName = "";
    
    public FileGenerator() {
        panel = new FileControlPanel(this);
    }

    @Override
    public double getValue(double time) {
        try {
            Properties properties = new Properties();
            try (InputStream in = new FileInputStream(fileName)) {
                properties.load(in);
            }
            String value = properties.getProperty(propertyName);
            return Double.parseDouble(value);
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
