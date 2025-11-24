import nl.saxion.app.SaxionApp;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Application implements Runnable {

    BufferedImage fairyScaled;
    BufferedImage playerScaled;
    String fairyScaledPath = "resources/fairy_scaled.png";
    String playerScaledPath = "resources/player_scaled.png";

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SaxionApp.start(new Application(), 1536, 1024);
    }

    public void run() {
        loadSprites();
        intro();
        enterGame();
    }

    public void intro() {
        SaxionApp.drawImage("resources/fairyButchersFrontPage.png", 0, 0);
        SaxionApp.pause();
    }

    public void enterGame() {
        SaxionApp.clear();
        SaxionApp.drawImage("resources/backgroundPictureBattleground.png", 0, 0);

        // Draw scaled fairy sprite
        SaxionApp.drawImage(fairyScaledPath, 700, 700);
        SaxionApp.drawImage(playerScaledPath, 200, 700);
    }


    public void loadSprites() {
        // Keep your existing hard-coded call but use the generalized function
        fairyScaled = loadSprite("resources/FairyNo2.png", fairyScaledPath, 8);
        playerScaled = loadSprite("resources/quake E.png", playerScaledPath, 10);
    }

    // Generalized sprite loader function
    public BufferedImage loadSprite(String inputPath, String outputPath, int scale) {
        try {
            BufferedImage original = ImageIO.read(new File(inputPath));

            int w = original.getWidth() * scale;
            int h = original.getHeight() * scale;

            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(original, 0, 0, w, h, null);
            g2.dispose();

            // Save scaled version if output path is provided
            if (outputPath != null && !outputPath.isEmpty()) {
                ImageIO.write(scaled, "png", new File(outputPath));
            }

            return scaled;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
