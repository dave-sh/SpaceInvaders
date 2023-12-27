//Abstract class for "scripts"
//Done for you!
public abstract class ScriptableBehavior {
    //All scripts will have some shared attributes and functions

    GameObject gameObject;//the object a script is attached to; allows modification of its data by the script

    ScriptableBehavior(GameObject g){
        gameObject=g;
    }

    //Start(), sets up anything required by the script;
    public abstract void Start();

    //Update(), performs actions each frame
    public abstract void Update();
}
