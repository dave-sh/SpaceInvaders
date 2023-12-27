//An example script; this one will look for character input, and translate whatever game object it is on
//Others may take additional arguments in their constructor or contain their own fields (i.e., storing an arbitrary distance to move the object).
public class MovementScript  extends ScriptableBehavior {
    MovementScript(GameObject g) {
        super(g);
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update(){
        if(Input.GetKeyDown('a'))
            gameObject.Translate(-2,0);
        if(Input.GetKeyDown('d'))
            gameObject.Translate(2,0);
        if(Input.GetKeyDown('w'))
            gameObject.Translate(0, -2);
        if(Input.GetKeyDown('s'))
            gameObject.Translate(0,2);

    }
}
