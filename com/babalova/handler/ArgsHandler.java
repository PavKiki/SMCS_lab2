package com.babalova.handler;

import com.babalova.exception.NoArgsException;

public class ArgsHandler{
    public static void checkArgs(String[] args) throws NoArgsException {
        if (args.length != 5) throw new NoArgsException(
            "Должно быть 5 аргументов: 1 - М, 2 - Название очереди канала, 3 - Название блока seize канала, 3 - Название очереди сервера, 4 - Название блока seize сервера");
    }
}