

/**
representasi papan othello
*/
import java.util.*;

public final class PapanBit
{
	public BitSet putih; //batu putih yang ada di papan
	public BitSet hitam; //batu hitam yang ada di papan
	public TreeMap<Byte,Byte> jalanPutih; //putih bisa jalan ke mana?
	public TreeMap<Byte,Byte> jalanHitam; //hitam bisa jalan ke mana?
	public boolean putihUpdate;
	public boolean hitamUpdate;
	public boolean gameover=false;
	public final static boolean BATU_PUTIH = true;
	public final static boolean BATU_HITAM = false;
	
	public final static int SIZE = 8; //ukuran papan
	
	public final static byte ARROW[] = {7,6,5,0,-1,4,1,2,3};
	public final static byte REV_ARROW[] = {3,6,7,8,5,2,1,0};
	
	public PapanBit()
	{
		putih = new BitSet();
		hitam = new BitSet();
		jalanPutih = new TreeMap<Byte,Byte>();
		jalanHitam = new TreeMap<Byte,Byte>();
		//jalanHitam = new Hashtable<Byte,Byte>();

		setPutih(3,3);
		setPutih(4,4);

		setHitam(4,3);
		setHitam(3,4);
		
		/*setBatu(3,3,batuPutih);
		setBatu(4,4,batuPutih);

		setBatu(4,3,batuHitam);
		setBatu(3,4,batuHitam);
		*/
		cariJalanHitam();
	}
	
	public PapanBit(PapanBit p)
	{
		this.putih=(BitSet)p.putih.clone();
		this.hitam=(BitSet)p.hitam.clone();
		this.jalanPutih=(TreeMap<Byte,Byte>) p.jalanPutih.clone();
		this.jalanHitam=(TreeMap<Byte,Byte>) p.jalanHitam.clone();
		this.putihUpdate=p.putihUpdate;
		this.hitamUpdate=p.hitamUpdate;
	}
	
	/*
	koordinat x, y menjadi satu bilangan
	*/
	public int ctp(int x,int y)
	{
		return SIZE*x + y;
	}
	
	public int dtp(int dx,int dy)
	{
		return (dx+1)*3 + dy+1;
	}
	/**
	menaruh batu putih di suatu titik
	*/
	public void setPutih(int x,int y)
	{
		putih.set(ctp(x,y));
		hitam.clear(ctp(x,y));
	}

	/**
	menaruh batu hitam di suatu titik
	*/
	public void setHitam(int x,int y)
	{
		hitam.set(ctp(x,y));
		putih.clear(ctp(x,y));
	}

	public void setEmpty(int x,int y)
	{
		hitam.clear(ctp(x,y));
		putih.clear(ctp(x,y));
	}
	public void setBatu(int x,int y,boolean batu)
	{
		hitam.set(ctp(x,y),!batu);
		putih.set(ctp(x,y),!batu);
	}

	/**
	membalik batu di suatu titik
	*/
	private void flip(int x,int y)
	{
		if(putih.get(ctp(x,y))||hitam.get(ctp(x,y)))
		{
			putih.flip(ctp(x,y));
			hitam.flip(ctp(x,y));
		}
	}

