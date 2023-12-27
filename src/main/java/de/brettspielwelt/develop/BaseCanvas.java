package de.brettspielwelt.develop;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.brettspielwelt.game.Board;

public class BaseCanvas extends JPanel  {

	HTMLWrapper game;
	Timer timer;
	
	public BaseCanvas(int nr) {
		game=new Board();
		game.addMouseListener(game);
		game.addMouseMotionListener(game);
		try {
			game.props.load(new FileInputStream("assets/Text.string"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		game.init();
		game.spielerNr=nr;
		Main.info.setBoard(game);
		
		timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                game.run();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000/24);
        
        setLayout(new BorderLayout());
        add(game,BorderLayout.CENTER);
        
        JPanel bottom=new JPanel();
        bottom.add(new JButton("Console"));
        add(bottom,BorderLayout.SOUTH);
	}

	public void removeNotify() {
		Main.info.removeBoard(game);
		timer.cancel();
	}
}
