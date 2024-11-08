package Referee.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.swing.*;

import Common.rendering.GameStateRenderer;
import Referee.Observer;


/**
 * A panel class for viewing a BazaarObserver GUI
 */
public class BazaarObserverPanel extends JPanel {

    protected GameStateRenderer renderer;
    protected Observer parentViewObserver;

    /**
     * If you use this constructor, you must manually set the parent later
     * with setParent.
     */
    public BazaarObserverPanel() {
        this(null);
    }

    public BazaarObserverPanel(Observer parentViewObserver) {
        KeyListener keylistener = new MyKeyListener();
        addKeyListener(keylistener);
        setFocusable(true);
        setParent(parentViewObserver);
    }

    public void setParent(Observer parentViewObserver) {
        this.parentViewObserver = parentViewObserver;
    }

    /**
     * Sets up the renderer for this panel
     * @param renderer
     */
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
        BufferedImage currentImage = renderer.render(parentViewObserver.currentGameState());
        updateFrameSize(currentImage);
        g.drawImage(currentImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    /**
     * Makes a call to update this component. May be overridden for testing purposes.
     */
    protected void update() {
        this.repaint();
    }

    /**
     * Dynamically updates the framesize of the parent frame
     * @param currentImage
     */
    public void updateFrameSize(BufferedImage currentImage) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.setSize(currentImage.getWidth(), currentImage.getHeight());
        }
    }

    /**
     * Private inner class for handling key events within a BazaarObserverPanel.
     */
    protected class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (Objects.isNull(renderer)) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                parentViewObserver.moveCurrentGameStateBackwards();
                update();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                parentViewObserver.moveCurrentGameStateForward();
                update();
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                String filename = JOptionPane.showInputDialog(BazaarObserverPanel.this, "Enter filename to save current state:");
                if (filename != null && !filename.trim().isEmpty()) {
                    try {
                        parentViewObserver.saveGameStateJson(filename);
                    } catch (IOException ex) {
                        //do nothing
                    }
                }
            }
        }
    }
}
