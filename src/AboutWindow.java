import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutWindow extends JDialog { //klasa z rozszerzeniem JDialog (tworzenie okien dialogowych)

    private static final long serialVersionUID = 1L;
    private JLabel l0, l1, l2, l3, l4, l6, l7;

    private Font font1 = new Font("TimesRoman", Font.PLAIN, 22); //obiekty czcionek
    private Font font2 = new Font("TimesRoman", Font.PLAIN, 12);
    private Font font3 = new Font("TimesRoman", Font.BOLD, 12);

    private JPanel panel;

    /**
     * Konstruktor bezparametryczny klasy <code>AboutWindow</code>
     */

    public AboutWindow() {
        initAboutWindow(); //wywolanie metody
    }

    public void initAboutWindow() {
        this.setTitle("Informacje o programie");
        this.setVisible(true); //widocznosc okna dialogowego
        this.setModal(true); //interakcja z innymi czesciami programu gdy wlaczone
        this.setSize(360, 300);
        this.setResizable(false);

        this.addWindowListener(new WindowAdapter() {
            // obsluga wcisniecia przycisku zamkniecia okna
            public void windowClosing(WindowEvent e) {
                setVisible(false); //widocznosc okna dialogowego
            }
        });

        Dimension dialogSize = getSize(); //obiekt dimension przez wywolanie metody getsize

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //obiekt Dimension, który reprezentuje rozmiar ekranu, poprzez wywołanie metody getScreenSize() klasy Toolkit.
        if (dialogSize.height > screenSize.height) //sprawdzanie czy wysokosc okna > wysokosc ekranu
            dialogSize.height = screenSize.height; //jestli tak wysokosc okna na wysokosci ekranu
        if (dialogSize.width > screenSize.width)
            dialogSize.height = screenSize.width;

        setLocation((screenSize.width - dialogSize.width) / 2, //wysrodkowanie okna na ekranie
                (screenSize.height - dialogSize.height) / 2);

        Container contentPane = getContentPane();
        contentPane.setBackground(Color.white); //kolor okna
        contentPane.setLayout(null); ///pozycja i rozmiar komponentow zostana okresona za pomoca metody setBounds

        l0 = new JLabel("Temp System");
        l0.setFont(font1);
        l0.setHorizontalAlignment(SwingConstants.CENTER);

        l1 = new JLabel("wersja 1.0.0");
        l1.setFont(font1);
        l1.setHorizontalAlignment(SwingConstants.CENTER);

        l2 = new JLabel("Copyright (C) by 2022");
        l2.setFont(font2);
        l2.setHorizontalAlignment(SwingConstants.CENTER);

        l3 = new JLabel("Aleksandra Sylwestrzak ");
        l3.setFont(font3);
        l3.setHorizontalAlignment(SwingConstants.CENTER);

        l6 = new JLabel("Marcin Roszczyk");
        l6.setFont(font3);
        l6.setHorizontalAlignment(SwingConstants.CENTER);

        l7 = new JLabel("Krystian Sikorski");
        l7.setFont(font3);
        l7.setHorizontalAlignment(SwingConstants.CENTER);

        l4 = new JLabel("Politechnika Koszalińska");
        l4.setFont(font3);
        l4.setHorizontalAlignment(SwingConstants.CENTER);



        panel = new JPanel();

        ImageIcon icon = new ImageIcon("Graphics\\author_logo.jpg");
        JLabel thumb = new JLabel();
        thumb.setIcon(icon);
        panel.add(thumb);

        this.add(panel);
        panel.setBackground(Color.white);

        panel.setBounds(10, 15, 120, 150);
        l0.setBounds(135, 20, 210, 30);
        l1.setBounds(135, 50, 210, 30);
        l2.setBounds(135, 100, 210, 20);
        l3.setBounds(135, 120, 210, 20);
        l6.setBounds(135,140,210,20);
        l7.setBounds(135,160,210,20);


        l4.setBounds(135, 200, 210, 20);

        contentPane.add(l0);
        contentPane.add(l1);
        contentPane.add(l2);
        contentPane.add(l3);
        contentPane.add(l6);
        contentPane.add(l7);

        contentPane.add(l4);
        contentPane.add(panel);

    }
}