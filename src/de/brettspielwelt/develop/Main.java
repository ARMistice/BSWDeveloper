package de.brettspielwelt.develop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import de.brettspielwelt.client.boards.games.PlainInformer;
import de.brettspielwelt.game.Informer;

public class Main extends JFrame {
    private JPanel canvasPanel;
    private JButton addButton,removeButton;
    private int canvasCount;
    public static PlainInformer info;
    
    public Main() {
        canvasCount = 0;
        info=new Informer();
        
        // Set up the frame
        setTitle("BrettspielWelt Develop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the bottom bar
        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new FlowLayout());

        JButton upload=new JButton("Publish to BrettspielWelt");
        upload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Publisher.publish();
            	Uploader.upload();
            }
        });
        
        JButton saveState=new JButton("Save State");
        saveState.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try (FileOutputStream fileOutputStream = new FileOutputStream("informer.state");
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                       objectOutputStream.writeObject(Main.info);
                   } catch (IOException ex) {
                	   ex.printStackTrace();
                       System.out.println("Error saving object: " + ex.getMessage());
                   }
            }
        });
        JButton loadState=new JButton("Load State");
        loadState.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (FileInputStream fileInputStream = new FileInputStream("informer.state");
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

                       // Read the object from the file
                       info = (Informer) objectInputStream.readObject();
                       info.boards=new ArrayList<>();
                       int ctmp=info.anzMitSpieler;
                       
                       mainPanel.remove(canvas);
                       canvas = new BaseCanvas(0);
       	              canvas.setBackground(Color.GRAY);
       	              mainPanel.add(canvas);
       	              while(canvasCount>0) {
       	            	  removeCanvas();
       	              }
                       for(int i=0; i<ctmp-1; i++) {
    	            	  addCanvas();
                       }
    	              info.sendComplete();
    	              canvas.repaint();
    	              validate();

                             System.out.println("Object loaded successfully!");
                       // Use the loaded object as needed
                   } catch (IOException | ClassNotFoundException ex) {
                       System.out.println("Error loading object: " + ex.getStackTrace());
                   }

            }
        });
        
        JButton reload=new JButton("Reload");
        reload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.remove(canvas);
                canvas = new BaseCanvas(0);
	              canvas.setBackground(Color.GRAY);
	              mainPanel.add(canvas);
	              int ctmp=canvasCount;
	              while(canvasCount>0) {
	            	  removeCanvas();
	              }
	              for(int i=0; i<ctmp; i++)
	            	  addCanvas();
	              
	              info.sendComplete();
	              canvas.repaint();
	              validate();
            }
        });
        JButton reset=new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.reset();
                info.sendComplete();
            }
        });
        JButton start=new JButton("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info.spielStart();
            }
        });
        bottomBar.add(saveState);
        bottomBar.add(loadState);
        bottomBar.add(reload);
        seperate(bottomBar, upload);
        bottomBar.add(reset);
        bottomBar.add(start);
        // Create the "Add" button
        addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCanvas();
                info.sendComplete();
            }
        });
        bottomBar.add(addButton);
        
        removeButton = new JButton("-");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeCanvas();
                info.sendComplete();
            }
        });
        bottomBar.add(removeButton);

        seperate(bottomBar, upload);
        
        bottomBar.add(upload);
        // Add the bottom bar to the frame
        add(bottomBar, BorderLayout.SOUTH);

        // Create the main panel for the canvas
        mainPanel = new JPanel();
        GridLayout gL=new GridLayout(1, 1);
        mainPanel.setLayout(gL);
        canvas = new BaseCanvas(0);
//        canvas.setPreferredSize(new Dimension(1220, 784));
//        canvas.setMinimumSize(new Dimension(610, 392));
        canvas.setBackground(Color.GRAY);
        mainPanel.add(canvas);

        
        
        canvasPanel = new JPanel();
        gridLayout=new GridLayout(1, 2);
        canvasPanel.setLayout(gridLayout);
        splitPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, canvasPanel);
        splitPane.setContinuousLayout(true);
        // Add the main panel to the frame
        add(splitPane, BorderLayout.CENTER);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateGridLayout();
            }
        });
    }

	private void seperate(JPanel bottomBar, JButton upload) {
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(10,upload.getHeight()));
        bottomBar.add(separator);
	}
    
    JPanel mainPanel;
    BaseCanvas canvas;
    JSplitPane splitPane;
    GridLayout gridLayout;
    
    private void updateGridLayout() {
        int panelWidth = canvasPanel.getWidth();
        int panelHeight = canvasPanel.getHeight();

        int columnCount = (int) Math.ceil(Math.sqrt(canvasCount));
        int rowCount = (int) Math.ceil((double) canvasCount / columnCount);

        gridLayout.setColumns(1);
        gridLayout.setRows(canvasCount);

        canvasPanel.revalidate();
    }

    private void addCanvas() {
        if (canvasCount >= 3) {
            JOptionPane.showMessageDialog(this, "You cannot add more than 4 canvases.");
            return;
        }

        if(canvasCount==0) {
        	splitPane.setDividerLocation(0.7);
        }
        // Create a new Canvas component
        BaseCanvas canvas = new BaseCanvas(canvasCount+1);
        //canvas.setPreferredSize(new Dimension(1220, 784));
        //canvas.setMinimumSize(new Dimension(610, 392));
        canvas.setBackground(Color.GRAY);
        canvasPanel.add(canvas);
        canvasPanel.revalidate();
        canvasPanel.repaint();

        canvasCount++;
        updateGridLayout();

        removeButton.setEnabled(canvasCount > 0);
        addButton.setEnabled(canvasCount < 3);
    }

    private void removeCanvas() {
        if (canvasCount <= 0) {
            JOptionPane.showMessageDialog(this, "You cannot remove the last canvas.");
            return;
        }

        canvasPanel.remove(canvasCount-1);
        canvasPanel.revalidate();
        canvasPanel.repaint();

        canvasCount--;
        updateGridLayout();

        removeButton.setEnabled(canvasCount > 0);
        addButton.setEnabled(canvasCount < 3);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main window = new Main();
                window.pack();
                window.setVisible(true);
            }
        });
    }
}