package com.babalova;

import java.util.*;

import com.babalova.exception.InvalidLineIndex;
import com.babalova.exception.NoArgsException;
import com.babalova.handler.*;

class Main {   
    public static void main(String[] args) {
        try {
            ArgsHandler.checkArgs(args);
            List<String> lines = InputHandler.inputHandler();
            TableCreation newTable = new TableCreation(Integer.parseInt(args[0]), args[1], args[2], args[3], args[4]);
            newTable.parseData(lines);
            newTable.checkData();
            newTable.outputSimpleTable();
            newTable.outputChains();
        }
        catch (NoArgsException e) {
            System.out.println(e.getMessage());
        }
        catch (InvalidLineIndex e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}