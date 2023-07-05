package com.samdobsondev.lcde4j.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samdobsondev.lcde4j.exception.BaselineResponseException;
import com.samdobsondev.lcde4j.exception.SSLContextCreationException;
import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.events.activeplayer.ActivePlayerEvent;
import com.samdobsondev.lcde4j.model.events.activeplayer.ActivePlayerEventType;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEvent;
import com.samdobsondev.lcde4j.model.events.announcer.AnnouncerNotificationEvent;
import com.samdobsondev.lcde4j.model.events.gamedata.GameDataEvent;
import com.samdobsondev.lcde4j.model.events.gamedata.GameDataEventType;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LCDE4J
{
    private static final String BASE_URL = "https://127.0.0.1:2999";
    private static final Gson GSON = new GsonBuilder().create();
    private final SSLContext sslContext;
    private final PortWatcher portWatcher;
    private final AtomicBoolean portWatcherStarted = new AtomicBoolean(false);
    private Boolean previousPortStatus = null;
    private final Consumer<Boolean> onPortStatusChange;
    private final AtomicBoolean isPolling = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> pollingTask;

    public LCDE4J() {
        this(isPortUp -> {});
    }

    public LCDE4J(Consumer<Boolean> onPortStatusChange) {
        try {
            this.sslContext = createSSLContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        portWatcher = new PortWatcher();
        this.onPortStatusChange = onPortStatusChange;
    }

    public void start() {
        startPortWatcherService();
    }

    private void startPortWatcherService() {
        new Thread(() -> {
            portWatcherStarted.set(true);
            while (portWatcherStarted.get()) {
                if (!checkPort()) {
                    try {
                        Thread.sleep(100); // we check the status of the port every 100 milliseconds
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }).start();
    }

    public boolean checkPort() {
        boolean isPortUp = this.portWatcher.isPortUp();

        if (previousPortStatus == null || isPortUp != previousPortStatus) {
            if (isPortUp) {
                System.out.println("API Online, loading...");
                startPolling();
            } else {
                System.out.println("API Offline, start an active game instance...");
                stopPolling();
            }
            previousPortStatus = isPortUp;
        }
        onPortStatusChange.accept(isPortUp);
        return isPortUp;
    }

    private void startPolling() {
        isPolling.set(true);

        // Load baseline response from JSON file
        AtomicReference<AllGameData> currentResponse = new AtomicReference<>(loadBaselineResponse());

        pollingTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                ApiResponse<AllGameData> apiResponse = requestLiveData("/liveclientdata/allgamedata", AllGameData.class);
                if (apiResponse == null || apiResponse.getStatusCode() == 404) {
                    System.out.println("loading...");
                } else {
                    AllGameData incomingResponse = apiResponse.getResponseObject();

                    // TODO: Fix bug where activePlayer.getAbilities(getQ) in the first incomingResponse is sometimes null

                    // Process all the events that have occurred in this response
                    processEvents(currentResponse.get(), incomingResponse);

                    // Update currentResponse to incomingResponse for the next poll
                    currentResponse.set(incomingResponse);
                }
            } catch (ConnectException e) { // it is possible (but-rare) that the port can go down mid-poll, such as when the game ends or crashes, so we catch that here
                System.out.println("API connection lost during active polling. Polling process will be terminated until connection is re-established...");
                stopPolling();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    private AllGameData loadBaselineResponse() {
        try (InputStream is = getClass().getResourceAsStream("/baselineResponse.json")) {
            assert is != null;
            return GSON.fromJson(new InputStreamReader(is), AllGameData.class);
        } catch (IOException e) {
            throw new BaselineResponseException("Failed to load baseline response", e);
        }
    }

    private void processEvents(AllGameData currentResponse, AllGameData incomingResponse) {
        ActivePlayerEventDetector activePlayerEventDetector = new ActivePlayerEventDetector();
        GameDataEventDetector gameDataEventDetector = new GameDataEventDetector();
        AnnouncerNotificationEventDetector announcerNotificationEventDetector = new AnnouncerNotificationEventDetector();
        AllPlayersEventDetector allPlayersEventDetector = new AllPlayersEventDetector();

        List<ActivePlayerEvent> activePlayerEvents = activePlayerEventDetector.detectEvents(currentResponse, incomingResponse, incomingResponse.getGameData().getGameTime());
        processActivePlayerEvents(activePlayerEvents);

        List<AllPlayersEvent> allPlayersEvents = allPlayersEventDetector.detectEvents(currentResponse, incomingResponse, incomingResponse.getGameData().getGameTime());
        processAllPlayerEvents(allPlayersEvents);

        List<AnnouncerNotificationEvent> announcerNotificationEvents = announcerNotificationEventDetector.detectEvents(currentResponse, incomingResponse);
        processAnnouncerNotificationEvents(announcerNotificationEvents);

        List<GameDataEvent> gameDataEvents = gameDataEventDetector.detectEvents(currentResponse, incomingResponse, incomingResponse.getGameData().getGameTime());
        processGameDataEvents(gameDataEvents);
    }

    private void processActivePlayerEvents(List<ActivePlayerEvent> activePlayerEvents) {
        // Rather than printing, we need to fire the activePlayerEvents (replace this with the actual activePlayerEvent handling code)
        for (ActivePlayerEvent activePlayerEvent : activePlayerEvents) {
            if (activePlayerEvent.getActivePlayerEventType().equals(ActivePlayerEventType.GOLD_CHANGE)) {
            } else {
                System.out.println(activePlayerEvent);
            }
        }
    }

    private void processAllPlayerEvents(List<AllPlayersEvent> allPlayersEvents) {
        for (AllPlayersEvent allPlayersEvent : allPlayersEvents) {
            System.out.println(allPlayersEvent);
        }
    }

    private void processAnnouncerNotificationEvents(List<AnnouncerNotificationEvent> announcerNotificationEvents) {
        // Rather than printing, we need to fire the announcerNotificationEvents (replace this with the actual announcerNotificationEvent handling code)
        for (AnnouncerNotificationEvent announcerNotificationEvent : announcerNotificationEvents) {
            System.out.println(announcerNotificationEvent);
        }
    }

    private void processGameDataEvents(List<GameDataEvent> gameDataEvents) {
        // Rather than printing, we need to fire the gameDataEvents (replace this with the actual gameDataEvent handling code)
        for (GameDataEvent gameDataEvent : gameDataEvents) {
            if (gameDataEvent.getGameDataEventType().equals(GameDataEventType.GAME_TIME_CHANGE)) {
            } else {
                System.out.println(gameDataEvent);
            }
        }
    }

    private void stopPolling() {
        isPolling.set(false);
        if (pollingTask != null) {
            pollingTask.cancel(false);
        }
    }

    public void stop() {
        portWatcherStarted.set(false);
        scheduler.shutdown();
    }

    private SSLContext createSSLContext() throws SSLContextCreationException {
        char[] password = "nopass".toCharArray();

        try (InputStream is = getClass().getResourceAsStream("/riotgames.pem")) {
            if (is == null) {
                throw new SSLContextCreationException("Could not find certificate file '/riotgames.pem'");
            }

            // Load the keystore
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, password);

            // Create a CertificateFactory and import the certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);

            // Add the certificate to the keystore
            ks.setCertificateEntry("riotgames", cert);

            // Create a TrustManagerFactory with the trusted store
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            // Create an SSLContext with the trust manager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            return context;
        } catch (Exception e) {
            throw new SSLContextCreationException("Failed to create SSLContext", e);
        }
    }

    public ApiResponse<AllGameData> requestLiveData(String path, Class<AllGameData> clz) throws Exception {
        return getResponse(path, clz, sslContext);
    }

    private <T> ApiResponse<T> getResponse(String endpoint, Class<T> clz, SSLContext sslContext) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // Set SSL context
        conn.setSSLSocketFactory(sslContext.getSocketFactory());

        // Set request method to GET
        conn.setRequestMethod("GET");

        // Get response code
        int statusCode = conn.getResponseCode();

        // If status code is 404, return error response
        if (statusCode == 404) {
            return new ApiResponse<>(null, "...", statusCode);
        }

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        // Close connections
        in.close();
        conn.disconnect();

        // Parse JSON response
        T t = null;
        String rawResponse = content.toString();

        if (statusCode / 200 == 1 && !rawResponse.isEmpty() && clz != Void.class) {
            t = GSON.fromJson(rawResponse, clz);
        }

        return new ApiResponse<>(t, rawResponse, statusCode);
    }
}
