import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HelpWindow extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel panel;
    private Font font3 = new Font("TimesRoman", Font.BOLD, 12);

    /**
     * Konstruktor bezparametrowy klasy <code>HelpWindow</code>
     */

    public HelpWindow() {
        initHelpWindow();
    }

    public void initHelpWindow() {
        this.setTitle("Pomoc");
        this.setVisible(true);
        this.setModal(false);
        this.setResizable(true);
        this.setSize(360,300);
        this.setBackground(Color.WHITE);

        this.addWindowListener	(new WindowAdapter(){ // obsluga zdarzenia okna
            public void windowClosing(WindowEvent e){
                setVisible(false); //widocznosc okna podczas zamykania
            }	// koniec metody windowClosing
        });

        Dimension dialogSize = getSize(); // ustawienie rozmiara i polaczenie wzgledem ekranu

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(dialogSize.height > screenSize.height)
            dialogSize.height = screenSize.height;
        if(dialogSize.width > screenSize.width)
            dialogSize.height = screenSize.width;

        setLocation((screenSize.width-dialogSize.width)/2,
                (screenSize.height-dialogSize.height)/2);






        panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);

        JLabel label = new JLabel("W ramach problemów, proszę udać się do ");
        JLabel napis = new JLabel("Krystia Sikorskiego i Marcina Roszczyka");
        label.setFont(font3);
        napis.setFont(font3);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        napis.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 10, 340, 20);
        napis.setBounds(10, 30, 340, 20); // ustawienie pozycji dla drugiego napisu
        panel.add(label);
        panel.add(napis); // dodanie drugiego napisu do panelu

        this.add(panel);

    }

}
