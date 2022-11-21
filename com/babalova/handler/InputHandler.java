package com.babalova.handler;

import java.util.*;

public class InputHandler {
    public static List<String> inputHandler() {
        Scanner in = new Scanner(System.in);
        List<String> lines = new ArrayList<String>();
        System.out.println("Вставьте логи GPSS World, он должен быть >= вашему М. В конце в консоли нужно дописать \"end\"");
        String curLine;
        while (!(curLine = in.nextLine()).equals("end")) {
            lines.add(curLine);
        }
        return lines;
    }
}
