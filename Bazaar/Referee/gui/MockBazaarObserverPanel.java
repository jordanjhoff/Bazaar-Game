package Referee.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import Referee.Observer;

public class MockBazaarObserverPanel extends BazaarObserverPanel {
   public MockBazaarObserverPanel(Observer parentViewObserver) {
      super(parentViewObserver);
   }

   public MockBazaarObserverPanel() {
      this(null);
   }

   @Override
   protected void update() {
      System.out.print("update::");
      super.update();
   }

   void mockInputs(List<KeyEvent> actions) {
      KeyAdapter k = new MyKeyListener();
      for (KeyEvent e : actions) {
         try {
            Thread.sleep(50);
         } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
         }
         System.out.printf("pressed_%s::",e.getKeyChar());
         k.keyPressed(e);
      }
   }
}