package com.petruk.othello;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/** Search engine for the Othello player. Scores are always from black's perspective. */
public final class PetrukBit
{
    private final int[][] evaluator = new int[8][8];
    int deepLimit = 6;
    int stage = 0;
    PapanBit papan;

    public PetrukBit()
    {
        String values =
            " 64 -30  10   5   5  10 -30  64 " +
            "-30 -40   2   2   2   2 -40 -30 " +
            " 10   2   5   1   1   5   2  10 " +
            "  5   2   1   0   0   1   2   5 " +
            "  5   2   1   0   0   1   2   5 " +
            " 10   2   5   1   1   5   2  10 " +
            "-30 -40   2   2   2   2 -40 -30 " +
            " 64 -30  10   5   5  10 -30  64 ";

        StringTokenizer tokens = new StringTokenizer(values);
        int index = 0;
        while (tokens.hasMoreTokens())
        {
            evaluator[index / 8][index % 8] = Integer.parseInt(tokens.nextToken());
            index++;
        }
        papan = new PapanBit();
    }

    /** Evaluates a position from black's perspective. */
    public int value(PapanBit p)
    {
        int black = p.hitam.cardinality();
        int white = p.putih.cardinality();
        int discDifference = black - white;
        int occupied = black + white;
        if (p.gameOver()) return discDifference * 10000;

        int positional = 0;
        for (int i = p.hitam.nextSetBit(0); i >= 0; i = p.hitam.nextSetBit(i + 1))
            positional += evaluator[i / 8][i % 8];
        for (int i = p.putih.nextSetBit(0); i >= 0; i = p.putih.nextSetBit(i + 1))
            positional -= evaluator[i / 8][i % 8];

        int mobility = p.mobility(PapanBit.BATU_HITAM) - p.mobility(PapanBit.BATU_PUTIH);
        int corners = cornerCount(p, PapanBit.BATU_HITAM) - cornerCount(p, PapanBit.BATU_PUTIH);
        int frontier = frontierCount(p, PapanBit.BATU_HITAM) - frontierCount(p, PapanBit.BATU_PUTIH);

        if (occupied < 20) return positional + mobility * 8 + corners * 100 - frontier * 3;
        if (occupied < 50) return positional + mobility * 5 + corners * 120 - frontier * 5 + discDifference;
        return positional + mobility * 2 + corners * 150 - frontier * 8 + discDifference * 8;
    }

    private int cornerCount(PapanBit p, boolean side)
    {
        BitSet stones = side == PapanBit.BATU_HITAM ? p.hitam : p.putih;
        int count = 0;
        if (stones.get(0)) count++;
        if (stones.get(7)) count++;
        if (stones.get(56)) count++;
        if (stones.get(63)) count++;
        return count;
    }

    private int frontierCount(PapanBit p, boolean side)
    {
        BitSet stones = side == PapanBit.BATU_HITAM ? p.hitam : p.putih;
        int count = 0;
        for (int i = stones.nextSetBit(0); i >= 0; i = stones.nextSetBit(i + 1))
        {
            int x = i / 8;
            int y = i % 8;
            boolean frontier = false;
            for (int dx = -1; dx <= 1 && !frontier; dx++)
                for (int dy = -1; dy <= 1; dy++)
                {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 &&
                        !p.hitam.get(nx * 8 + ny) && !p.putih.get(nx * 8 + ny))
                    {
                        frontier = true;
                        break;
                    }
                }
            if (frontier) count++;
        }
        return count;
    }

    private List<Byte> orderedMoves(final PapanBit node, boolean side)
    {
        final List<Byte> moves = new ArrayList<Byte>();
        Iterator iterator = node.iterator(side);
        while (iterator.hasNext()) moves.add((Byte) iterator.next());
        Collections.sort(moves, new Comparator<Byte>()
        {
            public int compare(Byte first, Byte second)
            {
                return moveOrderingScore(second) - moveOrderingScore(first);
            }
        });
        return moves;
    }

    private int moveOrderingScore(byte move)
    {
        int position = move & 0xff;
        if (position == 0 || position == 7 || position == 56 || position == 63) return 10000;
        int x = position / 8;
        int y = position % 8;
        if ((x == 1 || x == 6) && (y == 1 || y == 6)) return -1000;
        return evaluator[x][y];
    }

    public Move alphabeta(PapanBit node, boolean side)
    {
        if (side == PapanBit.BATU_HITAM)
            return alphabetamax(node, side, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return alphabetamin(node, side, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Move alphabetamax(PapanBit node, boolean side, int deep, int alpha, int beta)
    {
        int current = value(node);
        if (node.gameOver() || deep >= deepLimit) return new Move(current);
        if (!node.bisaJalan(side))
        {
            if (!node.bisaJalan(!side)) return new Move(current);
            return alphabetamin(node, !side, deep + 1, alpha, beta);
        }

        int best = Integer.MIN_VALUE;
        Move move = new Move(current);
        for (Byte place : orderedMoves(node, side))
        {
            PapanBit next = node.clone();
            next.jalankan(place / 8, place % 8, side);
            int score = alphabetamin(next, !side, deep + 1, alpha, beta).value;
            if (score > best)
            {
                best = score;
                move.move = place;
            }
            if (best > alpha) alpha = best;
            if (alpha >= beta) break;
        }
        move.value = best;
        return move;
    }

    public Move alphabetamin(PapanBit node, boolean side, int deep, int alpha, int beta)
    {
        int current = value(node);
        if (node.gameOver() || deep >= deepLimit) return new Move(current);
        if (!node.bisaJalan(side))
        {
            if (!node.bisaJalan(!side)) return new Move(current);
            return alphabetamax(node, !side, deep + 1, alpha, beta);
        }

        int best = Integer.MAX_VALUE;
        Move move = new Move(current);
        for (Byte place : orderedMoves(node, side))
        {
            PapanBit next = node.clone();
            next.jalankan(place / 8, place % 8, side);
            int score = alphabetamax(next, !side, deep + 1, alpha, beta).value;
            if (score < best)
            {
                best = score;
                move.move = place;
            }
            if (best < beta) beta = best;
            if (alpha >= beta) break;
        }
        move.value = best;
        return move;
    }

    public String score(PapanBit p)
    {
        int white = p.putih.cardinality();
        int black = p.hitam.cardinality();
        StringBuffer report = new StringBuffer();
        report.append("Batu putih ada ").append(white).append("\n");
        report.append("Batu hitam ada ").append(black).append("\n");
        if (white == black) report.append("Seri\n");
        else report.append(white > black ? "Putih Menang" : "Hitam Menang");
        return report.toString();
    }
}

class Move
{
    public int value;
    public int move;
    public Move(int value, int move) { this.value = value; this.move = move; }
    public Move(int value) { this(value, 0); }
    public Move() { this(0); }
    public String toString() { return "value=" + value + " move=" + move; }
}
