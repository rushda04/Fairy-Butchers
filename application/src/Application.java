import nl.saxion.app.CsvReader;
import nl.saxion.app.SaxionApp;
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
    String curAtkImg = "";


    ArrayList<PlayerCharacters> characters = readingCSVFile();
    ArrayList<Fairy> fairies = new ArrayList<>();
    ArrayList<String> fairyOriginalPaths = new ArrayList<>(Arrays.asList("fairyOne.png", "FairyNo2.png", "evilFairy3.png"));
    ArrayList<Integer> fairyScales = new ArrayList<>(Arrays.asList(8, 8, 9));
    ArrayList<String> fairyScaledPaths = new ArrayList<>();
    String[] fairyProjectiles = {"resources/mushroomAttack_fairy1.png", "resources/mushroomAttack_fairy2.png", "resources/mushroomAttack_fairy3.png"};

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
    boolean fairyAttack = false;
    boolean atkIsCrit = false;
    boolean introMusic = false;
    boolean isVictory = false;
    boolean inTutorial = false;

    long gameOverStartTime = -1;

    float attackProgress = 0f;
    float attackStep = 0.03f;

    int selectedCharacterIndex = 0;
    int currentFairyIndex = 0;
    Fairy currentFairy;
    int corruptionLevel = 0;
    int curAtkX, curAtkY, curAtkTrgX, curAtkTrgY;
    int frameCounter = 0;


    BufferedImage playerScaled;
    BufferedImage treeScaled;
    BufferedImage stoneScaled;
    BufferedImage wallScaled;
    BufferedImage punchScaled;
    BufferedImage potionFullScaled;
    BufferedImage potionEmptyScaled;

    // === Water monster ability sprites ===
    BufferedImage waveScaled;
    BufferedImage cageScaled;
    BufferedImage shieldScaled;
    BufferedImage bloodsuckerScaled;

    // === Air monster ability sprites ===
    BufferedImage airFartScaled;
    BufferedImage airShieldScaled;
    BufferedImage airTornadoScaled;
    BufferedImage airSuperAttackScaled;

    // === Fire monster ability sprites ===
    BufferedImage fireFireballScaled;
    BufferedImage fireWhipScaled;
    BufferedImage fireHelldoorScaled;
    BufferedImage fireBurnTheWichScaled;

    // === Ability icon images and paths ===
    BufferedImage iconTree;
    BufferedImage iconStone;
    BufferedImage iconWall;
    BufferedImage iconPunch;


    String iconTreePath = "resources/icon_tree_scaled.png";
    String iconStonePath = "resources/icon_stone_scaled.png";
    String iconWallPath = "resources/icon_wall_scaled.png";
    String iconPunchPath = "resources/icon_punch_scaled.png";

    // === Water ability icons ===
    BufferedImage iconWaterWave;
    BufferedImage iconWaterCage;
    BufferedImage iconWaterShield;
    BufferedImage iconWaterBloodsucker;

    String iconWaterWavePath = "resources/icon_water_wave_scaled.png";
    String iconWaterCagePath = "resources/icon_water_cage_scaled.png";
    String iconWaterShieldPath = "resources/icon_water_shield_scaled.png";
    String iconWaterBloodsuckerPath = "resources/icon_water_bloodsucker_scaled.png";

    // === Fire ability icons ===
    BufferedImage iconFireFireball;
    BufferedImage iconFireWhip;
    BufferedImage iconFireHelldoor;
    BufferedImage iconFireBurnTheWich;

    String iconFireFireballPath = "resources/fire_icon_fireball_scaled.png";
    String iconFireWhipPath = "resources/fire_icon_whip_scaled.png";
    String iconFireHelldoorPath = "resources/fire_icon_helldoor_scaled.png";
    String iconFireBurnTheWichPath = "resources/fire_icon_burnthewich_scaled.png";

    // === Air ability icons ===
    BufferedImage iconAirFart;
    BufferedImage iconAirShield;
    BufferedImage iconAirTornado;
    BufferedImage iconAirSuperAttack;


    String iconAirFartPath = "resources/air_icon_fart_scaled.png";
    String iconAirShieldPath = "resources/air_icon_shield_scaled.png";
    String iconAirTornadoPath = "resources/air_icon_tornado_scaled.png";
    String iconAirSuperAttackPath = "resources/air_icon_superattack_scaled.png";

    String potionFullScaledPath = "resources/Filled potion.png";
    String potionEmptyScaledPath = "resources/Potions Empty.png";


    String playerScaledPath = "resources/player_scaled.png";
    String fairyScaledPath = "resources/fairy_scaled.png";
    String wallScaledPath = "resources/ability_wall_scaled.png";
    String stoneScaledPath = "resources/ability_stone_scaled.png";
    String treeScaledPath = "resources/ability_tree_scaled.png";
    String punchScaledPath = "resources/ability_double_punch_scaled.png";

    String waveScaledPath = "resources/water_ability_wave_scaled.png";
    String cageScaledPath = "resources/water_ability_cage_scaled.png";
    String shieldScaledPath = "resources/water_ability_shield_scaled.png";
    String bloodsuckerScaledPath = "resources/water_ability_bloodsucker_scaled.png";

    String airFartScaledPath = "resources/air_ability_fart_scaled.png";
    String airShieldScaledPath = "resources/air_ability_shield_scaled.png";
    String airTornadoScaledPath = "resources/air_ability_tornado_scaled.png";
    String airSuperAttackScaledPath = "resources/air_ability_superattack_scaled.png";

    // === Fire abilities scaled output paths ===
    String fireFireballScaledPath = "resources/fire_ability_fireball_scaled.png";
    String fireWhipScaledPath = "resources/fire_ability_whip_scaled.png";
    String fireHelldoorScaledPath = "resources/fire_ability_helldoor_scaled.png";
    String fireBurnTheWichScaledPath = "resources/fire_ability_witch_scaled.png";

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
    int treeSpeedX = 35;
    String[] treeSpinPaths;
    int treeSpinFrame = 0;
    double TREE_DRAW_SCALE = 0.6;

    boolean stoneActive = false;
    int stoneX, stoneY;
    int stoneSpeedX = 25;

    boolean wallActive = false;
    int wallFrame = 0;
    double WALL_DRAW_SCALE = 0.7;

    boolean punchActive = false;
    int punchFrame = 0;

    // === Water abilities state ===
    boolean waveActive = false;
    int waveX, waveY;
    int waveSpeedX = 22;

    boolean cageActive = false;
    int cageFrame = 0;
    int CAGE_MAX_FRAMES = 60;

    boolean shieldActive = false;
    int shieldFrame = 0;
    int SHIELD_MAX_FRAMES = 60;

    boolean bloodsuckerActive = false;
    int bloodX, bloodY;
    int bloodSpeedX = 24;

    int waveFrame = 0;
    int WAVE_MAX_FRAMES = 30;

    int bloodFrame = 0;
    int BLOOD_MAX_FRAMES = 50;

    // === Air abilities state ===
    boolean airFartActive = false;
    int airFartFrame = 0;
    int AIR_FART_MAX_FRAMES = 30;

    boolean airShieldActive = false;
    int airShieldFrame = 0;
    int AIR_SHIELD_MAX_FRAMES = 60;

    boolean airTornadoActive = false;
    int airTornadoFrame = 0;
    int AIR_TORNADO_MAX_FRAMES = 50;

    boolean airSuperActive = false;
    int airSuperFrame = 0;
    int AIR_SUPER_MAX_FRAMES = 60;

    // === Fire abilities state ===
    boolean fireFireballActive = false;
    int fireFireballFrame = 0;
    int FIRE_FIREBALL_MAX_FRAMES = 30;

    boolean fireWhipActive = false;
    int fireWhipFrame = 0;
    int FIRE_WHIP_MAX_FRAMES = 24;

    boolean fireHelldoorActive = false;
    int fireHelldoorFrame = 0;
    int FIRE_HELLDOOR_MAX_FRAMES = 45;

    boolean fireBurnTheWichActive = false;
    int fireBurnTheWichFrame = 0;
    int FIRE_BURNTHEWICH_MAX_FRAMES = 50;

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

        for (int i = 0; i < fairies.size(); ++i) {
            String path = loadFairySprite(fairyOriginalPaths.get(i), fairyScales.get(i));
            fairyScaledPaths.add(path);
        }

        currentFairyIndex = 0;
        currentFairy = fairies.get(currentFairyIndex);
        currentFairyPath = fairyScaledPaths.get(currentFairyIndex);

        playSound("resources/Fantasy Choir 1.wav");
    }

    @Override
    public void loop() {
        frameCounter++;

        if (inTutorial) {
            drawTutorial();
            return;
        }

        if (inIntro) {
            drawIntro();
            return;
        }

        if (inCharacterSelection) {
            drawCharacterSelection();
            return;
        }

        PlayerCharacters player = characters.get(selectedCharacterIndex);

        if (corruptionLevel >= MAX_CORRUPTION) {
            corruptionLevel = 0;
        }

        if (corruptionLevel >= (MAX_CORRUPTION * 0.75)) {
            if (frameCounter % 60 == 0) {
                player.hp -= 2;
            }
        }

        // Check for player death - this sets isGameOver to true
        if (player.hp <= 0) {
            isGameOver = true;
        }

        // If game is over, draw game over screen and handle reset
        if (isGameOver) {
            drawGameOver();

            if (gameOverStartTime == -1) {
                gameOverStartTime = System.currentTimeMillis();
            }

            // Fixed: Use gameOverStartTime instead of gameStartTime
            if (System.currentTimeMillis() - gameOverStartTime > 3000) {
                resetGame();
            }
            return;  // Return early to prevent normal game drawing
        }

        // Only continue with normal game logic if game is not over
        if (currentFairy != null && !currentFairy.isAlive()) {
            nextFairy();
            if (currentFairy == null)
                return;
        }

        updateTurnTimer();

        if (currentTurn == Turn.FAIRY && !fairyAttack) {
            fairyTurn();
        }

        if (currentTurn == Turn.PLAYER && !corruptionDecayedThisTurn) {
            decayCorruption();
            corruptionDecayedThisTurn = true;
        } else if (currentTurn == Turn.FAIRY) {
            corruptionDecayedThisTurn = false;
        }

        drawGame();
    }

    private void handlePlayerAbility(int manaCost, Runnable abilityAction, int attackCount) {
        PlayerCharacters player = characters.get(selectedCharacterIndex);

        if (currentTurn == Turn.PLAYER && player.currentMana >= manaCost) {

            player.currentMana -= manaCost;

            int finalAtck = player.atk;
            if (corruptionLevel > 0 && corruptionLevel < (MAX_CORRUPTION * 0.75)) {
                finalAtck = (int) (player.atk * 1.5);
            }

            if (abilityAction != null) {
                abilityAction.run();
            }

            for (int i = 0; i < attackCount; i++) {
                attackEnemy(finalAtck);
            }

            currentTurn = Turn.FAIRY;
            turnTimerActive = false;
            corruptionDecayedThisTurn = false;

            player.currentMana = Math.min(player.baseMana, player.currentMana + 10);
        }
    }

    @Override
    public void keyboardEvent(KeyboardEvent e) {

        if (inTutorial) {
            if (e.getKeyCode() == KeyboardEvent.VK_SPACE) {
                inTutorial = false;
            }
            return;
        }

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

                    // Force reload the player sprite
                    loadPlayerSprite(selected.png);

                    // Also reset player stats for the new character
                    selected.hp = PLAYER_BASE_HP;
                    selected.currentMana = selected.baseMana;

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

        // Determine mana costs based on character type
        if (e.getKeyCode() == KeyboardEvent.VK_Q) {
            if (isWaterCharacter()) {
                handlePlayerAbility(15, this::startWaveAbility, 1);
            } else if (isFireCharacter()) {
                // Q: Fireball → 1 attack
                handlePlayerAbility(15, this::startFireFireballAbility, 1);
            } else if (isAirCharacter()) {
                handlePlayerAbility(15, this::startAirFartAbility, 1);
            } else if (isEarthCharacter()) {
                handlePlayerAbility(15, this::startTreeAbility, 1);
            }

        } else if (e.getKeyCode() == KeyboardEvent.VK_W) {
            if (isWaterCharacter()) {
                handlePlayerAbility(30, this::startCageAbility, 1);
            } else if (isFireCharacter()) {
                // W: Whip → 1 attack
                handlePlayerAbility(30, this::startFireWhipAbility, 1);
            } else if (isAirCharacter()) {
                handlePlayerAbility(30, this::startAirShieldAbility, 0);
            } else if (isEarthCharacter()) {
                handlePlayerAbility(30, this::startStoneAbility, 1);
            }

        } else if (e.getKeyCode() == KeyboardEvent.VK_E) {
            if (isWaterCharacter()) {
                handlePlayerAbility(20, this::startShieldAbility, 0);
            } else if (isFireCharacter()) {
                // E: Helldoor → 0 attacks (pure effect)
                handlePlayerAbility(20, this::startFireHelldoorAbility, 0);
            } else if (isAirCharacter()) {
                handlePlayerAbility(20, this::startAirTornadoAbility, 1);
            } else if (isEarthCharacter()) {
                handlePlayerAbility(20, this::startWallAbility, 0);
            }

        } else if (e.getKeyCode() == KeyboardEvent.VK_R) {
            if (isWaterCharacter()) {
                handlePlayerAbility(60, this::startBloodsuckerAbility, 2);
            } else if (isFireCharacter()) {
                // R: Burn the wich → 2 attacks (big damage)
                handlePlayerAbility(60, this::startFireBurnTheWichAbility, 2);
            } else if (isAirCharacter()) {
                handlePlayerAbility(60, this::startAirSuperAbility, 2);
            } else if (isEarthCharacter()) {
                handlePlayerAbility(60, this::startPunchAbility, 2);
            }
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
                // Removed stopAllSounds() since it's not defined
                inIntro = false;
                introMusic = false;
                inCharacterSelection = true;
                startTimerIfNeeded();
            } else if (hoveringTutorialButton) {
                inTutorial = true;
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

        if (currentFairyPath != null) {

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

        if (fairyAttack) {
            drawFairyAttack();
        }

        drawAllAbilities();
        drawInGameHud();
    }

    private void drawInGameHud() {

        // === 1. Basic positions (same as before, but grouped) ===
        int hudHpX = 10, hudHpY = 10, hudHpH = 10;
        int hudManaX = 10, hudManaY = 30, hudManaH = 10;
        int hudCorruptX = 1470, hudCorruptY = 110, hudCorruptW = 50;  // right-side corruption bar
        int hudEnemyX = 0, hudEnemyY = 0, hudEnemyH = 10;

        int hudAbilityX = 10;
        int hudAbilityStartY = 110;
        int hudAbilitySize = 60;

        // === 2. Draw empty bar backgrounds ===
        setFill(Color.lightGray);
        drawRectangle(hudHpX, hudHpY, HUD_HP_W, hudHpH);
        drawRectangle(hudManaX, hudManaY, HUD_MANA_W, hudManaH);
        drawRectangle(hudCorruptX, hudCorruptY, hudCorruptW, HUD_CORRUPT_H);

        // Enemy HP bar position (left for small fairies, above big fairy)
        if (currentFairyIndex < 2) {
            hudEnemyX = 950;
            hudEnemyY = 680;
        } else {
            hudEnemyX = FAIRY_DRAW_X + currentFairyWidth / 2 - HUD_ENEMY_HP_W / 2;
            hudEnemyY = FAIRY_DRAW_Y - 40;
        }
        drawRectangle(hudEnemyX, hudEnemyY, HUD_ENEMY_HP_W, hudEnemyH);

        // === 3. Ability icons Q/W/E/R in boxes ===
        for (int i = 0; i < 4; i++) {
            int boxX = hudAbilityX;
            int boxY = hudAbilityStartY + i * 70;

            // box background
            setFill(Color.lightGray);
            drawRectangle(boxX, boxY, hudAbilitySize, hudAbilitySize);

            // Decide which HUD icon belongs in this slot
            String iconPath = null;
            BufferedImage iconImage = null;

            if (isWaterCharacter()) {
                // Water monster icons (Q,W,E,R)
                if (i == 0) {              // Q - Wave
                    iconPath = iconWaterWavePath;
                    iconImage = iconWaterWave;
                } else if (i == 1) {       // W - Cage
                    iconPath = iconWaterCagePath;
                    iconImage = iconWaterCage;
                } else if (i == 2) {       // E - Shield
                    iconPath = iconWaterShieldPath;
                    iconImage = iconWaterShield;
                } else if (i == 3) {       // R - Bloodsucker
                    iconPath = iconWaterBloodsuckerPath;
                    iconImage = iconWaterBloodsucker;
                }

            } else if (isFireCharacter()) {
                // Fire monster icons (Q,W,E,R)
                if (i == 0) {              // Q - Fireball
                    iconPath = iconFireFireballPath;
                    iconImage = iconFireFireball;
                } else if (i == 1) {       // W - Whip
                    iconPath = iconFireWhipPath;
                    iconImage = iconFireWhip;
                } else if (i == 2) {       // E - Helldoor
                    iconPath = iconFireHelldoorPath;
                    iconImage = iconFireHelldoor;
                } else if (i == 3) {       // R - Burn the wich
                    iconPath = iconFireBurnTheWichPath;
                    iconImage = iconFireBurnTheWich;
                }

            } else if (isAirCharacter()) {
                // Air monster icons (Q,W,E,R)
                if (i == 0) {              // Q - Fart (or gust)
                    iconPath = iconAirFartPath;
                    iconImage = iconAirFart;
                } else if (i == 1) {       // W - Shield
                    iconPath = iconAirShieldPath;
                    iconImage = iconAirShield;
                } else if (i == 2) {       // E - Tornado
                    iconPath = iconAirTornadoPath;
                    iconImage = iconAirTornado;
                } else if (i == 3) {       // R - Super attack
                    iconPath = iconAirSuperAttackPath;
                    iconImage = iconAirSuperAttack;
                }

            } else if (isEarthCharacter()) {
                // Earth monster icons (Q,W,E,R)
                if (i == 0) {              // Q - Tree
                    iconPath = iconTreePath;
                    iconImage = iconTree;
                } else if (i == 1) {       // W - Stone
                    iconPath = iconStonePath;
                    iconImage = iconStone;
                } else if (i == 2) {       // E - Wall
                    iconPath = iconWallPath;
                    iconImage = iconWall;
                } else if (i == 3) {       // R - Punch
                    iconPath = iconPunchPath;
                    iconImage = iconPunch;
                }
            }

            // Scale the icon to fit nicely in the box
            if (iconPath != null && iconImage != null) {
                int originalW = iconImage.getWidth();
                int originalH = iconImage.getHeight();

                int maxSize = hudAbilitySize - 8;  // small margin
                double scale = Math.min(
                        maxSize / (double) originalW,
                        maxSize / (double) originalH
                );

                int scaledW = (int) (originalW * scale);
                int scaledH = (int) (originalH * scale);

                int iconX = boxX + (hudAbilitySize - scaledW) / 2;
                int iconY = boxY + (hudAbilitySize - scaledH) / 2;

                drawImage(iconPath, iconX, iconY, scaledW, scaledH);
            }

            // Draw the key labels Q/W/E/R next to each box
            setTextDrawingColor(Color.white);
            String keyLabel = (i == 0) ? "Q"
                    : (i == 1) ? "W"
                    : (i == 2) ? "E" : "R";
            drawText(keyLabel,
                    boxX + hudAbilitySize + 10,
                    boxY + hudAbilitySize - 10,
                    18);
        }

        // === 4. Fill bars with actual values ===
        if (!characters.isEmpty()) {
            fillHealth(characters.get(selectedCharacterIndex).hp);
        }
        PlayerCharacters player = characters.get(selectedCharacterIndex);
        fillMana(player.currentMana, player.baseMana);
        fillCorruption(corruptionLevel);
        fillEnemy(currentFairy.hp, currentFairy.maxHp, hudEnemyX, hudEnemyY);

        // === 5. Timer, turn timer, potions ===
        drawTimer();
        drawTurnTimer();
        drawPotionsHud();

        // Draw percentages
        int hpPercent = (int) ((double) player.hp / PLAYER_BASE_HP * 100);
        if (hpPercent < 0) hpPercent = 0;
        if (hpPercent <= 25) {
            setTextDrawingColor(new Color(220, 0, 0));
        } else {
            setTextDrawingColor(Color.white);
        }
        drawText(hpPercent + "%", 520, 22, 18);

        setTextDrawingColor(Color.cyan);
        int manaPercent = (int) ((double) player.currentMana / player.baseMana * 100);
        drawText(manaPercent + "%", 270, 42, 18);

        int corruptPercent = (int) ((double) corruptionLevel / MAX_CORRUPTION * 100);
        if (corruptPercent > 100) corruptPercent = 100;
        if (corruptPercent > 75) setTextDrawingColor(new Color(220, 0, 0));
        else setTextDrawingColor(Color.white);
        drawText(corruptPercent + "%", 1475, 800, 18);

        if (currentFairy != null) {
            int enemyPercent = (int) ((double) currentFairy.hp / currentFairy.maxHp * 100);
            if (enemyPercent < 0) enemyPercent = 0;

            if (enemyPercent > 50) {
                int blueVal = (int) (255 * (enemyPercent - 50) / 50.0);
                setTextDrawingColor(new Color(255, 255, blueVal));
            } else {
                int greenVal = 140 + (int) (115 * (enemyPercent / 50.0));
                setTextDrawingColor(new Color(255, greenVal, 0));
            }

            drawText(enemyPercent + "%", hudEnemyX + (HUD_ENEMY_HP_W / 2) - 15, hudEnemyY - 15, 18);
        }
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
                character.png = reader.getString(1);
                character.atk = reader.getInt(2);
                character.hp = reader.getInt(3);
                character.baseMana = reader.getInt(4);
                character.currentMana = character.baseMana;
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
                pc.currentMana = pc.baseMana;
                characters.add(pc);
            }
        }
        return characters;
    }

    private void loadPlayerSprite(String imageFileName) {
        try {
            String inputPath = "resources/" + imageFileName;
            // Create a unique output path for each character
            String outputFileName = imageFileName.replace(".png", "_scaled.png");
            String outputPath = "resources/" + outputFileName;

            playerScaled = loadSprite(inputPath, outputPath, 10);
            playerScaledPath = outputPath; // Update the path to the new file
        } catch (Exception e) {
            try {
                // Fallback to default sprite with unique name
                String outputPath = "resources/player_scaled_default.png";
                playerScaled = loadSprite("resources/" + DEFAULT_PLAYER_SPRITE, outputPath, 10);
                playerScaledPath = outputPath;
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
            potionFullScaled = loadSprite("resources/Full potion.png", potionFullScaledPath, 2);
            potionEmptyScaled = loadSprite("resources/Empty potion.png", potionEmptyScaledPath, 2);

            // === Water monster abilities ===
            waveScaled = loadSprite("resources/water_ability_wave.png", waveScaledPath, 5);
            cageScaled = loadSprite("resources/water_ability_cage.png", cageScaledPath, 10);
            shieldScaled = loadSprite("resources/water_ability_shield.png", shieldScaledPath, 5);
            bloodsuckerScaled = loadSprite("resources/water_ability_bloodsucker.png", bloodsuckerScaledPath, 5);

            // === Air monster abilities ===
            airFartScaled = loadSprite("resources/air_ability_fart.png", airFartScaledPath, 10);
            airShieldScaled = loadSprite("resources/air_ability_shield.png", airShieldScaledPath, 10);
            airTornadoScaled = loadSprite("resources/air_ability_tornado.png", airTornadoScaledPath, 10);
            airSuperAttackScaled = loadSprite("resources/air_ability_superattack.png", airSuperAttackScaledPath, 10);

            // === Fire monster abilities ===
            fireFireballScaled = loadSprite("resources/fire_ability_fireball.png", fireFireballScaledPath, 5);
            fireWhipScaled = loadSprite("resources/fire_ability_whip.png", fireWhipScaledPath, 5);
            fireHelldoorScaled = loadSprite("resources/fire_ability_helldoor.png", fireHelldoorScaledPath, 10);
            fireBurnTheWichScaled = loadSprite("resources/fire_ability_witch.png", fireBurnTheWichScaledPath, 10);

            // === Load separate ability icons for HUD ===
            iconTree = loadSprite("resources/icon_tree.png", iconTreePath, 1);
            iconStone = loadSprite("resources/icon_stone.png", iconStonePath, 1);
            iconWall = loadSprite("resources/icon_wall.png", iconWallPath, 1);
            iconPunch = loadSprite("resources/icon_punch.png", iconPunchPath, 1);

            // === Load water ability icons ===
            iconWaterWave = loadSprite("resources/icon_water_wave.png", iconWaterWavePath, 1);
            iconWaterCage = loadSprite("resources/icon_water_cage.png", iconWaterCagePath, 1);
            iconWaterShield = loadSprite("resources/icon_water_shield.png", iconWaterShieldPath, 1);
            iconWaterBloodsucker = loadSprite("resources/icon_water_bloodsucker.png", iconWaterBloodsuckerPath, 1);

            // === Load fire ability icons ===
            iconFireFireball = loadSprite("resources/fire_icon_fireball.png", iconFireFireballPath, 1);
            iconFireWhip = loadSprite("resources/fire_icon_whip.png", iconFireWhipPath, 1);
            iconFireHelldoor = loadSprite("resources/fire_icon_helldoor.png", iconFireHelldoorPath, 1);
            iconFireBurnTheWich = loadSprite("resources/fire_icon_burnthewich.png", iconFireBurnTheWichPath, 1);

            // === Load air ability icons ===
            iconAirFart = loadSprite("resources/air_icon_fart.png", iconAirFartPath, 1);
            iconAirShield = loadSprite("resources/air_icon_shield.png", iconAirShieldPath, 1);
            iconAirTornado = loadSprite("resources/air_icon_tornado.png", iconAirTornadoPath, 1);
            iconAirSuperAttack = loadSprite("resources/air_icon_superattack.png", iconAirSuperAttackPath, 1);

            treeSpinPaths = new String[TREE_SPIN_FRAME_COUNT];
            for (int i = 0; i < TREE_SPIN_FRAME_COUNT; i++) {
                double angleRad = 2 * Math.PI * i / TREE_SPIN_FRAME_COUNT;
                String path = "resources/ability_tree_spin_" + i + ".png";
                rotateSprite(treeScaled, angleRad, path);
                treeSpinPaths[i] = path;
            }

            playButtonWidth = (int) (ImageIO.read(new File(playButtonScaledPath)).getWidth());
            playButtonHeight = (int) (ImageIO.read(new File(playButtonScaledPath)).getHeight());
            playButtonHoverWidth = (int) (ImageIO.read(new File(playButtonHoverScaledPath)).getWidth());
            playButtonHoverHeight = (int) (ImageIO.read(new File(playButtonHoverScaledPath)).getHeight());

            tutorialButtonWidth = (int) (ImageIO.read(new File(tutorialButtonScaledPath)).getWidth());
            tutorialButtonHeight = (int) (ImageIO.read(new File(tutorialButtonScaledPath)).getHeight());
            tutorialButtonHoverWidth = (int) (ImageIO.read(new File(tutorialButtonHoverScaledPath)).getWidth());
            tutorialButtonHoverHeight = (int) (ImageIO.read(new File(tutorialButtonHoverScaledPath)).getHeight());


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
            System.err.println("Error loading sprite: " + inputPath + " - " + ex.getMessage());
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

    public void fillMana(int current, int max) {
        setFill(Color.blue);
        int filler = (int) ((double) current / max * HUD_MANA_W);
        drawRectangle(10, 30, filler, 10);
    }

    public void fillCorruption(int corruptionLevel) {
        if (corruptionLevel >= 90) {
            setFill(Color.red);
        } else {
            setFill(Color.magenta);
        }
        int visibleLevel = Math.min(corruptionLevel, MAX_CORRUPTION);
        double ratio = (double) visibleLevel / MAX_CORRUPTION;
        int filler = (int) (ratio * HUD_CORRUPT_H);

        int hudCorruptX = 1470;
        int hudCorruptY = 110;
        int hudCorruptW = 50;
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
        return FAIRY_DRAW_X + currentFairyWidth / 2;
    }

    private int getFairyCenterY() {
        return FAIRY_DRAW_Y + currentFairyHeight / 2;
    }

    // Earth = character 4 (index 3), Water = character 3 (index 2), Fire = character 2 (index 1), Air = character 1 (index 0)
    private boolean isEarthCharacter() {
        return selectedCharacterIndex == 3;   // key '4'
    }

    private boolean isWaterCharacter() {
        return selectedCharacterIndex == 0;   // key '1'
    }

    private boolean isFireCharacter() {
        return selectedCharacterIndex == 1;   // key '2'
    }

    private boolean isAirCharacter() {
        return selectedCharacterIndex == 2;   // key '3'
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
        treeSpinFrame = 0;

        int centerX = getPlayerCenterX();
        int playerFeetY = getPlayerFeetY();

        // Original (loaded) tree size
        int origW = (treeScaled != null ? treeScaled.getWidth() : 80);
        int origH = (treeScaled != null ? treeScaled.getHeight() : 80);

        // Size we will actually draw
        int drawW = (int) (origW * TREE_DRAW_SCALE);
        int drawH = (int) (origH * TREE_DRAW_SCALE);

        // Start just in front of the player, near his feet
        treeX = centerX - drawW / 2;
        treeY = playerFeetY - drawH + 20;   // adjust +20 up/down if needed
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

    // === WATER MONSTER ABILITIES ===

    private void startWaveAbility() {
        waveActive = true;
        waveFrame = 0;   // reset animation

        int centerX = getPlayerCenterX();
        int playerFeetY = getPlayerFeetY();
        int h = (waveScaled != null ? waveScaled.getHeight() : 80);

        waveX = centerX;
        waveY = playerFeetY - h + 80;  // base Y for the arc
    }

    private void startCageAbility() {
        cageActive = true;
        cageFrame = 0;
    }

    private void startShieldAbility() {
        shieldActive = true;
        shieldFrame = 0;
    }

    private void startBloodsuckerAbility() {
        bloodsuckerActive = true;
        bloodFrame = 0;     // reset animation
    }

    // === AIR MONSTER ABILITIES ===

    // Q - Fart: fast arc projectile from player to fairy
    private void startAirFartAbility() {
        airFartActive = true;
        airFartFrame = 0;
    }

    // W - Shield: orbiting shield around player
    private void startAirShieldAbility() {
        airShieldActive = true;
        airShieldFrame = 0;
    }

    // E - Tornado: tornado under the fairy
    private void startAirTornadoAbility() {
        airTornadoActive = true;
        airTornadoFrame = 0;
    }

    // R - Super attack: black hole at fairy center grows then shrinks
    private void startAirSuperAbility() {
        airSuperActive = true;
        airSuperFrame = 0;
    }

    // === FIRE MONSTER ABILITIES ===
    // Q - Fireball: projectile from player to fairy
    private void startFireFireballAbility() {
        fireFireballActive = true;
        fireFireballFrame = 0;
    }

    // W - Whip: horizontal lash from player towards fairy
    private void startFireWhipAbility() {
        fireWhipActive = true;
        fireWhipFrame = 0;
    }

    // E - Helldoor: portal behind fairy that grows then shrinks
    private void startFireHelldoorAbility() {
        fireHelldoorActive = true;
        fireHelldoorFrame = 0;
    }

    // R - Burn the wich: big vertical flames on the fairy
    private void startFireBurnTheWichAbility() {
        fireBurnTheWichActive = true;
        fireBurnTheWichFrame = 0;
    }

    private void drawTreeAbility() {
        if (!treeActive) return;

        // Move tree horizontally to the right
        treeX += treeSpeedX;

        // Original sprite size
        int origW = (treeScaled != null ? treeScaled.getWidth() : 80);
        int origH = (treeScaled != null ? treeScaled.getHeight() : 80);

        // Draw size after scaling
        int drawW = (int) (origW * TREE_DRAW_SCALE);
        int drawH = (int) (origH * TREE_DRAW_SCALE);

        // Choose spin frame (if available)
        String framePath;
        if (treeSpinPaths != null && treeSpinPaths.length > 0) {
            framePath = treeSpinPaths[treeSpinFrame];
            treeSpinFrame = (treeSpinFrame + 1) % TREE_SPIN_FRAME_COUNT;
        } else {
            framePath = treeScaledPath;
        }

        // Draw the scaled tree
        drawImage(framePath, treeX, treeY, drawW, drawH);

        // ==== Collision: let the tree go all the way across the fairy ====
        int treeRight = treeX + drawW;
        int fairyRight = FAIRY_DRAW_X + currentFairyWidth + drawW / 3;   // full width of fairy

        if (treeRight >= fairyRight) {
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

        // Original wall sprite size
        int origW = (wallScaled != null ? wallScaled.getWidth() : 100);
        int origH = (wallScaled != null ? wallScaled.getHeight() : 120);

        // Final draw size (scaled down so it isn't so tall)
        int wallW = (int) (origW * WALL_DRAW_SCALE);
        int wallH = (int) (origH * WALL_DRAW_SCALE);

        // ----- POSITION: just in front (bottom-right) of the player -----

        // Left edge of the player on screen
        int playerLeft = PLAYER_DRAW_X;

        // Horizontal offset from the player's left side to where the wall should stand.
        // Tweak this until it looks good. 240 is a good starting value.
        int offsetFromPlayer = 500;

        // Center the wall around that point
        int wallXCenter = playerLeft + offsetFromPlayer;
        int wallX = wallXCenter - wallW / 2;

        // Y: make the wall stand on the same "ground" as the player
        int playerFeetY = getPlayerFeetY();
        int groundOffsetY = 250;              // tweak this
        int baseY = playerFeetY - wallH + groundOffsetY;

        // ----- Rising / staying / sinking animation -----

        int riseDuration = 20;           // frames to rise up
        int sinkDuration = 20;           // frames to sink down
        int totalFrames = WALL_MAX_FRAMES;

        int riseOffset = 0;

        if (wallFrame < riseDuration) {
            // Start below ground and move up
            riseOffset = (riseDuration - wallFrame) * 3;   // positive → lower (below baseY)
        } else if (wallFrame > totalFrames - sinkDuration) {
            // Sink back into the ground at the end
            riseOffset = (wallFrame - (totalFrames - sinkDuration)) * 3;
        }

        int wallY = baseY + riseOffset;

        // Draw the scaled wall in front of the player
        drawImage(wallScaledPath, wallX, wallY, wallW, wallH);

        // End of animation
        if (wallFrame >= WALL_MAX_FRAMES) {
            wallActive = false;
            wallFrame = 0;
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

    private void drawFireWhipAbility() {
        if (!fireWhipActive) return;

        fireWhipFrame++;
        double t = fireWhipFrame / (double) FIRE_WHIP_MAX_FRAMES;

        if (t >= 1.0) {
            fireWhipActive = false;
            fireWhipFrame = 0;
            return;
        }

        int w = (fireWhipScaled != null ? fireWhipScaled.getWidth() : 120);
        int h = (fireWhipScaled != null ? fireWhipScaled.getHeight() : 60);

        int baseX = getPlayerCenterX();
        int baseY = getPlayerCenterY() - h / 4;  // around the hand

        // Extend then retract
        double reach = 300;   // max distance
        double factor = (t < 0.5) ? (t / 0.5) : (1.0 - (t - 0.5) / 0.5); // 0→1→0
        int offsetX = (int) (reach * factor);

        int x = baseX + offsetX - w / 2;
        int y = baseY - h / 2;

        drawImage(fireWhipScaledPath, x, y);
    }

    private void drawWaveAbility() {
        if (!waveActive) return;

        waveFrame++;
        double t = waveFrame / (double) WAVE_MAX_FRAMES;   // 0..1

        if (t >= 1.0) {
            waveActive = false;
            return;
        }

        int w = (waveScaled != null ? waveScaled.getWidth() : 80);
        int h = (waveScaled != null ? waveScaled.getHeight() : 80);

        // Start at player, end at fairy
        int startX = getPlayerCenterX() - w / 2;
        int endX = getFairyCenterX() - w / 2;

        // Base Y is where you already placed the wave
        int baseY = waveY;

        // Make it follow an arc (up then down)
        int arcHeight = 60;
        int offsetY = (int) (Math.sin(t * Math.PI) * -arcHeight);

        int x = (int) (startX + t * (endX - startX));
        int y = baseY + offsetY;

        drawImage(waveScaledPath, x, y);
    }

    private void drawCageAbility() {
        if (!cageActive || currentFairy == null) return;

        cageFrame++;

        // Make the cage a bit larger than the fairy
        int margin = 200;  // adjust this if you want tighter/looser cage
        int w = currentFairyWidth + margin;
        int h = currentFairyHeight + margin;

        // Center on the fairy
        int centerX = FAIRY_DRAW_X + currentFairyWidth / 2;
        int centerY = FAIRY_DRAW_Y + currentFairyHeight / 2;

        int x = centerX - w / 2;
        int y = centerY - h / 2;

        // Draw cage scaled to this size
        drawImage(cageScaledPath, x, y, w, h);

        if (cageFrame >= CAGE_MAX_FRAMES) {
            cageActive = false;
        }
    }

    private void drawShieldAbility() {
        if (!shieldActive) return;

        shieldFrame++;

        int fullW = (shieldScaled != null ? shieldScaled.getWidth() : 100);
        int fullH = (shieldScaled != null ? shieldScaled.getHeight() : 100);

        // How much bigger than the original sprite you want the shield
        double baseFactor = 1.8;      // 1.0 = original, 1.5 = 50% bigger, 2.0 = twice as big

        // Pulse the shield: scale up and down around that base size
        double phase = (shieldFrame % 20) / 20.0 * 2 * Math.PI;  // 0..2π
        double pulse = 0.9 + 0.2 * (0.5 + 0.5 * Math.sin(phase));  // ~0.9 .. 1.1

        int w = (int) (fullW * baseFactor * pulse);
        int h = (int) (fullH * baseFactor * pulse);

        // CENTER BETWEEN PLAYER AND FAIRY
        int midX = (getPlayerCenterX() + getFairyCenterX()) / 2;
        int midY = (getPlayerCenterY() + getFairyCenterY()) / 2;

        int x = midX - w / 2;
        int y = midY - h / 2;

        // Draw scaled shield in the middle
        drawImage(shieldScaledPath, x, y, w, h);

        if (shieldFrame >= SHIELD_MAX_FRAMES) {
            shieldActive = false;
        }
    }

    private void drawFireHelldoorAbility() {
        if (!fireHelldoorActive || currentFairy == null) return;

        fireHelldoorFrame++;
        double t = fireHelldoorFrame / (double) FIRE_HELLDOOR_MAX_FRAMES;

        if (t >= 1.0) {
            fireHelldoorActive = false;
            fireHelldoorFrame = 0;
            return;
        }

        int fullW = (fireHelldoorScaled != null ? fireHelldoorScaled.getWidth() : 120);
        int fullH = (fireHelldoorScaled != null ? fireHelldoorScaled.getHeight() : 180);

        // Grow then shrink (0.3 → 1.1 → 0.3)
        double scale = 0.3 + 0.8 * Math.sin(t * Math.PI);
        int w = (int) (fullW * scale);
        int h = (int) (fullH * scale);

        int centerX = getFairyCenterX();
        int fairyFeetY = FAIRY_DRAW_Y + currentFairyHeight;

        int x = centerX - w / 2;
        int y = fairyFeetY - h;  // stand on ground

        drawImage(fireHelldoorScaledPath, x, y, w, h);
    }

    private void drawFireBurnTheWichAbility() {
        if (!fireBurnTheWichActive || currentFairy == null) return;

        fireBurnTheWichFrame++;
        double t = fireBurnTheWichFrame / (double) FIRE_BURNTHEWICH_MAX_FRAMES;

        if (t >= 1.0) {
            fireBurnTheWichActive = false;
            fireBurnTheWichFrame = 0;
            return;
        }

        int fullW = (fireBurnTheWichScaled != null ? fireBurnTheWichScaled.getWidth() : 140);
        int fullH = (fireBurnTheWichScaled != null ? fireBurnTheWichScaled.getHeight() : 200);

        // Flicker: scale oscillates a bit
        double flicker = 0.85 + 0.15 * Math.sin(t * 6 * Math.PI);  // fast flicker
        double baseScale = 0.7 + 0.3 * Math.sin(t * Math.PI);      // grow then shrink
        double scale = baseScale * flicker;

        int w = (int) (fullW * scale);
        int h = (int) (fullH * scale);

        int centerX = getFairyCenterX();
        int fairyFeetY = FAIRY_DRAW_Y + currentFairyHeight;

        int baseY = fairyFeetY - h;
        int rise = (int) (t * 40);  // flames rise a bit
        int x = centerX - w / 2;
        int y = baseY - rise;

        drawImage(fireBurnTheWichScaledPath, x, y, w, h);
    }

    private void drawBloodsuckerAbility() {
        if (!bloodsuckerActive) return;

        bloodFrame++;
        double t = bloodFrame / (double) BLOOD_MAX_FRAMES;  // 0..1

        if (t >= 1.0) {
            bloodsuckerActive = false;
            return;
        }

        int w = (bloodsuckerScaled != null ? bloodsuckerScaled.getWidth() : 60);
        int h = (bloodsuckerScaled != null ? bloodsuckerScaled.getHeight() : 60);

        int playerX = getPlayerCenterX();
        int playerY = getPlayerCenterY();
        int fairyX = getFairyCenterX();
        int fairyY = getFairyCenterY();

        // First half: player -> fairy, second half: fairy -> player
        double p;
        int fromX, fromY, toX, toY;

        if (t < 0.5) {
            // Going from player to fairy
            p = t / 0.5;          // 0..1
            fromX = playerX;
            fromY = playerY;
            toX = fairyX;
            toY = fairyY;
        } else {
            // Coming back from fairy to player
            p = (t - 0.5) / 0.5;  // 0..1
            fromX = fairyX;
            fromY = fairyY;
            toX = playerX;
            toY = playerY;
        }

        int centerX = (int) (fromX + p * (toX - fromX));
        int centerY = (int) (fromY + p * (toY - fromY));

        // Small vertical curve to make the path less straight
        int curveHeight = 30;
        centerY += (int) (Math.sin(p * Math.PI) * -curveHeight);

        int drawX = centerX - w / 2;
        int drawY = centerY - h / 2;

        drawImage(bloodsuckerScaledPath, drawX, drawY);
    }

    private void drawAirFartAbility() {
        if (!airFartActive) return;

        airFartFrame++;
        double t = airFartFrame / (double) AIR_FART_MAX_FRAMES;   // 0..1

        if (t >= 1.0) {
            airFartActive = false;
            return;
        }

        int w = (airFartScaled != null ? airFartScaled.getWidth() : 40);
        int h = (airFartScaled != null ? airFartScaled.getHeight() : 40);

        int startX = getPlayerCenterX() - w / 2;
        int endX = getFairyCenterX() - w / 2;

        // Base Y: middle of player
        int baseY = getPlayerCenterY() - h / 2;

        // Small arc (up then down)
        int arcHeight = 40;
        int offsetY = (int) (Math.sin(t * Math.PI) * -arcHeight);

        int x = (int) (startX + t * (endX - startX));
        int y = baseY + offsetY;

        drawImage(airFartScaledPath, x, y);
    }

    private void drawFireFireballAbility() {
        if (!fireFireballActive) return;

        fireFireballFrame++;
        double t = fireFireballFrame / (double) FIRE_FIREBALL_MAX_FRAMES;  // 0..1

        if (t >= 1.0) {
            fireFireballActive = false;
            fireFireballFrame = 0;
            return;
        }

        int w = (fireFireballScaled != null ? fireFireballScaled.getWidth() : 50);
        int h = (fireFireballScaled != null ? fireFireballScaled.getHeight() : 50);

        int startX = getPlayerCenterX() - w / 2;
        int endX = getFairyCenterX() - w / 2;

        int baseY = getPlayerCenterY() - h / 2;

        int arcHeight = 60;
        int offsetY = (int) (Math.sin(t * Math.PI) * -arcHeight);

        int x = (int) (startX + t * (endX - startX));
        int y = baseY + offsetY;

        drawImage(fireFireballScaledPath, x, y);
    }

    private void drawAirShieldAbility() {
        if (!airShieldActive) return;

        airShieldFrame++;

        int w = (airShieldScaled != null ? airShieldScaled.getWidth() : 60);
        int h = (airShieldScaled != null ? airShieldScaled.getHeight() : 60);

        int centerX = getPlayerCenterX();
        int centerY = getPlayerCenterY();

        double angle = airShieldFrame * 0.2;  // radians; tweak for speed
        int radius = 80;                    // distance from player center
        int orbitX = centerX + (int) (radius * Math.cos(angle));
        int orbitY = centerY + (int) (radius * Math.sin(angle));

        int x = orbitX - w / 2;
        int y = orbitY - h / 2;

        drawImage(airShieldScaledPath, x, y);

        if (airShieldFrame >= AIR_SHIELD_MAX_FRAMES) {
            airShieldActive = false;
            airShieldFrame = 0;
        }
    }

    private void drawAirTornadoAbility() {
        if (!airTornadoActive) return;

        airTornadoFrame++;
        double t = airTornadoFrame / (double) AIR_TORNADO_MAX_FRAMES;

        if (t >= 1.0) {
            airTornadoActive = false;
            airTornadoFrame = 0;
            return;
        }

        int w = (airTornadoScaled != null ? airTornadoScaled.getWidth() : 80);
        int h = (airTornadoScaled != null ? airTornadoScaled.getHeight() : 120);

        int centerX = getFairyCenterX();
        int fairyFeetY = FAIRY_DRAW_Y + currentFairyHeight;

        int baseY = fairyFeetY - h;   // tornado stands on fairy's ground

        // Rise up slightly over time
        int rise = (int) (t * 30);     // move up to 30 pixels

        int x = centerX - w / 2;
        int y = baseY - rise;

        drawImage(airTornadoScaledPath, x, y);
    }

    private void drawAirSuperAbility() {
        if (!airSuperActive) return;

        airSuperFrame++;
        double t = airSuperFrame / (double) AIR_SUPER_MAX_FRAMES;

        if (t >= 1.0) {
            airSuperActive = false;
            airSuperFrame = 0;
            return;
        }

        int fullW = (airSuperAttackScaled != null ? airSuperAttackScaled.getWidth() : 80);
        int fullH = (airSuperAttackScaled != null ? airSuperAttackScaled.getHeight() : 80);

        // Grow then shrink (pulse)
        double scale = 1.0 + 0.5 * Math.sin(t * Math.PI); // 1.0 → 1.5 → 1.0

        int w = (int) (fullW * scale);
        int h = (int) (fullH * scale);

        int centerX = getFairyCenterX();
        int centerY = getFairyCenterY();

        int x = centerX - w / 2;
        int y = centerY - h / 2;

        drawImage(airSuperAttackScaledPath, x, y, w, h);
    }

    private void drawAllAbilities() {
        if (isWaterCharacter()) {
            drawWaveAbility();
            drawCageAbility();
            drawShieldAbility();
            drawBloodsuckerAbility();
        } else if (isFireCharacter()) {
            drawFireFireballAbility();
            drawFireWhipAbility();
            drawFireHelldoorAbility();
            drawFireBurnTheWichAbility();
        } else if (isAirCharacter()) {
            drawAirFartAbility();
            drawAirShieldAbility();
            drawAirTornadoAbility();
            drawAirSuperAbility();
        } else if (isEarthCharacter()) {
            drawTreeAbility();
            drawStoneAbility();
            drawWallAbility();
            drawPunchAbility();
        }

        drawPotionAnimation();
    }

    public void attackEnemy(int damage) {
        if (characters.isEmpty() || currentFairy == null) return;
        PlayerCharacters character = characters.get(selectedCharacterIndex);

        currentFairy.hp -= damage;

        if (currentFairy.hp < 0) {
            currentFairy.hp = 0;
        }
    }

    private String loadFairySprite(String fileName, int scale) {
        try {
            String inputPath = "resources/" + fileName;
            String outputFileName = fileName.replace(".png", "_scaled.png");
            String outputPath = "resources/" + outputFileName;

            loadSprite(inputPath, outputPath, scale);
            return outputPath;
        } catch (Exception e) {
            System.err.println("Error loading sprites");
            return null;
        }
    }

    private void nextFairy() {
        currentFairyIndex++;

        if (currentFairyIndex < fairies.size()) {
            currentFairy = fairies.get(currentFairyIndex);
            currentFairyPath = fairyScaledPaths.get(currentFairyIndex);

            // Normal turn reset
            currentTurn = Turn.PLAYER;
            corruptionLevel = 0;
            corruptionDecayedThisTurn = false;
            startTurnTimer();
        } else {
            // VICTORY: Stop everything and trigger the Game Over state
            currentFairy = null;
            isGameOver = true;
            gameOverStartTime = System.currentTimeMillis();
        }
    }

    public void fairyTurn() {
        if (currentFairy == null || characters.isEmpty()) return;

        PlayerCharacters player = characters.get(selectedCharacterIndex);
        if (player.hp <= 0) return;

        curAtkImg = fairyProjectiles[currentFairyIndex];
        atkIsCrit = false;
        playSound("resources/sfx_fly.wav");

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
            curAtkTrgY = PLAYER_DRAW_Y + 50;
        } else {
            damage = currentFairy.getGroundDmg();
            curAtkTrgY = PLAYER_DRAW_Y + 150;
        }

        double critChance = 0.1 + 0.4 * corruptionRatio;
        if (Math.random() < critChance) {
            damage *= 2;
            atkIsCrit = true;
        }

        attackProgress = 0;
        fairyAttack = true;

        player.hp -= damage;
        System.out.println("Fairy deals " + damage + " damage. Player HP: " + player.hp);
        if (player.hp < 0) player.hp = 0;

        increaseCorruptionDamage(damage);
    }

    public void increaseCorruptionDamage(int damage) {
        int corruptionIncrease = 35 + (int) (0.15 * damage);
        corruptionLevel += corruptionIncrease;
        if (corruptionLevel > MAX_CORRUPTION) {
            corruptionLevel = MAX_CORRUPTION;
        }
    }

    public void decayCorruption() {
        int totalDecay = 10;

        corruptionLevel -= totalDecay;
        if (corruptionLevel < 0) corruptionLevel = 0;

        PlayerCharacters player = characters.get(selectedCharacterIndex);
        player.currentMana = Math.min(player.baseMana, player.currentMana + 10);
    }

    private void drawPotionsHud() {
        int slotSize = 48;   // size of each potion box (now only used for spacing)
        int spacing = 8;    // space between potions

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
        int currentY = potionAnimY - (int) (progress * 50);

        // Draw the actual image
        drawImage(potionFullScaledPath, potionAnimX, currentY);
    }

    private void drawGameOver() {
        drawImage("resources/Game Over.png", 0, 0);
    }

    private void drawFairyAttack() {
        if (!fairyAttack) return;

        int startX = FAIRY_DRAW_X;
        int startY = FAIRY_DRAW_Y + (currentFairyHeight / 2);
        int targetX = PLAYER_DRAW_X + 100;

        curAtkX = (int) (startX + (targetX - startX) * attackProgress);
        curAtkY = (int) (startY + (curAtkTrgY - startY) * attackProgress);

        int displayX = curAtkX;
        int displayY = curAtkY;
        if (atkIsCrit) {
            displayX += (int) (Math.random() * 10 - 5);
            displayY += (int) (Math.random() * 10 - 5);
            setFill(new Color(255, 0, 0, 80));
            drawCircle(displayX + 30, displayY + 30, 40);
        }

        drawImage(curAtkImg, displayX, displayY);

        attackProgress += attackStep;

        if (attackProgress >= 1.0f) {
            fairyAttack = false;
            attackProgress = 0;
            currentTurn = Turn.PLAYER;
            startTurnTimer();
        }
    }

    private void resetGame() {
        isGameOver = false;
        isVictory = false;
        inIntro = true;
        inCharacterSelection = false;
        gameOverStartTime = -1;
        gameStartTime = -1;

        // Reset character selection to the first one
        selectedCharacterIndex = 0;
        if (!characters.isEmpty()) {
            PlayerCharacters firstChar = characters.get(0);
            // This should reload the sprite properly
            loadPlayerSprite(firstChar.png);

            // Reset stats for all characters
            for (PlayerCharacters pc : characters) {
                pc.hp = PLAYER_BASE_HP;
                pc.currentMana = pc.baseMana;
            }
        }

        // Reset Fairies
        currentFairyIndex = 0;
        currentFairy = fairies.get(0);
        currentFairyPath = fairyScaledPaths.get(0);
        for (Fairy f : fairies) {
            f.hp = f.maxHp;
        }

        corruptionLevel = 0;
        potionsLeft = maxPotions;
        currentTurn = Turn.PLAYER;

        // Reset any active ability animations
        treeActive = false;
        stoneActive = false;
        wallActive = false;
        punchActive = false;
        waveActive = false;
        cageActive = false;
        shieldActive = false;
        bloodsuckerActive = false;
        potionActive = false;
        fairyAttack = false;

        // Reset air and fire abilities
        airFartActive = false;
        airShieldActive = false;
        airTornadoActive = false;
        airSuperActive = false;
        fireFireballActive = false;
        fireWhipActive = false;
        fireHelldoorActive = false;
        fireBurnTheWichActive = false;
    }

    public void drawTutorial() {
        clear();
        drawImage("resources/tutorial.jpeg", 0, 0);
        setTextDrawingColor(Color.white);
        drawText("Press SPACE to exit tutorial screen", SCREEN_WIDTH / 2 - 120, SCREEN_HEIGHT / 2 - 50, 20);
    }
}