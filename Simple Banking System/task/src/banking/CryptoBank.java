package banking;

import java.util.ArrayList;
import java.util.Random;

public class CryptoBank {
    private String generateCardNumber() {
        Random random = new Random();

        int mii; //Major Industry Identifier
        int accountID;
        int checksum;
        String cardNumber;

        mii = 400000;
        accountID = random.nextInt(999999999 - 100000000 + 1) + 100000000;
        checksum = generateChecksum(mii, accountID);
        cardNumber = "" + mii + accountID + checksum;
        return cardNumber;
    }

    private int generateChecksum(int mii, int accountID){
        //have mii and accountID in one array
        ArrayList<Integer> aiBIN = new ArrayList<>();
        String[] miiStr = String.valueOf(mii).split("");
        String[] accountIDStr = String.valueOf(accountID).split("");

        for (String e: miiStr) {
            aiBIN.add(Integer.parseInt(e));
        }
        for (String e: accountIDStr) {
            aiBIN.add(Integer.parseInt(e));
        }

        ArrayList<Integer> aiBINTemp = aiBIN;
        //multiply odd by 2
        for (int i = 0; i < aiBINTemp.size(); i = i + 2) {
            aiBINTemp.set(i, aiBINTemp.get(i) * 2);
        }

        //subtract 9 from numbers > 9
        for (int j = 0; j < aiBINTemp.size(); j++) {
            if (aiBINTemp.get(j) > 9) {
                aiBINTemp.set(j, aiBINTemp.get(j) - 9);
            }
        }

        //Add all numbers
        int sum = 0;
        for (Integer integer : aiBINTemp) {
            sum += integer;
        }

        String[] sumTOString = String.valueOf(sum).split("");

        int lastSiffra = Integer.parseInt(sumTOString[sumTOString.length - 1]);
        if (lastSiffra != 0) {
            return 10 - lastSiffra;
        } else {
            return 0;
        }
    }

    private int generatePin() {
        Random random = new Random();
        return random.nextInt(9999 - 1000 + 1) + 1000;
    }

    public Account generateAccount() {
        return new Account(generateCardNumber(), generatePin());
    }

    public boolean luhnAlgorithmCheck(String cardNumber) {
        ArrayList<Integer> cardNumberList = new ArrayList<>();
        String[] accountIDStr = String.valueOf(cardNumber).split("");
        for (String e: accountIDStr) {
            cardNumberList.add(Integer.parseInt(e));
        }

        ArrayList<Integer> cardNumberListTemp = cardNumberList;
        //multiply odd by 2
        for (int i = 0; i < cardNumberListTemp.size(); i = i + 2) {
            cardNumberListTemp.set(i, cardNumberListTemp.get(i) * 2);
        }

        //subtract 9 from numbers > 9
        for (int j = 0; j < cardNumberListTemp.size(); j++) {
            if (cardNumberListTemp.get(j) > 9) {
                cardNumberListTemp.set(j, cardNumberListTemp.get(j) - 9);
            }
        }

        //Add all numbers
        int sum = 0;
        for (Integer integer : cardNumberListTemp) {
            sum += integer;
        }

        String[] sumTOString = String.valueOf(sum).split("");

        int lastDigit = Integer.parseInt(sumTOString[sumTOString.length - 1]);

        return lastDigit == 0;
    }
}
