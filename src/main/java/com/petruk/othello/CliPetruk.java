package com.petruk.othello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Command-line front end for human games and self-play. */
public final class CliPetruk
{
    private CliPetruk() {}

    public static void main(String[] args) throws IOException
    {
        boolean selfPlay = args.length > 0 && "--cli-selfplay".equals(args[0]);
        boolean computerBlack = args.length > 1 && "black".equalsIgnoreCase(args[1]);
        if (selfPlay) selfPlay();
        else play(computerBlack);
    }

    public static void play(boolean computerBlack) throws IOException
    {
        PapanBit board = new PapanBit();
        PetrukBit engine = new PetrukBit();
        boolean computer = computerBlack ? PapanBit.BATU_HITAM : PapanBit.BATU_PUTIH;
        boolean side = PapanBit.BATU_HITAM;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Petruk Othello CLI");
        System.out.println("Enter moves as a letter and row, for example d3. Type q to quit.");

        while (!finished(board))
        {
            printBoard(board, side);
            if (!board.bisaJalan(side))
            {
                System.out.println(color(side) + " has no legal move; passing.");
                side = !side;
                continue;
            }

            if (side == computer)
            {
                Move move = engine.alphabeta(board, side);
                board.jalankan(move.move / 8, move.move % 8, side);
                System.out.println("Computer " + color(side) + " plays " + formatMove(move.move));
            }
            else
            {
                System.out.print("Your move (" + color(side) + "): ");
                String line = input.readLine();
                if (line == null || "q".equalsIgnoreCase(line.trim())) return;
                int move = parseMove(line);
                if (move < 0 || !board.bisaJalan(move / 8, move % 8, side))
                {
                    System.out.println("Invalid move. Choose one of the marked squares.");
                    continue;
                }
                board.jalankan(move / 8, move % 8, side);
            }
            side = !side;
        }

        printBoard(board, side);
        System.out.println("Game over.");
        System.out.println(engine.score(board));
    }

    public static void selfPlay()
    {
        PapanBit board = new PapanBit();
        PetrukBit engine = new PetrukBit();
        boolean side = PapanBit.BATU_HITAM;
        int turn = 1;

        while (!finished(board))
        {
            if (!board.bisaJalan(side))
            {
                System.out.println("Turn " + turn + ": " + color(side) + " passes.");
                side = !side;
                turn++;
                continue;
            }

            Move move = engine.alphabeta(board, side);
            board.jalankan(move.move / 8, move.move % 8, side);
            System.out.println("Turn " + turn + ": " + color(side) + " plays " +
                formatMove(move.move) + " (evaluation " + move.value + ")");
            printBoard(board, !side);
            side = !side;
            turn++;
        }

        System.out.println("Self-play complete.");
        System.out.println(engine.score(board));
    }

    private static boolean finished(PapanBit board)
    {
        return board.gameOver() ||
            (!board.bisaJalan(PapanBit.BATU_HITAM) && !board.bisaJalan(PapanBit.BATU_PUTIH));
    }

    private static void printBoard(PapanBit board, boolean side)
    {
        System.out.println("  a b c d e f g h");
        for (int y = 0; y < 8; y++)
        {
            StringBuilder line = new StringBuilder();
            line.append(y + 1).append(' ');
            for (int x = 0; x < 8; x++)
            {
                int position = x * 8 + y;
                char stone = '.';
                if (board.hitam.get(position)) stone = '#';
                else if (board.putih.get(position)) stone = 'O';
                else if (board.bisaJalan(x, y, side)) stone = '*';
                line.append(stone).append(' ');
            }
            System.out.println(line);
        }
        System.out.println("# black, O white, * legal for " + color(side));
    }

    private static int parseMove(String text)
    {
        String move = text.trim().toLowerCase();
        if (move.length() != 2) return -1;
        char column = move.charAt(0);
        char row = move.charAt(1);
        if (column < 'a' || column > 'h' || row < '1' || row > '8') return -1;
        return (column - 'a') * 8 + (row - '1');
    }

    private static String formatMove(int move)
    {
        return "" + (char) ('a' + move / 8) + (char) ('1' + move % 8);
    }

    private static String color(boolean side)
    {
        return side == PapanBit.BATU_HITAM ? "black" : "white";
    }
}
