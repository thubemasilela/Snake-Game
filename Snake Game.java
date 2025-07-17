import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Random;

public class Main extends Application {
    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;

    private LinkedList<Point> snake = new LinkedList<>();
    private Point food = new Point(0, 0);
    private Direction direction = Direction.RIGHT;
    private boolean running = true;
    private Random random = new Random();

    private int score = 0;
    private int highScore = 0;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);

        Scene scene = new Scene(new javafx.scene.Group(canvas));
        scene.setOnKeyPressed(this::handleKeyPress);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();

        startGame();
        timeline.play();
    }

    private void startGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        placeFood();
        score = 0;
        running = true;
    }

    private void placeFood() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.UP && direction != Direction.DOWN) {
            direction = Direction.UP;
        } else if (event.getCode() == KeyCode.DOWN && direction != Direction.UP) {
            direction = Direction.DOWN;
        } else if (event.getCode() == KeyCode.LEFT && direction != Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (event.getCode() == KeyCode.RIGHT && direction != Direction.LEFT) {
            direction = Direction.RIGHT;
        }
    }

    private void run(GraphicsContext gc) {
        if (!running) {
            gc.setFill(Color.RED);
            gc.fillText("Game Over", WIDTH * TILE_SIZE / 2.0 - 30, HEIGHT * TILE_SIZE / 2.0);
            gc.fillText("High Score: " + highScore, WIDTH * TILE_SIZE / 2.0 - 40, HEIGHT * TILE_SIZE / 2.0 + 20);
            gc.fillText("Press ENTER to restart", WIDTH * TILE_SIZE / 2.0 - 50, HEIGHT * TILE_SIZE / 2.0 + 40);
            return;
        }

        update();
        draw(gc);
    }

    private void update() {
        Point head = snake.getFirst();
        Point newPoint = switch (direction) {
            case UP -> new Point(head.x, head.y - 1);
            case DOWN -> new Point(head.x, head.y + 1);
            case LEFT -> new Point(head.x - 1, head.y);
            case RIGHT -> new Point(head.x + 1, head.y);
        };

        // Wrap around the screen
        if (newPoint.x < 0) newPoint.x = WIDTH - 1;
        if (newPoint.x >= WIDTH) newPoint.x = 0;
        if (newPoint.y < 0) newPoint.y = HEIGHT - 1;
        if (newPoint.y >= HEIGHT) newPoint.y = 0;

        if (snake.contains(newPoint)) {
            running = false;
            if (score > highScore) {
                highScore = score;
            }
            return;
        }

        snake.addFirst(newPoint);
        if (newPoint.equals(food)) {
            score++;
            placeFood();
        } else {
            snake.removeLast();
        }
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        gc.setFill(Color.RED);
        gc.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        gc.setFill(Color.GREEN);
        for (Point point : snake) {
            gc.fillRect(point.x * TILE_SIZE, point.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 10);
        gc.fillText("High Score: " + highScore, 10, 30);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
