package googlesheetschallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@SpringBootApplication
public class GoogleSheetsChallengeApplication {

    private static SheetsQuickstart data;

    static {
        try {
            data = new SheetsQuickstart();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(GoogleSheetsChallengeApplication.class, args);
    }

    // All the Consumers
    @CrossOrigin
    @GetMapping("/consumers")
    private List<Consumers> getConsumers() {return data.getConsumersList();
    }

    // Consumers born between 1957 and 1967 who have a master's degree and who are married
    @CrossOrigin
    @GetMapping("/firstchallange")
    private List<Consumers> firstChallange() {
        return data.getFirstFilter();
    }

    // Percentage of consumers found in relation to the total
    @CrossOrigin
    @GetMapping("/percentage")
    private float percentage() {
        return data.percentageToTheTotal();
    }

    // Married consumers
    @CrossOrigin
    @GetMapping("/marriedconsumers")
    private List<Consumers> marriedConsumers() {
        return data.getMarriedConsumers();
    }

    // All the Consumers who have children at home
    @CrossOrigin
    @GetMapping("/consumerswithchildren")
    private List<Consumers> consumersWithChildren() {
        return data.getConsumersWithChildren();
    }

    // All the Consumers who have teenagers at home
    @CrossOrigin
    @GetMapping("/consumerswithteenagersathome")
    private List<Consumers> consumersWithTeenagers() {
        return data.getConsumersWithTeenagers();
    }


}
