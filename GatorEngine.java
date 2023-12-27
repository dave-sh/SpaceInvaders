import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GatorEngine {
    //UI Components (things that are "more" related to the UI)
    static JFrame WINDOW;
    static JPanel DISPLAY_CONTAINER;
    static JLabel DISPLAY_LABEL;
    static BufferedImage DISPLAY;
    static int WIDTH=500, HEIGHT=500;

    //Engine Components (things that are "more" related to the engine structures)
    static Graphics2D RENDERER;
    static ArrayList<GameObject> OBJECTLIST = new ArrayList<>(); //list of GameObjects in the scene
    static ArrayList<GameObject> CREATELIST = new ArrayList<>(); //list of GameObjects to add to OBJECTLIST at the end of the frame
    static ArrayList<GameObject> DELETELIST = new ArrayList<>(); //list of GameObjects to remove from OBJECTLIST at the end fo the frame
    static float FRAMERATE = 144; //target frames per second;
    static float FRAMEDELAY = 1000/FRAMERATE; //target delay between frames
    static Timer FRAMETIMER; //Timer controlling the update loop
    static Thread FRAMETHREAD; //the Thread implementing the update loop
    static Thread ACTIVE_FRAMETHREAD; //a copy of FRAMETHREAD that actually runs.

    // game variables
    //static boolean started = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CreateEngineWindow();
            }
        });
    }

    static void CreateEngineWindow(){
        //Sets up the GUI
        WINDOW = new JFrame("Gator Engine");
        WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WINDOW.setVisible(true);

        DISPLAY = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
        RENDERER = (Graphics2D) DISPLAY.getGraphics();
        DISPLAY_CONTAINER = new JPanel();
        DISPLAY_CONTAINER.setFocusable(true);
        DISPLAY_LABEL = new JLabel(new ImageIcon(DISPLAY));
        DISPLAY_CONTAINER.add(DISPLAY_LABEL);
        WINDOW.add(DISPLAY_CONTAINER);
        WINDOW.pack();

        //TODO: make this 1)execute Update(), 2) clear any inputs that need to be removed between frames, and 3) repaint the GUI back on the EDT.
        FRAMETHREAD = new Thread(new Runnable() {
            @Override
            public void run() {
                Update();
                Input.UpdateInputs();
                UpdateObjectList();
                WINDOW.repaint();
            }
        });

        //This copies the template thread made above
        ACTIVE_FRAMETHREAD = new Thread(FRAMETHREAD);

        //TODO: create a timer that will create/run ACTIVE_FRAMETHREAD, but only if it it hasn't started/has ended
        FRAMETIMER = new Timer((int)FRAMEDELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if the current active_framethread is alive or dead
                if(!ACTIVE_FRAMETHREAD.isAlive()){
                    ACTIVE_FRAMETHREAD = new Thread(FRAMETHREAD);
                    ACTIVE_FRAMETHREAD.start();
                }
            }
        });
        FRAMETIMER.start();

        Start();

        //===================INPUT=========================
        //Set up some action listeners for input on the PANEL
        //These should update the Input classes ArrayLists and other members
        //TODO: use the correct listener functions to modify INPUT
        DISPLAY_CONTAINER.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                Character key = e.getKeyChar();
                Input.pressed.add(key);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                Character key = e.getKeyChar();
                Input.held.add(key);
                //System.out.println(key);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Character key = e.getKeyChar();
                Input.released.add(key);
                //System.out.println(key);
            }
        });
        DISPLAY_CONTAINER.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Input.MouseClicked = true;
                //System.out.println(Input.MouseClicked);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Input.MousePressed = true;
                //System.out.println(Input.MousePressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Input.MousePressed = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Input.MouseX = x;
                Input.MouseY = y;
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        DISPLAY_CONTAINER.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Input.MouseX = x;
                Input.MouseY = y;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Input.MouseX = x;
                Input.MouseY = y;

                //System.out.println(x + " " + y);
            }
        });
    }

    //TODO: add the GameObject to the OBJECTLIST
    static void Create(GameObject g){
        CREATELIST.add(g);
    }

    //TODO: remove the GameObject from the OBJECTLIST
    static void Delete(GameObject g){
        DELETELIST.add(g);
    }

    //TODO: 1) remove objects in DELETELIST from OBJECTLIST, 2) add objects in CREATELIST to OBJECTLIST, 3) remove all items from DELETELIST and CREATELIST
    static void UpdateObjectList(){
        OBJECTLIST.removeAll(DELETELIST);
        OBJECTLIST.addAll(CREATELIST);

        DELETELIST.clear();
        CREATELIST.clear();
    }

    //This begins the "user-side" of the software; above should set up the engine loop, data, etc.
    //Here you can create GameObjects, assign scripts, set parameters, etc.
    //NOTE: This is where we should be able to insert out own code and scripts
    static void Start(){
        //Tests.TestFour();//create some example objects, see the function in Tests.java
        GatorInvaders.StartGame();

        //TODO: Start() all objects in OBJECTLIST
        for(GameObject g : OBJECTLIST){
            g.Start();
        }
    }

    //TODO: Redraw the Background(), then Draw() and Update() all GameObjects in OBJECTLIST
    static void Update(){
        GatorInvaders.Update();

        Background();

        for(GameObject g: OBJECTLIST){
            g.Draw(RENDERER);
            g.Update();
        }
    }

    //draws a background on the Renderer. right now it is solid, but we could load an image
    //done for you!
    static void Background() {
        RENDERER.setColor(Color.WHITE);
        RENDERER.fillRect(0, 0, WIDTH, HEIGHT);
    }


}
