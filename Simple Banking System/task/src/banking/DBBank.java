package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Random;

public class DBBank {
    Connection conn;

    public DBBank(String filename) {
        String url = "jdbc:sqlite:" + filename;

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try {
            this.conn = dataSource.getConnection();
            if (conn.isValid(5)) {
                System.out.println("Connection is valid.");
            }
            try (Statement statement = conn.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createAccount(Account account) {
        String sql = "INSERT INTO card(number, pin) VALUES(?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.cardNumber);
            pstmt.setString(2, String.valueOf(account.pin));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // tries to login to bank with given account
    // returns token if success, -1 otherwise
    public int login(Account candidateAccount) {
        Random random = new Random();

        String sql = "SELECT number, pin FROM card WHERE number = ? AND pin = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidateAccount.cardNumber);
            pstmt.setString(2, String.valueOf(candidateAccount.pin));
            ResultSet rs = pstmt.executeQuery();

            int i = 0;
            while (rs.next()) {
                i++;
            }

            if (i == 0) {
                System.out.println("Wrong card number or PIN!\n");
                return -1;
            }

            int token = random.nextInt(9999 - 1000 + 1) + 1000;
            System.out.println("You have successfully logged in!\n");
            return token;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getBalance(Account candidateAccount) {
        String sql = "SELECT balance FROM card WHERE number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidateAccount.cardNumber);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    public void addIncome(Account candidateAccount, int addSumm) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addSumm);
            pstmt.setString(2, candidateAccount.cardNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean cardExists(String toCard) {
        String sql = "SELECT number FROM card WHERE number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, toCard);
            ResultSet rs = pstmt.executeQuery();

            int i = 0;
            while (rs.next()) {
                i++;
            }

            return i > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void transfer(Account fromAccount, Account toAccount, int transferSumm) {
        //subtract from candidate
        addIncome(fromAccount, -transferSumm);
        //add to transfer
        addIncome(toAccount, transferSumm);
    }

    public void closeAccount(Account candidateAccount) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidateAccount.cardNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