	/**
	mencari jalan yang valid bagi batu Putih
	*/
	public void cariJalanPutih()
	{
		/*
		clearkan semua jalan
		*/
		jalanPutih.clear();
		
		for(int i=hitam.nextSetBit(0); i>=0; i=hitam.nextSetBit(i+1))
		{// operate on index i here
			//System.out.println("checking "+i);
			for(int x=-1;x<=1;x++) for(int y=-1;y<=1;y++) {
				int posx=i/8 + x;
				int posy=i%8 + y;
				
				if(posx>=0 && posx<SIZE && posy>=0 && posy<SIZE && !putih.get(posx*8+posy) && !hitam.get(posx*8+posy))
				{
					if(adaBatuPutih(posx-2*x,posy-2*y,-x,-y))
					{
						//System.out.println("kudalumping "+posx + " "+posy);

						byte tmp = (byte)((1<<ARROW[dtp(-x,-y)]));
						byte posisi = (byte)(posx*8+posy);
						if(jalanPutih.containsKey(posisi))
						{
							tmp |= jalanPutih.get(posisi);
						}
						jalanPutih.put(posisi ,tmp);
						
						//System.out.println(dtp(-x,-y));
						//System.out.println(posisi);
						//System.out.println("kudalumping "+posx + " "+posy);
					}
				}
			}
		}
		putihUpdate=true;
		/*Iterator iterate = jalanPutih.keySet().iterator();
		
		while(iterate.hasNext())
		{
			byte x = (Byte) iterate.next();
			//System.out.println(x/8+" "+x%8+" "+Integer.toBinaryString(jalanPutih.get(x)&255));
		}*/
	}
	public void cariJalanHitam()
	{
		/*
		clearkan semua jalan
		*/
		jalanHitam.clear();
		
		for(int i=putih.nextSetBit(0); i>=0; i=putih.nextSetBit(i+1))
		{// operate on index i here
			//System.out.println("checking "+i);
			for(int x=-1;x<=1;x++) for(int y=-1;y<=1;y++) {
				int posx=i/8 + x;
				int posy=i%8 + y;
				
				if(posx>=0 && posx<SIZE && posy>=0 && posy<SIZE && !putih.get(posx*8+posy) && !hitam.get(posx*8+posy))
				{
					if(adaBatuHitam(posx-2*x,posy-2*y,-x,-y))
					{
						//System.out.println("kudalumping "+posx + " "+posy);

						byte tmp = (byte)((1<<ARROW[dtp(-x,-y)]));
						byte posisi = (byte)(posx*8+posy);
						if(jalanHitam.containsKey(posisi))
						{
							tmp |= jalanHitam.get(posisi);
						}
						jalanHitam.put(posisi ,tmp);
						
						//System.out.println(dtp(-x,-y));
						//System.out.println(posisi);
						//System.out.println("kudalumping "+posx + " "+posy);
					}
				}
			}
		}
		hitamUpdate=true;
		/*Iterator iterate = jalanHitam.keySet().iterator();
		
		while(iterate.hasNext())
		{
			byte x = (Byte) iterate.next();
			//System.out.println(x/8+" "+x%8+" "+Integer.toBinaryString(jalanHitam.get(x)&255));			
		}*/
	}
	
	boolean adaBatuPutih(int fromx,int fromy,int dx, int dy)
	{
		while(fromx>=0&&fromx<SIZE&&fromy>=0&&fromy<SIZE)
		{
			if(putih.get(fromx*SIZE+fromy)) return true;
			else if(hitam.get(fromx*SIZE+fromy))
			{
				fromx+=dx;
				fromy+=dy;
			}
			else return false;
		}
		return false;
	}
	
	boolean adaBatuHitam(int fromx,int fromy,int dx, int dy)
	{
		while(fromx>=0&&fromx<SIZE&&fromy>=0&&fromy<SIZE)
		{
			if(hitam.get(fromx*SIZE+fromy)) return true;
			else if(putih.get(fromx*SIZE+fromy))
			{
				fromx+=dx;
				fromy+=dy;
			}
			else return false;
		}
		return false;
	}
	
	
	public void jalankanPutih(int x,int y)
	{
		if(!putihUpdate) cariJalanPutih();
		byte arah;
		try{
			arah = jalanPutih.get((byte)(x*8+y));
		}
		catch (NullPointerException e) {
			System.out.println("gak valid");
			return;
		}
		byte count=0;
		byte dx,dy;
		int xx,yy;
		//System.out.println("masuk");
		
		while(arah!=0)
		{
			if((arah&1)==1)
			{
				xx=x;
				yy=y;
				//System.out.println("xx="+xx+" yy="+yy);
				//System.out.println("arah="+Integer.toBinaryString(arah)+" count="+count);
				//System.out.println("[debug]\n"+this);
				dx = (byte)(REV_ARROW[count]/3 - 1);
				dy = (byte)(REV_ARROW[count]%3 - 1);
				
				//System.out.println("dx="+dx+" dy="+dy);
				xx+=dx;
				yy+=dy;
				//System.out.println("xx="+xx+" yy="+yy);
				while(!putih.get(xx*8+yy))
				{
					flip(xx,yy);
					xx+=dx;
					yy+=dy;
				}
			}
			count++;
			arah=(byte)((arah&255)>>>1);
		}
		setPutih(x,y);
		
		hitamUpdate=false;
		putihUpdate=false;
		
		cariJalanHitam();
		if(jalanHitam.size()==0)
		{
			cariJalanPutih();
			if(jalanPutih.size()==0) gameover=true;
		}
	}
	public void jalankanHitam(int x,int y)
	{
		if(!hitamUpdate) cariJalanHitam();
		byte arah;
		try{
			arah = jalanHitam.get((byte)(x*8+y));
		}
		catch (NullPointerException e) {
			return;
		}
		byte count=0;
		byte dx,dy;
		int xx,yy;
		
		while(arah!=0)
		{
			if((arah&1)==1)
			{
				xx=x;
				yy=y;
				//System.out.println("arah="+arah+" count="+count);
				
				dx = (byte)(REV_ARROW[count]/3 - 1);
				dy = (byte)(REV_ARROW[count]%3 - 1);
				
				//System.out.println("dx="+dx+" dy="+dy);
				xx+=dx;
				yy+=dy;
				//System.out.println("xx="+xx+" yy="+yy);
				while(!hitam.get(xx*8+yy))
				{
					flip(xx,yy);
					xx+=dx;
					yy+=dy;
				}
			}
			count++;
			arah=(byte)((arah&255)>>>1);
		}
		setHitam(x,y);
		
		hitamUpdate=false;
		putihUpdate=false;
		
		cariJalanPutih();
		if(jalanPutih.size()==0)
		{
			cariJalanHitam();
			if(jalanHitam.size()==0) gameover=true;
		}
	}
	public void jalankan(int x,int y,boolean siapa)
	{
		if(siapa==BATU_PUTIH) jalankanPutih(x,y);
		else if(siapa==BATU_HITAM) jalankanHitam(x,y);
	}
	/*
	public void jalankan(int place,boolean siapa)
	{
		jalankan(place/10,place%10,siapa);
	}*/
	
	
	public boolean bisaJalan(int x,int y,boolean siapa)
	{
		if(siapa==BATU_PUTIH)
		{
			if(!putihUpdate) cariJalanPutih();
			return jalanPutih.containsKey((byte)(x*8+y));
		}
		
		if(!hitamUpdate) cariJalanHitam();
		return jalanHitam.containsKey((byte)(x*8+y));
	}
	
