/*
 * Copyright (c) 2025.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tank1990.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @class TexturedFontPanel
 * @brief A JPanel that renders text with a texture applied to it.
 * @details This panel can display text with a texture background, supporting both plain text and HTML formatted text.
 */
public class TexturedFontPanel extends JPanel {
    private TexturePaint texturePaint;
    private String text = "Textured Text!";
    private boolean isHtml = false;
    private Font font = new Font("Arial", Font.BOLD, 80);

    public TexturedFontPanel(ImageIcon textureIcon) {
        if (textureIcon != null) {
            BufferedImage textureImage = toBufferedImage(textureIcon.getImage());
            Rectangle rect = new Rectangle(0, 0, textureImage.getWidth(), textureImage.getHeight());
            texturePaint = new TexturePaint(textureImage, rect);
        }
    }

    public void setText(String newText) {
        this.text = newText;
        this.isHtml = newText != null && newText.trim().toLowerCase().startsWith("<html>");
        repaint();
    }

    public void setFont(Font font) {
        this.font = font;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (texturePaint == null || text == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(texturePaint);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (!isHtml) {
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() / 2 + fm.getAscent() / 2;
            g2d.drawString(text, x, y);
        } else {
            // Render HTML using JTextPane painted manually
            JTextPane pane = new JTextPane();
            pane.setContentType("text/html");
            pane.setText(text);
            pane.setFont(font);
            pane.setOpaque(false);
            pane.setSize(getWidth(), getHeight());

            // Create a temporary image to render into
            BufferedImage htmlBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gHtml = htmlBuffer.createGraphics();
            gHtml.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            pane.paint(gHtml);
            gHtml.dispose();

            // Use texture paint to fill the HTML text area
            g2d.setPaint(texturePaint);
            g2d.setComposite(AlphaComposite.SrcAtop); // Text only
            g2d.drawImage(htmlBuffer, 0, 0, null);
        }
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;

        BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
}
