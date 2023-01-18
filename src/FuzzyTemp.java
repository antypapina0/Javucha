import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuzzyTemp {

    // Zmienne służące do pobrania wartości temperatury oraz wilgotności
    String ut = new String(); // Temperatura użytkownika
    private String rt = new String(); // Temperatura realna
    private String h = new String();// Wilgotność

    // Tablice dla rozmytych wartości:
    private Double[][] temDif = new Double[5][301]; // różnica temperatur od 0 do 10 stopni
    private Double[][] airHum = new Double[3][101]; // wilgotność powietrza od 0 do 100%
    private Double[][] heatPower = new Double[5][101]; // moca grzania od 0 do 100%
    private Double[][] infHeatPower = new Double[5][101]; // tabela wnioskowania dla mocy pieca
    private Double[] inf = new Double[301]; // tablica wnioskowania

    // Zmienne pomocnicze przy obliczeniach
    private Double a = 0.0;     // Licznik
    private Double b = 0.0;     // Mianownik
    private Double i;
    Integer def;  // Wynik wyostrzania metodą środka ciężkości
    private Double utem;        // Temperatura użytkownika
    private Double rtem;        // Temperatura realna
    private Integer hum;         // Wilgotność
    private Double dif;         // Różnica temperatur

    //Zmienne dla reguł
    private Double R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15, low, medium, high, vLow, vHigh;

    public void goFuzzy() { //Rozmywanie ODPALAĆ TYLKO RAZ!!!!

        //---------------------------------------------------------------------------------------------------------------------//
        // Rozmywanie
        //---------------------------------------------------------------------------------------------------------------------//

        // Rozmycie dla różnicy temperatur
        for (int i = 0; i < 301; i++) { // rozmycie dla bardzo niskiej różnicy temperatur, wartości są umowane

            if (i <= 40)
                temDif[0][i] = (40.0 - i) / (40.0);            //od 1.5 do 3.5 stopni waga stopnia maleje
            else
                temDif[0][i] = 0.0;                             //powyżej 3.5 stopni waga stopnia wynosi 0

            // System.out.println("temp dif 0" + i + ": " + temDif[0][i]);

        }

        for (int i = 0; i < 301; i++) { // rozmycie dla niskiej różnicy temperatur
            if ((i > 20) && (i <= 60))
                temDif[1][i] = (i - 20.0) / (60.0 - 20.0);      // od 2.5 do 4.5 stopni rosnie
            else if ((i > 60) && (i <= 100))
                temDif[1][i] = (100.0 - i) / (100.0 - 60.0);      // od 6 do 8 stopni maleje
            else
                temDif[1][i] = 0.0;                             //inaczej wynosi 0

            // System.out.println("temp dif 1i : " + tem_dif[1][i]);

        }

        for (int i = 0; i < 301; i++) { // rozmycie dla średniej różnicy temperatur
            if ((i > 80) && (i <= 130))                          // od 6.5 do 8.5 rosnie
                temDif[2][i] = (i - 80.0) / (130.0 - 80.0);
            else if ((i > 130) && (i <= 180))
                temDif[2][i] = (180.0 - i) / (180.0 - 130.0);      // od 6 do 8 stopni maleje
            else                                                // inaczej wynosi 0
                temDif[2][i] = 0.0;
        }

        for (int i = 0; i < 301; i++) { // rozmycie dla wysokiej różnicy temperatur
            if ((i > 150) && (i <= 195))                          // od 6.5 do 8.5 rosnie
                temDif[3][i] = (i - 150.0) / (195.0 - 150.0);
            else if ((i > 195) && (i <= 240))
                temDif[3][i] = (240.0 - i) / (240.0 - 195.0);      // od 6 do 8 stopni maleje
            else                                                // inaczej wynosi 0
                temDif[3][i] = 0.0;
        }

        for (int i = 0; i < 301; i++) { // rozmycie dla wysokiej różnicy temperatur
            if (i > 220)                                         // od 6.5 do 8.5 rosnie
                temDif[4][i] = (i - 220.0) / (300.0 - 220.0);
            else                                                // inaczej wynosi 0
                temDif[4][i] = 0.0;
        }


        // Rozmycie dla wilgotności

        for (int i = 0; i < 101; i++) { // rozmycie dla niskiej wilgotnosci
            if (i <= 15.0)
                airHum[0][i] = 1.0;
            else if (i <= 35)
                airHum[0][i] = (35.0 - i) / (35.0 - 15.0);
            else
                airHum[0][i] = 0.0;
            //System.out.println(airHum[0][i]);
        }

        for (int i = 0; i < 101; i++) { // rozmycie dla średniej wilgotnosci
            if ((i > 25) && (i <= 45))
                airHum[1][i] = (i - 25.0) / (45 - 25.0);
            else if ((i > 45) && (i <= 60))
                airHum[1][i] = 1.0;
            else if ((i > 60) && (i <= 80))
                airHum[1][i] = (80.0 - i) / (80 - 60.0);
            else
                airHum[1][i] = 0.0;
        }

        for (int i = 0; i < 101; i++) { // rozmycie dla wysokiej wilgotności
            if ((i > 65) && (i <= 85))
                airHum[2][i] = (i - 65.0) / (85 - 65);
            else if (i > 85)
                airHum[2][i] = 1.0;
            else
                airHum[2][i] = 0.0;
        }

        // Rozmycie dla mocy pieca
        for (int i = 0; i < 101; i++) { // rozmycie dla bardzo niskiej mocy pieca
            if (i <= 20)
                heatPower[0][i] = (20.0 - i) / (20.0);
            else
                heatPower[0][i] = 0.0;
        }

        for (int i = 0; i < 101; i++) { //rozmycie dla niskiej mocy pieca
            if ((i > 10) && (i <= 25))
                heatPower[1][i] = (i - 10.0) / (25.0 - 10);
            else if ((i > 25) && (i <= 40))
                heatPower[1][i] = (40.0 - i) / (40.0 - 25);
            else
                heatPower[1][i] = 0.0;
        }

        for (int i = 0; i < 101; i++) { //rozmycie dla średniej mocy pieca
            if ((i > 30) && (i <= 50))
                heatPower[2][i] = (i - 30.0) / (50.0 - 30.0);
            else if ((i > 50) && (i <= 70))
                heatPower[2][i] = (70.0 - i) / (70.0 - 50.0);
            else
                heatPower[2][i] = 0.0;
        }
        for (int i = 0; i < 101; i++) { //rozmycie dla wysokiej mocy pieca
            if ((i > 55) && (i <=70 ))
                heatPower[3][i] = (i - 55.0) / (70.0 - 55.0);
            else if ((i > 70) && (i <= 90))
                heatPower[3][i] = (90.0 - i) / (90.0 - 70.0);
            else
                heatPower[3][i] = 0.0;
        }
        for (int i = 0; i < 101; i++) { //rozmycie dla bardzo wysokiej mocy pieca
            if (i > 80)
                heatPower[4][i] = (i - 80.0) / (100.0 - 80.0);
            else
                heatPower[4][i] = 0.0;
        }
    }

    public Integer FuzzyTemp(String ut, String rt, String h)  throws IOException{

        //---------------------------------------------------------------------------------------------------------------------//
        // Pobieranie wartości
        //---------------------------------------------------------------------------------------------------------------------//

        utem = Double.parseDouble(ut); // oczekiwana
        rtem = Double.parseDouble(rt); // aktualna
        hum = Integer.parseInt(h); // wilgotnosc

        dif = utem - rtem; // roznica temperatur
        dif = dif * 10; //
        if (dif > 300.0) {
            dif = 300.0;
        }

        Integer dif2 = dif.intValue();
        //---------------------------------------------------------------------------------------------------------------------//
        // Reguły
        //---------------------------------------------------------------------------------------------------------------------//

        //nasze reguły to

        //R10:IF dif = BNiska AND Wil = Niska THEN Moc = BNiska
        //R11:IF dif = BNiska AND Wil = Średnia THEN Moc = BNiska
        //R12:IF dif = BNiska AND Wil = Średnia THEN Moc = Niska

        //R1:IF dif = Niska AND Wil = Niska THEN Moc = Niska
        //R2:IF dif = Niska AND Wil = Średnia THEN Moc = Niska
        //R3:IF dif = Niska AND Wil = Wysoka THEN Moc = Średnia

        //R4:IF dif = Średnia AND Wil = Niska THEN Moc = Niska
        //R5:IF dif = Średnia AND Wil = Średnia THEN Moc = Średnia
        //R6:IF dif = Średnia AND Wil = Wysoka THEN Moc = Wysoka

        //R7:IF dif = Wysoka AND Wil = Niska THEN Moc = Średnia
        //R8:IF dif = Wysoka AND wil = Średnia THEN Moc = Wysoka
        //R9:IF dif = Wysoka AND wil = Wysoka THEN Moc = Wysoka

        //R13:IF DIF = BWysoka AND Wil = Niska THEN Moc = Wysoka
        //R14:IF dif = BWysoka AND Wil = Średnia THEN Moc = BWysoka
        //R15:IF dif = BWysoka AND Wil = Wysoka THEN Moc = BWysoka

        //Są to reguły przykładowe i można je dowolnie zmieniać
        R10 = Math.min(temDif[0][dif2], airHum[0][hum]);
        System.out.println(temDif[0][dif2]);
        System.out.println(temDif[1][dif2]);
        System.out.println(temDif[2][dif2]);
        System.out.println(temDif[3][dif2]);
        System.out.println(temDif[4][dif2]);
        R11 = Math.min(temDif[0][dif2], airHum[1][hum]);
        //System.out.println(R11);
        R12 = Math.min(temDif[0][dif2], airHum[2][hum]);

        R1 = Math.min(temDif[1][dif2], airHum[0][hum]);
        R2 = Math.min(temDif[1][dif2], airHum[1][hum]);
        R3 = Math.min(temDif[1][dif2], airHum[2][hum]);

        R4 = Math.min(temDif[2][dif2], airHum[0][hum]);
        R5 = Math.min(temDif[2][dif2], airHum[1][hum]);
        R6 = Math.min(temDif[2][dif2], airHum[2][hum]);

        R7 = Math.min(temDif[3][dif2], airHum[0][hum]);
        R8 = Math.min(temDif[3][dif2], airHum[1][hum]);
        R9 = Math.min(temDif[3][dif2], airHum[2][hum]);

        R13 = Math.min(temDif[4][dif2], airHum[0][hum]);
        R14 = Math.min(temDif[4][dif2], airHum[1][hum]);
        R15 = Math.min(temDif[4][dif2], airHum[2][hum]);

        vLow = Math.max(R10, R11);

        low = Math.max(R12, R1);
        low = Math.max(low, R2);
        low = Math.max(low, R4);

        medium = Math.max(R3, R5);
        medium = Math.max(medium, R7);

        high = Math.max(R6, R8);
        high = Math.max(high, R9);
        high = Math.max(high, R13);

        vHigh = Math.max(R14, R15);
        System.out.println("Grzanie");
        System.out.println("Very Low: " + vLow);
        System.out.println("Low : " + low);
        System.out.println("Medium : " + medium);
        System.out.println("High : " + high);
        System.out.println("Very High: " + vHigh);

        //---------------------------------------------------------------------------------------------------------------------//
        // Wnioskowanie
        //---------------------------------------------------------------------------------------------------------------------//

        for(int i = 0; i < 101; i++){
            infHeatPower[0][i] = heatPower[0][i];
            infHeatPower[1][i] = heatPower[1][i];
            infHeatPower[2][i] = heatPower[2][i];
            infHeatPower[3][i] = heatPower[3][i];
            infHeatPower[4][i] = heatPower[4][i];
            if(low < heatPower[0][i]){
                infHeatPower[0][i] = vLow;
            }
            if(low < heatPower[1][i]){
                infHeatPower[1][i] = low;
            }
            if(medium < heatPower[2][i]){
                infHeatPower[2][i] = medium;
            }
            if(high < heatPower[3][i]){
                infHeatPower[3][i] = high;
            }
            if(vHigh < heatPower[4][i]){
                infHeatPower[4][i] = vHigh;
            }
        }


        for (int i = 0; i < 101; i++){
            inf[i] = Math.max(infHeatPower[0][i], infHeatPower[1][i]);
            inf[i] = Math.max(inf[i], infHeatPower[2][i]);
            inf[i] = Math.max(inf[i], infHeatPower[3][i]);
            inf[i] = Math.max(inf[i], infHeatPower[4][i]);
        }

        for (int i = 0; i < 101; i++) {
            a = (i * inf[i]) + a;
            b = inf[i] + b;
            //System.out.println(inf[i]);
        }
        def =  a.intValue()/b.intValue(); //wartośc wysotrzania środka ciężkości
        System.out.println("Wynik:" + def);
        return def;

    }

    //---------------------------------------------------------------------------------------------------------------------//
    // Wykresy
    //---------------------------------------------------------------------------------------------------------------------//

    double[][] temdif = new double[5][301]; // różnica temperatur od 0 do 10 stopni
    double[][] airhum = new double[3][101]; // wilgotność powietrza od 0 do 100%
    double[][] heatpower = new double[5][101]; // moca grzania od 0 do 100%
    double[] xAxis = new double[101]; // tabela dla osi x od 0 do 100
    double[] xAxis2 = new double[301];
    double[] inference = new double[101];

    public void goChart() {

        List<XYChart> charts = new ArrayList<XYChart>();
        for (int i = 0; i < 301; i++) {
            temdif[0][i] = temDif[0][i];
            temdif[1][i] = temDif[1][i];
            temdif[2][i] = temDif[2][i];
            temdif[3][i] = temDif[3][i];
            temdif[4][i] = temDif[4][i];
            xAxis2[i] = i / 10.0;
        }
        for (int i = 0; i < 101; i++) {
            airhum[0][i] = airHum[0][i];
            airhum[1][i] = airHum[1][i];
            airhum[2][i] = airHum[2][i];
            heatpower[0][i] = heatPower[0][i];
            heatpower[1][i] = heatPower[1][i];
            heatpower[2][i] = heatPower[2][i];
            heatpower[3][i] = heatPower[3][i];
            heatpower[4][i] = heatPower[4][i];
            xAxis[i] = i;
            //inference[i] = inf[i];
        }
        // Create Chart
        XYChart temDifChart = new XYChartBuilder().width(800).height(600).title("Różnica temperatur").xAxisTitle("Iteration").yAxisTitle("Result").build();

        // Customize Chart
        temDifChart.getStyler().setAxisTitlesVisible(false);
        temDifChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        temDifChart.getStyler().setMarkerSize(0);

        // Series
        temDifChart.addSeries("Bardzo niska", xAxis2, temdif[0]);
        temDifChart.addSeries("niska", xAxis2, temdif[1]);
        temDifChart.addSeries("średnia", xAxis2, temdif[2]);
        temDifChart.addSeries("wysoka", xAxis2, temdif[3]);
        temDifChart.addSeries("bardzo wysoka", xAxis2, temdif[4]);

        XYChart airHumChart = new XYChartBuilder().width(800).height(600).title("Wilgotność Powietrza").xAxisTitle("Iteration").yAxisTitle("Result").build();

        // Customize Chart
        airHumChart.getStyler().setAxisTitlesVisible(false);
        airHumChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        airHumChart.getStyler().setMarkerSize(0);

        // Series
        airHumChart.addSeries("niska", xAxis, airhum[0]);
        airHumChart.addSeries("średnia", xAxis, airhum[1]);
        airHumChart.addSeries("wysoka", xAxis, airhum[2]);


        XYChart heatPowerChart = new XYChartBuilder().width(800).height(600).title("Moc pieca").xAxisTitle("Iteration").yAxisTitle("Result").build();

        // Customize Chart
        heatPowerChart.getStyler().setAxisTitlesVisible(false);
        heatPowerChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        heatPowerChart.getStyler().setMarkerSize(0);

        // Series
        heatPowerChart.addSeries("bardzo niska", xAxis, heatpower[0]);
        heatPowerChart.addSeries("niska", xAxis, heatpower[1]);
        heatPowerChart.addSeries("średnia", xAxis, heatpower[2]);
        heatPowerChart.addSeries("wysoka", xAxis, heatpower[3]);
        heatPowerChart.addSeries("bardzo wysoka", xAxis, heatpower[4]);

       /* XYChart infChart = new XYChartBuilder().width(800).height(600).title("Inference").xAxisTitle("X").yAxisTitle("Y").build();

        // Customize Chart
        infChart.getStyler().setAxisTitlesVisible(false);
        infChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        infChart.getStyler().setMarkerSize(0);


        // Series
        infChart.addSeries("Inference", xAxis, inference); */

        charts.add(temDifChart);
        charts.add(airHumChart);
        charts.add(heatPowerChart);
        //charts.add(infChart);
        new SwingWrapper<XYChart>(charts).displayChartMatrix();

    }

    public static void main(String[] args) throws IOException {
        System.out.println("uruchomiono fuzzy temp");
        String a = "32";
        String b = "2";
        String c = "100";
        FuzzyTemp f = new FuzzyTemp();
        f.goFuzzy();
        try {
            f.FuzzyTemp(a, b, c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




