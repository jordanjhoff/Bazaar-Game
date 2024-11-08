package Referee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Common.converters.JSONSerializer;
import Referee.gui.MockBazaarObserverPanel;

public class MockObserver extends Observer {

  public MockObserver() {
    super(new ArrayList<>(), new MockBazaarObserverPanel(), false);
    mainPanel.setParent(this);
  }

  public void notifyOfGameStateUpdate(GameState gs) {
    if (!shutDown) {
      updateGameState(gs);
    }
  }

  public void openFrame() {
    // don't do any frame stuff. we don't care.

    mainPanel.requestFocusInWindow();
    mainPanel.setVisible(true);
    mainPanel.repaint();
  }

  public GameState currentGameState() {
    return gameStateHistory.get(gameStatePointer);
  }

  public void shutDown() {
    System.out.print("shutdown::");
    super.shutDown();
  }

  /**
   * Requires renderer & panel to be instantiated. todo make safe
   */
  protected void updateGameState(GameState gs) {
    System.out.print("update::");
    super.updateGameState(gs);
  }

  /**
   * Requires renderer to be instantiated. todo make safe
   */
  public void saveGameStateImage(GameState gameState, String fileName){
    enforceSetup();
    File directory = new File("Tmp");
    // always record this attempt for consistency
    System.out.printf("createDir_Tmp::");
    if (!directory.exists()) {
      // do nothing
    }
    System.out.printf("createFile_%s::", fileName);
    // dump the rendered image
    System.out.printf("renderer.render::");
    renderer.render(gameState);
  }


  public void moveCurrentGameStateForward() {
    System.out.print("advance::");
    super.moveCurrentGameStateForward();
  }

  public void moveCurrentGameStateBackwards() {
    System.out.print("retreat::");
    super.moveCurrentGameStateBackwards();
  }

  public void checkPointer() {
    System.out.printf("pointer_%d::", gameStatePointer);
  }

  public void saveGameStateJson(String fileName) throws IOException {
    enforceSetup();
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    System.out.printf("fileName_%s::", fileName);
    System.out.print(JSONSerializer.gameStateToJson(currentGameState()));
    writer.close();
  }
}
