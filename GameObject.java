import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GameObject {
    public AffineTransform transform; //the location/scale/rotation of our object
    public Shape shape; //the collider/rendered shape of this object
    public Material material; //data about the fill color, border color, and border thickness
    public ArrayList<ScriptableBehavior> scripts = new ArrayList<>(); //all scripts attached to the object
    public boolean active = true; //whether this gets Updated() and Draw()n

    //TODO: create the default GameObject use a default AffineTransform, default Material, and a 10x10 pix rectangle Shape at 0,0
    public GameObject(){
        transform = new AffineTransform();
        material = new Material();
        shape = new Rectangle(0, 0, 10, 10);
    }

    //TODO: create the default GameObject, but with its AffineTransform translated to the coordinate x,y
    public GameObject(int x, int y){
        this();
        transform.translate(x, y);
    }

    //TODO: 1) save the pen's old transform, 2) transform it based on this object's transform, 3) draw either the styled shape, or the image scaled to the bounds of the shape.
    public void Draw(Graphics2D pen){
        if(!active){
            return;
        }

        AffineTransform currTransform = pen.getTransform();
        pen.setTransform(transform);
        pen.setColor(material.fill);

        if(material.isShape){
            pen.fill(shape);
            pen.setColor(material.border);
            pen.setStroke(new BasicStroke(material.borderWidth));
            pen.draw(shape);
        }else if(material.img != null){
            int width = shape.getBounds().width;
            int height = shape.getBounds().height;
            pen.drawImage(material.img, 0, 0, width, height, null);
        }

        pen.setTransform(currTransform);
    }

    //TODO: start all scripts on the object
    public void Start(){
        if(!active){
            return;
        }

        for(ScriptableBehavior behavior : scripts){
            behavior.Start();
        }
    }

    //TODO: update all scripts on the object
    public void Update(){
        if(!active){
            return;
        }

        for(ScriptableBehavior behavior : scripts){
            behavior.Update();
        }
    }

    //TODO: move the GameObject's transform
    public void Translate(float dX, float dY){
        transform.translate(dX, dY);
    }

    //TODO: scale the GameObject's transform around the CENTER of its shape
    public void Scale(float sX, float sY){
        Rectangle bounds = shape.getBounds();
        float centerX = bounds.x + bounds.width / 2.0f;
        float centerY = bounds.y + bounds.height / 2.0f;

        transform.translate(centerX, centerY);
        transform.scale(sX, sY);
        transform.translate(-centerX, -centerY);
    }

    //TODO: should return true if the two objects are touching (i.e., the intersection of their areas is not empty)
    public boolean CollidesWith(GameObject other){
        Area area1 = new Area(this.shape);
        area1.transform(this.transform);

        Area area2 = new Area(other.shape);
        area2.transform(other.transform);

        area1.intersect(area2);
        return !area1.isEmpty();
    }

    //TODO: should return true of the shape on screen contains the point
    public boolean Contains(Point2D point){
        return shape.contains(point);
    }

}
