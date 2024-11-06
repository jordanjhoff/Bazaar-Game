package Referee.gui;

import Common.rendering.GameStateRenderer;
import Referee.GameState;
import Referee.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

public class BazaarObserverPanel extends JPanel {

    protected GameStateRenderer renderer;
    protected Observer parentObserver;

    public BazaarObserverPanel(Observer parentObserver) {
        this.parentObserver = parentObserver;
        KeyListener keylistener = new MyKeyListener();
        addKeyListener(keylistener);
        setFocusable(true);
    }

    public void setup(GameStateRenderer renderer) {
        this.renderer = renderer;
    }


    /**
     * Paints the component.
     *
     * @param g The Graphics context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage currentImage = renderer.render(parentObserver.currentGameState());
        updateFrameSize(currentImage);
        g.drawImage(currentImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }



    public void updateFrameSize(BufferedImage currentImage) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.setSize(currentImage.getWidth(), currentImage.getHeight());
        }
    }

    /**
     * Private inner class for handling key events within the a BazaarObserverPanel.
     */
    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (Objects.isNull(renderer)) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                parentObserver.retreatPointer();
                BazaarObserverPanel.this.repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                parentObserver.advancePointer();
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                String filename = JOptionPane.showInputDialog(BazaarObserverPanel.this, "Enter filename to save current state:");
                if (filename != null && !filename.trim().isEmpty()) {
                    try {
                        parentObserver.saveGameStateJson(filename);
                    } catch (IOException ex) {
                        //do nothing
                    }
                }
            }
        }
    }
}
