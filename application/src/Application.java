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

public class Application implements GameLoop {

    ArrayList<Player_characters> characters = readingCSVFile();
    ArrayList<Fairy> fairies = new ArrayList<>();

    enum turn {
        Player,
        Fairy
    }

    turn currentTurn = turn.Player;


    BufferedImage fairyScaled;
    BufferedImage playerScaled;
    BufferedImage playButtonScaled;
    BufferedImage playButtonHoverScaled;
    BufferedImage tutorialButtonScaled;
    BufferedImage tutorialButtonHoverScaled;


    String fairyScaledPath = "resources/fairy_scaled.png";
    String playerScaledPath = "resources/player_scaled.png";
    String playButtonScaledPath = "resources/playButton_scaled.png";
    String playButtonHoverScaledPath = "resources/playButton_hover_scaled.png";
    String tutorialButtonScaledPath = "resources/tutorialButton_scaled.png";
    String tutorialButtonHoverScaledPath = "resources/tutorialButton_hover_scaled.png";

    boolean hoveringPlayButton = false;
    boolean hoveringTutorialButton = false;

    boolean playerTurn = true;
    boolean actionPending = false;


    int playButtonHoverWidth = 0;
    int playButtonHoverHeight = 0;


    int playButtonX = 450;
    int playButtonY = 550;
    int playButtonWidth = 0;
    int playButtonHeight = 0;


    int tutorialButtonHoverWidth = 0;
    int tutorialButtonHoverHeight = 0;

    int tutorialButtonX = 810;
    int tutorialButtonY = 440;
    int tutorialButtonWidth = 0;
    int tutorialButtonHeight = 0;

    boolean inIntro = true;
    boolean corruptionDecayedThisTurn = false;

    int selectedCharacterIndex = 0;
    int currentFairyIndex = 0;

    Fairy currentFairy;
    int enemyHp = 100;
    int corruptionLevel = 0;

