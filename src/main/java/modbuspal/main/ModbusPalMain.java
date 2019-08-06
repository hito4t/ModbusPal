package modbuspal.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import modbuspal.automation.Automation;
import modbuspal.link.ModbusLink;
import modbuspal.link.ModbusLinkListener;
import modbuspal.link.ModbusReplayLink;
import modbuspal.link.ModbusSerialLink;
import modbuspal.link.ModbusSerialLink.CommPortList;
import modbuspal.link.ModbusTcpIpLink;
import modbuspal.master.ModbusMasterRequest;
import modbuspal.slave.ModbusRegisters;
import modbuspal.slave.ModbusSlave;
import modbuspal.slave.ModbusSlaveAddress;

public class ModbusPalMain {
    
    private static Logger logger = Logger.getLogger(ModbusPalMain.class.getName());

    public static void main(String[] args) throws Exception {
        File loggingPropertiesfile = new File("logging.properties");
        if (!loggingPropertiesfile.exists()) {
            System.err.println("logging.properties doesn't exist.");
        } else {
            try (InputStream in = new FileInputStream(loggingPropertiesfile)) {
                LogManager.getLogManager().readConfiguration(in);
            }
        }
        
        if (args.length < 1) {
            ModbusPalGui.run();
            return;
        }
        
        if (args[0].equals("-install")) {
            ModbusPalGui.install();
            return;
        }
        
        File file = new File(args[0]);
        
        ModbusPalProject project = ModbusPalProject.load(file);
        System.out.println("Slaves:");
        for (ModbusSlave slave : project.getModbusSlaves()) {
            logger.info(String.format("  Slave ID=%s, Name=%s", slave.getSlaveId(), slave.getName()));
        }
        
        for (Automation automation : project.getAutomations()) {
            automation.start();
        }
        
        ModbusLink link = createLink(project);
        link.start(new ModbusLinkListener() {
            @Override
            public void linkBroken() {
                logger.info("linkBroken.");
            }
        });
        
        while (true) {
            for (ModbusSlave slave : project.getModbusSlaves()) {
                ModbusRegisters registers = slave.getHoldingRegisters();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < registers.getRowCount(); i++) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    builder.append(String.format("(%d: %d)", registers.getAddressOf(i) + 1, registers.getValue(i)));
                }
                logger.info(String.format("Slave %s : %s", slave.getName(), builder.toString()));
            }
            
            Thread.sleep(1000);
        }
        
    }
    
    
    private static ModbusLink createLink(ModbusPalProject project) throws Exception {
        switch (project.selectedLink) {
            case "TCP/IP":
                return new ModbusTcpIpLink(project, Integer.parseInt(project.linkTcpipPort));
                
            case "Serial":
                return new ModbusSerialLink(project, 
                        getCommPortIndex(project.linkSerialComId), 
                        Integer.parseInt(project.linkSerialBaudrate),
                        project.linkSerialParity,
                        project.linkSerialStopBits, 
                        project.linkSerialXonXoff,
                        project.linkSerialRtsCts);
                
            case "Replay":
                return new ModbusReplayLink(project, project.linkReplayFile);
                
            default:
                return new ModbusLink() {
                    @Override
                    public void stopMaster() {
                    }
                    @Override
                    public void stop() {
                    }
                    @Override
                    public void startMaster(ModbusLinkListener l) throws IOException {
                    }
                    @Override
                    public void start(ModbusLinkListener l) throws IOException {
                    }
                    @Override
                    public void execute(ModbusSlaveAddress dst, ModbusMasterRequest req, int timeout) throws IOException {
                    }
                };
        }
    }
    
    private static int getCommPortIndex(String portName) {
        CommPortList ports = ModbusSerialLink.getListOfCommPorts();
        for (int i = 0; i < ports.getSize(); i++) {
            if (ports.getElementAt(i).equals(portName)) {
                return i;
            }
        }
        return -1;
    }

}
