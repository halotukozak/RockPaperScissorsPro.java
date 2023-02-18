package rockpaperscissors;

import java.io.File;
import java.util.*;

public class Game {
    static Integer points;
    private static final Scanner scanner = new Scanner(System.in);

    private OPTIONS options;

    public void run() {
        init();
        while (true) {
            String input = scanner.nextLine();
            if (input.isBlank()) {
                continue;
            }
            if (input.equals("!exit")) {
                System.out.println("Bye!");
                System.exit(0);
            }
            if (input.equals("!rating")) {
                System.out.println("Your rating: " + points);
            } else if (OPTIONS.hasName(input)) {
                play(input);
            } else System.out.println("Invalid input");

        }
    }


    private void play(String input) {
        Move computer = options.getRandomOption();
        Move player = options.getMove(input);
        assert player != null;
        points += calculateResult(player, computer);
        printResult(player, computer);
    }

    private static int calculateResult(Move player, Move computer) {
        int output = 0;
        switch (player.compareTo(computer)) {
            case -1 -> output = 0;
            case 0 -> output = 50;
            case 1 -> output = 100;
        }
        return output;
    }

    private static void printResult(Move player, Move computer) {
        String output;
        switch (player.compareTo(computer)) {
            case -1 -> output = "Sorry, but the computer chose " + computer;
            case 0 -> output = "There is a draw (" + player + ")";
            case 1 -> output = "Well done. The computer chose " + computer + " and failed";
            default -> output = "You cannot see this waring";
        }
        System.out.println(output);

    }

    private void init() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.println("Hello, " + name);
        System.out.println("Type in your own moves (splited by commas) or use default ones.");

        setPoints(name);
        String op = scanner.nextLine();
        List<String> optionsInput = op.isBlank() ? List.of() : List.of(op.split(","));
        options = new OPTIONS(optionsInput);
        System.out.println("Okay, let's start");
    }

    private void setPoints(String name) {
        File file = new File("rating.txt");
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String[] line = fileScanner.nextLine().split(" ");
                if (line[0].equals(name)) {
                    points = Integer.getInteger(line[1]);
                    break;
                }
            }
            if (null == points) throw new Exception("There is no such a name");
        } catch (Exception ignored) {
            points = 350;
        }
    }

    private static class OPTIONS {
        private static final Random random = new Random();
        private static final Set<String> names = new HashSet<>();

        public OPTIONS(List<String> inputOptions) {
            if (inputOptions.isEmpty()) {
                inputOptions = List.of("rock", "paper", "scissors");
                names.addAll(inputOptions);
            }
            Move last = null;
            for (String option : inputOptions) {
                Move move = new Move(option);
                if (last != null) last.setNext(move);
                last = move;
                names.add(option);
            }
            last.next = Move.first;
        }

        public static boolean hasName(String input) {
            return names.contains(input);
        }

        public Move getRandomOption() {
            int r = random.nextInt(0, Move.getSize());
            Move tmp = Move.first;
            for (int i = 0; i <= r; i++) {
                tmp = tmp.next;
            }
            return tmp;
        }

        public Move getMove(String name) {
            if (!hasName(name)) return null;
            Move tmp = Move.first;
            while (true) {
                if (tmp.name.equals(name)) return tmp;
                tmp = tmp.next;
            }
        }
    }

    static class Move implements Comparable<Move> {
        private static Move first;
        private final String name;

        private Move next;
        private static int size = 0;

        public Move(String name) {
            this.name = name;
            if (first == null) first = this;
            size++;
        }

        public static int getSize() {
            return size;
        }

        public void setNext(Move next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Move o)) return false;
            return this.name.equals(o.name);
        }

        @Override
        public int compareTo(Move o) {
            if (this == o) return 0;
            Move tmp = this.next;
            for (int i = 1; i <= (size / 2); i++) {
                if (tmp == o) return -1;
                tmp = tmp.next;
            }
            return 1;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
