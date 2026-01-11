import nl.saxion.app.CsvReader;
import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static nl.saxion.app.SaxionApp.*;

public class Application implements GameLoop {

    static int SCREEN_WIDTH = 1536;
    static int SCREEN_HEIGHT = 1024;
    static int TARGET_FPS = 60;
    int PLAYER_BASE_HP = 150;
    int MAX_CORRUPTION = 120;
    int TURN_DURATION_MS = 5000;
    int TREE_SPIN_FRAME_COUNT = 48;
    int WALL_MAX_FRAMES = 90;
    int PUNCH_MAX_FRAMES = 32;


    int PLAY_BUTTON_X = 450;
    int PLAY_BUTTON_Y = 550;
    int TUTORIAL_BUTTON_X = 810;
    int TUTORIAL_BUTTON_Y = 440;
    int PLAYER_DRAW_X = 200;
    int PLAYER_DRAW_Y = 670;
    int FAIRY_DRAW_X = 970;
    int FAIRY_DRAW_Y = 700;


    int HUD_HP_W = 500;
    int HUD_MANA_W = 250;
    int HUD_CORRUPT_H = 750;
    int HUD_ENEMY_HP_W = 300;
    int GROUND_LEVEL = 800;
    int GROUND_OFFSET_FOR_SMALL_FAIRIES = 50;
    int GROUND_OFFSET_FOR_LARGE_FAIRY = 100;


    String BACKGROUND_INTRO_PATH = "resources/fairyButchersFrontPage.png";
    String BACKGROUND_SELECTION_PATH = "resources/characterSelection.png";
    String BACKGROUND_GAME_PATH = "resources/backgroundPictureBattleground.png";
    String DEFAULT_PLAYER_SPRITE = "quakeE.png";



    ArrayList<PlayerCharacters> characters = readingCSVFile();
    ArrayList<Fairy> fairies = new ArrayList<>();
    ArrayList<String> fairyOriginalPaths = new ArrayList<>(Arrays.asList("fairyOne.png", "FairyNo2.png", "evilFairy3.png"));
    ArrayList<Integer> fairyScales = new ArrayList<>(Arrays.asList(8 , 8, 9));
    ArrayList<String> fairyScaledPaths = new ArrayList<>();

    enum Turn {
        PLAYER,
        FAIRY
    }

    Turn currentTurn = Turn.PLAYER;
    boolean hoveringPlayButton = false;
    boolean hoveringTutorialButton = false;
    boolean inIntro = true;
    boolean inCharacterSelection = false;
    boolean corruptionDecayedThisTurn = false;
    boolean isGameOver = false;

    int selectedCharacterIndex = 0;
    int currentFairyIndex = 0;
    Fairy currentFairy;
    int corruptionLevel = 0;


    BufferedImage playerScaled;
    BufferedImage treeScaled;
    BufferedImage stoneScaled;
    BufferedImage wallScaled;
    BufferedImage punchScaled;
    BufferedImage potionFullScaled;
    BufferedImage potionEmptyScaled;

    // === Ability icon images and paths ===
    BufferedImage iconTree;
    BufferedImage iconStone;
    BufferedImage iconWall;
    BufferedImage iconPunch;


    String iconTreePath  = "resources/icon_tree_scaled.png";
    String iconStonePath = "resources/icon_stone_scaled.png";
    String iconWallPath  = "resources/icon_wall_scaled.png";
    String iconPunchPath = "resources/icon_punch_scaled.png";


    String potionFullScaledPath  = "resources/Filled potion.png";
    String potionEmptyScaledPath = "resources/Potions Empty.png";



    String playerScaledPath = "resources/player_scaled.png";
    String fairyScaledPath = "resources/fairy_scaled.png";
    String wallScaledPath = "resources/ability_wall_scaled.png";
    String stoneScaledPath = "resources/ability_stone_scaled.png";
    String treeScaledPath = "resources/ability_tree_scaled.png";
    String punchScaledPath = "resources/ability_double_punch_scaled.png";

    String playButtonScaledPath = "resources/playButton_scaled.png";
    String playButtonHoverScaledPath = "resources/playButton_hover_scaled.png";
    String tutorialButtonScaledPath = "resources/tutorialButton_scaled.png";
    String tutorialButtonHoverScaledPath = "resources/tutorialButton_hover_scaled.png";
    String currentFairyPath;


    int playButtonWidth = 0;
    int playButtonHeight = 0;
    int playButtonHoverWidth = 0;
    int playButtonHoverHeight = 0;
    int tutorialButtonWidth = 0;
    int tutorialButtonHeight = 0;
    int tutorialButtonHoverWidth = 0;
    int tutorialButtonHoverHeight = 0;


    boolean treeActive = false;
    int treeX, treeY;
    int treeSpeedX = 18;
    String[] treeSpinPaths;
    int treeSpinFrame = 0;

