package modbuspal.main;

import java.io.File;

import modbuspal.automation.Automation;
import modbuspal.slave.ModbusRegisters;
import modbuspal.slave.ModbusSlave;

public class ModbusPalCui {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("You need to specify ModbusPal project file.");
            System.exit(1);
        }
        
        File file = new File(args[0]);
        
        ModbusPalProject project = ModbusPalProject.load(file);
        System.out.println("Slaves:");
        for (ModbusSlave slave : project.getModbusSlaves()) {
            System.out.println(String.format("  Slave ID=%s, Name=%s", slave.getSlaveId(), slave.getName()));
        }
        
        for (Automation automation : project.getAutomations()) {
            automation.start();
        }
        
        
        while (true) {
            for (ModbusSlave slave : project.getModbusSlaves()) {
                ModbusRegisters registers = slave.getHoldingRegisters();
                for (int i = 0; i < registers.getRowCount(); i++) {
                    System.out.println(String.format("Slave=%s, Address=%d, Value=%d", slave.getName(), registers.getAddressOf(i), registers.getValue(i)));
                }
            }
            
            Thread.sleep(1000);
        }
        
    }

}
