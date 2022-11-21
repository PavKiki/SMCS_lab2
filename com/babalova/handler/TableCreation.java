package com.babalova.handler;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.babalova.accumulator.Chain;
import com.babalova.accumulator.ChainStorage;
import com.babalova.accumulator.SimpleTable;
import com.babalova.exception.InvalidLineIndex;

public class TableCreation {
    
    private HashMap<Integer, SimpleTable> transacts = new HashMap<>();
    private HashMap<Float, ChainStorage> events = new HashMap<>();

    private int M;
    private String channelQueueName;
    private String channelSeizeName;
    private String serverQueueName;
    private String serverSeizeName;

    private Chain timerChain;

    public TableCreation() {
    }

    public TableCreation(int m, String channelQueueName, String channelSeizeName, String serverQueueName,
            String serverSeizeName) {
        M = m;
        this.channelQueueName = channelQueueName;
        this.channelSeizeName = channelSeizeName;
        this.serverQueueName = serverQueueName;
        this.serverSeizeName = serverSeizeName;

        timerChain = new Chain();
        timerChain.setTranscatNum(2);
        timerChain.setPriority(0);
        timerChain.setTime(M);
        timerChain.setCurrentBlock(0);
        timerChain.setNextBlock(13);
    }

    public void parseData(List<String> data) throws InvalidLineIndex{
        int index = 0;
        Pattern step = Pattern.compile("STEP 1");
        while (!step.matcher(data.get(index++)).find());
        while (!step.matcher(data.get(index++)).find());

        String input1 = "QUEUE " + channelQueueName;
        Pattern enterChannelQueue = Pattern.compile(input1);

        String input2 = "RELEASE " + channelSeizeName;
        Pattern exitChannelQueue = Pattern.compile(input2);
        
        String input3 = "QUEUE " + serverQueueName;
        Pattern enterServerQueue = Pattern.compile(input3);

        String input4 = "RELEASE " + serverSeizeName;
        Pattern exitServerQueue = Pattern.compile(input4);

        String input5 = "DEPART " + channelQueueName;
        Pattern startChannelProcessing = Pattern.compile(input5);

        String input6 = "DEPART " + serverQueueName;
        Pattern startServerProcessing = Pattern.compile(input6);

        Pattern transactNum = Pattern.compile("Halt. XN: (\\d+).");
        Pattern time = Pattern.compile("Clock:\\t*(\\d+.\\d+). Next:");

        for (; index < data.size(); index += 5) {
            if (enterChannelQueue.matcher(data.get(index-2)).find()) {
                SimpleTable acc = new SimpleTable();

                Matcher numMatch = transactNum.matcher(data.get(index - 4));
                if (!numMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на номер транзакта");

                Matcher clockMatch = time.matcher(data.get(index - 3));
                if (!clockMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на время транзакта");

                acc.setEnterChannelQueue(Double.parseDouble(clockMatch.group(1)));
                transacts.put(Integer.parseInt(numMatch.group(1)), acc);
            }
            else if (exitChannelQueue.matcher(data.get(index-2)).find()) {
                Matcher numMatch = transactNum.matcher(data.get(index - 4));
                if (!numMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на номер транзакта");

                Matcher clockMatch = time.matcher(data.get(index - 3));
                if (!clockMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на время транзакта");

                SimpleTable buf = transacts.get(Integer.parseInt(numMatch.group(1)));
                buf.setExitChannelQueue(Double.parseDouble(clockMatch.group(1)));
            }
            else if (enterServerQueue.matcher(data.get(index-2)).find()) {
                Matcher numMatch = transactNum.matcher(data.get(index - 4));
                if (!numMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на номер транзакта");

                Matcher clockMatch = time.matcher(data.get(index - 3));
                if (!clockMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на время транзакта");

                transacts.get(Integer.parseInt(numMatch.group(1))).setEnterServerQueue(Double.parseDouble(clockMatch.group(1)));
            }
            else if (exitServerQueue.matcher(data.get(index-2)).find()) {
                Matcher numMatch = transactNum.matcher(data.get(index - 4));
                if (!numMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на номер транзакта");

                Matcher clockMatch = time.matcher(data.get(index - 3));
                if (!clockMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на время транзакта");

                transacts.get(Integer.parseInt(numMatch.group(1))).setExitServerQueue(Double.parseDouble(clockMatch.group(1)));
            }
            else if (startChannelProcessing.matcher(data.get(index-2)).find()) {
                Matcher numMatch = transactNum.matcher(data.get(index - 4));
                if (!numMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на номер транзакта");

                Matcher clockMatch = time.matcher(data.get(index - 3));
                if (!clockMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на время транзакта");

                transacts.get(Integer.parseInt(numMatch.group(1))).setStartChannelProcessing(Double.parseDouble(clockMatch.group(1)));
            }
            else if (startServerProcessing.matcher(data.get(index-2)).find()) {
                Matcher numMatch = transactNum.matcher(data.get(index - 4));
                if (!numMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на номер транзакта");

                Matcher clockMatch = time.matcher(data.get(index - 3));
                if (!clockMatch.find()) throw new InvalidLineIndex("Не ту строку анализируешь на время транзакта");

                transacts.get(Integer.parseInt(numMatch.group(1))).setStartServerProcessing(Double.parseDouble(clockMatch.group(1)));
            }
            else continue;
        }
    }

    public void outputSimpleTable() {
        StringBuilder Tsum = new StringBuilder("Tsum = {");
        StringBuilder Tgen = new StringBuilder("Tgen = {");
        StringBuilder Tser = new StringBuilder("Tser = {");
        float buf = 0.0f;

        System.out.printf("%-5s%-10s%-10s%-10s\n", "Num", "Gen", "Channel", "Server");
        for (Map.Entry<Integer, SimpleTable> entry : transacts.entrySet()) {
            float gen = (float)(entry.getValue().getEnterChannelQueue());
            float channel = (float)(entry.getValue().getExitChannelQueue() - entry.getValue().getStartChannelProcessing());
            float server = (float)(entry.getValue().getExitServerQueue() - entry.getValue().getStartServerProcessing());
            System.out.printf("%-5d%-10.1f%-10.1f%-10.1f\n", entry.getKey(), gen, channel, server);

            Tsum.append(String.format("%.1f", gen) + ", ");
            Tgen.append(String.format("%.1f", (gen - buf)) + ", ");
            Tser.append(String.format("%.1f", server) + ", ");
            buf = gen;
        }
        Tsum.setCharAt(Tsum.length()-2, '}');
        Tgen.setCharAt(Tgen.length()-2, '}');
        Tser.setCharAt(Tser.length()-2, '}');

        System.out.println(Tsum.toString());
        System.out.println(Tgen.toString());
        System.out.println(Tser.toString());
    }

    public void checkData() {
        ouputRowTable();

        Scanner in = new Scanner(System.in);
        String answer;
        do {
            System.out.println("Sometimes values turn into zero (<---). Anything to fix?\nyes - y\nno - n");
        } while (!(answer = in.nextLine()).equals("y") && !answer.equals("Y") && !answer.equals("n") && !answer.equals("N"));

        if (answer.equals("y") || answer.equals("Y")) {
            do {
                fixData(in);
                System.out.println("Anything more to fix?\nyes - y\nno - n");
            } while ((answer = in.nextLine()).equals("y") || answer.equals("Y"));
        }
    }

    private void fixData(Scanner in) {
        int y;
        String x;
        Double value;
        while (true) {
            System.out.print("Enter number of transact: ");
            y = in.nextInt();
            in.nextLine();
            System.out.print("Enter name of column (as in table above): ");
            x = in.nextLine();
            System.out.print("Enter new value: ");
            value = (double)in.nextFloat();
            in.nextLine();

            if (x.equals("Enter channel")) {
                transacts.get(y).setEnterChannelQueue(value);
                break;
            }
            else if (x.equals("Proc channel")) {
                transacts.get(y).setStartChannelProcessing(value);
                break;
            }
            else if (x.equals("Exit channel")) {
                transacts.get(y).setExitChannelQueue(value);
                break;
            }
            else if (x.equals("Enter server")) {
                transacts.get(y).setEnterServerQueue(value);
                break;
            }
            else if (x.equals("Proc server")) {
                transacts.get(y).setStartServerProcessing(value);
                break;
            }
            else if (x.equals("Exit server")) {
                transacts.get(y).setExitServerQueue(value);
                break;
            }
            else System.out.println("Wrong column name. Try again");
        }
        ouputRowTable();
    }

    private void ouputRowTable() {
        System.out.printf("%-5s%-15s%-15s%-15s%-15s%-15s%-15s\n", "Num", "Enter channel", "Proc channel", "Exit channel", "Enter server", 
            "Proc server", "Exit server");
        for (Map.Entry<Integer, SimpleTable> entry : transacts.entrySet()) {
            System.out.printf("%-5d%-15.1f%-15.1f%-15.1f%-15.1f%-15.1f%-15.1f", entry.getKey(), entry.getValue().getEnterChannelQueue(), 
                entry.getValue().getStartChannelProcessing(), entry.getValue().getExitChannelQueue(), entry.getValue().getEnterServerQueue(), 
                    entry.getValue().getStartServerProcessing(), entry.getValue().getExitServerQueue());
            if (entry.getValue().getEnterChannelQueue() * entry.getValue().getStartChannelProcessing() * entry.getValue().getExitChannelQueue() *
                entry.getValue().getEnterServerQueue() * entry.getValue().getStartServerProcessing() * entry.getValue().getExitServerQueue() == 0) {
                    System.out.printf("%-5s<---", "");
                    if (entry.getValue().getEnterChannelQueue() == 0) {
                        System.out.printf("(%d, %s)\n", entry.getKey(), "Enter channel");
                    }
                    else if (entry.getValue().getStartChannelProcessing() == 0) {
                        System.out.printf("(%d, %s)\n", entry.getKey(), "Proc channel");
                    }
                    else if (entry.getValue().getExitChannelQueue() == 0) {
                        System.out.printf("(%d, %s)\n", entry.getKey(), "Exit channel");
                    }
                    else if (entry.getValue().getEnterServerQueue() == 0) {
                        System.out.printf("(%d, %s)\n", entry.getKey(), "Enter server");
                    }
                    else if (entry.getValue().getStartServerProcessing() == 0) {
                        System.out.printf("(%d, %s)\n", entry.getKey(), "Proc server");
                    }
                    else if (entry.getValue().getExitServerQueue() == 0) {
                        System.out.printf("(%d, %s)\n", entry.getKey(), "Exit server");
                    }
                }
            else {System.out.println();}
        }
    }

    private float findPreviousTime(double time) {
        float min = Float.MAX_VALUE;
        float tmpTime = 0.0f;
        for (Map.Entry<Float, ChainStorage> event : events.entrySet()) {
            float buf = (float)time - event.getKey();
            if (buf <= 0) continue;
            if (buf < min) {
                min = buf;
                tmpTime = event.getKey();
            }
        }
        return tmpTime;
    }
    
    private void formEventsTable() {
        ChainStorage firstChainStorage = new ChainStorage();
        firstChainStorage.addToFuture(timerChain);
        events.put(0.0f, firstChainStorage);

        int[] curBlocksCurrent = {1, 5, 10};
        int[] curBlocksFuture = {5, 10, 12};
        int[] futBlocksCurrent = {0, 5, 10};
        int[] futBlocksFuture = {1, 6, 11};
        for (Map.Entry<Integer, SimpleTable> entry : transacts.entrySet()) {
            int curBlockCur = 0;
            int curBlockFut = 0;
            int futBlockFut = 0;
            int futBlockCur = 0;
            float timeBuf;

            if (entry.getValue().getStartChannelProcessing() == 0) break;
            Chain curChain = new Chain();
            curChain.setTranscatNum(entry.getKey());
            curChain.setPriority(0);
            curChain.setTime(entry.getValue().getStartChannelProcessing());
            curChain.setCurrentBlock(curBlocksCurrent[curBlockCur++ % 3]);
            curChain.setNextBlock(curBlocksFuture[curBlockFut++ % 3]);

            events.get(findPreviousTime(curChain.getTime())).addToFuture(
                curChain.cloneInPast(futBlocksCurrent[futBlockCur++], futBlocksFuture[futBlockFut++]));

            if (events.containsKey(timeBuf = (float)curChain.getTime())) {
                events.get(timeBuf).addToCurrent(curChain);
                events.get(timeBuf).addToFuture(curChain.cloneInPast(futBlocksCurrent[futBlockCur++], futBlocksFuture[futBlockFut++], entry.getValue().getStartServerProcessing()));
            }
            else {
                ChainStorage curChainStorage = new ChainStorage();
                curChainStorage.addToCurrent(curChain);
                curChainStorage.addToFuture(timerChain);
                curChainStorage.addToFuture(curChain.cloneInPast(futBlocksCurrent[futBlockCur++], futBlocksFuture[futBlockFut++], entry.getValue().getStartServerProcessing()));
                events.put((float)(curChain.getTime()), curChainStorage);
            }

            if (entry.getValue().getStartServerProcessing() == 0) break;
            Chain nextChain = curChain.clone();
            nextChain.setCurrentBlock(curBlocksCurrent[curBlockCur++ % 3]);
            nextChain.setNextBlock(curBlocksFuture[curBlockFut++ % 3]);
            nextChain.setTime(entry.getValue().getStartServerProcessing());

            if (events.containsKey(timeBuf = (float)nextChain.getTime())) {
                events.get(timeBuf).addToCurrent(nextChain);
                events.get(timeBuf).addToFuture(nextChain.cloneInPast(futBlocksCurrent[futBlockCur++], futBlocksFuture[futBlockFut++], entry.getValue().getExitServerQueue()));
            }
            else {
                ChainStorage nextChainStorage = new ChainStorage();
                nextChainStorage.addToCurrent(nextChain);
                nextChainStorage.addToFuture(timerChain);
                nextChainStorage.addToFuture(nextChain.cloneInPast(futBlocksCurrent[futBlockCur++], futBlocksFuture[futBlockFut++], entry.getValue().getExitServerQueue()));
                events.put((float)(nextChain.getTime()), nextChainStorage);
            }

            if (entry.getValue().getExitServerQueue() == 0) break;
            Chain lastChain = nextChain.clone();
            lastChain.setCurrentBlock(curBlocksCurrent[curBlockCur++ % 3]);
            lastChain.setNextBlock(curBlocksFuture[curBlockFut++ % 3]);
            lastChain.setTime(entry.getValue().getExitServerQueue());

            if (events.containsKey(timeBuf = (float)lastChain.getTime())) {
                events.get(timeBuf).addToCurrent(lastChain);
            }
            else {
                ChainStorage lastChainStorage = new ChainStorage();
                lastChainStorage.addToCurrent(lastChain);
                lastChainStorage.addToFuture(timerChain);
                events.put((float)(lastChain.getTime()), lastChainStorage);
            }
        }
    }

    private void postProduction(List<Float> keys) {
        Collections.sort(events.get(keys.get(0)).getCurrent(), (a1, a2) -> {
            return a1.getTranscatNum() - a2.getTranscatNum();
        });
        Collections.sort(events.get(keys.get(0)).getFuture(), (a1, a2) -> {
            return a1.getTranscatNum() - a2.getTranscatNum();
        });
        for (int i = 1; i < keys.size(); i++) {
            for (int k = 0; k < events.get(keys.get(i)).getFuture().size(); k++) {
                Chain futChain = events.get(keys.get(i)).getFuture().get(k);
                if (futChain.equals(timerChain)) continue;
                int j = i + 1;
                while (j < keys.size() && keys.get(j) < (float)futChain.getTime()) {
                    if (!events.get(keys.get(j)).isThereThisChainInFuture(futChain)) events.get(keys.get(j)).addToFuture(futChain);
                    j++;
                }
            }
            Collections.sort(events.get(keys.get(i)).getCurrent(), (a1, a2) -> {
                return a1.getTranscatNum() - a2.getTranscatNum();
            });
            Collections.sort(events.get(keys.get(i)).getFuture(), (a1, a2) -> {
                return a1.getTranscatNum() - a2.getTranscatNum();
            });
        }
    }

    public void outputChains() {
        formEventsTable();
        int num = 1;
        System.out.printf("%-5s%-10s%-30s%-30s\n", "Num", "Time", "Current", "Future");
        List<Float> sortedKeys = new ArrayList<>(events.keySet());
        Collections.sort(sortedKeys);
        postProduction(sortedKeys);
        for (Float key: sortedKeys) {
            System.out.printf("%-5d%-10.1f", num++, key);
            int height = Math.max(events.get(key).getCurrent().size(), events.get(key).getFuture().size());
            for (int i = 0; i < height; i++) {
                boolean curFlag = false;
                if (i < events.get(key).getCurrent().size()) {
                    curFlag = true;
                    if (i == 0) System.out.printf("%-30s", events.get(key).getCurrent().get(i).toString());
                    else System.out.printf("%-15s%-30s", "", events.get(key).getCurrent().get(i).toString());
                }
                if (i < events.get(key).getFuture().size()) {
                    if (curFlag) System.out.printf("%-30s", events.get(key).getFuture().get(i).toString());
                    else if (i == 0) {
                        System.out.printf("%-30s%-30s", "", events.get(key).getFuture().get(i).toString());
                    }
                    else {
                        System.out.printf("%-45s%-30s", "", events.get(key).getFuture().get(i).toString());
                    }
                }
                System.out.println();
            }
        }
    }

    public HashMap<Integer, SimpleTable> getTransacts() {
        return transacts;
    }
}
