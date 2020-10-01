import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class FloodIt extends JPanel {
  
  private static final Color BG_COLOR = new Color(0x000000);
  private static final String FONT_NAME = "Ariel";
  private static final int TILE_SIZE = 20;
  
  private int[] gameTiles;
  private Rectangle[] colorButtons;
  private Rectangle[] sizeButtons;
  private Rectangle refreshButton;
  
  boolean gameWin = false;
  boolean gameLose = false;
  int gameSteps = 0; // Current steps
  int gameMaxSteps = 25; // Maximum steps to win or lose 
  int gameSize = 14; // How many rows/columns on the board
  int gameColors = 6; // Number of colors in the game
  int currentColor = 0; 
  int previousColor = 0;
  int buttonX = 90;
  int buttonY = 22 * TILE_SIZE + 15;
  int buttonSize = 30; 
  int stepsY = buttonY + buttonSize + 30;
  int currentSize = 1;
  int startingX = 80;
  int startingY = 100;
  // HighscoreManager scoresS = new HighscoreManager();
  // HighscoreManager scoresM = new HighscoreManager(); 
  // HighscoreManager scoresL = new HighscoreManager();
  
  
  public FloodIt() {
    setPreferredSize(new Dimension(1000, 1000));
    setFocusable(true);
    
    addMouseListener(new MouseAdapter() {
      @Override
      // Checks everytime the mouse is pressed.
        public void mousePressed(MouseEvent e) {
        Point mPoint = e.getPoint(); // Gets the point where the mouse was pressed
        
        // If the Reset button is pressed, the game is reset.
        if (refreshButton.contains(mPoint)) {
          resetGame();
        }
        
        // If the S (small) size button is pressed, the board is changed to the small board.
        if (sizeButtons[0].contains(mPoint) &&
            currentSize != 0) {
          currentSize = 0;
          gameSize = 8;
          gameMaxSteps = 14;
          startingX = 140;
          startingY = 160;
          resetGame();
        }
        
        // If the M (medium) size button is pressed, the board is changed to the medium board.
        if (sizeButtons[1].contains(mPoint) &&
            currentSize != 1) {
          currentSize = 1;
          gameSize = 14;
          gameMaxSteps = 25;
          startingX = 80;
          startingY = 100;
          resetGame();
        }
        
        // If the L (large) size button is pressed, the board is changed to the large board.
        if (sizeButtons[2].contains(mPoint) &&
            currentSize != 2) {
          currentSize = 2;
          gameSize = 22;
          gameMaxSteps = 39;
          startingX = 0;
          startingY = 0;
          resetGame();
        }
        
        // If the game isn't won and the board isn't one color,
        // checks which colored button is pressed, recolors the board with the selected color.
        // Omitting !gameLost allows the player to continue finishing the game even though the game is lost
        if (!gameWin && !sameColor()) { 
          for (int i = 0; i < gameColors; i++) {
            // colorButtons[] is an array of colored buttons starting with yellow, green,
            // red, purple, blue, and pink.
            if (colorButtons[i].contains(mPoint) &&
                currentColor != i + 1) { 
              previousColor = currentColor; // previousColor is what is used to check for adjacent same-colored tiles.
              currentColor = i + 1; // Changes currentColor to the color of the button that was pressed
              checkAndColorAdjacent(0); // Checks if adjacent tiles are the same color and recolors them
              gameSteps += 1; // Increases the game steps
            }
          }
        }
        // After a move is made, checks if the game is won or lost.
        gameResult();
        
        repaint();
      }
    });
    resetGame();
  }
  
  /* Creates the game board */
  public void createBoard() {
    // Randomizes the colors of each tile on the board.
    for (int i = 0; i < gameTiles.length; i++) {
      gameTiles[i] = (int) (Math.random() * gameColors + 1);
    }
    
    // Initializes the colored button rectangles and their positions/sizes.
    for (int i = 0; i < gameColors; i++) {
      colorButtons[i] = new Rectangle(buttonX + 45 * i, buttonY, buttonSize, buttonSize);
    } 
    // Initializes the size/refresh button rectangles and their positions/sizes.
    for (int i = 0; i < 4; i++) {
      if (i < 3) {
        sizeButtons[i] = new Rectangle(buttonY + 15 + 35 * i, 30, buttonSize, buttonSize);
      }
      else {
        refreshButton = new Rectangle(buttonY + 15 + 35 * i, 30, buttonSize, buttonSize);
      }
    } 
    
    currentColor = gameTiles[0]; // Sets the currentColor to the color of the top-leftmost tile. 
  }
  
  /* Creates a new game. */
  public void resetGame() {
    gameSteps = 0;
    gameWin = false;
    gameLose = false;
    gameTiles = new int[gameSize * gameSize];
    colorButtons = new Rectangle[gameColors];
    sizeButtons = new Rectangle[3];
    createBoard();
  }
  
  /* The core algorithm of the game which checks for adjacent same-colored
   * tiles and recolors them. Uses recursion to check all adjacent same-colored tiles. */
  public void checkAndColorAdjacent(int index) {
    gameTiles[0] = currentColor; // Recolors the top-left tile
    
    int right = index + 1; 
    int under = index + gameSize;
    int left  = index - 1;
    int top   = index - gameSize;
    boolean leftFound = false;
    
    // Creates an array of the index numbers for the tiles on the far right of the board.
    // ie. tile 13, 27, 41, etc. for the medium sized board
    int[] leftArray = new int[gameSize];
    for (int i = 0; i < gameSize; i++) {
      leftArray[i] = (gameSize - 1) + gameSize * i;
    }
    
    // Checks the tile to the right. First checks if the tile to the right isn't "off the board"
    // and then if it's also the same as the previous color of the top-left tile, it recolors 
    // and checks the right tile for adjacent, same-colored tiles. 
    if (right % (gameSize) != 0 &&
        right <= gameSize * gameSize &&
        gameTiles[right] == previousColor) {
      gameTiles[right] = currentColor;
      checkAndColorAdjacent(right); // Recursive statement
    }
    // Checks the tile under. First checks if the tile under isn't "off the board"
    // and then if it's also the same as the previous color of the top-left tile, it recolors 
    // and checks the tile under for adjacent, same-colored tiles. 
    if (under < (gameSize * gameSize) && 
        gameTiles[under] == previousColor) {
      gameTiles[under] = currentColor;
      checkAndColorAdjacent(under); // Recursive statement
    }
    // Checks the tile to the left. First checks if the tile to the left isn't "off the board"
    // and then if it's also the same as the previous color of the top-left tile, it recolors 
    // and checks the tile to the left for adjacent, same-colored tiles. 
    if (index != 0 &&
        gameTiles[left] == previousColor) {
      for (int i = 0; i < gameSize; i++) {
        // If the tile to the left of the current tile is a tile on the very right  
        // (ie. Tile 14 is on the very left, so the tile "to the left" is tile 13,
        // which is on the very right of the board), then it is not checked or recolored.
        if (left == leftArray[i]) { 
          leftFound = true; 
        }
      }
      // If the tile to the left is not one of those tiles on the very right, then it is recolored and checked.
      if (!leftFound) { 
        gameTiles[left] = currentColor;
        checkAndColorAdjacent(left); // Recursive statement
      }
    }
    
    // Checks the tile on top. First checks if the tile under isn't "off the board"
    // and then if it's also the same as the previous color of the top-left tile, it recolors 
    // and checks the tile above for adjacent, same-colored tiles. 
    if (top >= 0 &&
        gameTiles[top] == previousColor) {
      gameTiles[top] = currentColor;
      checkAndColorAdjacent(top); // Recursive statement
    }
  }
  
  /* Checks if the game is lost or won */
  public void gameResult() {
    // If the board isn't one color and you have hit the max steps, game is lost.
    if (gameSteps == gameMaxSteps && !sameColor()) {
      gameLose = true;
    }    
    // If the board is one color and you are under or equal to the max steps, game is won.
    if (gameSteps <= gameMaxSteps && sameColor()) {
      gameWin = true;
      
//      // Checks the current size of the board, and adds the steps to the corresponding high score list.
//      switch (currentSize) {
//        case 0: scoresS.addScore(gameSteps);
//        case 1: scoresM.addScore(gameSteps);
//        case 2: scoresL.addScore(gameSteps);
//      }
    }
  }
  
  /* Checks if the entire board is one color */
  public boolean sameColor() {
    int color = gameTiles[0]; // Stores the color of the top-leftmost tile
    // If every other tile is the same color, it returns true, if not it returns false.
    for (int i = 0; i < gameTiles.length; i++) {
      if (gameTiles[i] != color) {
        return false;
      }
    }
    return true;
  }
  
  
  // Overrides the paint method from the Component class
  @Override
  /* Paints the board. */
    public void paint(Graphics g) {
    super.paint(g);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, this.getSize().width, this.getSize().height); // Sets the background color
    int indexX = 0;
    int indexY = 0;
    // Draws the game board
    for (int y = 0; y < gameSize; y++) {
      for (int x = 0; x < gameSize; x++) {
        drawTile(g, gameTiles[x + y * gameSize], x, y);
      }
    }
    // Draws the colored buttons
    for (int i = 0; i < gameColors; i++) {
      g.setColor(getBackground(i + 1));
      g.fillRect(colorButtons[i].x, colorButtons[i].y, colorButtons[i].width, colorButtons[i].height);
      g.setColor(Color.white);
      g.drawRect(colorButtons[i].x, colorButtons[i].y, colorButtons[i].width, colorButtons[i].height);
    }
    
    g.setFont(new Font(FONT_NAME, Font.BOLD, 28));
    String[] string = {"S", "M", "L"};
    
    // Draws the size buttons
    for (int i = 0; i < 3; i++) {
      g.setColor(Color.gray);
      g.fillRect(sizeButtons[i].x, sizeButtons[i].y, sizeButtons[i].width, sizeButtons[i].height);
      g.setColor(Color.white);
      g.drawRect(sizeButtons[i].x, sizeButtons[i].y, sizeButtons[i].width, sizeButtons[i].height);
      // Writes the size button letters
      if (i == 0 || i == 2) {
        g.drawString(string[i], sizeButtons[i].x + 5, sizeButtons[i].y + 25);
      }
      else {
        g.drawString(string[i], sizeButtons[i].x + 3, sizeButtons[i].y + 25);
      }
    }
    // Draws the refresh button image
    Image refresh = new ImageIcon("refresh.png").getImage();
    g.drawImage(refresh, sizeButtons[2].x + 40, sizeButtons[2].y, null);
    
//    // Draws the high score headers and box
//    Rectangle highScore = new Rectangle(460, 150, 150, 200);
//    g.setColor(Color.magenta);
//    g.drawRect(highScore.x, highScore.y, highScore.width, highScore.height);
//    g.setFont(new Font(FONT_NAME, Font.BOLD, 14));
//    g.drawString("SCORE       MOVES", highScore.x + 10, highScore.y + 25);
//    
//    // Prints the high score list
//    String highscorePlace = "";
//    String highscoreScore = "";
//    int max = 5;
//    
//    ArrayList<Integer> scores;
//    scores = scoresM.getScores();
//    String[] place = {"1ST", "2ND", "3RD", "4TH", "5TH"};
//    
//    int i = 0;
//    int x = scores.size();
//    if (x > max) {
//      x = max;
//    }
//    while (i < x) {
//      highscorePlace = place[i];
//      highscoreScore = scores.get(i) + "/" + 25;
//      g.setColor(getBackground(i+1));
//      g.drawString(highscorePlace, highScore.x + 10, highScore.y + 55 + i * 30); // Draws the place
//      g.drawString(highscoreScore, highScore.x + 95, highScore.y + 55 + i * 30); // Draws the score
//      i++;
//    }
  }
  
  /** Draws the tile on the board */
  private void drawTile(Graphics g2, int tile, int x, int y) {
    Graphics2D g = ((Graphics2D) g2);
    int xOffset = x * TILE_SIZE + startingX; // Calculates the X position of the tile
    int yOffset = y * TILE_SIZE + startingY; // Calculates the Y position of the tile
    g.setColor(getBackground(tile)); // Sets the color of the tile
    g.fillRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE); // Draws the tile
    
    // Writes the move numbers on the board
    g.setColor(Color.white);
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
    g.drawString("Moves: " + gameSteps + "/" + gameMaxSteps, buttonX, stepsY);
    
    // Writes the You Lose text if you lose
    if (gameLose) {
      g.setFont(new Font(FONT_NAME, Font.BOLD, 25));
      g.drawString("You Lose!", buttonX + 150, stepsY);
    }
    
    // Writes the You Win text if you win
    if (gameWin) {
      g.setFont(new Font(FONT_NAME, Font.BOLD, 25));
      g.drawString("You Win!", buttonX + 150, stepsY);
    }
    
    // Writes the instructions
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
    g.drawString("How to play: Click colored buttons to fill the board with a single color.", 15, stepsY + 40);
    
  }
  
  /* Determines the color of the tile */
  public Color getBackground(int value) {
    switch (value) {
      case 1:    return new Color(0xf6e715); // Yellow
      case 2:    return new Color(0x67e94f); // Green
      case 3:    return new Color(0xe1230a); // Red
      case 4:    return new Color(0xb301c3); // Purple
      case 5:    return new Color(0x0d7fe3); // Blue
      case 6:    return new Color(0xff758d); // Pink
    }
    return new Color(0xcdc1b4);
  }
  
  
  /* Main Method */
  public static void main(String[] args) {
    JFrame game = new JFrame();
    game.setTitle("Flood It");
    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    game.setSize(640, 600);
    game.setResizable(false);
    
    game.add(new FloodIt());
    
    game.setLocationRelativeTo(null);
    game.setVisible(true);
  }
}