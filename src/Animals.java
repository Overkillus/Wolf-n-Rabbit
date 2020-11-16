import java.util.ArrayList;
import java.util.Random;

class Animals {
    /**
     * Width
     */
    public int w;
    /**
     * Height
     */
    public int h;
    /**
     * Delay time
     */
    public int k;
    /**
     * Rabbit number
     */
    public int r;
    /**
     * Rabbit List
     */
    public ArrayList<Rabbit> rabbits = new ArrayList<Rabbit>();
    /**
     * Single wolf that hunts for rabbits
     */
    public Wolf wolf;
    /**
     * SudoRandom seed generating all the sudorandom elements
     */
    public Random random = new Random();

    /**
     * Rabbit that escapes from wolf using a separate thread
     */
    class Rabbit extends Thread {
        /**
         * Rabbit x coordinate
         */
        public int x;
        /**
         * Rabbit y coordinate
         */
        public int y;
        /**
         * States if rabbit is alive
         */
        public boolean alive;

        /**
         * Creates a new rabbit at provided coordinates
         */
        public Rabbit(int x, int y) {
            this.x = x;
            this.y = y;
            this.alive = true;
        }

        /**
         * Moves the rabbit
         */
        public synchronized void move() {
            /**
             * Potential x difference
             */
            int px;
            /**
             * Potential y difference
             */
            int py;
            /**
             * Potential distance
             */
            int pdistance;
            /**
             * Potential max distance
             */
            int mdistance = -1;
            /**
             * Potential max distance
             */
            int counter = 0;
            /**
             * Random index
             */
            int ri;

            // If rabbit is on border picks at random
            if (isBorder(x, y)) {
                do {
                    px = random.nextInt(3) - 1;
                    py = random.nextInt(3) - 1;
                } while (!isValid(x+px, y+py));

                if (!isOccupied(x + px, y + py)) {
                    x += px;
                    y += py;
                }
            }
            // Tries to escape the wolf
            else {
                int[][] points = new int[3][3];
                ri = random.nextInt(3);

                // Generates
                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        pdistance = distanceFromWolf(x + i - 1, y + j - 1);
                        points[i][j] = pdistance;
                        if (pdistance > mdistance) {
                            mdistance = pdistance;
                        }
                    }
                }

                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        if (points[i][j] == mdistance) {
                            if (counter == ri) {
                                px = i - 1;
                                py = j - 1;
                                if (!isOccupied(x + px, y + py)) {
                                    x += px;
                                    y += py;
                                }
                                //System.out.println("normal " + x + " / " + y + " moving to: " + (x+px) + " / " + (y+py));
                            }
                            counter++;
                        }
                    }
                }
                counter = 0;
            }
        }

        /**
         * Separate thread responsible for rabbit movements
         */
        @Override
        public void run() {
            while (alive) {
                move();
                try {
                    sleep( (long)((random.nextDouble() + 0.5) * k) );
                }
                catch (InterruptedException e) {
                    System.out.println("wait error");
                }
            }
        }
    }
    /**
     * Wolf that hunts for rabbits using a separate thread
     */
    class Wolf extends Thread{
        /**
         * Wolf x coordinate
         */
        public int x;
        /**
         * Wolf y coordinate
         */
        public int y;
        /**
         * Controls if wolf has to sleep
         * if > 0 then wolf sleeps
         */
        public int sleep;
        
        /**
         * Creates a new wolf at provided coordinates
         */
        public Wolf(int x, int y){
            this.x = x;
            this.y = y;
            this.sleep = 0;
        }

        /**
         * Decides where the wolf will move
         * @param wolf
         * @return
         */
        public synchronized void move(){ //woof woof
            int minDistance = -1;
            int currentDistance = 0;   
            ArrayList<Rabbit> closestRabbits = new ArrayList<Rabbit>();
            Rabbit pickedRabbit;
            int dx;
            int dy;

            //Checks for nearest rabbits
            for(int i=0; i<rabbits.size(); i++){
                if(rabbits.get(i).alive){
                    currentDistance = distanceFromWolf(rabbits.get(i).x, rabbits.get(i).y);
                    if(currentDistance < minDistance || minDistance == -1){
                        closestRabbits = new ArrayList<Rabbit>();
                        closestRabbits.add(rabbits.get(i));
                        minDistance = currentDistance;
                    }
                    else if(currentDistance == minDistance){
                        closestRabbits.add(rabbits.get(i));
                    }
                }
            }

            //Picks one rabbit at random
            pickedRabbit = closestRabbits.get(random.nextInt(closestRabbits.size()));
            dx = pickedRabbit.x - x;
            dy = pickedRabbit.y - y;

            //Decides where to move
            x += 1*Integer.signum(dx);
            y += 1*Integer.signum(dy);

            //Kills a rabbit
            hunt(pickedRabbit);
        }

        /**
         * Attempts to kill a rabbit if a wolf is standing on the same tile
         */
        public synchronized void hunt(Rabbit pickedRabbit){
            if(x == pickedRabbit.x && y == pickedRabbit.y){
                pickedRabbit.alive = false;
                sleep = 5;
            }
        }

        /**
         * Wolf sleeps for one turn
         */
        public void sleep(){
            sleep--;
        }

        /**
         * Checks if there is a rabbit alive
         * @return
         */
        public boolean prey(){
            for(int i=0; i<rabbits.size(); i++){
                if(rabbits.get(i).alive) return true;
            }
            return false;
        }

        /**
         * Separate thread responsible for wolf movements and actions
         */
        @Override
        public void run(){
            while(prey()){
                if(sleep > 0){
                    sleep();
                }
                else {
                    move();
                }
                try {
                    sleep( (long)((random.nextDouble() + 0.5) * k) );
                }
                catch (InterruptedException e) {
                    System.out.println("wait error");
                }
            }
        }
    }
    
    /**
     * Animals constructor
     * @param w width
     * @param h height
     * @param k delay time
     * @param r # rabbits
     */
    public Animals(int w, int h, int k, int r){
        this.w = w;
        this.h = h;
        this.k = k;
        this.r = r;
        populate();
    }

    /**
     * Checks if location is already occupied
     * true if occupied
     */
    public boolean isOccupied(int x, int y){
        //Checks for wolf
        if(wolf != null){
            if(wolf.x == x && wolf.y == y) return true;
        }
        //Checks for rabbits
        for(int i=0; i<rabbits.size(); i++){
            if(rabbits.get(i).x == x && rabbits.get(i).y == y && rabbits.get(i).alive) return true;
        }
        return false;
    }
    
    /**
     * Checks if a point falls within the boundaries
     */
    public boolean isValid(int x, int y){
        if (x >= 0 && x <= w-1 && y >= 0 && y <= h-1) return true;
        else return false;
    }

    /**
     * Checks if a point lies on the border
     * @param x
     * @param y
     * @return
     */
    public boolean isBorder(int x, int y){
        if ((x == 0 || y == 0 || x == w-1 || y == h-1) && isValid(x,y)) return true;
        else return false;
    }
    
    /**
     * Populates the playing field with r rabbits and 1 wolf on random locations
     */
    public void populate(){
        int x;
        int y;

        //Rabbits
        for(int i=0; i<r; i++){
            do{
                x = random.nextInt(w);
                y = random.nextInt(h);
            }
            while(isOccupied(x, y));

            rabbits.add(new Rabbit(x,y));
        }
        //Wolf
        do{
            x = random.nextInt(w);
            y = random.nextInt(h);
        }
        while(isOccupied(x, y));
        wolf = new Wolf(x, y);
    }

    /**
     * Starts all the threads
     * */
    public void startAll(){
        for(int i=0; i<rabbits.size(); i++){
            rabbits.get(i).start();
        }
        wolf.start();
    }

    /**
     * Checks for distance from wolf
     * @param x
     * @param y
     * @return int distance
     */
    public int distanceFromWolf(int x, int y){
        return Math.max(Math.abs(x-wolf.x), Math.abs(y-wolf.y));
    }
}