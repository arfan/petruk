

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class GUIPetruk extends JFrame implements ActionListener
{
	JLabel pesan;
	ViewPanel panel;
	JMenuBar menuBar;
	JMenu menuGame;
		JMenuItem menuNewGame1;
		JMenuItem menuNewGame2;
		JMenuItem menuExit;
	JMenu menuHelp;
		JMenuItem menuCreator;

	int lastMove=-1;

	AiThread ai;
	
	public static final int SISI_ATAS = 20;// jarak dari pinggir frame ke pinggir papan vertikal (pixel)
	public static final int SISI_BAWAH = 20;// jarak dari pinggir frame ke pinggir papan vertikal (pixel)
	public static final int SISI_KIRI = 20;// jarak dari pinggir frame ke pinggir papan horizontal (pixel)
	public static final int SISI_KANAN = 20;// jarak dari pinggir frame ke pinggir papan horizontal (pixel)
	
	public static final int SISI_V = 20;// jarak dari pinggir frame ke pinggir papan horizontal (pixel)
	public static final int SISI_H = 20;// jarak dari pinggir frame ke pinggir papan horizontal (pixel)

	public static final int H = 400; //tinggi window(pixel)
	public static final int W = 400; //lebar window(pixel)

	int panjang;
	int lebar;

	PetrukBit mitello;
	PapanBit papan;
	Move best = new Move();

	public static int alpha = Integer.MIN_VALUE;
	public static int beta  = Integer.MAX_VALUE;

	boolean kompi;
	boolean lock;

	public GUIPetruk(boolean com)
	{
		super("Petruk The Othello Player");
		
		Container c = getContentPane();
		menuBar = new JMenuBar();
		
		menuGame = new JMenu("Game");
		menuNewGame1 = new JMenuItem("new game computer white human black");
		menuNewGame2 = new JMenuItem("new game computer black human white");
		menuExit = new JMenuItem("Exit");
		
		menuNewGame1.addActionListener(this);
		menuNewGame2.addActionListener(this);
		menuExit.addActionListener(this);
		
		menuGame.add(menuNewGame1);
		menuGame.add(menuNewGame2);
		menuGame.add(menuExit);
		
		menuHelp = new JMenu("Help");
		menuCreator = new JMenuItem("Creator");
		
		menuHelp.add(menuCreator);
		
		menuCreator.addActionListener(this);
		
		pesan = new JLabel("There is no end in this path");
		
		c.setLayout(new BorderLayout());
		//panel.setBackground(Color.GREEN);

		c.add(pesan,BorderLayout.SOUTH);


		menuBar.add(menuGame);
		menuBar.add(menuHelp);

		//setBackground(Color.GREEN);
		panjang = (W - ( SISI_KIRI+SISI_KANAN))/8;
		lebar   = (H - (SISI_ATAS+SISI_BAWAH))/8;

		mitello = new PetrukBit();
		//mitello.playAsBlack(mitello.papan);
		papan = mitello.papan;

		kompi = com;//PapanBit.BATU_HITAM;

		setVisible(true);
		setSize(new Dimension(W+8,H+60));
		setLocation(200,100);
		setJMenuBar(menuBar);
		lock = false;

		
		panel = new ViewPanel(this);
		c.add(panel,BorderLayout.CENTER);
		addMouseListener(panel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		
		repaint();
		
		validate();
	}
	
	public void startGame()
	{
		if(kompi==PapanBit.BATU_HITAM)
		{
			//pesan.setText(((kompi)?"putih":"hitam")+" to move");
			
			ai = new AiThread(this);
			ai.start();
		}
		else
		{
			pesan.setText(((!kompi)?"putih":"hitam")+" to move");
		}
	}
	
	public void selfPlay()
	{
		kompi = PapanBit.BATU_HITAM;
		//mitello.deepLimit = 6;
		while(!papan.gameOver())
		{
			if(papan.bisaJalan(kompi))
			{
				ai = new AiThread(this);
				ai.run();
			}
			
			kompi = !kompi;
		}
	}

	public void actionPerformed(ActionEvent evt)
	{
		//System.out.println(evt);
		
		if(evt.getSource() == menuNewGame1)
		{
			if(ai!=null) ai.kill();
			lock = false;
			mitello = new PetrukBit();
			papan = mitello.papan;
			kompi = PapanBit.BATU_PUTIH;
			lastMove = -1;
			startGame();
			repaint();
		}
		else if(evt.getSource() == menuNewGame2)
		{
			if(ai!=null) ai.kill();
			lock = false;
			mitello = new PetrukBit();
			papan = mitello.papan;
			kompi = PapanBit.BATU_HITAM;
			lastMove = -1;
			startGame();
			repaint();
		}
		else if(evt.getSource() == menuExit)
		{
			System.out.println("bye");
			System.exit(0);
		}
		else if(evt.getSource() == menuCreator)
		{
			JOptionPane.showMessageDialog(null, "   Petruk ( c ) 2004-2007 \nabdul.arfan <at> gmail <dot> com");	
		}
		
	}	
		/*  width
	panjang
	l h
	e e
	b i
	a g
	r h
	  t
*/
	
	public void jalankan(int xx,int yy,boolean kompi)
	{
		papan.jalankan(xx,yy,kompi);
		mitello.stage++;
		lastMove=xx*8+yy;

		if(mitello.stage>45) mitello.deepLimit = 20;   //end game
		//else if(mitello.stage>30) mitello.deepLimit = 7;
		//else if(mitello.stage>35) mitello.deepLimit = 9;
		//else if(mitello.stage>33) mitello.deepLimit = 8;
		//else if(mitello.stage>30) mitello.deepLimit = 7;

		//pesan.setText((new Date()).toString());
		repaint();
		//pesan.setText(((!kompi)?"putih":"hitam")+" to move");

		System.out.println(papan);
		System.out.println(((!kompi)?"putih":"hitam")+" to move");
		System.out.println("stage="+mitello.stage+" maxDeep="+mitello.deepLimit+" value="+best.value);
		//if(mitello.stage>=44) System.out.print();
		//System.out.println();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		panel.repaint();
	}
	
	final class ViewPanel extends JPanel implements MouseListener
	{
		public GUIPetruk parent;
		public ViewPanel(GUIPetruk parent)
		{
			this.parent = parent;
			addMouseListener(this);
		}
		
		public void paint(Graphics g)
		{
			//g = getGraphics();
			//super.paint(g);
			//setBackground(Color.GREEN);

			g.setColor(Color.green);

			//g.fillRect(0 + SISI_H, 0 + SISI_V, W- 2*SISI_H,H-2*SISI_V);
			g.fillRect(0, 0, W,H);

			g.setColor(Color.red);
			for (int a=0;a<9;a++)
			{
				g.drawLine(SISI_H + a*panjang,SISI_V, SISI_H + a*panjang, 8*lebar + SISI_V);
				g.drawLine(SISI_H , a*lebar + SISI_V, SISI_H + 8*panjang, a*lebar + SISI_V);
			}

			int x;
			int y;

			//try{

			//System.out.println(mitello.papan.hitam);
			//System.out.println(mitello.papan.putih);
				g.setColor(Color.BLACK);
				for(int i=mitello.papan.hitam.nextSetBit(0); i>=0; i=mitello.papan.hitam.nextSetBit(i+1))
				{
					x=i/8;
					y=i%8;
					g.fillOval(SISI_H+panjang*x,SISI_V+lebar*y,panjang,lebar);
				}

				g.setColor(Color.WHITE);
				for(int i=mitello.papan.putih.nextSetBit(0); i>=0; i=mitello.papan.putih.nextSetBit(i+1))
				{
					x=i/8;
					y=i%8;
					g.fillOval(SISI_H+panjang*x,SISI_V+lebar*y,panjang,lebar);
				}
			//}
			//catch(NullPointerException e)
			//{

			//}


			if (lastMove!=-1)
			{
				g.setColor(Color.RED);
				g.fillOval(SISI_H+panjang*(lastMove/8)+(panjang-10)/2,SISI_V+lebar*(lastMove%8)+(lebar-10)/2,10,10);
			}

		}

		public void mouseClicked(MouseEvent event)
		{
			//System.out.println("kambing");
			//repaint();
			if(papan.gameOver())
			{
				System.out.println("gameover");
				return;
				//System.exit(0);
			}
			if(lock)
			{
				System.out.println("lock");
				//JOptionPane.showMessageDialog(null," anda belum boleh jalan");
				repaint();
				return;
			}
			int xx=(event.getX() - SISI_KIRI);
			int yy=(event.getY() - SISI_ATAS);
			//System.out.println("xx="+xx + " yy="+yy );

			if(xx<0||xx>panjang*8||yy<0||yy>panjang*8)
			{
				System.out.println("salah tempat");
				/*xx=-1;
				yy=-1;*/
				return;
			}
			
			if (xx>=0&&yy>=0)
			{
				System.out.println("jalan yg benar");
				xx/=panjang;
				yy/=lebar;
			}
			System.out.println("xx="+xx + " yy="+yy );


			if(papan.bisaJalan(xx,yy,!kompi))
			{
				System.out.println("bisa jalan");
				jalankan(xx,yy,!kompi);
			}
			else
			{
				System.out.println("gak bisa jalan");
				return;
			}
			if(!papan.bisaJalan(kompi))
			{
				if(papan.gameOver())
				{
					JOptionPane.showMessageDialog(null,mitello.score(papan));
					System.out.println(mitello.score(papan));
					System.out.println("selesai");
					return;
					//System.exit(0);
				}
				else
				{
					//JOptionPane.showMessageDialog(null,(kompi?"putih":"hitam")+" tidak bisa jalan, pemain ganti");
					return;
				}
			}
			else //jika komputer bisa jalan
			{
				ai = new AiThread(parent);
				ai.start();

				/*while(!papan.bisaJalan(!kompi))
				{
					if(papan.gameOver())
					{
						//JOptionPane.showMessageDialog(this,mitello.score(papan));
						System.out.println(mitello.score(papan));
						System.out.println("selesai");
						return;
						//System.exit(0);
					}
					//JOptionPane.showMessageDialog(this,(!kompi?"putih":"hitam")+" tidak bisa jalan, pemain ganti");

					ai = new AiThread(this);
					ai.start();
				}*/
			}
		}
		public void mousePressed(MouseEvent event)
		{}
		public void mouseReleased(MouseEvent event)
		{}
		public void mouseExited(MouseEvent event)
		{}
		public void mouseEntered(MouseEvent event)
		{}

	}
	public static void main(String args[])
	{
		GUIPetruk game = new GUIPetruk(args.length>0 && args[0].equals("black"));
		
		if(args.length>0 && args[0].equals("self"))
			game.selfPlay();
		else
			game.startGame();
		//
		//GUIPetruk game = new GUIPetruk();
	}
}


