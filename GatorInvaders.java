import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;
import java.util.*;


// TODO
// Add in Delay between enemy generation
// Set Player Life Count back to default at end of each level [ Should be working ]

public class GatorInvaders {
    // Player, movement, A and D
    // Player will fire objects
    // Player will have three lives, use a counter, maybe an array of images
    // Player needs a set starting position
    // if bullet collides with player, or enemy reaches y location, then lose a life
    static int currentLevel;
    static GameObject player;
    static boolean immunity = false;
    static long immunityStartTime;
    static ArrayList<GameObject> lives = new ArrayList<>();
    static int livesCounter = 3;
    static long playerTime;

    // enemies
    static ArrayList<Enemy> enemies = new ArrayList<>();
    static ArrayList<GameObject> enemyBullets = new ArrayList<>();
    static long levelEndTime;
    static Timer gameTimer;
    static boolean timerStarted;

    // keep track of the bullets;
    static ArrayList<GameObject> bullets = new ArrayList<>();


    static boolean gameOver = false;
    static boolean startButtonClicked = false;
    static boolean resetButtonClicked = false;
    static boolean resetButtonCreated = false;

    // three different states, Waiting to Start, gameActive, and gameOver
    static boolean waitingToStart = true;
    static boolean gameActive = false;

    static GameObject background;
    static GameObject startButton;
    static GameObject resetButton;

    // player can fire bullets, enemy ships might have variable health, create a counter on each
    // once counter hits zero, hit the delete function
    static class Enemy{
        GameObject enemyObject;
        int level;
        Enemy(GameObject enemyObject, int level){
            this.level = level;
            this.enemyObject = enemyObject;
        }
    }

    static void GenerateEnemies(int currentLevel) {

        livesCounter = 3;

            for(GameObject life : lives){
                GatorEngine.Delete(life);
            }
            lives.clear();

            for(int i = 0; i < livesCounter; i++){
                GameObject life = new GameObject(i*35, 475);
                life.shape = new Rectangle2D.Float(0, 0, 25, 25);
                life.material = new Material("resources/heart.png");
                lives.add(life);
                GatorEngine.Create(life);
            }

            // cap the number of enemies to 5 rows
            int numEnemies = currentLevel * 8;
            int numCols = numEnemies / currentLevel;
            int initialX = (500 - 280) / 2;

            for (int i = 0; i < numEnemies; i++) {
                int row = i / numCols;
                int col = i % numCols;

                int x = initialX + col * (40);
                int y = row * (40);

                GameObject enemy = new GameObject(x, y);
                enemy.shape = new Rectangle2D.Float(0, 0, 25, 25);

                int enemyLevel = Math.min(3, Math.max(1, currentLevel - row));
                String enemyMaterial = "resources/Enemy" + enemyLevel + ".png";

                enemy.material = new Material(enemyMaterial);
                enemy.scripts.add(new EnemyMovement(enemy));

                enemies.add(new Enemy(enemy, enemyLevel));
            }

            for (Enemy g : enemies) {
                GatorEngine.Create(g.enemyObject);
            }
    }