	/*
	public boolean bisaJalan(int place,boolean siapa)
	{
		return bisaJalan(place/10,place%10,siapa);
	}*/

	public boolean bisaJalan(boolean siapa)
	{
		if(siapa==BATU_PUTIH)
		{
			if(!putihUpdate) cariJalanPutih();
			return jalanPutih.size()>0;
		}
		
		if(!hitamUpdate) cariJalanHitam();
		return jalanHitam.size()>0;
	}
	
	public int mobility(boolean siapa)
	{
		if(siapa==BATU_PUTIH)
		{
			if(!putihUpdate) cariJalanPutih();
			return jalanPutih.size();
		}
		
		if(!hitamUpdate) cariJalanHitam();
		return jalanHitam.size();
	}
	
	public boolean gameOver()
	{
		return gameover;
	}

	public String toString()
	{
		StringBuffer hasil=new StringBuffer();
		hasil.append("  01234567\n");
		hasil.append("  ========\n");
		
		for (int y=0; y<=7;y++)
		{
			hasil.append(y+"|");
			for (int x=0 ; x<=7;x++)
			{
				if (putih.get(ctp(x,y))) hasil.append('0');
				else if (hitam.get(ctp(x,y))) hasil.append('#');
				
				else hasil.append('.');
			}
			hasil.append('\n');
		}
		hasil.append(gameover?"game is over":"game is not over yet");
		return hasil.toString();
	}

	
	public PapanBit clone()
	{
		return new PapanBit(this);
	}
	
	public Iterator iterator(boolean siapa)
	{
		if(siapa==BATU_PUTIH)
		{
			if(!putihUpdate) cariJalanPutih();
		}
		else if(!hitamUpdate) cariJalanHitam();

		if(siapa==PapanBit.BATU_PUTIH)
		{
			return jalanPutih.keySet().iterator();
		}
		return jalanHitam.keySet().iterator();
	}
	
	public boolean equals(Object obj)
	{
		PapanBit p = (PapanBit) obj;
		
		return p.putih.equals(putih) && p.hitam.equals(hitam);
	}
	
	public int hashCode()
	{
		return putih.hashCode() + hitam.hashCode();
	}
	
	public static void main(String args[])
	{
		PapanBit p = new PapanBit();
		
		p.setPutih(2,2);
		p.setPutih(2,3);
		p.setPutih(2,4);
		p.setPutih(4,2);
		p.setPutih(4,3);
		p.setPutih(4,4);
		p.setPutih(3,2);
		p.setPutih(3,4);
		
		p.setEmpty(3,3);
		
		
		p.setHitam(1,1);
		p.setHitam(1,2);
		p.setHitam(1,3);
		p.setHitam(1,4);
		p.setHitam(1,5);
		p.setHitam(5,1);
		p.setHitam(5,2);
		p.setHitam(5,3);
		p.setHitam(5,4);
		p.setHitam(5,5);
		p.setHitam(2,1);
		p.setHitam(3,1);
		p.setHitam(4,1);
		p.setHitam(2,5);
		p.setHitam(3,5);
		p.setHitam(4,5);
		
		
		System.out.println(p);
		p.cariJalanPutih();
		System.out.println(p);
		p.cariJalanHitam();
	}
}

