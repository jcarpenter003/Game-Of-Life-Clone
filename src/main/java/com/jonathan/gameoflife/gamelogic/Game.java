package com.jonathan.gameoflife.gamelogic;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

/*
       -- Potential New Features --
       Configurable grid size
       A menu with preloadable board setups (from well known seeds)

*/

public class Game extends Application {

    private static final int width = 500;
    private static final int height = 500;
    private static final int cellSize = 10;

    private Random random = new Random();

    private int rows = (int) Math.floor(height / cellSize);
    private int cols = (int) Math.floor(width / cellSize);
    private int[][] cells = new int[rows][cols];

    private GraphicsContext gc;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage)
    {
        VBox root = new VBox(10);
        Scene scene = new Scene(root, width, height + 100);
        final Canvas canvas = new Canvas(width, height);

        canvas.setOnMouseClicked(l -> FillCellClickHandler(l));

        gc = canvas.getGraphicsContext2D();

        Button randomButton = new Button("Randomize");
        Button clearButton = new Button("Clear");
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button stepButton = new Button("Step");

        randomButton.setOnAction(l -> RandomizeGrid());
        clearButton.setOnAction(l -> ResetGameAndBoard());
        stepButton.setOnAction(l -> Tick());

        gc.setFill(Color.ORANGE);   // Fill color is for rectangle drawing
        gc.setStroke(Color.BLACK); // Stroke color is for line drawing
        DrawGridGraphic();


        AnimationTimer timer = new AnimationTimer() {
           Instant then = Instant.now();
           Duration duration = Duration.ofMillis(500);

            @Override
            public void handle(long now) {
                if(Duration.between(then, Instant.now()).toMillis() > duration.toMillis()) // Timey wimey type stuff
                {
                    Tick();
                    then = Instant.now();
                }
            }
        };
        startButton.setOnAction(l -> timer.start());
        stopButton.setOnAction(l -> timer.stop());

        root.getChildren().addAll(canvas, new HBox(10, randomButton, clearButton, startButton, stopButton, stepButton));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void FillCellClickHandler(MouseEvent event)
    {
        // What this essentially does is round the click coordinates to the lower 10, then divides by 10 to get the array indexes
        int remainderX =  (int) event.getX() % 10;
        int remainderY =  (int) event.getY() % 10;
        int x = (int) (event.getX() - remainderX) / 10;
        int y = (int) (event.getY() - remainderY) / 10;

        if(cells[x][y] == 1)
        {
            cells[x][y] = 0;
        }
        else
        {
            cells[x][y] = 1;
        }

        FillCells();
    }

    public void DrawGridGraphic()
    {
        for(int x = 1; x < 500; x += cellSize)
        {
            gc.strokeLine(1, x, width, x); // Draw the Rows
            gc.strokeLine(x, 1, x, height); // Draw the Columns
        }

        gc.strokeLine(0, 500, width, 500); // Bottom Grid Edge
        gc.strokeLine(500, 0, 500, height); // Right Grid Edge
    }

    public void RandomizeGrid()
    {
        ResetGameAndBoard();

        for(int row = 0; row < rows; row++)
        {
            for(int col = 0; col < cols; col++)
            {
                if(random.nextInt(2 ) == 1)
                {
                    cells[row][col] = random.nextInt(2);
                }
                else
                {
                    cells[row][col] = 0;
                }
            }
        }
        FillCells();
    }

    public void FillCells()
    {
        gc.clearRect(0,0,height + 10,width + 10); // Clear the grid graphically
        for(int row = 0; row < cells.length; row++)
        {
            for(int col = 0; col < cells.length; col++)
            {
                if(cells[row][col] == 1)
                {
                    gc.fillRect(row * cellSize, col * cellSize, cellSize, cellSize);
                }
            }
        }
        DrawGridGraphic();
    }

    public void ResetGameAndBoard()
    {
        cells = new int[rows][cols];
        gc.clearRect(0,0,height + 10,width + 10);
        DrawGridGraphic();
    }

    public void Tick()
    {
        int[][] next = new int[rows][cols];

        for(int r = 0; r < cells.length; r++)
        {
            for(int c = 0; c < cells[r].length; c++)
            {
                int neighbors = GetNeighborCount(r, c);
                if(neighbors == 3)
                {
                    next[r][c] = 1;
                } else if(neighbors < 2 || neighbors > 3)
                {
                    next[r][c] = 0;
                } else
                {
                    next[r][c] = cells[r][c];
                }
            }
        }

        ResetGameAndBoard();
        cells = next;
        FillCells();
    }

    private int GetNeighborCount(int row, int column)
    {
        int neighborCount = 0;

        int startRow = row == 0 ? 1 : row;
        int startCol = column == 0 ? 1 : column;
        int endRow = row == 49 ? 48 : row;
        int endCol = column == 49 ? 48 : column;

        for(int r = startRow - 1; r <  endRow + 2; r++ )
        {
            for(int c = startCol - 1; c < endCol + 2; c++)
            {
                neighborCount += cells[r][c];
            }
        }

        neighborCount -= cells[row][column];
        return neighborCount;
    }

//    public void test()
//    {
//        try {
//            FileOutputStream fos = new FileOutputStream("C:\\Users\\********\\Downloads\\test.dat");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(cells);
//
//        } catch (Exception exception)
//        {
//        System.out.println("ooooooooooooooooooooooooooooooooooooooo weeeee");
//        }
//    }

//    public void initfromfile()
//    {
//        try {
//            FileInputStream fis = new FileInputStream("C:\\Users\\**********\\Downloads\\test.dat");
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            cells = (int[][]) ois.readObject();
//        }
//        catch (Exception ex)
//        {
//            System.out.println("OOOOOOOOOOOOOweeeeeeeeeeeee");
//        }
//        FillCells();
//    }
}
