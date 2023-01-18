import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Symulator {
    String outTemp;                 //Temperatura na zewnątrz
    String outHumidity;             //Wilgotność na zewnątrz
    String expected;                //Oczekiwana temperatura
    Integer dif=0;
    Integer dif2=0;
    Integer h = 0;
    Double a = 0.0;
    List<Double> chartTemp = new ArrayList<>(); // różnica temperatur od 0 do 10 stopni
    List<Double> chartIter = new ArrayList<>();
    List<Integer> chartDif = new ArrayList<>();
    List<Integer> chartDif2 = new ArrayList<>();
    Double hPower = 4.0;     //100% mocy pieca to 4 stopnie na godzinę
    Double Temp;             //Aktualna temperatura
    Double oTemp;            //Zmienna pomocnicza do wykresu
    Double oExp;

    public Symulator(Float ex, Integer column) throws IOException, InterruptedException {
        List<XYChart> charts = new ArrayList<XYChart>();
        Temp = 5.0;
        expected = String.valueOf(ex); //
        ReadData rd = new ReadData();
        FuzzyTemp ft = new FuzzyTemp();
        FuzzyClim fc = new FuzzyClim();
        ft.goFuzzy();
        fc.goFuzzy();
        double[][] start = new double[2][1];
        start[0][0] = 0;
        start[1][0] = 15;
        final XYChart RealTimeChart1 = QuickChart.getChart("Temperatura w czasie rzeczywistym", "Godzina", "Temperatura", "Temperatura", start[0], start[1]); // seria danych zaleznosc 1 opierajacych sie na tablicy start
        final XYChart RealTimeChart2 = QuickChart.getChart("Moc grzania w czasie rzeczywistym", "Godzina", "Temperatura", "Moc grzania", start[0], start[0]);
        final XYChart RealTimeChart3 = QuickChart.getChart("Moc chłodzenia w czasie rzeczywistym", "Godzina", "Temperatura", "Moc chłodzenia", start[0], start[0]);
        final SwingWrapper<XYChart> sw1 = new SwingWrapper<XYChart>(RealTimeChart1);
        final SwingWrapper<XYChart> sw2 = new SwingWrapper<XYChart>(RealTimeChart2);
        final SwingWrapper<XYChart> sw3 = new SwingWrapper<XYChart>(RealTimeChart3);
        sw1.displayChart();
        sw2.displayChart();
        sw3.displayChart();
        if (column == 1 || column ==  3) {
            for (int i = 1; i < 25; i++) {
                try {
                    rd.read(column + 1, i + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outTemp = rd.todayTemperature; //odczyt temperatury
                outHumidity = rd.todayHumidity; // odczyt wilgotności
                oTemp = Double.valueOf(outTemp); //oTemp zwraca co przechowuje outTemp, temperatura

                if (Temp > ex) // temperatura jest większa od oczekiwanej,
                    dif = 0;
                else
                    dif = ft.FuzzyTemp(expected, outTemp, outHumidity);
                Temp = Temp + (hPower * dif / 100) - (Temp - oTemp) / 10; // Aktualna temperatura + moc pieca * wynik wnioskowania - chłodzenie (na każdy 1 stopień różnicy pomiędzy aktualną wewnątrz a aktualną zewnątrz temperatura zmniejsza się o 0.1)
                dif2 = 0;
                Thread.sleep(1000);
                chartTemp.add(Temp);
                chartDif.add(dif);
                chartDif2.add(dif2);
                Double I = (double) i + a;
                chartIter.add(I);

                RealTimeChart1.updateXYSeries("Temperatura", chartIter, chartTemp, null);
                RealTimeChart2.updateXYSeries("Moc grzania", chartIter, chartDif, null);
                RealTimeChart3.updateXYSeries("Moc chłodzenia", chartIter, chartDif2, null);
                sw1.repaintChart(); //Aktualizowanie wykresu
                sw2.repaintChart();
                sw3.repaintChart();
            }//Koniec for
        }//Koniec if
        else {
            Temp = 24.0;
            for (int i = 1; i < 25; i++) {
                try {
                    rd.read(column + 1, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outTemp = rd.todayTemperature; //odczyt temperatury
                outHumidity = rd.todayHumidity; // odczyt wilgotności
                oTemp = Double.valueOf(outTemp); //metoda zwraca co przechowuje outTemp
                oExp = Double.valueOf(expected); //metoda zwraca co przechowuje expected


               // RealTimeChart.updateXYSeries("Zależność1", chartIter, chartTemp, null);
               // sw.repaintChart(); //Aktualizowanie wykresu
                if ((Temp < ex)&&(oTemp>oExp)) { // temperatura zewnętrzna<oczekiwana temperatura wewnątrz  i aktualna temperatura wewnątrz>oczekiwanej temperatury wewnątrz
                    //System.out.println(1);
                    dif = 0;
                    dif2 = 0;
                    Temp = Temp + (oTemp - Temp) / 10; // Aktualna temperatura  + ocieplenie (na każdy 1 stopień różnicy pomiędzy aktualną wewnątrz a aktualną zewnątrz temperatura zmniejsza się o 0.1)
                }
                else if ((oTemp < oExp)&&(Temp > oExp)) { //temperatura zewnętrzna<oczekiwana temperatura wewnątrz i aktualna temperatura wewnątrz>oczekiwanej temperatury wewnątrz
                    //System.out.println(2);
                    dif2 = 0;
                    dif = 0;
                    Temp = Temp - (Temp - oTemp)/10; // Aktualna temperatura - ochłodzenie (na każdy 1 stopień różnicy pomiędzy aktualną wewnątrz a aktualną zewnątrz temperatura zmniejsza się o 0.1)
                }
                else  if (oTemp < oExp){ // aktualna temperatura zewnętrzna>oczekiwana temperatura wewnątrz
                    //System.out.println(3);
                    dif2 = 0;
                    dif = ft.FuzzyTemp(expected, "2", outHumidity);
                    Temp = Temp + ((hPower * dif / 100) - (Temp - oTemp)/10)/10; // Aktualna temperatura + moc pieca * wynik wnioskowania - chłodzenie (na każdy 1 stopień różnicy pomiędzy aktualną wewnątrz a aktualną zewnątrz temperatura zmniejsza się o 0.1)
                }
                else {
                    //System.out.println("4");
                    dif = 0;
                    dif2 = fc.FuzzyClim(expected, outTemp, outHumidity);
                    Temp = Temp - (hPower * dif2 / 100) + (Temp - oTemp) / 10; // Aktualna temperatura - moc chłodzenia * wynik wnioskowania + ocieplenie (na każdy 1 stopień różnicy pomiędzy aktualną wewnątrz a aktualną zewnątrz temperatura zmniejsza się o 0.1)
                }
                chartTemp.add(Temp);
                chartDif.add(dif);
                chartDif2.add(dif2);
                Double I = Double.valueOf(i + a);
                chartIter.add(I);
                RealTimeChart1.updateXYSeries("Temperatura", chartIter, chartTemp, null);
                RealTimeChart2.updateXYSeries("Moc grzania", chartIter, chartDif, null);
                RealTimeChart3.updateXYSeries("Moc chłodzenia", chartIter, chartDif2, null);
                sw1.repaintChart(); //Aktualizowanie wykresu
                sw2.repaintChart();
                sw3.repaintChart();
                Thread.sleep(3000);
            }//Koniec for
        }//Koniec else
    }//Koniec Symulator()
}//Koniec klasy Symulator
