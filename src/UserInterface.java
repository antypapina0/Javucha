import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class UserInterface {

    private JFrame window = new JFrame("Temperature System");

    // Menu
    private JMenuBar menuBar = new JMenuBar();
    private JMenu control = new JMenu("Wykres");
    private JMenu help = new JMenu("Pomoc");
    private JMenuItem c2 = new JMenuItem("Pokaż wykresy");

    private JMenuItem h1 = new JMenuItem("Korzystanie z aplikacji");
    private JMenuItem h2 = new JMenuItem("O autorach");

    // Panel kontrolny użytkownika, GridLayout(2, 3)
    private JPanel userPanel = new JPanel(new GridLayout(2, 3));

    // Pierwszy wiersz userPanel
    private JLabel infoTempLabel = new JLabel("Dzisiejsza temperatura:  ", JLabel.LEFT);
    public static JLabel showTempLabel = new JLabel(" ", JLabel.LEFT);
    private JLabel celsiusLabel = new JLabel(" °C", JLabel.LEFT);

    // Drugi wiersz userPanel
    private JLabel setTempLabel = new JLabel("Ustaw oczekiwaną temperaturę:  ", JLabel.LEFT);
    private JTextField setTempField = new JTextField(JTextField.LEFT);
    private JButton acceptTempButton = new JButton("OK!");

    JPanel buttonPanel = new JPanel();
    JButton button12 = new JButton("Zima");
    JButton button23 = new JButton("Lato");

    JButton buttonW = new JButton("Wiosna");

    JButton buttonJ = new JButton("Jesień");

    // Panel wykresu
    private JPanel chartPanel = new JPanel(new GridLayout(1, 1));

    // Zmienna przekazywana klasie fuzyfikacji - oczekiwana temperatura
    public static String getExcTemp;
    public static String currentTemp;

    public Integer Column = 0;
    // Konstruktor
    public UserInterface() {
        initGUI();
    }

    public void initGUI() throws NullPointerException, NumberFormatException {
        acceptTempButton.setForeground(Color.BLUE); // kolor tekstu
        acceptTempButton.setBorder(new LineBorder(Color.black, 1)); // kolor ramki i grubość linii
        // Okno
        window.setVisible(true);
        window.setLocationRelativeTo(null);
        window.setLayout(new FlowLayout());
        window.setMinimumSize(new Dimension(600,200));
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.setResizable(false);

        // Menu
        window.setJMenuBar(menuBar);
        menuBar.add(control);
        menuBar.add(help);
        control.add(c2);

//        control.add(summerButton);
//        control.add(winterButton);
        help.add(h1);
        help.add(h2);

        // Panel sterowania uzytkownika
        window.add(userPanel);
        userPanel.setSize(200,300);
        // Dodanie komponentow do pierwszego wiersza panelu
        userPanel.add(infoTempLabel);
        userPanel.add(showTempLabel);
        userPanel.add(celsiusLabel);
        // Dodanie komponentow do drugiego wiersza panelu
        userPanel.add(setTempLabel);
        userPanel.add(setTempField);
        userPanel.add(acceptTempButton);

        //Akcja dla przycisków Zima i Lato
        buttonPanel.add(buttonW);
        buttonPanel.add(button23);
        buttonPanel.add(buttonJ);
        buttonPanel.add(button12);

        window.add(buttonPanel);
//        summerButton.addActionListener(actionEvent ->{
//            Column = 0;
//        });
//
//        winterButton.addActionListener(actionEvent ->{
//            Column = 1;
//        });

        button23.addActionListener(actionEvent ->{
            Column = 0;
        });

        button12.addActionListener(actionEvent ->{
            Column = 1;
        });

        buttonW.addActionListener(actionEvent ->{
            Column = 2;
        });

        buttonJ.addActionListener(actionEvent ->{
            Column = 3;
        });
        // Akcja dla przycisku zatwierdzajacego ustawienie oczekiwanej temperatury
        acceptTempButton.addActionListener(actionEvent -> {
            getExcTemp = setTempField.getText();

           /* ReadData rd = new ReadData();
            try {
                rd.read();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            // Zmienne odbierane z klasy ReadData.class - obecna temperatura i wilgotność

            showTempLabel.setText(currentTemp);

            // Sprawdzenie poprawnosci wprowadzenia danej
            try {
                Float exceptedTemp = Float.valueOf(getExcTemp);
            }
            catch (NumberFormatException nfe) {
                showInvalidDialog("Wprowadź wartość numeryczną, np. 22 lub 23.5");
            }

            Float exceptedTemp = Float.valueOf(getExcTemp);
            if (exceptedTemp < 0 || exceptedTemp > 28) {
                showInvalidDialog("Wprowadź temperaturę z zakresu 18-28 stopni!");
            }
            else {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Symulator(exceptedTemp, Column);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                showConfirmDialog();
            }
        });



        // Akcja dla opcji "Pokaż wykresy" w menu

        c2.addActionListener(actionEvent -> {
            FuzzyTemp ft = new FuzzyTemp();
            ft.goFuzzy();
            ft.goChart();
        });


        // Akcja dla opcji "Korzystanie z aplikacji" w menu
        h1.addActionListener(actionEvent -> {
            new HelpWindow();
        });

        // Akcja dla opcji "O autorach"
        h2.addActionListener(actionEvent -> {
            new AboutWindow();
        });

        // Akcja zamknięcia okna
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeTheWindow();
            }
        });

    } // Koniec metody initGUI()

    // Metoda opisująca zamknięcie okna
    private void closeTheWindow() {
        int value = JOptionPane.showOptionDialog(null,
                "Czy jesteś pewien, że chcesz zakończyć działanie aplikacji?",
                "Wyjście z aplikacji",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"Zamknij", "Anuluj"},
                "Zamknij");

        if (value == JOptionPane.YES_OPTION) {
            window.dispose();
            System.exit(0);
        }
    }

    // Wyskakujace okienko w przypadku powodzenia (poprawnie ustawiona oczekiwana temperatura)
    private void showConfirmDialog() {
        JOptionPane.showMessageDialog(window,
                "Ustawiono poprawnie");
    }

    // Wyskakujace okienko w przypadku niepowodzenia (niepoprawnie ustawiona oczekiwana temperatura)
    private void showInvalidDialog(String temp) {
        JOptionPane.showMessageDialog(window,
                temp,
                "Inne error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {

        // W watku, aby uniknac zakleszczen przy tworzeniu GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                }
                catch (UnsupportedLookAndFeelException e) {
                    java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                }
                catch (ClassNotFoundException e) {
                    java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                }
                catch (InstantiationException e) {
                    java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                }
                catch (IllegalAccessException e) {
                    java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
                }
                new UserInterface(); //Create and show the GUI.
            }
        });

    }

}



