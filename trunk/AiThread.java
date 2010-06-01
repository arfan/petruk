

public final class AiThread extends Thread
{
	GUIPetruk gm;
	boolean kill;
	
	public AiThread()
	{
		kill = false;
	}
	
	public AiThread(GUIPetruk gm)
	{
		gm.lock = true;
		gm.pesan.setText(((gm.kompi)?"putih":"hitam")+" to move");
		this.gm = gm;
		kill = false;
	}
	
		
	public void run()
	{
		gm.best = gm.mitello.alphabeta(gm.papan, gm.kompi);
		
		//System.out.println("kambing = "+gm.best.value);
		if(!kill)
		{
			gm.jalankan(gm.best.move/8,gm.best.move%8,gm.kompi);
			System.out.println("jalan1");
		}
		else
			System.out.println("killed1");
		while(!gm.papan.bisaJalan(!gm.kompi) & !kill)
		{
			if(gm.papan.gameOver())
			{
				javax.swing.JOptionPane.showMessageDialog(null,gm.mitello.score(gm.papan));
				System.out.println(gm.mitello.score(gm.papan));
				System.out.println("selesai");
				return;
				//System.exit(0);
			}
			//javax.swing.JOptionPane.showMessageDialog(null,(!gm.kompi?"putih":"hitam")+" tidak bisa jalan, pemain ganti");
			
			gm.best = gm.mitello.alphabeta(gm.papan, gm.kompi);
			if(!kill)
			{
				System.out.println("jalan2");
				gm.jalankan(gm.best.move/8,gm.best.move%8,gm.kompi);
			}
			else
				System.out.println("killed1");
		}
		
		gm.pesan.setText(((!gm.kompi)?"putih":"hitam")+" to move");
		gm.lock = false;
		//System.out.println("kambing");
	}
	
	public void kill()
	{
		gm.lock = false;
		kill = true;
	}
	public static void main(String args[])
	{
		AiThread ai = new AiThread();
		ai.start();
	}
}