    double fairyFloatOffset = 0;
    double floatAmplitude = 10;
    double floatSpeed = 0.05;
    double time = 0;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SaxionApp.startGameLoop(new Application(), 1536, 1024, 16);
    }


    @Override
    public void init() {
        loadSprites();

        fairies.add(new Fairy(10, 20, 100));
        fairies.add(new Fairy(10, 20, 120));
        fairies.add(new Fairy(10, 20, 150));

        currentFairy = fairies.get(currentFairyIndex);
    }

    @Override
    public void loop() {

        if (inIntro) {
            drawIntro();
            return;
        }
        if (currentTurn == turn.Fairy) {
            fairyTurn();
            currentTurn = turn.Player;
        }

        if (!corruptionDecayedThisTurn) {
            decayCorruption();
            corruptionDecayedThisTurn = true;
        }

        drawGame();
        
    }

    @Override
    public void keyboardEvent(KeyboardEvent e) {
        if (!inIntro && e.getKeyCode() == KeyboardEvent.VK_Q) {
            if (currentTurn == turn.Player) {
                attackEnemy();
                currentTurn = turn.Fairy;
            }
        }
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        //when the mouse is exactly at the playbutton X, Y, width and height,
        //the image will shrink and do it's function
        hoveringPlayButton = (mx >= playButtonX && mx <= playButtonX + playButtonWidth &&
                my >= playButtonY && my <= playButtonY + playButtonHeight);

        hoveringTutorialButton = (mx >= tutorialButtonX && mx <= tutorialButtonY + tutorialButtonWidth
                && my >= tutorialButtonY && my <= tutorialButtonY + tutorialButtonHeight);


        if (inIntro && e.isMouseUp() && e.isLeftMouseButton()) {
            if (hoveringPlayButton || hoveringTutorialButton) {
                inIntro = false;
                SaxionApp.printLine("Play button clicked at: " + mx + "," + my);
            }
        }
    }


    private void drawIntro() {
        SaxionApp.clear();
        SaxionApp.drawImage("resources/fairyButchersFrontPage.png", 0, 0);
        introHud();
    }

    private void drawGame() {
        SaxionApp.clear();
        SaxionApp.drawImage("resources/backgroundPictureBattleground.png", 0, 0);

        SaxionApp.drawImage(fairyScaledPath, 970, 700);
        SaxionApp.drawImage(playerScaledPath, 200, 670);

        inGameHud();
    }

    public ArrayList<Player_characters> readingCSVFile() {
        CsvReader reader = new CsvReader("application/src/character.csv");
        reader.skipRow();
        reader.setSeparator(',');

        ArrayList<Player_characters> characters = new ArrayList<>();

        while (reader.loadRow()) {

            Player_characters character = new Player_characters();
            //String name;
            //String png;
            //int atk;
            //int hp;
            //int baseMana;
            //Ability characterAbility;
            Player_characters.name = reader.getString(0);
            Player_characters.png = reader.getString(1);
            Player_characters.atk = reader.getInt(2);
            Player_characters.hp = reader.getInt(3);
            Player_characters.baseMana = reader.getInt(4);
            //character ability needs to be added
            characters.add(character);
        }
        return characters;
    }


    public void loadSprites() {
        fairyScaled = loadSprite("resources/FairyNo2.png", fairyScaledPath, 8);
        playerScaled = loadSprite("resources/quake E.png", playerScaledPath, 10);
        playButtonScaled = loadSprite("resources/playButton.png", playButtonScaledPath, 8);
        playButtonHoverScaled = loadSprite("resources/playButton.png", playButtonHoverScaledPath, 6);
        tutorialButtonScaled = loadSprite("resources/tutorialButton.png", tutorialButtonScaledPath, 8);
        tutorialButtonHoverScaled = loadSprite("resources/tutorialButton.png", tutorialButtonHoverScaledPath, 6);

        if (playButtonScaled != null) {
            playButtonWidth = playButtonScaled.getWidth();
            playButtonHeight = playButtonScaled.getHeight();
        } else {
            playButtonWidth = 200;
            playButtonHeight = 80;
        }

        if (playButtonHoverScaled != null) {
            playButtonHoverWidth = playButtonHoverScaled.getWidth();
            playButtonHoverHeight = playButtonHoverScaled.getHeight();
        } else {
            playButtonHoverWidth = playButtonWidth;
            playButtonHoverHeight = playButtonHeight;
        }

        if (tutorialButtonScaled != null) {
            tutorialButtonWidth = tutorialButtonScaled.getWidth();
            tutorialButtonHeight = tutorialButtonScaled.getHeight();
        } else {
            tutorialButtonWidth = 200;
            tutorialButtonHeight = 80;
        }

        if (tutorialButtonHoverScaled != null) {
            tutorialButtonHoverWidth = tutorialButtonHoverScaled.getWidth();
            tutorialButtonHoverHeight = tutorialButtonHoverScaled.getHeight();
        } else {
            tutorialButtonHoverWidth = playButtonWidth;
            tutorialButtonHoverHeight = playButtonHeight;
        }
    }

    public BufferedImage loadSprite(String inputPath, String outputPath, int scale) {
        try {
            BufferedImage original = ImageIO.read(new File(inputPath));
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
            SaxionApp.printLine("Failed to load/scale sprite: " + inputPath);
            ex.printStackTrace();
            return null;
        }
    }


    public void inGameHud() {
        SaxionApp.setFill(Color.lightGray);
        SaxionApp.drawRectangle(10, 10, 500, 10);
        SaxionApp.drawRectangle(10, 30, 250, 10);
        SaxionApp.drawRectangle(1470, 110, 50, 750);
        SaxionApp.drawRectangle(950, 680, 300, 10);
        SaxionApp.drawRectangle(10, 110, 60, 60);
        SaxionApp.drawRectangle(10, 180, 60, 60);
        SaxionApp.drawRectangle(10, 250, 60, 60);
        SaxionApp.drawRectangle(10, 320, 60, 60);

        Player_characters player = characters.get(selectedCharacterIndex);
        fillHealth(player.hp);
        fillMana(75);
        fillCorruption(corruptionLevel);
        fillEnemy(enemyHp);
    }

    public void fillHealth(int healthPoints) {
        SaxionApp.setFill(Color.red);
        int filler = (int) ((healthPoints / 100.0) * 500);
        SaxionApp.drawRectangle(10, 10, filler, 10);
    }

    public void fillMana(int manaPoints) {
        SaxionApp.setFill(Color.blue);
        int filler = (int) ((manaPoints / 100.0) * 250);
        SaxionApp.drawRectangle(10, 30, filler, 10);
    }

    public void fillCorruption(int corruptionLevel) {
        SaxionApp.setFill(Color.magenta);

        // Visual bar capped at 100
        int visibleLevel = Math.min(corruptionLevel, 100);
        int filler = (int) ((visibleLevel / 100.0) * 750);

        if (filler < 1 && corruptionLevel > 0) filler = 1; // ensure visibility
        SaxionApp.drawRectangle(1470, 110, 50, filler);
    }

    public void fillEnemy(int hp) {
        SaxionApp.setFill(Color.red);
        int filler = (int) ((hp / 100.0) * 300);
        SaxionApp.drawRectangle(950, 680, filler, 10);
    }

    public void introHud() {

        if (hoveringPlayButton && playButtonHoverScaled != null) {
            int hoverX = playButtonX - (playButtonHoverWidth - playButtonWidth) / 2;
            int hoverY = playButtonY - (playButtonHoverHeight - playButtonHeight) / 2;
            SaxionApp.drawImage(playButtonHoverScaledPath, hoverX, hoverY);
        } else {
            SaxionApp.drawImage(playButtonScaledPath, playButtonX, playButtonY);
        }

        if (hoveringTutorialButton && tutorialButtonHoverScaled != null) {
            int tutorialHoverX = tutorialButtonX - (tutorialButtonHoverWidth - tutorialButtonWidth) / 2;
            int tutorialHoverY = tutorialButtonY - (tutorialButtonHoverHeight - tutorialButtonHeight) / 2;
            SaxionApp.drawImage(tutorialButtonHoverScaledPath, tutorialHoverX, tutorialHoverY);

        } else {
            SaxionApp.drawImage(tutorialButtonScaledPath, tutorialButtonX, tutorialButtonY);
        }


    }

    public void attackEnemy() {
        if (characters.isEmpty()) return; //safety check

        //Get the selected character
        Player_characters character = characters.get(selectedCharacterIndex);

        //Deal damage
        enemyHp -= character.atk;

        //Hp doesn't go below 0
        if (enemyHp < 0) enemyHp = 0;
    }

    private void nextFairy() {
        currentFairyIndex++;

        if (currentFairyIndex >= fairies.size())
            currentFairyIndex = 0;

        currentFairy = fairies.get(currentFairyIndex);

        if (currentFairyIndex > 0) {
            currentFairy.increaseScaling();
        }
    }


    public void fairyTurn() {
        if (currentFairy == null || !currentFairy.isAlive()) return;

        Player_characters player = characters.get(selectedCharacterIndex);

        boolean shouldHeal = currentFairy.hp < currentFairy.maxHp && Math.random() < 0.3;

        if(shouldHeal)
        {
            int healAmount = (int)(currentFairy.hp * 0.25);
            currentFairy.hp += healAmount;
            if(currentFairy.hp > currentFairy.maxHp) currentFairy.hp = currentFairy.maxHp;
            nextFairy();
            return;
        }

        int damage;
        boolean criticalHit = false;
        double playerHealthRatio = player.hp / 100.0;
        double corruptionRatio = corruptionLevel / 100.0;
        double fairyScaling = currentFairy.scaling;
        double airChance = 0.4;

        airChance += 0.4 * (1-playerHealthRatio);
        airChance += 0.3 * corruptionRatio;

        airChance += 0.2 * (fairyScaling - 1);

        airChance = Math.min(Math.max(airChance, 0.1), 0.9);

        if (Math.random() < airChance) {
            damage = currentFairy.getAirDmg();
        } else {
            damage = currentFairy.getGroundDmg();
        }

        double critChance = 0.1 + 0.4 * corruptionRatio;
        if(Math.random() < critChance){
            damage *= 2;
            criticalHit = true;
        }

        player.hp -= damage;
        if (player.hp < 0) player.hp = 0;

        increaseCorruptionDamage(damage);

        nextFairy();
    }

    public void increaseCorruptionDamage(int damage) {
        double randomPercent = 0.10 + Math.random() * 0.05; // 10-15% of damage
        int corruptionIncrease = (int) (randomPercent * damage);

        corruptionLevel += corruptionIncrease;
        if (corruptionLevel > 120) corruptionLevel = 120; // allow temporary overflow
    }

    public void decayCorruption() {
        // Base decay
        double baseDecay = 20 + Math.random() * 5; // 5-10 points

        // Extra decay if corruption > 100
        double extraDecay = 0;
        if (corruptionLevel > 100) {
            extraDecay = (corruptionLevel - 100) * 0.5; // half of overflow
        }

        int totalDecay = (int) (baseDecay + extraDecay);
        if (totalDecay < 1) totalDecay = 1;

        corruptionLevel -= totalDecay;
        if (corruptionLevel < 0) corruptionLevel = 0;
    }

}
