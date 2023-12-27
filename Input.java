import java.util.ArrayList;

public class Input {
    static public ArrayList<Character> pressed = new ArrayList<>(); //contains characters that were just pressed this frame
    static public ArrayList<Character> held = new ArrayList<>(); //contains characters that were either just pressed this frame, or have been down for any number of previous frames
    static public ArrayList<Character> released = new ArrayList<>(); //contains characters that were just released this frame

    public static int MouseX; //the current mouse X position on the DISPLAY BufferedImage
    public static int MouseY; //the current mouse Y position on the DISPLAY BufferedImage
    public static boolean MousePressed; //true if the mouse is currently down
    public static boolean MouseClicked; //true if the mouse was clicked this frame

    //TODO: this function should happen every frame, and should clear any values in the Input class that need to be removed
    //i.e., if a key was pressed on one frame, on the next it should be removed from the pressed list
    static void UpdateInputs(){
        pressed.removeAll(released);
        held.removeAll(released);
        released.clear();
        MouseClicked = false;
        MousePressed = false;
    }

    //TODO: return true if c is in the pressed list
    static boolean GetKeyPressed(char c){
        if(pressed.contains(c)){
            return true;
        }
        return false;
    }

    //TODO: return true if c is in the held list
    static boolean GetKeyDown(char c){
        if(held.contains(c)){
            return true;
        }
        return false;
    }

    //TODO: return true if c is in the released list
    static boolean GetKeyUp(char c){
        if(released.contains(c)){
            return true;
        }
        return false;
    }
}
