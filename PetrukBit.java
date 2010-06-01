

import java.util.*;

public final class PetrukBit
{
	int evaluator[][] = new int[8][8];
	int deepLimit = 6;
	int stage = 0;
	public static int alpha = Integer.MIN_VALUE;
	public static int beta  = Integer.MAX_VALUE;
	PapanBit papan;
	
	Hashtable table = new Hashtable<PetrukBit, Integer>();
	
	public PetrukBit()
	{
		String nilaiEvaluator =

		" 64 -30  10   5   5  10 -30  64 "+

		"-30 -40   2   2   2   2 -40 -30 "+

		" 10   2   5   1   1   5   2  10 "+

		"  5   2   1   0   0   1   2   5 "+

		"  5   2   1   0   0   1   2   5 "+

		" 10   2   5   1   1   5   2  10 "+

		"-30 -40   2   2   2   2 -40 -30 "+

		" 64 -30  10   5   5  10 -30  64 ";
		

		/*String nilaiEvaluator =

		" 50 -10   5   2   2   5 -10  50 "+

		"-10  -5   2   2   2   2  -5  -1 "+

		"  5   2   5   1   1   5   2   5 "+

		"  2   2   1   1   1   1   2   2 "+

		"  2   2   1   1   1   1   2   2 "+

		"  5   2   5   1   1   5   2   5 "+
 
		"-10  -5   2   2   2   2  -5 -10 "+

		" 50 -10   5   2   2   5 -10  50 ";*/
		
		
		StringTokenizer tokenEval = new StringTokenizer(nilaiEvaluator);
		int index=0;
		while (tokenEval.hasMoreTokens())
		{
			int value = Integer.parseInt(tokenEval.nextToken());
			evaluator[index%8][index/8]=value;
			index++;
		}
		
		papan = new PapanBit();
		System.out.println(papan);
		//selfPlay(papan, PapanBit.BATU_HITAM);
		//playAsBlack(papan);
	}

	/*
	semakin kecil nilainya berarti semakin bagus untuk putih
	semakin besar nilainya berarti semakin bagus untuk hitam

	karena hitam jalan duluan
	*/
	public int value(PapanBit p)
	{
		//int mobility = p.mobility(PapanBit.BATU_HITAM)-p.mobility(PapanBit.BATU_PUTIH);
		
		if(stage <= 45)
		{
			int value=0;
		
			for(int i=p.hitam.nextSetBit(0); i>=0; i=p.hitam.nextSetBit(i+1))
			{
				value += evaluator[i/8][i%8];
			}
			for(int i=p.putih.nextSetBit(0); i>=0; i=p.putih.nextSetBit(i+1))
			{
				value -= evaluator[i/8][i%8];
			}
			
			return value;
		}
		return p.hitam.cardinality() - p.putih.cardinality();
	}

	Move getMax(PapanBit node, boolean side,int deep)
	{
		int temp = value(node);

		if(node.gameOver()) return new Move(temp);

		if(!node.bisaJalan(side)) return getMin(node, !side, deep+1);

		PapanBit salin;

		int max = Integer.MIN_VALUE;

		Move move = new Move();

		Iterator iterate=node.iterator(side);

		while(iterate.hasNext())
		{
			salin=node.clone();
			byte place = (Byte) iterate.next();
			
			int x=place/8;
			int y=place%8;
			
			salin.jalankan(x,y,side);
			temp = value(salin);

			if(deep<deepLimit)
			{
				temp = getMin(salin,!side,deep+1).value;
			}

			if(temp>max)
			{
				//System.out.println("temp="+temp);
				max = temp;
				move.move = 8*x + y;
			}
		}
		move.value=max;
		return move;
	}

	Move getMin(PapanBit node, boolean side, int deep)
	{
		int temp = 0;
		
		if(node.gameOver()) return new Move(value(node));

		if(!node.bisaJalan(side)) return getMax(node, !side, deep+1);

		PapanBit salin;

		int min = Integer.MAX_VALUE;

		Move move = new Move();

		//for(int x=0;x<=7;x++) for(int y=0;y<=7;y++)
		Iterator iterate=null;
		if(side==PapanBit.BATU_PUTIH) iterate = node.jalanPutih.keySet().iterator();
		else iterate = node.jalanHitam.keySet().iterator();

		while(iterate.hasNext())
		{
			salin=node.clone();
			byte place = (Byte) iterate.next();
			
			int x=place/8;
			int y=place%8;
			
			salin.jalankan(x,y,side);
			
			if(deep>=deepLimit) temp = value(salin);
			else if(deep<deepLimit)
			{
				temp = getMax(salin,!side,deep+1).value;
			}

			if(temp<min)
			{
				//System.out.println("temp="+temp);
				min = temp;
				move.move = 8*x + y;
			}
		}
		move.value=min;
		return move;
	}
	