    boolean stoneActive = false;
    int stoneX, stoneY;
    int stoneSpeedX = 25;

    boolean wallActive = false;
    int wallFrame = 0;

    boolean punchActive = false;
    int punchFrame = 0;

    // ===== POTIONS =====
    int maxPotions = 3;     // total potions for the game
    int potionsLeft = 3;    // how many are still available

    boolean potionActive = false;   // is a drink animation playing?
    long potionStartTime = 0;       // when the animation began (ms)
    int potionDurationMs = 600;     // duration of animation in milliseconds (~0.6s)
    int potionAnimX, potionAnimY;   // where we draw the potion animation
    int currentFairyWidth;
    int currentFairyHeight;


    long gameStartTime = -1;
    long turnStartTime = -1;
    boolean turnTimerActive = false;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        startGameLoop(new Application(), SCREEN_WIDTH, SCREEN_HEIGHT, TARGET_FPS);
    }

    @Override
    public void init() {
        loadSprites();

        if (!characters.isEmpty()) {
            loadPlayerSprite(characters.get(0).png);
        } else {
            loadPlayerSprite(DEFAULT_PLAYER_SPRITE);
        }


        fairies.add(new Fairy(10, 20, 100));
        fairies.add(new Fairy(10, 20, 120));
        fairies.add(new Fairy(10, 20, 150));

        for(int i=0; i < fairies.size(); ++i)
        {
            String path = loadFairySprite(fairyOriginalPaths.get(i), fairyScales.get(i));
            fairyScaledPaths.add(path);
        }

        currentFairyIndex = 0;
        currentFairy = fairies.get(currentFairyIndex);
        currentFairyPath = fairyScaledPaths.get(currentFairyIndex);
    }

    @Override
    public void loop() {
        if (inIntro) {
            drawIntro();
            return;
        }

        if (inCharacterSelection) {
            drawCharacterSelection();
            return;
        }

        PlayerCharacters player = characters.get(selectedCharacterIndex);
        if(player.hp<=0){
            isGameOver = true;
            drawGame();
            return;
        }

        if(isGameOver){
            drawGameOver();
            return;
        }


        if(currentFairy != null && !currentFairy.isAlive()){
            nextFairy();
            if(currentFairy == null)
                return;
        }

        updateTurnTimer();

        if (currentTurn == Turn.FAIRY) {
            fairyTurn();
            currentTurn = Turn.PLAYER;
            startTurnTimer();
        }

        if (currentTurn == Turn.PLAYER && !corruptionDecayedThisTurn) {
            decayCorruption();
            corruptionDecayedThisTurn = true;
        } else if (currentTurn == Turn.FAIRY) {
            corruptionDecayedThisTurn = false;
        }

        drawGame();
    }

    private void handlePlayerAbility(int abilityKey, Runnable abilityAction, int attackCount) {
        if (currentTurn == Turn.PLAYER) {
            if (abilityAction != null) {
                abilityAction.run();
            }

            for (int i = 0; i < attackCount; i++) {
                attackEnemy();
            }

            currentTurn = Turn.FAIRY;
            turnTimerActive = false;
            corruptionDecayedThisTurn = false;
        }
    }

    @Override
    public void keyboardEvent(KeyboardEvent e) {
        if (e.getKeyCode() == KeyboardEvent.VK_ESCAPE) {
            System.exit(0);
        }

        if (inIntro) return;

        if (inCharacterSelection) {

            if (e.getKeyCode() >= KeyboardEvent.VK_1 && e.getKeyCode() <= KeyboardEvent.VK_4) {
                int index = e.getKeyCode() - KeyboardEvent.VK_1;
                if (index < characters.size()) {
                    selectedCharacterIndex = index;


                    PlayerCharacters selected = characters.get(selectedCharacterIndex);
                    loadPlayerSprite(selected.png);

                    inCharacterSelection = false;
                    startTurnTimer();
                }
                return;
            }
            return;
        }

        if (!inIntro && e.getKeyCode() == KeyboardEvent.VK_F) {
            if (currentTurn == Turn.PLAYER) {
                usePotion();
            }
        }


        if (e.getKeyCode() == KeyboardEvent.VK_Q) {
            handlePlayerAbility(KeyboardEvent.VK_Q, this::startTreeAbility, 1);
        } else if (e.getKeyCode() == KeyboardEvent.VK_W) {
            handlePlayerAbility(KeyboardEvent.VK_W, this::startStoneAbility, 1);
        } else if (e.getKeyCode() == KeyboardEvent.VK_E) {
            handlePlayerAbility(KeyboardEvent.VK_E, this::startWallAbility, 0);
        } else if (e.getKeyCode() == KeyboardEvent.VK_R) {
            handlePlayerAbility(KeyboardEvent.VK_R, this::startPunchAbility, 2);
        }
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        hoveringPlayButton = (mx >= PLAY_BUTTON_X && mx <= PLAY_BUTTON_X + playButtonWidth &&
                my >= PLAY_BUTTON_Y && my <= PLAY_BUTTON_Y + playButtonHeight);

        hoveringTutorialButton = (mx >= TUTORIAL_BUTTON_X && mx <= TUTORIAL_BUTTON_X + tutorialButtonWidth &&
                my >= TUTORIAL_BUTTON_Y && my <= TUTORIAL_BUTTON_Y + tutorialButtonHeight);

        if (inIntro && e.isMouseUp() && e.isLeftMouseButton()) {
            if (hoveringPlayButton) {
                inIntro = false;
                inCharacterSelection = true;
                startTimerIfNeeded();
            }
        }
    }



    private void drawIntro() {
        clear();
        drawImage(BACKGROUND_INTRO_PATH, 0, 0);


        if (hoveringPlayButton && playButtonHoverScaledPath != null) {
            int hoverX = PLAY_BUTTON_X - (playButtonHoverWidth - playButtonWidth) / 2;
            int hoverY = PLAY_BUTTON_Y - (playButtonHoverHeight - playButtonHeight) / 2;
            drawImage(playButtonHoverScaledPath, hoverX, hoverY);
        } else {
            drawImage(playButtonScaledPath, PLAY_BUTTON_X, PLAY_BUTTON_Y);
        }


        if (hoveringTutorialButton && tutorialButtonHoverScaledPath != null) {
            int hoverX = TUTORIAL_BUTTON_X - (tutorialButtonHoverWidth - tutorialButtonWidth) / 2;
            int hoverY = TUTORIAL_BUTTON_Y - (tutorialButtonHoverHeight - tutorialButtonHeight) / 2;
            drawImage(tutorialButtonHoverScaledPath, hoverX, hoverY);
        } else {
            drawImage(tutorialButtonScaledPath, TUTORIAL_BUTTON_X, TUTORIAL_BUTTON_Y);
        }
    }

    private void drawCharacterSelection() {
        clear();
        drawImage(BACKGROUND_SELECTION_PATH, 0, 0);

    }

    private void drawGame() {
        clear();
        drawImage(BACKGROUND_GAME_PATH, 0, 0);

        if(currentFairyPath != null) {

            // Calculate fairy dimensions
            int scale = fairyScales.get(currentFairyIndex);
            currentFairyWidth = 32 * scale;
            currentFairyHeight = 32 * scale;

            // Position fairy on right side
            FAIRY_DRAW_X = SCREEN_WIDTH - 570;

            // Calculate positions for each fairy size
            int groundLevel = SCREEN_HEIGHT - 50; // Ground is 150px from bottom

            if (currentFairyIndex == 0) {
                // First fairy (256px): place on ground
                FAIRY_DRAW_Y = groundLevel - currentFairyHeight;
            } else if (currentFairyIndex == 1) {
                // Second fairy (256px): slightly above ground
                FAIRY_DRAW_Y = groundLevel - currentFairyHeight - 20;
            } else {
                // Third fairy (320px): center vertically
                FAIRY_DRAW_Y = SCREEN_HEIGHT / 2 - currentFairyHeight / 2;
            }

            drawImage(currentFairyPath, FAIRY_DRAW_X, FAIRY_DRAW_Y);
        }

        if (playerScaled != null) {
            drawImage(playerScaledPath, PLAYER_DRAW_X, PLAYER_DRAW_Y);
        }

        drawAllAbilities();
        drawInGameHud();
    }

    private void drawInGameHud() {

        int hudHpX=10, hudHpY=10, hudHpH=10;
        int hudManaX=10, hudManaY=30, hudManaH=10;
        int hudCorruptX=1470, hudCorruptY=110, hudCorruptW=50;
        int hudEnemyX=0, hudEnemyY=0, hudEnemyH=10;
        int hudAbilityX=10;
        int hudAbilityStartY=110;
        int hudAbilitySize=60;

        setFill(Color.lightGray);
        drawRectangle(hudHpX, hudHpY, HUD_HP_W, hudHpH);
        drawRectangle(hudManaX, hudManaY, HUD_MANA_W, hudManaH);
        drawRectangle(hudCorruptX, hudCorruptY, hudCorruptW, HUD_CORRUPT_H);

        if(currentFairyIndex < 2){
            hudEnemyX = 950;
            hudEnemyY = 680;
        }
        else {
            hudEnemyX = FAIRY_DRAW_X + currentFairyWidth / 2 - HUD_ENEMY_HP_W /2;
            hudEnemyY = FAIRY_DRAW_Y - 40;
        }

        drawRectangle(hudEnemyX, hudEnemyY, HUD_ENEMY_HP_W, hudEnemyH);


        // Draw 4 ability slots + icons for Q, W, E, R
        for (int i = 0; i < 4; i++) {
            int boxX = hudAbilityX;
            int boxY = hudAbilityStartY + i * 70;

            // Draw the ability box
            drawRectangle(boxX, boxY, hudAbilitySize, hudAbilitySize);

            // Decide which HUD icon belongs in this slot
            String        iconPath  = null;
            BufferedImage iconImage = null;

            if (i == 0) {              // Q - Tree
                iconPath  = iconTreePath;
                iconImage = iconTree;
            } else if (i == 1) {       // W - Stone
                iconPath  = iconStonePath;
                iconImage = iconStone;
            } else if (i == 2) {       // E - Wall
                iconPath  = iconWallPath;
                iconImage = iconWall;
            } else if (i == 3) {       // R - Punch
                iconPath  = iconPunchPath;
                iconImage = iconPunch;
            }

            // ---- SCALE THE ICON TO FIT IN THE BOX ----
            if (iconPath != null && iconImage != null) {
                int originalW = iconImage.getWidth();
                int originalH = iconImage.getHeight();

                // how big the icon may be inside the box (leave margin 4 px each side)
                int maxSize = hudAbilitySize - 8;

                // scale to fit, keeping aspect ratio
                double scale = Math.min(
                        maxSize / (double) originalW,
                        maxSize / (double) originalH
                );

                int scaledW = (int) (originalW * scale);
                int scaledH = (int) (originalH * scale);

                // center the scaled icon in the box
                int iconX = boxX + (hudAbilitySize - scaledW) / 2;
                int iconY = boxY + (hudAbilitySize - scaledH) / 2;

                // this overload exists: (String path, int x, int y, int width, int height)
                drawImage(iconPath, iconX, iconY, scaledW, scaledH);
            }

            // Draw the key labels Q/W/E/R
            setTextDrawingColor(Color.white);
            String keyLabel = (i == 0) ? "Q"
                    : (i == 1) ? "W"
                    : (i == 2) ? "E" : "R";
            drawText(keyLabel,
                    boxX + hudAbilitySize + 10,
                    boxY + hudAbilitySize - 10,
                    18);
        }


        if (!characters.isEmpty()) {
            fillHealth(characters.get(selectedCharacterIndex).hp);
        }
        fillMana(75);
        fillCorruption(corruptionLevel);
        fillEnemy(currentFairy.hp, currentFairy.maxHp, hudEnemyX, hudEnemyY);

        drawTimer();
        drawTurnTimer();
        drawPotionsHud();
    }

    private ArrayList<PlayerCharacters> readingCSVFile() {
        ArrayList<PlayerCharacters> characters = new ArrayList<>();

        try {

            CsvReader reader = new CsvReader("resources/character.csv");
            reader.skipRow();
            reader.setSeparator(',');

            while (reader.loadRow()) {
                PlayerCharacters character = new PlayerCharacters();
                character.name = reader.getString(0);
                character.png = reader.getString(1); // ATENȚIE: numele cu spații și majuscule
                character.atk = reader.getInt(2);
                character.hp = reader.getInt(3);
                character.baseMana = reader.getInt(4);
                characters.add(character);
            }
        } catch (Exception e) {
            String[] names = {"Quake E", "Blood Twister", "Floaty Chocker", "Scorching Imp"};
            String[] images = {"quake E.png", "Blood Twister.png", "Floaty Choker.png", "Scorching imp.png"};
            int[] atks = {45, 35, 25, 15};

            for (int i = 0; i < 4; i++) {
                PlayerCharacters pc = new PlayerCharacters();
                pc.name = names[i];
                pc.png = images[i];
                pc.atk = atks[i];
                pc.hp = PLAYER_BASE_HP;
                pc.baseMana = 110;
                characters.add(pc);
            }
        }
        return characters;
    }

    private void loadPlayerSprite(String imageFileName) {
        try {
            String inputPath = "resources/" + imageFileName;
            playerScaled = loadSprite(inputPath, playerScaledPath, 10);
        } catch (Exception e) {
            try {
                playerScaled = loadSprite("resources/" + DEFAULT_PLAYER_SPRITE, playerScaledPath, 10);
            } catch (Exception ex) {
                System.err.println("Failed to load default sprite");
            }
        }
    }

    public void loadSprites() {
        try {
            BufferedImage originalFairy = loadSprite("resources/FairyNo2.png", fairyScaledPath, 8);

            BufferedImage playButtonOriginal = ImageIO.read(new File("resources/playButton.png"));
            loadScaledAndSave(playButtonOriginal, playButtonScaledPath, 8);
            loadScaledAndSave(playButtonOriginal, playButtonHoverScaledPath, 9);

            BufferedImage tutorialButtonOriginal = ImageIO.read(new File("resources/tutorialButton.png"));
            loadScaledAndSave(tutorialButtonOriginal, tutorialButtonScaledPath, 8);
            loadScaledAndSave(tutorialButtonOriginal, tutorialButtonHoverScaledPath, 9);

            wallScaled = loadSprite("resources/ability_wall.png", wallScaledPath, 1);
            treeScaled = loadSprite("resources/ability_tree.png", treeScaledPath, 1);
            stoneScaled = loadSprite("resources/ability_stone.png", stoneScaledPath, 1);
            punchScaled = loadSprite("resources/ability_double_punch.png", punchScaledPath, 1);
            potionFullScaled = loadSprite("resources/Full potion.png",  potionFullScaledPath, 2);
            potionEmptyScaled = loadSprite("resources/Empty potion.png", potionEmptyScaledPath, 2);


            // === Load separate ability icons for HUD ===
            iconTree  = loadSprite("resources/icon_tree.png",  iconTreePath, 1);
            iconStone = loadSprite("resources/icon_stone.png", iconStonePath, 1);
            iconWall  = loadSprite("resources/icon_wall.png",  iconWallPath, 1);
            iconPunch = loadSprite("resources/icon_punch.png", iconPunchPath, 1);


            treeSpinPaths = new String[TREE_SPIN_FRAME_COUNT];
            for (int i = 0; i < TREE_SPIN_FRAME_COUNT; i++) {
                double angleRad = 2 * Math.PI * i / TREE_SPIN_FRAME_COUNT;
                String path = "resources/ability_tree_spin_" + i + ".png";
                rotateSprite(treeScaled, angleRad, path);
                treeSpinPaths[i] = path;
            }

            playButtonWidth = (int)(ImageIO.read(new File(playButtonScaledPath)).getWidth());
            playButtonHeight = (int)(ImageIO.read(new File(playButtonScaledPath)).getHeight());
            playButtonHoverWidth = (int)(ImageIO.read(new File(playButtonHoverScaledPath)).getWidth());
            playButtonHoverHeight = (int)(ImageIO.read(new File(playButtonHoverScaledPath)).getHeight());

            tutorialButtonWidth = (int)(ImageIO.read(new File(tutorialButtonScaledPath)).getWidth());
            tutorialButtonHeight = (int)(ImageIO.read(new File(tutorialButtonScaledPath)).getHeight());
            tutorialButtonHoverWidth = (int)(ImageIO.read(new File(tutorialButtonHoverScaledPath)).getWidth());
            tutorialButtonHoverHeight = (int)(ImageIO.read(new File(tutorialButtonHoverScaledPath)).getHeight());


        } catch (Exception ex) {
            printLine("Failed to load sprites: " + ex.getMessage());
        }
    }

    private void loadScaledAndSave(BufferedImage original, String outputPath, int scale) throws Exception {
        int w = original.getWidth() * scale;
        int h = original.getHeight() * scale;

        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(original, 0, 0, w, h, null);
        g2.dispose();

        ImageIO.write(scaled, "png", new File(outputPath));
    }


    public BufferedImage loadSprite(String inputPath, String outputPath, int scale) {
        try {
            BufferedImage original = ImageIO.read(new File(inputPath));
            if (original == null) throw new Exception("Image not found: " + inputPath);

            int w = original.getWidth() * scale;
            int h = original.getHeight() * scale;

            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(original, 0, 0, w, h, null);
            g2.dispose();

            ImageIO.write(scaled, "png", new File(outputPath));
            return scaled;
        } catch (Exception ex) {
            System.err.println("Eroare la incarcarea/scalarea sprite-ului: " + inputPath + " - " + ex.getMessage());
            return null;
        }
    }

    private BufferedImage rotateSprite(BufferedImage src, double angleRad, String outputPath) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rotated.createGraphics();
        g2.translate(w / 2.0, h / 2.0);
        g2.rotate(angleRad);
        g2.drawImage(src, -w / 2, -h / 2, null);
        g2.dispose();

        try {
            ImageIO.write(rotated, "png", new File(outputPath));
        } catch (Exception ex) {
            printLine("Failed to rotate sprite: " + outputPath);
        }
        return rotated;
    }

    private void startTimerIfNeeded() {
        if (gameStartTime < 0) {
            gameStartTime = System.currentTimeMillis();
        }
    }

    private void drawTimer() {
        if (gameStartTime < 0) return;

        long elapsedMillis = System.currentTimeMillis() - gameStartTime;
        int totalSeconds = (int) (elapsedMillis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String timeText = String.format("%02d:%02d", minutes, seconds);
        setTextDrawingColor(Color.white);
        drawText("Time: " + timeText, 20, 70, 20);
    }

    public void fillHealth(int healthPoints) {
        setFill(Color.red);

        int maxPlayerHp = PLAYER_BASE_HP;

        int filler = (int) ((double) healthPoints / maxPlayerHp * HUD_HP_W);

        if (filler < 0) filler = 0;
        if (filler > HUD_HP_W) filler = HUD_HP_W;

        drawRectangle(10, 10, filler, 10);
    }

    public void fillMana(int manaPoints) {
        setFill(Color.blue);
        int maxMana = 100;
        int filler = (int) (Math.min(manaPoints, maxMana) / (double) maxMana * HUD_MANA_W);
        drawRectangle(10, 30, filler, 10);
    }

    public void fillCorruption(int corruptionLevel) {
        setFill(Color.magenta);
        int visibleLevel = Math.min(corruptionLevel, MAX_CORRUPTION);
        int filler = (int) ((double) visibleLevel / MAX_CORRUPTION * HUD_CORRUPT_H);
        if (filler < 1 && corruptionLevel > 0) filler = 1;

        int hudCorruptX=1470;
        int hudCorruptY=110;
        int hudCorruptW=50;
        int drawY = hudCorruptY + HUD_CORRUPT_H - filler;
        drawRectangle(hudCorruptX, drawY, hudCorruptW, filler);
    }

    public void fillEnemy(int hp, int maxHp, int x, int y) {
        setFill(Color.red);
        int filler = (int) ((double) hp / maxHp * HUD_ENEMY_HP_W);
        drawRectangle(x, y, filler, 10);
    }

    private int getPlayerCenterX() {
        return PLAYER_DRAW_X + (playerScaled != null ? playerScaled.getWidth() / 2 : 50);
    }

    private int getPlayerCenterY() {
        return PLAYER_DRAW_Y + (playerScaled != null ? playerScaled.getHeight() / 2 : 50);
    }

    private int getPlayerFeetY() {
        return PLAYER_DRAW_Y + (playerScaled != null ? playerScaled.getHeight() : 100);
    }

    private int getFairyCenterX() {
        return FAIRY_DRAW_X + 50;
    }

    private int getFairyCenterY() {
        return FAIRY_DRAW_Y + 50;
    }

    private void startTurnTimer() {
        turnStartTime = System.currentTimeMillis();
        turnTimerActive = true;
    }

    private void updateTurnTimer() {
        if (!turnTimerActive || currentTurn != Turn.PLAYER) return;

        long elapsed = System.currentTimeMillis() - turnStartTime;
        if (elapsed >= TURN_DURATION_MS) {
            turnTimerActive = false;
            currentTurn = Turn.FAIRY;
        }
    }

    private void drawTurnTimer() {
        if (!turnTimerActive || currentTurn != Turn.PLAYER) return;

        long elapsed = System.currentTimeMillis() - turnStartTime;
        long remainingMs = TURN_DURATION_MS - elapsed;
        if (remainingMs < 0) remainingMs = 0;
        int secondsLeft = (int) Math.floor(remainingMs / 1000.0);

        double phase = elapsed / 250.0;
        double scale = 1.0 + 0.15 * Math.sin(phase);
        int fontSize = (int) (40 * scale);

        int textX = SCREEN_WIDTH / 2 - 20;
        int textY = 80;

        setTextDrawingColor(Color.red);
        drawText(String.valueOf(secondsLeft), textX, textY, fontSize);
        setTextDrawingColor(Color.white);
    }

    private void startTreeAbility() {
        treeActive = true;
        int centerX = getPlayerCenterX();
        int playerFeetY = getPlayerFeetY();
        int treeH = (treeScaled != null ? treeScaled.getHeight() : 80);

        treeX = centerX - 400;
        treeY = playerFeetY - treeH + 100;
        treeSpinFrame = 0;
    }

    private void startStoneAbility() {
        stoneActive = true;
        int centerX = getPlayerCenterX();
        int playerFeetY = getPlayerFeetY();
        int stoneH = (stoneScaled != null ? stoneScaled.getHeight() : 50);

        stoneX = centerX - 475;
        stoneY = playerFeetY - stoneH + 250;
    }

    private void startWallAbility() {
        wallActive = true;
        wallFrame = 0;
    }

    private void startPunchAbility() {
        punchActive = true;
        punchFrame = 0;
    }

    private void drawTreeAbility() {
        if (!treeActive) return;

        treeX += treeSpeedX;

        if (treeSpinPaths != null && treeSpinPaths.length > 0) {
            String framePath = treeSpinPaths[treeSpinFrame];
            drawImage(framePath, treeX, treeY);
            treeSpinFrame = (treeSpinFrame + 1) % TREE_SPIN_FRAME_COUNT;
        } else {
            drawImage(treeScaledPath, treeX, treeY);
        }

        if (treeX > getFairyCenterX() + 30) {
            treeActive = false;
            treeSpinFrame = 0;
        }
    }

    private void drawStoneAbility() {
        if (!stoneActive) return;

        stoneX += stoneSpeedX;
        drawImage(stoneScaledPath, stoneX, stoneY);

        if (stoneX > getFairyCenterX() + 30) {
            stoneActive = false;
        }
    }

    private void drawWallAbility() {
        if (!wallActive) return;

        wallFrame++;
        int midX = (getPlayerCenterX() + getFairyCenterX()) / 2;
        int wallW = (wallScaled != null ? wallScaled.getWidth() : 100);
        int wallH = (wallScaled != null ? wallScaled.getHeight() : 120);
        int wallX = midX - wallW / 2;
        int playerFeetY = getPlayerFeetY();
        int baseY = playerFeetY - wallH + 100;

        int riseOffset = 0;
        if (wallFrame < 20) {
            riseOffset = (20 - wallFrame) * 3;
        } else if (wallFrame > WALL_MAX_FRAMES - 20) {
            riseOffset = (wallFrame - (WALL_MAX_FRAMES - 20)) * 3;
        }

        drawImage(wallScaledPath, wallX, baseY + riseOffset);

        if (wallFrame >= WALL_MAX_FRAMES) {
            wallActive = false;
        }
    }

    private void drawPunchAbility() {
        if (!punchActive) return;

        punchFrame++;
        int baseX = getPlayerCenterX() + 20;
        int baseY = 200;
        int quarter = PUNCH_MAX_FRAMES / 4;
        int speed = 10;
        int offsetX = 0;

        if (punchFrame < quarter) {
            offsetX = punchFrame * speed;
        } else if (punchFrame < 2 * quarter) {
            offsetX = (2 * quarter - punchFrame) * speed;
        } else if (punchFrame < 3 * quarter) {
            offsetX = (punchFrame - 2 * quarter) * speed;
        } else {
            offsetX = (4 * quarter - punchFrame) * speed;
        }

        int drawX = baseX + offsetX;
        drawImage(punchScaledPath, drawX, baseY);

        if (punchFrame >= PUNCH_MAX_FRAMES) {
            punchActive = false;
            punchFrame = 0;
        }
    }

    private void drawAllAbilities() {
        drawTreeAbility();
        drawStoneAbility();
        drawWallAbility();
        drawPunchAbility();
        drawPotionAnimation();
    }

    public void attackEnemy() {
        if (characters.isEmpty() || currentFairy == null) return;
        PlayerCharacters character = characters.get(selectedCharacterIndex);

        currentFairy.hp -= character.atk;

        if (currentFairy.hp < 0) {
            currentFairy.hp = 0;
        }
    }

    private String loadFairySprite(String fileName, int scale){
        try{
            String inputPath = "resources/" + fileName;
            String outputFileName = fileName.replace(".png", "_scaled.png");
            String outputPath =  "resources/" + outputFileName;

            loadSprite(inputPath, outputPath, scale);
            return outputPath;
        }
        catch(Exception e){
            System.err.println("Error loading sprites");
            return null;
        }
    }

    private void nextFairy() {
        currentFairyIndex++;

        boolean foundNextFairy = false;

        // Wrap around method
        for(int i=0; i<fairies.size();++i) {
            int index = (currentFairyIndex + i) % fairies.size();
            Fairy upcomingFairy = fairies.get(index);

            if (upcomingFairy.isAlive()) {
                currentFairy = upcomingFairy;
                currentFairyIndex = index;
                foundNextFairy = true;

                currentFairyPath = fairyScaledPaths.get(currentFairyIndex);


                if (currentFairyIndex > 0) {
                    currentFairy.increaseScaling();
                }

                currentTurn = Turn.PLAYER;
                startTurnTimer();
                corruptionLevel = 0;
                corruptionDecayedThisTurn = false;

                return;
            }
        }
        if(!foundNextFairy) {
            currentFairy = null;
            currentFairyPath = null;
        }
    }

    public void fairyTurn() {
        if (currentFairy == null || characters.isEmpty()) return;

        PlayerCharacters player = characters.get(selectedCharacterIndex);
        if (player.hp <= 0) return;

        boolean shouldHeal = currentFairy.hp < currentFairy.maxHp && Math.random() < 0.3;

        if (shouldHeal) {
            int healAmount = (int) (currentFairy.maxHp * 0.25);
            currentFairy.hp += healAmount;
            if (currentFairy.hp > currentFairy.maxHp) currentFairy.hp = currentFairy.maxHp;
            return;
        }

        int damage;
        double playerHealthRatio = (double) player.hp / PLAYER_BASE_HP;
        double corruptionRatio = (double) corruptionLevel / MAX_CORRUPTION;
        double fairyScaling = currentFairy.scaling;

        double airChance = 0.4;
        airChance += 0.4 * (1 - playerHealthRatio);
        airChance += 0.3 * corruptionRatio;
        airChance += 0.2 * (fairyScaling - 1);
        airChance = Math.min(Math.max(airChance, 0.1), 0.9);

        if (Math.random() < airChance) {
            damage = currentFairy.getAirDmg();
        } else {
            damage = currentFairy.getGroundDmg();
        }

        double critChance = 0.1 + 0.4 * corruptionRatio;
        if (Math.random() < critChance) {
            damage *= 2;
        }

        player.hp -= damage;
        System.out.println("Fairy deals " + damage + " damage. Player HP: " + player.hp);
        if (player.hp < 0) player.hp = 0;

        increaseCorruptionDamage(damage);
    }

    public void increaseCorruptionDamage(int damage) {
        double randomPercent = 0.10 + Math.random() * 0.05;
        int corruptionIncrease = (int) (randomPercent * damage);
        corruptionLevel += corruptionIncrease;
        if (corruptionLevel > MAX_CORRUPTION) corruptionLevel = MAX_CORRUPTION;
    }

    public void decayCorruption() {
        double baseDecay = 20 + Math.random() * 5;
        double extraDecay = 0;

        if (corruptionLevel > 100) {
            extraDecay = (corruptionLevel - 100) * 0.5;
        }

        int totalDecay = (int) (baseDecay + extraDecay);
        if (totalDecay < 1) totalDecay = 1;

        corruptionLevel -= totalDecay;
        if (corruptionLevel < 0) corruptionLevel = 0;
    }

    private void drawPotionsHud() {
        int slotSize = 48;   // size of each potion box (now only used for spacing)
        int spacing  = 8;    // space between potions

        // position on screen (adjust to where you want the potions)
        int startX = 10;
        int startY = 400;

        for (int i = 0; i < maxPotions; i++) {
            int x = startX + i * (slotSize + spacing);
            int y = startY;

            // REMOVE the background slot
            // SaxionApp.setFill(Color.darkGray);
            // SaxionApp.drawRectangle(x, y, slotSize, slotSize);

            // if this potion index is still available, show full; otherwise empty
            if (i < potionsLeft) {
                drawImage(potionFullScaledPath, x, y);
            } else {
                drawImage(potionEmptyScaledPath, x, y);
            }
        }
        // Optional hint text
        setTextDrawingColor(Color.white);
        drawText("Press F To Drink Potion", startX, startY + slotSize + 20, 16);

    }

    // ===== POTION ABILITY LOGIC =====
    private void usePotion() {
        // No potions or animation already playing: do nothing
        if (potionsLeft <= 0 || potionActive) return;

        if (characters.isEmpty()) return;  // safety

        PlayerCharacters player = characters.get(selectedCharacterIndex);

        int healAmount = 30;   // how much a potion heals (tune this)

        player.hp += healAmount;

        // Your HP bar fillHealth() uses 0-100, so we cap at 100
        if (player.hp > PLAYER_BASE_HP) player.hp = PLAYER_BASE_HP;

        potionsLeft--;

        // Start visual animation over the player
        startPotionAnimation();

        // If taking a potion should COST your turn:
        currentTurn = Turn.FAIRY;
        turnTimerActive = false;

        // If you want potion to be "free", delete the two lines above.
    }

    // Start the potion animation at the player's head
    private void startPotionAnimation() {
        potionActive = true;
        potionStartTime = System.currentTimeMillis();  // start time of animation

        // center of player sprite
        int centerX = getPlayerCenterX();

        // from drawGame() you draw player at y = 670
        int playerTopY = 670;

        int potionW = (potionFullScaled != null) ? potionFullScaled.getWidth() : 32;
        int potionH = (potionFullScaled != null) ? potionFullScaled.getHeight() : 32;

        // start just above the player's head
        potionAnimX = centerX - potionW / 2;
        potionAnimY = playerTopY - potionH - 20;
    }

    private void drawPotionAnimation() {
        if (!potionActive) return;

        long elapsed = System.currentTimeMillis() - potionStartTime;

        // If the animation time is up
        if (elapsed >= potionDurationMs) {
            System.out.println("Potion animation finished. Ready for next one.");
            potionActive = false; // THIS IS THE FIX: Allow another potion use
            return;
        }

        // Animation: Move upward based on time
        double progress = elapsed / (double) potionDurationMs;
        int currentY = potionAnimY - (int)(progress * 50);

        // Draw the actual image
        drawImage(potionFullScaledPath, potionAnimX, currentY);
    }

    private void drawGameOver() {
        // Darken the screen slightly
        setFill(new Color(0, 0, 0, 150));
        drawRectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Draw "GAME OVER" Text
        setTextDrawingColor(Color.red);
        drawText("GAME OVER", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2, 60);

        setTextDrawingColor(Color.white);
        drawText("The Fairy was too strong...", SCREEN_WIDTH / 2 - 140, SCREEN_HEIGHT / 2 + 50, 20);
        drawText("Press ESC to exit", SCREEN_WIDTH / 2 - 80, SCREEN_HEIGHT / 2 + 100, 15);
    }
}