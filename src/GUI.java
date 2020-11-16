import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;

/**
 * GUI class
 */
class Grid extends JPanel{
    private static final long serialVersionUID = 1L;

    // Repaints the grid
    public void paint(Graphics g){

        //Grid
        int cellsize = 700/Math.max(WolfnRabbit.animals.w, WolfnRabbit.animals.h);
        for(int i=0; i<WolfnRabbit.animals.w*cellsize; i+=cellsize){
            for(int j=0; j<WolfnRabbit.animals.h*cellsize; j+=cellsize){
                g.setColor(Color.WHITE);
                g.drawRect(i, j, cellsize, cellsize);
            }
        }

        //Rabbits
        for(int i=0; i<WolfnRabbit.animals.rabbits.size(); i++){
            if(WolfnRabbit.animals.rabbits.get(i).alive){
                g.setColor(Color.BLUE);
                g.fillRect(WolfnRabbit.animals.rabbits.get(i).x*cellsize, WolfnRabbit.animals.rabbits.get(i).y*cellsize, cellsize, cellsize);
            }
        }

        //Wolf
        g.setColor(Color.RED);
        g.fillRect(WolfnRabbit.animals.wolf.x*cellsize, WolfnRabbit.animals.wolf.y*cellsize, cellsize, cellsize);
    }
}