	public Move alphabeta(PapanBit node, boolean side)
	{
		Move m;
		if(side==PapanBit.BATU_HITAM) m = alphabetamax(papan,side,0,alpha,beta);
		else m = alphabetamin(papan,side,0,alpha,beta);
			
		//if(stage>=44) System.out.println("nilai="+m.value);
		return m;
	}

	public Move alphabetamax(PapanBit node, boolean side, int deep,int alpha,int beta)
	{
		int temp = value(node);

		if(node.gameOver()||alpha>beta) return new Move(temp);

		if(!node.bisaJalan(side)) return alphabetamin(node, !side, deep+1,alpha,beta);

		PapanBit salin;

		int max = Integer.MIN_VALUE;

		Move move = new Move();

		Iterator iterate=null;
		if(side==PapanBit.BATU_PUTIH) iterate = node.jalanPutih.keySet().iterator();
		else iterate = node.jalanHitam.keySet().iterator();

		while(iterate.hasNext())
		{
			
			salin=node.clone();
			
			byte place = (Byte) iterate.next();
			
			int x=place/8;
			int y=place%8;
			
			salin.jalankan(x,y,side);
			
			if(deep>=deepLimit) temp = value(salin);
			else if(deep<deepLimit)
			{
				temp = alphabetamin(salin,!side,deep+1,alpha,beta).value;
			}

			if(temp>max)
			{
				//System.out.println("temp="+temp);
				max = temp;
				move.move = 8*x + y;
				//table.put(salin, temp);
			}

			if(temp >= beta)
			{
				//System.out.println("beta pruning");
				break;
			}

			if(max>alpha) alpha=max;
		}
		move.value=max;
		return move;
	}

	public Move alphabetamin(PapanBit node, boolean side, int deep,int alpha,int beta)
	{
		int temp = value(node);

		if(node.gameOver()||alpha>beta) return new Move(temp);

		if(!node.bisaJalan(side)) return alphabetamax(node, !side, deep+1,alpha,beta);

		PapanBit salin;

		int min = Integer.MAX_VALUE;

		Move move = new Move();

		Iterator iterate=null;
		if(side==PapanBit.BATU_PUTIH) iterate = node.jalanPutih.keySet().iterator();
		else iterate = node.jalanHitam.keySet().iterator();

		while(iterate.hasNext())
		{
			salin=node.clone();
			
			byte place = (Byte) iterate.next();
			
			int x=place/8;
			int y=place%8;
			
			salin.jalankan(x,y,side);
			
			if(deep>=deepLimit) temp = value(salin);
			else if(deep<deepLimit)
			{
				temp = alphabetamax(salin,!side,deep+1,alpha,beta).value;
			}

			if(temp<min)
			{
				//System.out.println("temp="+temp);
				min = temp;
				move.move = 8*x + y;
				//table.put(salin, temp);
			}

			if(temp <=alpha)
			{
				//System.out.println("alpha pruning");
				break;
			}

			if(min<beta) beta=min;
		}
		move.value=min;
		return move;
	}

	public String score(PapanBit p)
	{
		int angkaPutih = p.putih.cardinality();
		int angkaHitam = p.hitam.cardinality();
		
		StringBuffer laporan = new StringBuffer();
		
		laporan.append("Batu putih ada "+angkaPutih+"\n");
		laporan.append("Batu hitam ada "+angkaHitam+"\n");

		if (angkaPutih==angkaHitam) laporan.append("Seri\n");
		else laporan.append((angkaPutih>angkaHitam? "Putih ":"Hitam ")+"Menang");
		return laporan.toString();
	}	
}

class Move
{
	public int value;
	public int move;

	public Move(int value,int move)
	{
		this.value=value;
		this.move=move;
	}
	public Move(int value)
	{
		this(value,0);
	}
	public Move()
	{
		this(0);
	}

	public String toString()
	{
		return "value="+value + " move="+move;
	}
}
