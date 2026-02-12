package adventuresInJava;

import javax.swing.JFrame;

public class Game {

    public static void main(String[] args) {
    	//Title of the top
        JFrame window = new JFrame("Adventures In Java");

        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        //Adds Game panel which for now will be a black box
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}

