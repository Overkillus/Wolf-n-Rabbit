import java.awt.Color;
import java.util.Scanner;

import javax.swing.JFrame;

/**
 * Main program class
 */
public class WolfnRabbit{
    public static JFrame frame;
    public static Animals animals;
    public static void main(String[] args){
        /**
         * Parsing data from input to animals instance
         */
        try{
            //Ask for manual input
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Do you want to provide input parameters manually? (y/n)");
            String myString = keyboard.next();
            Integer[] arguments = new Integer[4];
            if (myString.equals("y") || myString.equals("yes") || myString.equals("Yes")) {
                System.out.println("Enter <width> <height> <speed> <rabbitCount> as integers");
                for (int i=0; i<4; i++) {
                	arguments[i] = keyboard.nextInt();
                }
            }
            else {
            	arguments[0] = 30;
            	arguments[1] = 30;
            	arguments[2] = 100;
            	arguments[3] = 25;
            }
            keyboard.close();
            
            //Assigns values and creates a new parameter instance
            animals = new Animals(arguments[0], arguments[1], arguments[2], arguments[3]);
            animals.startAll();
        }
        catch (Exception e){ System.out.println("input error: provide <width> <height> <speed> <rabbitCount>"); }

        myGui();

        while(animals.wolf.prey()){
            frame.repaint();
        }
    }

    public static void myGui(){
        frame = new JFrame();
        frame.setSize(750,750);
        frame.getContentPane().add(new Grid());
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.LIGHT_GRAY);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}