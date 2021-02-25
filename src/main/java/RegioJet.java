import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


/**
 * Task:
 *  BackEnd Gradle project by sending REST api calls which you obtain as a response
 */

public class RegioJet {
    private static final LocalDateTime now = LocalDateTime.now();


    public static String calcNextMonday() {
        LocalDateTime nextMondayDate = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        String patternWeekDay = "yyyy-MM-dd";
        return nextMondayDate.format(DateTimeFormatter.ofPattern(patternWeekDay, Locale.ENGLISH));
    }


    public RegioJet() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        String OstravaId = "10202000";
        String BrnoId = "10202002";
        URL url = new URL("https://brn-ybus-pubapi.sa.cz/restapi/routes/search/simple?departureDate="+RegioJet.calcNextMonday()+"&fromLocationId="+ OstravaId +"&fromLocationType=CITY&tariffs=REGULAR&toLocationId="+ BrnoId + "&toLocationType=CITY");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println("Status for the GET Request is: " + status );

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

//        Manipulating with Response to get answers
        JSONObject Response = new JSONObject(content.toString());


        ArrayList<LocalTime> ArrivalTimeList = new ArrayList<>();
        ArrayList<LocalTime> TimeTravelList = new ArrayList<>();
        List<Double> PriceList = new ArrayList<>();
        
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        DateTimeFormatter hourDF = DateTimeFormatter.ofPattern("H:mm");

        for (int i = 0; i < Response.getJSONArray("routes").length(); i++) {
            ArrivalTimeList.add(LocalTime.parse((String) Response.getJSONArray("routes").getJSONObject(i).get("arrivalTime"), df));
            TimeTravelList.add(LocalTime.parse(((String) Response.getJSONArray("routes").getJSONObject(i).get("travelTime")).substring(0,5), hourDF));
            PriceList.add((Double) Response.getJSONArray("routes").getJSONObject(i).get("priceFrom"));

        }
        Collections.sort(ArrivalTimeList);
        Collections.sort(TimeTravelList);
        Collections.sort(PriceList);
        System.out.println("The fastest arrival time: " + ArrivalTimeList.get(0));
        System.out.println("The shortest time spent with travelling: " + TimeTravelList.get(0));
        System.out.println("The lowest price of the journey: " + PriceList.get(0));

    }
}

