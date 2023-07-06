package com.samdobsondev.lcde4j.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samdobsondev.lcde4j.api.detector.ActivePlayerEventDetector;
import com.samdobsondev.lcde4j.api.detector.AllPlayersEventDetector;
import com.samdobsondev.lcde4j.api.detector.AnnouncerNotificationEventDetector;
import com.samdobsondev.lcde4j.api.detector.GameDataEventDetector;
import com.samdobsondev.lcde4j.api.listener.ActivePlayerEventListener;
import com.samdobsondev.lcde4j.api.listener.AllPlayersEventListener;
import com.samdobsondev.lcde4j.api.listener.AnnouncerNotificationEventListener;
import com.samdobsondev.lcde4j.api.listener.GameDataEventListener;
import com.samdobsondev.lcde4j.api.watcher.PortWatcher;
import com.samdobsondev.lcde4j.exception.BaselineResponseException;
import com.samdobsondev.lcde4j.exception.SSLContextCreationException;
import com.samdobsondev.lcde4j.exception.SSLContextCreationFailedException;
import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.events.activeplayer.*;
import com.samdobsondev.lcde4j.model.events.allplayers.*;
import com.samdobsondev.lcde4j.model.events.announcer.*;
import com.samdobsondev.lcde4j.model.events.gamedata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LCDE4J {
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
    private static final Logger logger = LoggerFactory.getLogger(LCDE4J.class);
    private final List<ActivePlayerEventListener> activePlayerEventListeners = new ArrayList<>();
    private final List<AllPlayersEventListener> allPlayersEventListeners = new ArrayList<>();
    private final List<AnnouncerNotificationEventListener> announcerNotificationEventListeners = new ArrayList<>();
    private final List<GameDataEventListener> gameDataEventListeners = new ArrayList<>();

    public LCDE4J() {
        this(isPortUp -> {});
    }

    public LCDE4J(Consumer<Boolean> onPortStatusChange) {
        try {
            this.sslContext = createSSLContext();
            logger.info("SSL context created successfully.");
        } catch (Exception e) {
            logger.error("Failed to create SSL context", e);
            throw new SSLContextCreationFailedException("Failed to create SSL context", e);
        }
        portWatcher = new PortWatcher();
        this.onPortStatusChange = onPortStatusChange;
    }

    private SSLContext createSSLContext() throws SSLContextCreationException {
        char[] password = "nopass".toCharArray();

        try (InputStream is = getClass().getResourceAsStream("/riotgames.pem")) {
            if (is == null) {
                String errMsg = "Could not find certificate file '/riotgames.pem'";
                logger.error(errMsg);
                throw new SSLContextCreationException(errMsg);
            }

            // Load the keystore
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, password);
            logger.info("Keystore loaded...");

            // Create a CertificateFactory and import the certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
            logger.info("Certificate imported...");

            // Add the certificate to the keystore
            ks.setCertificateEntry("riotgames", cert);
            logger.info("Certificate added to keystore...");

            // Create a TrustManagerFactory with the trusted store
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            logger.info("TrustManagerFactory initialized with the trusted keystore...");

            // Create an SSLContext with the trust manager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            logger.info("Creating SSL context...");
            return context;
        } catch (Exception e) {
            String errMsg = "Failed to create SSLContext";
            logger.error(errMsg, e);
            throw new SSLContextCreationException(errMsg, e);
        }
    }

    public void start() {
        logger.info("Starting LCDE4J...");
        startPortWatcherService();
    }

    private void startPortWatcherService() {
        logger.info("Starting port watcher service...");
        new Thread(() -> {
            portWatcherStarted.set(true);
            while (portWatcherStarted.get()) {
                if (!checkPort()) {
                    try {
                        Thread.sleep(100); // we check the status of the port every 100 milliseconds
                    } catch (InterruptedException ignored) {
                        logger.warn("Interrupted while waiting to check port status.");
                    }
                }
            }
        }).start();
        logger.info("Port watcher service started.");
    }

    public boolean checkPort() {
        boolean isPortUp = this.portWatcher.isPortUp();
        if (previousPortStatus == null || isPortUp != previousPortStatus) {
            if (isPortUp) {
                logger.info("API Online");
                startPolling();
            } else {
                logger.info("API Offline, start an active game instance...");
                stopPolling();
            }
            previousPortStatus = isPortUp;
        }
        onPortStatusChange.accept(isPortUp);
        return isPortUp;
    }

    private void startPolling() {
        logger.info("Polling...");
        isPolling.set(true);

        // Load baseline response from JSON file
        AtomicReference<AllGameData> currentResponse = new AtomicReference<>(loadBaselineResponse());

        pollingTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                ApiResponse<AllGameData> apiResponse = requestLiveData("/liveclientdata/allgamedata", AllGameData.class);
                if (apiResponse == null || apiResponse.statusCode() == 404) {
                    logger.info("...");
                } else {
                    AllGameData incomingResponse = apiResponse.responseObject();

                    // TODO: Fix bug where activePlayer.getAbilities(getQ) in the first incomingResponse is sometimes null

                    // Dispatches all events that have occurred to the relevant listeners
                    processEvents(currentResponse.get(), incomingResponse);

                    // Update currentResponse to incomingResponse for the next poll
                    currentResponse.set(incomingResponse);
                }
            } catch (ConnectException e) { // it is possible (but-rare) that the port can go down mid-poll, such as when the game ends or crashes, so we catch that here
                logger.error("API connection lost during active polling. Polling process will be terminated until connection is re-established...");
                stopPolling();
            } catch (Exception e) {
                logger.error("Unexpected exception occurred during polling", e);
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    private AllGameData loadBaselineResponse() {
        logger.info("Loading baseline response...");
        try (InputStream is = getClass().getResourceAsStream("/baselineResponse.json")) {
            assert is != null;
            AllGameData data = GSON.fromJson(new InputStreamReader(is), AllGameData.class);
            logger.info("Baseline response successfully loaded.");
            return data;
        } catch (IOException e) {
            logger.error("Failed to load baseline response", e);
            throw new BaselineResponseException("Failed to load baseline response", e);
        }
    }

    public ApiResponse<AllGameData> requestLiveData(String path, Class<AllGameData> clz) throws IOException {
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

    private void stopPolling() {
        isPolling.set(false);
        if (pollingTask != null) {
            logger.info("Stopping polling...");
            pollingTask.cancel(false);
            logger.info("Polling has been stopped.");
        }
    }

    public void stop() {
        logger.info("Stopping the LCDE4J service...");
        portWatcherStarted.set(false);
        scheduler.shutdown();
        logger.info("LCDE4J service has been stopped.");
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
        for (ActivePlayerEvent activePlayerEvent : activePlayerEvents) {
            for (ActivePlayerEventListener listener : activePlayerEventListeners) {
                switch (activePlayerEvent.getActivePlayerEventType()) {
                    case ABILITY_LEVEL_UP -> {
                        assert activePlayerEvent instanceof AbilityLevelUpEvent;
                        listener.onAbilityLevelUp((AbilityLevelUpEvent) activePlayerEvent);
                    }
                    case GENERAL_RUNE -> {
                        assert activePlayerEvent instanceof GeneralRuneEvent;
                        listener.onGeneralRune((GeneralRuneEvent) activePlayerEvent);
                    }
                    case GOLD_CHANGE -> {
                        assert activePlayerEvent instanceof GoldChangeEvent;
                        listener.onGoldChange((GoldChangeEvent) activePlayerEvent);
                    }
                    case KEYSTONE -> {
                        assert activePlayerEvent instanceof KeystoneEvent;
                        listener.onKeystone((KeystoneEvent) activePlayerEvent);
                    }
                    case LEVEL_UP -> {
                        assert activePlayerEvent instanceof ActivePlayerLevelUpEvent;
                        listener.onLevelUp((ActivePlayerLevelUpEvent) activePlayerEvent);
                    }
                    case PRIMARY_RUNE_TREE -> {
                        assert activePlayerEvent instanceof PrimaryRuneTreeEvent;
                        listener.onPrimaryRuneTree((PrimaryRuneTreeEvent) activePlayerEvent);
                    }
                    case RESOURCE_TYPE -> {
                        assert activePlayerEvent instanceof ResourceTypeChangeEvent;
                        listener.onResourceTypeChange((ResourceTypeChangeEvent) activePlayerEvent);
                    }
                    case SECONDARY_RUNE_TREE -> {
                        assert activePlayerEvent instanceof SecondaryRuneTreeEvent;
                        listener.onSecondaryRuneTree((SecondaryRuneTreeEvent) activePlayerEvent);
                    }
                    case STAT_CHANGE -> {
                        assert activePlayerEvent instanceof StatChangeEvent;
                        listener.onStatChange((StatChangeEvent) activePlayerEvent);
                    }
                    case STAT_RUNE -> {
                        assert activePlayerEvent instanceof StatRuneEvent;
                        listener.onStatRune((StatRuneEvent) activePlayerEvent);
                    }
                    case SUMMONER_NAME -> {
                        assert activePlayerEvent instanceof SummonerNameEvent;
                        listener.onSummonerName((SummonerNameEvent) activePlayerEvent);
                    }
                    case TEAM_RELATIVE_COLORS_CHANGE -> {
                        assert activePlayerEvent instanceof TeamRelativeColorsChangeEvent;
                        listener.onTeamRelativeColorsChange((TeamRelativeColorsChangeEvent) activePlayerEvent);
                    }
                }
            }
        }
    }


    private void processAllPlayerEvents(List<AllPlayersEvent> allPlayersEvents) {
        for (AllPlayersEvent allPlayersEvent : allPlayersEvents) {
            for (AllPlayersEventListener listener : allPlayersEventListeners) {
                switch (allPlayersEvent.getAllPlayersEventType()) {
                    case ASSISTS_CHANGE -> {
                        assert allPlayersEvent instanceof AssistsChangeEvent;
                        listener.onAssistsChange((AssistsChangeEvent) allPlayersEvent);
                    }
                    case CS_CHANGE -> {
                        assert allPlayersEvent instanceof CreepScoreChangeEvent;
                        listener.onCreepScoreChange((CreepScoreChangeEvent) allPlayersEvent);
                    }
                    case DEATH -> {
                        assert allPlayersEvent instanceof DeathEvent;
                        listener.onDeath((DeathEvent) allPlayersEvent);
                    }
                    case DEATHS_CHANGE -> {
                        assert allPlayersEvent instanceof DeathsChangeEvent;
                        listener.onDeathsChange((DeathsChangeEvent) allPlayersEvent);
                    }
                    case EYE_OF_HERALD_USED_OR_LOST -> {
                        assert allPlayersEvent instanceof EyeOfHeraldUsedOrLostEvent;
                        listener.onEyeOfHeraldUsedOrLost((EyeOfHeraldUsedOrLostEvent) allPlayersEvent);
                    }
                    case ITEM_ACQUIRED -> {
                        assert allPlayersEvent instanceof ItemAcquiredEvent;
                        listener.onItemAcquired((ItemAcquiredEvent) allPlayersEvent);
                    }
                    case ITEM_SLOT_CHANGE -> {
                        assert allPlayersEvent instanceof ItemSlotChangeEvent;
                        listener.onItemSlotChange((ItemSlotChangeEvent) allPlayersEvent);
                    }
                    case ITEM_SOLD_OR_CONSUMED -> {
                        assert allPlayersEvent instanceof ItemSoldOrConsumedEvent;
                        listener.onItemSoldOrConsumed((ItemSoldOrConsumedEvent) allPlayersEvent);
                    }
                    case ITEM_TRANSFORMATION -> {
                        assert allPlayersEvent instanceof ItemTransformationEvent;
                        listener.onItemTransformation((ItemTransformationEvent) allPlayersEvent);
                    }
                    case KILLS_CHANGE -> {
                        assert allPlayersEvent instanceof KillsChangeEvent;
                        listener.onKillsChange((KillsChangeEvent) allPlayersEvent);
                    }
                    case LEVEL_UP -> {
                        assert allPlayersEvent instanceof LevelUpEvent;
                        listener.onLevelUp((LevelUpEvent) allPlayersEvent);
                    }
                    case PLAYER_JOINED -> {
                        assert allPlayersEvent instanceof PlayerJoinedEvent;
                        listener.onPlayerJoined((PlayerJoinedEvent) allPlayersEvent);
                    }
                    case RESPAWN -> {
                        assert allPlayersEvent instanceof RespawnEvent;
                        listener.onRespawn((RespawnEvent) allPlayersEvent);
                    }
                    case RESPAWN_TIMER_CHANGE -> {
                        assert allPlayersEvent instanceof RespawnTimerChangeEvent;
                        listener.onRespawnTimerChange((RespawnTimerChangeEvent) allPlayersEvent);
                    }
                    case SUMMONER_SPELL_ONE_CHANGE -> {
                        assert allPlayersEvent instanceof SummonerSpellOneChangeEvent;
                        listener.onSummonerSpellOneChange((SummonerSpellOneChangeEvent) allPlayersEvent);
                    }
                    case SUMMONER_SPELL_TWO_CHANGE -> {
                        assert allPlayersEvent instanceof SummonerSpellTwoChangeEvent;
                        listener.onSummonerSpellTwoChange((SummonerSpellTwoChangeEvent) allPlayersEvent);
                    }
                    case VISION_SCORE_CHANGE -> {
                        assert allPlayersEvent instanceof VisionScoreChangeEvent;
                        listener.onVisionScoreChange((VisionScoreChangeEvent) allPlayersEvent);
                    }
                }
            }
        }
    }

    private void processAnnouncerNotificationEvents(List<AnnouncerNotificationEvent> announcerNotificationEvents) {
        for (AnnouncerNotificationEvent announcerNotificationEvent : announcerNotificationEvents) {
            for (AnnouncerNotificationEventListener listener : announcerNotificationEventListeners) {
                switch (announcerNotificationEvent.getAnnouncerNotificationEventType()) {
                    case ACE ->
                    {
                        assert announcerNotificationEvent instanceof AceEvent;
                        listener.onAce((AceEvent) announcerNotificationEvent);
                    }
                    case BARON_KILL ->
                    {
                        assert announcerNotificationEvent instanceof BaronKillEvent;
                        listener.onBaronKill((BaronKillEvent) announcerNotificationEvent);
                    }
                    case CHAMPION_KILL ->
                    {
                        assert announcerNotificationEvent instanceof ChampionKillEvent;
                        listener.onChampionKill((ChampionKillEvent) announcerNotificationEvent);
                    }
                    case DRAGON_KILL ->
                    {
                        assert announcerNotificationEvent instanceof DragonKillEvent;
                        listener.onDragonKill((DragonKillEvent) announcerNotificationEvent);
                    }
                    case FIRST_BLOOD ->
                    {
                        assert announcerNotificationEvent instanceof FirstBloodEvent;
                        listener.onFirstBlood((FirstBloodEvent) announcerNotificationEvent);
                    }
                    case FIRST_TURRET ->
                    {
                        assert announcerNotificationEvent instanceof FirstTurretEvent;
                        listener.onFirstTurret((FirstTurretEvent) announcerNotificationEvent);
                    }
                    case GAME_END ->
                    {
                        assert announcerNotificationEvent instanceof GameEndEvent;
                        listener.onGameEnd((GameEndEvent) announcerNotificationEvent);
                    }
                    case GAME_START ->
                    {
                        assert announcerNotificationEvent instanceof GameStartEvent;
                        listener.onGameStart((GameStartEvent) announcerNotificationEvent);
                    }
                    case HERALD_KILL ->
                    {
                        assert announcerNotificationEvent instanceof HeraldKillEvent;
                        listener.onHeraldKill((HeraldKillEvent) announcerNotificationEvent);
                    }
                    case INHIBITOR_KILL ->
                    {
                        assert announcerNotificationEvent instanceof InhibitorKillEvent;
                        listener.onInhibitorKill((InhibitorKillEvent) announcerNotificationEvent);
                    }
                    case INHIBITOR_RESPAWN ->
                    {
                        assert announcerNotificationEvent instanceof InhibitorRespawnEvent;
                        listener.onInhibitorRespawn((InhibitorRespawnEvent) announcerNotificationEvent);
                    }
                    case INHIBITOR_RESPAWNING_SOON ->
                    {
                        assert announcerNotificationEvent instanceof InhibitorRespawingSoonEvent;
                        listener.onInhibitorRespawningSoon((InhibitorRespawingSoonEvent) announcerNotificationEvent);
                    }
                    case MINIONS_SPAWNING ->
                    {
                        assert announcerNotificationEvent instanceof MinionsSpawningEvent;
                        listener.onMinionsSpawning((MinionsSpawningEvent) announcerNotificationEvent);
                    }
                    case MULTIKILL ->
                    {
                        assert announcerNotificationEvent instanceof MultikillEvent;
                        listener.onMultikill((MultikillEvent) announcerNotificationEvent);
                    }
                    case TURRET_KILL ->
                    {
                        assert announcerNotificationEvent instanceof TurretKillEvent;
                        listener.onTurretKill((TurretKillEvent) announcerNotificationEvent);
                    }
                }
            }
        }
    }

    private void processGameDataEvents(List<GameDataEvent> gameDataEvents) {
        for (GameDataEvent gameDataEvent : gameDataEvents) {
            for (GameDataEventListener listener : gameDataEventListeners) {
                switch (gameDataEvent.getGameDataEventType())
                {
                    case GAME_MODE ->
                    {
                        assert gameDataEvent instanceof GameModeEvent;
                        listener.onGameModeChange((GameModeEvent) gameDataEvent);
                    }
                    case GAME_TIME_CHANGE ->
                    {
                        assert gameDataEvent instanceof GameTimeChangeEvent;
                        listener.onGameTimeChange((GameTimeChangeEvent) gameDataEvent);
                    }
                    case MAP_NAME ->
                    {
                        assert gameDataEvent instanceof MapNameEvent;
                        listener.onMapName((MapNameEvent) gameDataEvent);
                    }
                    case MAP_NUMBER ->
                    {
                        assert gameDataEvent instanceof MapNumberEvent;
                        listener.onMapNumber((MapNumberEvent) gameDataEvent);
                    }
                    case MAP_TERRAIN_CHANGE ->
                    {
                        assert gameDataEvent instanceof MapTerrainChangeEvent;
                        listener.onMapTerrainChange((MapTerrainChangeEvent) gameDataEvent);
                    }
                }
            }
        }
    }

    public void registerActivePlayerEventListener(ActivePlayerEventListener listener) {
        activePlayerEventListeners.add(listener);
        logger.info("ActivePlayerEventListener registered");
    }

    public void removeActivePlayerEventListener(ActivePlayerEventListener listener) {
        activePlayerEventListeners.remove(listener);
        logger.info("ActivePlayerEventListener removed");
    }

    public void registerAllPlayersEventListener(AllPlayersEventListener listener) {
        allPlayersEventListeners.add(listener);
        logger.info("AllPlayersEventListener registered");
    }

    public void removeAllPlayersEventListener(AllPlayersEventListener listener) {
        allPlayersEventListeners.remove(listener);
        logger.info("AllPlayersEventListener removed");
    }

    public void registerAnnouncerNotificationEventListener(AnnouncerNotificationEventListener listener) {
        announcerNotificationEventListeners.add(listener);
        logger.info("AnnouncerNotificationEventListener registered");
    }

    public void removeAnnouncerNotificationEventListener(AnnouncerNotificationEventListener listener) {
        announcerNotificationEventListeners.remove(listener);
        logger.info("AnnouncerNotificationEventListener removed");
    }

    public void registerGameDataEventListener(GameDataEventListener listener) {
        gameDataEventListeners.add(listener);
        logger.info("GameDataEventListener registered");
    }

    public void removeGameDataEventListener(GameDataEventListener listener) {
        gameDataEventListeners.remove(listener);
        logger.info("GameDataEventListener removed");
    }

}