    static void Update(){
        // this is our GameLoop
        if(waitingToStart){
            if(startButtonClicked){
                waitingToStart = false;
                gameActive = true;
                //start the game
                background.material = new Material("resources/Background.png");
                startButton.active = false;
                //GatorEngine.Delete(startButton);
                player = new GameObject(225, 450);
                player.shape = new Rectangle2D.Float(0, 0, 25, 25);
                player.material = new Material("resources/Ship.png");
                player.scripts.add(new PlayerMovement(player, 10));
                GatorEngine.Create(player);
                // initialize enemies
                currentLevel = 1;
                GenerateEnemies(currentLevel);

                for(int i = 0; i < livesCounter; i++){
                    GameObject life = new GameObject(i*35, 475);
                    life.shape = new Rectangle2D.Float(0, 0, 25, 25);
                    life.material = new Material("resources/heart.png");
                    lives.add(life);
                    GatorEngine.Create(life);
                }
                startButtonClicked = false;
            }
        }else if(gameActive){
            // continuously generate levels
            if(lives.isEmpty()){
                System.out.println("Player lost lives");
                gameActive = false;
                gameOver = true;
            }
            if (enemies.size() == 0) {
                currentLevel++;

                for(GameObject bullet : bullets){
                    GatorEngine.Delete(bullet);
                }
                bullets.clear();

                for(GameObject enemyBullet : enemyBullets){
                    GatorEngine.Delete(enemyBullet);
                }
                enemyBullets.clear();

                for(Enemy enemy : enemies){
                    GatorEngine.Delete(enemy.enemyObject);
                }
                enemies.clear();

                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    System.out.println(e.getStackTrace());
                }

                GenerateEnemies(currentLevel);

                // regenerate lives
                //System.out.println(currentLevel);
            }else {
                for (Enemy e : enemies) {
                    if (e.enemyObject.transform.getTranslateY() >= player.transform.getTranslateY()) {
                        System.out.println("Enemies reached player");
                        gameActive = false;
                        gameOver = true;
                        break;
                        //break makes sure only one enemy needs to reach the player for this to be called, and ends the game
                    }
                }
            }
        }else if(gameOver){
            if (!resetButtonCreated) {
                GatorEngine.Delete(player);
                for(GameObject life : lives){
                    GatorEngine.Delete(life);
                }
                lives.clear();
                for(GameObject bullet : bullets){
                    GatorEngine.Delete(bullet);
                }
                bullets.clear();
                for(Enemy e : enemies){
                    GatorEngine.Delete(e.enemyObject);
                }
                enemies.clear();
                for(GameObject bullet : enemyBullets){
                    GatorEngine.Delete(bullet);
                }
                enemyBullets.clear();

                System.out.println(GatorEngine.OBJECTLIST.size());
                resetButton = new GameObject();
                resetButton.shape = new Rectangle2D.Float(150, 380, 200, 50);
                resetButton.material = new Material(new Color(0, 0, 0, 0), new Color(0, 0, 10, 0), 1);
                resetButton.scripts.add(new UpdateResetButton(resetButton));
                GatorEngine.Create(resetButton);

                // Set the flag to true to indicate that the button has been created
                resetButtonCreated = true;
            }

            background.material = new Material("resources/DeathScreen.png");

            if (resetButtonClicked) {
                currentLevel = 1;
                resetButtonCreated = false;
                RestartGame();
            }
        }
    }

    static void StartGame(){
        background = new GameObject();
        background.shape = new Rectangle2D.Float(0, 0, 500, 500);
        background.material = new Material("resources/Title.png");
        GatorEngine.Create(background);

        startButton = new GameObject();
        startButton.shape = new Rectangle2D.Float(200, 315, 100, 50);
        startButton.material = new Material(new Color(0,0,0,0), new Color(0,0,0,0), 1);
        startButton.scripts.add(new UpdateStartButton(startButton));
        GatorEngine.Create(startButton);
    }

    static void RestartGame(){
        System.out.println("Restart Game Called.");

        // Remove old game objects

        GatorEngine.Delete(resetButton);

        // Reset game variables

        gameActive = false;
        startButton.active = true;
        waitingToStart = true;
        resetButtonClicked = false;

        // Reset background
        background.material = new Material("resources/Title.png");
    }

    static class UpdateResetButton extends ScriptableBehavior{
        UpdateResetButton(GameObject g){
            super(g);
        }
        @Override
        public void Start() {

        }

        @Override
        public void Update() {
            if(Input.MouseX >= 150 && Input.MouseX <= 400){
                if(Input.MouseY >= 380 && Input.MouseY <= 430){
                    gameObject.material = new Material(new Color(0,245,0,76), new Color(0,245,0,76), 1);
                    if(Input.MouseClicked){
                        //waitingToStart = true;
                        //gameOver = false;
                        System.out.println("Restart Clicked");
                        resetButton.active = false;
                        resetButtonClicked = true;

                        //RestartGame();
                        //resetButtonClicked = true;
                    }
                }else{
                    gameObject.material = new Material(new Color(0,0,0,0), new Color(0,0,0,0), 1);
                }
            }else{
                gameObject.material = new Material(new Color(0,0,0,0), new Color(0,0,0,0), 1);
            }
        }
    }

    static class UpdateStartButton extends ScriptableBehavior{
        UpdateStartButton(GameObject g){
            super(g);
        }
        @Override
        public void Start() {

        }

        @Override
        public void Update() {
            if(Input.MouseX >= 200 && Input.MouseX <= 300){
                if(Input.MouseY >= 315 && Input.MouseY <= 365){
                    gameObject.material = new Material(new Color(0,255,0,76), new Color(0,255,0,76), 1);
                    //gameObject.shape = new Rectangle2D.Float(200, 315, 100, 50);
                    if(Input.MouseClicked){
                        //gameOver = false;
                        gameActive = true;
                        startButtonClicked = true;
                        //System.out.println("Clicked");
                    }
                }else{
                    gameObject.material = new Material(new Color(0,0,0,0), new Color(0,0,0,0), 1);
                }
            }else{
                gameObject.material = new Material(new Color(0,0,0,0), new Color(0,0,0,0), 1);
            }
        }
    }

    static class PlayerMovement extends ScriptableBehavior{
        int playerSpeed = 1;
        int delay = 0;

        PlayerMovement(GameObject g, int playerSpeed){
            super(g);
            this.playerSpeed = playerSpeed;
        }

        @Override
        public void Start() {
            delay = 0;
        }

        @Override
        public void Update() {
            // move ship
            playerTime = System.currentTimeMillis();
            delay++;
            // incorporate bounds checking for the ship
            if(Input.GetKeyDown('d')){
                if(gameObject.transform.getTranslateX() + 25 < 500){
                    gameObject.Translate(playerSpeed,0);
                }
            }

            if(Input.GetKeyDown('a')) {
                if (gameObject.transform.getTranslateX() - 5 > 0) {
                    gameObject.Translate(-playerSpeed, 0);
                }
            }

            //shoot stuff
            if(Input.GetKeyDown((char) KeyEvent.VK_SPACE)){
                //System.out.println("fire! ");
                if(delay > 15){
                    // player position:
                    GameObject bullet = new GameObject((int)player.transform.getTranslateX() + 11, (int)player.transform.getTranslateY());
                    bullet.shape = new Ellipse2D.Float(0, 0, 5, 5);
                    bullet.material = new Material(Color.GREEN, Color.GREEN, 2);
                    bullet.scripts.add(new BulletMovement(bullet, 5));
                    bullets.add(bullet);
                    GatorEngine.Create(bullet);
                    delay = 0;
                }
            }

            if(immunity){
                long currentTime = System.currentTimeMillis();
                if((currentTime - immunityStartTime) % 200 < 100){
                    gameObject.material = new Material("resources/Ship.png");
                }else{
                    gameObject.material = new Material("resources/ShipHit.png");
                }

                if (currentTime - immunityStartTime >= 2000) {
                    immunity = false; // Immunity period has ended
                    gameObject.material = new Material("resources/Ship.png");
                }
            }
        }
    }

    // bullet script
    // bullet stays at the same x location, and moves a set number of y every tick
    static class BulletMovement extends ScriptableBehavior{
        int speed = 5;
        BulletMovement(GameObject g, int speed){
            super(g);
            this.speed = speed;
        }
        @Override
        public void Start() {

        }

        @Override
        public void Update() {
            gameObject.Translate(0, -speed);

            if(gameObject.transform.getTranslateY() < 0){
                //System.out.println("game object removed");
                GatorEngine.Delete(gameObject);
            }

            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();

                if (enemy.enemyObject.CollidesWith(gameObject)) {
                    GatorEngine.Delete(gameObject);
                    enemy.level--;

                    if (enemy.level == 2) {
                        enemy.enemyObject.material = new Material("resources/Enemy2.png");
                    } else if (enemy.level == 1) {
                        enemy.enemyObject.material = new Material("resources/Enemy1.png");
                    } else {
                        GatorEngine.Delete(enemy.enemyObject);
                        iterator.remove();
                    }
                }
            }
        }
    }

    static class EnemyMovement extends ScriptableBehavior{
        int movementDelay;
        int speed = 1;
        int bulletDelay = 0;

        EnemyMovement(GameObject g){
            super(g);
            //this.level = level;
        }

        @Override
        public void Start() {
            movementDelay = 0;
            bulletDelay = 0;
        }

        @Override
        public void Update() {
            movementDelay++;
            if(movementDelay < 50){
                gameObject.Translate(speed, 0);
            }else if(movementDelay > 50 && movementDelay < 100){
                gameObject.Translate(0, speed);
            }else if(movementDelay > 100 && movementDelay < 150){
                gameObject.Translate(-speed, 0);
            }else if(movementDelay > 150 && movementDelay < 200){
                gameObject.Translate(0, speed);
            }else if(movementDelay > 200){
                movementDelay = 0;
            }

            for (Enemy e : enemies) {
                if (e.enemyObject == gameObject) {
                    Random random = new Random();
                    int rng = random.nextInt(100);
                    int bulletThreshold;
                    if(e.level == 3){
                        bulletThreshold = 50;
                    }else if(e.level == 2){
                        bulletThreshold = 25;
                    }else{
                        bulletThreshold = 0;
                    }

                    if (movementDelay % 50 == 0 && rng < bulletThreshold && bulletDelay == 0) {
                        GameObject bullet = new GameObject((int) gameObject.transform.getTranslateX() + 11, (int) gameObject.transform.getTranslateY());
                        bullet.shape = new Ellipse2D.Float(0, 0, 5, 5);
                        bullet.material = new Material(Color.RED, Color.RED, 2);
                        bullet.scripts.add(new EnemyFire(bullet));
                        enemyBullets.add(bullet);
                        GatorEngine.Create(bullet);
                        bulletDelay = 10;
                    }

                    // Decrease the bullet delay (if it's greater than 0)
                    if (bulletDelay > 0) {
                        bulletDelay--;
                    }
                }
            }
        }
    }

    static class EnemyFire extends ScriptableBehavior {
        EnemyFire(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {

        }

        @Override
        public void Update() {
            gameObject.Translate(0, 5);

            if (gameObject.transform.getTranslateY() > 500) {
                GatorEngine.Delete(gameObject);
            }
            if (gameObject.CollidesWith(player) && !immunity) {
                GatorEngine.Delete(gameObject);

                // Use an iterator to safely remove elements from the list
                Iterator<GameObject> iterator = enemyBullets.iterator();
                while (iterator.hasNext()) {
                    GameObject bullet = iterator.next();
                    if (bullet == gameObject) {
                        iterator.remove();
                        break; // Assuming there's only one bullet to remove
                    }
                }

                if (!lives.isEmpty()) {
                    GatorEngine.Delete(lives.get(lives.size() - 1));
                    lives.remove(lives.size() - 1);
                    immunity = true;
                    immunityStartTime = System.currentTimeMillis();
                    livesCounter--;
                }
                System.out.println("player took a hit");
            }
        }
    }

}

//    static void GameOverReset(){
//        //gameOver = true;
//        gameOver = true;
//        gameActive = false;
//        for(GameObject life : lives){
//            GatorEngine.Delete(life);
//        }
//        lives.clear();
//        for(GameObject bullet : bullets){
//            GatorEngine.Delete(bullet);
//        }
//        bullets.clear();
//        for(Enemy e : enemies){
//            GatorEngine.Delete(e.g);
//        }
//        enemies.clear();
//        for(GameObject bullet : enemyBullets){
//            GatorEngine.Delete(bullet);
//        }
//        enemyBullets.clear();
//        GatorEngine.Delete(player);
//        //System.out.println(gameOver);
//    }
