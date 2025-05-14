import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SlotMachineGUI {
    private static final String[] SYMBOLS = {"🍒", "🍋", "🔔", "⭐", "7"};
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel gamePanel;

    private JLabel balanceLabel;
    private JLabel[] slotLabels;
    private JTextField betField;
    private JButton spinButton, loadButton, withdrawButton, logoutButton;
    private JLabel messageLabel;

    private Timer spinTimer;
    private int spinCount;
    private User currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SlotMachineGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Casino Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createGamePanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(gamePanel, "GAME");

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(mainPanel, "LOGIN");
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton signupBtn = new JButton("Sign Up");
        JButton loginBtn  = new JButton("Login");

        signupBtn.addActionListener(e -> {
            String pwd = JOptionPane.showInputDialog(frame, "Enter password for new account:");
            if (pwd != null && !pwd.trim().isEmpty()) {
                currentUser = UserManager.signUp(pwd);
                JOptionPane.showMessageDialog(frame, "Account created. Your User ID: " + currentUser.getId());
                refreshGamePanel();
                cardLayout.show(mainPanel, "GAME");
            }
        });

        loginBtn.addActionListener(e -> {
            String id  = JOptionPane.showInputDialog(frame, "Enter User ID:");
            String pwd = JOptionPane.showInputDialog(frame, "Enter Password:");
            User user = UserManager.login(id, pwd);
            if (user != null) {
                currentUser = user;
                JOptionPane.showMessageDialog(frame, "Login successful.");
                refreshGamePanel();
                cardLayout.show(mainPanel, "GAME");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials.");
            }
        });

        loginPanel.add(new JLabel("Welcome to the Casino", SwingConstants.CENTER));
        loginPanel.add(signupBtn);
        loginPanel.add(loginBtn);
    }

    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout(10, 10));

        // Top: balance display
        JPanel top = new JPanel();
        balanceLabel = new JLabel();
        top.add(balanceLabel);
        gamePanel.add(top, BorderLayout.NORTH);

        // Center: slot labels
        JPanel slots = new JPanel(new GridLayout(1, 3, 10, 10));
        slotLabels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            slotLabels[i] = new JLabel("—", SwingConstants.CENTER);
            slotLabels[i].setFont(new Font("SansSerif", Font.PLAIN, 48));
            slots.add(slotLabels[i]);
        }
        gamePanel.add(slots, BorderLayout.CENTER);

        // Bottom: controls + message
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        controls.add(new JLabel("Bet:"));
        betField = new JTextField("1.00", 5);
        controls.add(betField);
        spinButton    = new JButton("Spin");
        loadButton    = new JButton("Load");
        withdrawButton= new JButton("Withdraw");
        logoutButton  = new JButton("Logout");
        controls.add(spinButton);
        controls.add(loadButton);
        controls.add(withdrawButton);
        controls.add(logoutButton);
        bottom.add(controls, BorderLayout.CENTER);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        bottom.add(messageLabel, BorderLayout.SOUTH);

        gamePanel.add(bottom, BorderLayout.SOUTH);

        spinTimer = new Timer(100, e -> animateSlots());
        spinButton.addActionListener(e -> startSpin());
        loadButton.addActionListener(e -> loadMoney());
        withdrawButton.addActionListener(e -> withdrawMoney());
        logoutButton.addActionListener(e -> {
            UserManager.updateUser(currentUser);
            cardLayout.show(mainPanel, "LOGIN");
        });
    }

    private void refreshGamePanel() {
        balanceLabel.setText(String.format("Balance: $%.2f", currentUser.getBalance()));
        messageLabel.setText(" ");
        for (JLabel lbl : slotLabels) lbl.setText("—");
    }

    private void startSpin() {
        try {
            double bet = Double.parseDouble(betField.getText());
            if (bet < 0.10 || bet > 100 || bet > currentUser.getBalance()) {
                messageLabel.setText("Invalid bet amount.");
                return;
            }
            currentUser.setBalance(currentUser.getBalance() - bet);
            updateBalance();
            messageLabel.setText("Spinning...");
            spinCount = 0;
            spinButton.setEnabled(false);
            spinTimer.start();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Enter a valid number.");
        }
    }

    private void animateSlots() {
        Random rand = new Random();
        for (JLabel slot : slotLabels) {
            slot.setText(SYMBOLS[rand.nextInt(SYMBOLS.length)]);
        }
        spinCount++;
        if (spinCount >= 20) {
            spinTimer.stop();
            resolveSpin();
            spinButton.setEnabled(true);
        }
    }

    private void resolveSpin() {
        String a = slotLabels[0].getText(), b = slotLabels[1].getText(), c = slotLabels[2].getText();
        double bet = Double.parseDouble(betField.getText()), payout = 0;
        if (a.equals(b) && b.equals(c)) {
            payout = bet * 10;
            messageLabel.setText(String.format("Jackpot! You win $%.2f", payout));
        } else if (a.equals(b) || b.equals(c) || a.equals(c)) {
            payout = bet * 2;
            messageLabel.setText(String.format("Match two! You win $%.2f", payout));
        } else {
            messageLabel.setText("No match. You lose.");
        }
        currentUser.setBalance(currentUser.getBalance() + payout);
        updateBalance();
    }

    private void loadMoney() {
        String amt = JOptionPane.showInputDialog(frame, "Enter amount to load:");
        try {
            double amount = Double.parseDouble(amt);
            if (amount > 0) {
                currentUser.setBalance(currentUser.getBalance() + amount);
                updateBalance();
            }
        } catch (Exception ign) {
        }
    }

    private void withdrawMoney() {
        String amt = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
        try {
            double amount = Double.parseDouble(amt);
            if (amount > 0 && amount <= currentUser.getBalance()) {
                currentUser.setBalance(currentUser.getBalance() - amount);
                updateBalance();
            }
        } catch (Exception ign) {
        }
    }

    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: $%.2f", currentUser.getBalance()));
    }
}