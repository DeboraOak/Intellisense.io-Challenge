package googlesheetschallenge;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "IntelliSense.io Challenge";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "../client_secret.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private final List<Consumers> consumersList = new ArrayList<>();
    private final List<Consumers> firstFilter = new ArrayList<>();
    private final List<Consumers> consumersWithChildren = new ArrayList<>();
    private final List<Consumers> consumersWithTeenagersAtHome = new ArrayList<>();
    private final List<Consumers> marriedConsumers = new ArrayList<>();

    SheetsQuickstart() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1KSZZrFZ64sgic3ccQJZIJGzdUOvk1LOa50fYPL-5i6Q";
        final String range = "A2:G2241"; //Using just the data I need.

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                Consumers consumers = new Consumers();
                for (int i = 0; i < row.size(); i++) {
                    switch (i) {
                        case 0:
                            consumers.setId(row.get(i).toString());
                            break;
                        case 1:
                            consumers.setYearBirth(row.get(i).toString());
                            break;
                        case 2:
                            consumers.setEducation(row.get(i).toString());
                            break;
                        case 3:
                            consumers.setMaritalStatus(row.get(i).toString());
                            break;
                        case 4:
                            consumers.setIncome(row.get(i).toString());
                            break;
                        case 5:
                            consumers.setKidhome(row.get(i).toString());
                            break;
                        case 6:
                            consumers.setTeenhome(row.get(i).toString());
                    }
                }

                int yearBirth = Integer.parseInt(consumers.getYearBirth());
                boolean condition = (yearBirth >= 1957 && yearBirth <= 1967)
                        && consumers.getEducation().equals("Master")
                        && consumers.getMaritalStatus().equals("Married");

                if (condition) {
                    firstFilter.add(consumers);
                }

                int childrenAmount = Integer.parseInt(consumers.getKidhome());
                if (childrenAmount != 0) {
                    consumersWithChildren.add(consumers);
                }

                int teenagersAtHome = Integer.parseInt(consumers.getTeenhome());
                if (teenagersAtHome != 0) {
                    consumersWithTeenagersAtHome.add(consumers);
                }

                if (consumers.getMaritalStatus().equals("Married")) {
                    marriedConsumers.add(consumers);
                }

                this.consumersList.add(consumers);
            }
        }
    }

    public List<Consumers> getConsumersList() {
        return consumersList;
    }

    public Consumers getConsumers(int id) {
        final Consumers[] resultConsumers = new Consumers[1];
        consumersList.forEach(consumers -> {
            if (Objects.equals(consumers.getId(), Integer.toString(id))) {
                resultConsumers[0] = consumers;
            }
        });

        return resultConsumers[0];
    }

    public List<Consumers> getFirstFilter() {
        return firstFilter;
    }

    public float percentageToTheTotal() {
       return (firstFilter.size() * 100f / consumersList.size());
    }

    public List<Consumers> getMarriedConsumers() {
        return marriedConsumers;
    }

    public List<Consumers> getConsumersWithChildren() {
        return consumersWithChildren;
    }

    public List<Consumers> getConsumersWithTeenagers() {
        return consumersWithTeenagersAtHome;
    }
}
