package banking;

import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    //static final String DUMMY_FILENAME = "db.s3db";
    // main menu
    static final int CMD_CREATE = 1;
    static final int CMD_LOGIN = 2;

    // user menu
    static final int CMD_BALANCE = 1;
    static final int CMD_ADD_INCOME = 2;
    static final int CMD_DO_TRANSFER = 3;
    static final int CMD_CLOSE_ACCOUNT = 4;
    static final int CMD_LOG_OUT = 5;
    static final int CMD_EXIT = 0;

    public static void main(String[] args) {
        int input;
        CryptoBank cryptoBank = new CryptoBank();
        DBBank dbBank = new DBBank(args[1]);

        generalMenu:
        do {
            printMenu();
            input = Integer.parseInt(scanner.nextLine());

            switch (input) {
                case CMD_CREATE:
                    //generate account
                    Account newAccount = cryptoBank.generateAccount();
                    //save account in DB
                    dbBank.createAccount(newAccount);
                    System.out.println(generateCreateAccountMsg(newAccount));
                    break;
                case CMD_LOGIN:
                    System.out.println("Enter your card number:");
                    String inputCardNr = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    int inputPin = Integer.parseInt(scanner.nextLine());

                    Account candidateAccount = new Account(inputCardNr, inputPin);
                    int token = dbBank.login(candidateAccount);

                    if (token > 0) {
                        do {
                            printAccountMenu();
                            input = Integer.parseInt(scanner.nextLine());

                            if (input == CMD_BALANCE) {
                                System.out.println("\nBalance: " + dbBank.getBalance(candidateAccount));
                            } else if (input == CMD_ADD_INCOME) {
                                System.out.println("Enter income:");
                                int addSumm = Integer.parseInt(scanner.nextLine());
                                dbBank.addIncome(candidateAccount, addSumm);
                                System.out.println("Income was added!");
                            } else if (input == CMD_DO_TRANSFER) {
                                System.out.println("Transfer\n" +
                                        "Enter card number:");
                                Account toCard = new Account(scanner.nextLine(), 0);
                                if (toCard.cardNumber.equals(inputCardNr)) {
                                    System.out.println("You can't transfer money to the same account!");
                                    continue;
                                }
                                if (!cryptoBank.luhnAlgorithmCheck(toCard.cardNumber)) {
                                    System.out.println("Probably you made mistake in the card number. Please try again!");
                                    continue;
                                }
                                //If the receiver's card number doesn’t exist,
                                // you should output: “Such a card does not exist.”
                                if (!dbBank.cardExists(toCard.cardNumber)) {
                                    System.out.println("Such a card does not exist.");
                                    continue;
                                }
                                System.out.println("Enter how much money you want to transfer:");
                                int transferSumm = Integer.parseInt(scanner.nextLine());
                                if (dbBank.getBalance(candidateAccount) < transferSumm) {
                                    System.out.println("Not enough money!");
                                    continue;
                                }

                                dbBank.transfer(candidateAccount, toCard, transferSumm);
                                System.out.println("Success!");

                            } else if (input == CMD_CLOSE_ACCOUNT) {
                                dbBank.closeAccount(candidateAccount);
                                System.out.println("The account has been closed!");
                                break generalMenu;
                            } else if (input == CMD_EXIT) {
                                break generalMenu;
                            }

                        } while (input != CMD_LOG_OUT);
                    } else {
                        break;
                    }

                    System.out.println("\nYou have successfully logged out!");
                    break;
            }
        } while(input != CMD_EXIT);

        System.out.println("Bye!");

    }

    private static String generateCreateAccountMsg(Account account) {
        return String.format("Your card has been created\n" +
                "Your card number:\n" +
                "%s\n" +
                "Your card PIN:\n" +
                "%d\n", account.cardNumber, account.pin);
    }

    private static void printMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    private static void printAccountMenu() {
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
    }
}
