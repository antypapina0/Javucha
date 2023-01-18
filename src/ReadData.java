import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lombok.Getter;
import lombok.Setter;


import java.io.File;
import java.io.IOException;

@Getter
@Setter

public class ReadData {

    private String inputFile; // nazwa pliku z danimi
    public static String todayTemperature; // dane o temp dla dnia obecnego
    public static String todayHumidity; //dane o  wilkgonosci dla dnia obecneogo
    // metoda do odczytania danych z pliku (2 argumenty wiersz i kolumny z ktorych sa odczytywane dane)
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    // Czytanie wszystkich danych w arkuszu
    public void read(int row, int column) throws IOException  {

        System.out.println("Wczytywanie danych...");
        //ReadData temperatures = new ReadData();
        this.setInputFile("temperatures.xls"); //pplik z danymi
        //temperatures.read();

        File inputWorkbook = new File(inputFile); //tworzy obiekt typu File na podstawie ścieżki podanej w zmiennej "inputFile".
        Workbook w; //tworzy zmienną typu Workbook o nazwie "w".


        try { //obsluga bledow
            w = Workbook.getWorkbook(inputWorkbook); // przypisuje do zmiennej w obiekt zczytany z pliku inputWorkbook
            // pobiera pierwszy arkusz z pliku
            Sheet sheet = w.getSheet(0);

            // Odczyt wszystkich elementow

                    Cell celltemp = sheet.getCell(row, column); //pobiera komórkę z arkusza o współrzędnych (row, column) i przypisuje ją do zmiennej "celltemp".
                    Cell cellhum = sheet.getCell(row + 4, column); // pobiera komórkę z arkusza o współrzędnych (row + 2, column) i przypisuje ją do zmiennej "cellhum"

                    // Temperatura
                        todayTemperature = celltemp.getContents(); // przypisuje zawartość komórki "celltemp" do zmiennej "todayTemperature".
                        UserInterface.showTempLabel.setText(todayTemperature); //stawia tekst w określonym elemencie interfejsu użytkownika na wartość temperatury
                        System.out.println("Row: " + celltemp.getRow() + ", today temperature: " + todayTemperature); //wyswietla na konsoli informacje o odczytanej tempetaturze

                    // Wilgotność
                        todayHumidity = cellhum.getContents(); // wartosc wilgotnosci dla dnia obecnego
                        System.out.println("Row: " + cellhum.getRow() + ", today humidity: " + todayHumidity);


        } catch (BiffException e) {
            e.printStackTrace();
        }

    } // koniec metody read()

    public static void main(String[] args) throws IOException {
        int x = 1;
        int y = 1;
        System.out.println("uruchomiono read data"); //informację o uruchomieniu programu
        ReadData temperatures = new ReadData(); // nowy obiekt klasy "ReadData"
        temperatures.setInputFile("temperatures.xls"); //ustawia nazwę pliku wejściowego na "temperatures.xls"
        temperatures.read(x, y); //wywołuje metodę "read" z argumentami "x" i "y"
        // System.out.println(todayTemperature + todayHumidity); test is okay
    }

}
