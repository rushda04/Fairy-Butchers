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

    BufferedImage fairyScaled;
    BufferedImage playerScaled;
    BufferedImage playButtonScaled;
    BufferedImage playButtonHoverScaled;



    String fairyScaledPath = "resources/fairy_scaled.png";
    String playerScaledPath = "resources/player_scaled.png";
    String playButtonScaledPath = "resources/playButton_scaled.png";
    String playButtonHoverScaledPath = "resources/playButton_hover_scaled.png";

    boolean hoveringPlayButton = false;




    int playButtonHoverWidth = 0;
    int playButtonHoverHeight = 0;


    int playButtonX = 450;
    int playButtonY = 550;
    int playButtonWidth = 0;
    int playButtonHeight = 0;

    boolean inIntro = true;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SaxionApp.startGameLoop(new Application(), 1536, 1024, 16);
    }


    @Override
    public void init() {
        loadSprites();
    }

    @Override
    public void loop() {
        if (inIntro) {
            drawIntro();
        } else {
            drawGame();
        }
    }

    @Override
    public void keyboardEvent(KeyboardEvent e) {
        if (inIntro) {
            inIntro = false;
            SaxionApp.printLine("Started by keyboard event: " + e.getKeyCode());
        }
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        hoveringPlayButton = (mx >= playButtonX && mx <= playButtonX + playButtonWidth &&
                my >= playButtonY && my <= playButtonY + playButtonHeight);

        if (inIntro && e.isMouseUp() && e.isLeftMouseButton()) {
            if (hoveringPlayButton) {
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

    public ArrayList<Player_characters> readingCSVFile(){
        CsvReader reader = new CsvReader("application/src/character.csv");
        reader.skipRow();
        reader.setSeparator(',');

        ArrayList<Player_characters> characters = new ArrayList<>();

        while(reader.loadRow()) {

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

        fillHealth(75);
        fillMana(75);
        fillCorruption(25);
        fillEnemy(35);
    }

    public void fillHealth(int healthPoints) {
        SaxionApp.setFill(Color.red);
        int filler = (int)((healthPoints / 100.0) * 500);
        SaxionApp.drawRectangle(10, 10, filler, 10);
    }

    public void fillMana(int manaPoints) {
        SaxionApp.setFill(Color.blue);
        int filler = (int)((manaPoints / 100.0) * 250);
        SaxionApp.drawRectangle(10, 30, filler, 10);
    }

    public void fillCorruption(int corruptionLevel) {
        SaxionApp.setFill(Color.magenta);
        int filler = (int)((corruptionLevel / 100.0) * 750);
        SaxionApp.drawRectangle(1470, 110, 50, filler);
    }

    public void fillEnemy(int hp) {
        SaxionApp.setFill(Color.red);
        int filler = (int)((hp / 100.0) * 300);
        SaxionApp.drawRectangle(950, 680, filler, 10);
    }

    public void introHud() {
        if (hoveringPlayButton && playButtonHoverScaled != null) {
            int hx = playButtonX - (playButtonHoverWidth - playButtonWidth) / 2;
            int hy = playButtonY - (playButtonHoverHeight - playButtonHeight) / 2;
            SaxionApp.drawImage(playButtonHoverScaledPath, hx, hy);
        } else {
            SaxionApp.drawImage(playButtonScaledPath, playButtonX, playButtonY);
        }
    }


}
