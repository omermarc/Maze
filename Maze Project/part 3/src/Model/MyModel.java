
package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.IServerStrategy;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
//added imports
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyModel extends Observable implements IModel {
    // addlogerr
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Maze maze;
    static Server generator_server;
    static Server solver_server;
    private boolean viwe_soltion;
    private Position my_Position;
    private Position StartPosition;
    private Position EndPosition;
    private Solution mazeSolution;
    private int characterPositionRow;
    private int characterPositionColumn;
    //added for logs
    private  static org.apache.log4j.Logger loggerStart= org.apache.log4j.Logger.getLogger("start");
    private  static org.apache.log4j.Logger loggerEnd= org.apache.log4j.Logger.getLogger("end");

    public MyModel() {
        this.maze=null;
        generator_server = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solver_server = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        Connect_Servers();
        this.my_Position=null;
        this.StartPosition=null;
        this.EndPosition=null;
        this.mazeSolution=null;
        this.characterPositionRow = 1;
        this.characterPositionColumn = 1;

    }

    public static void Connect_Servers(){
        // if(server instanceof ServerStrategyGenerateMaze) {
        generator_server.start();
        // }
        //else if(server instanceof ServerStrategySolveSearchProblem){
        solver_server.start();
        // }solver_server.start();

        //added
        SimpleLayout simple= new SimpleLayout();
        Appender appender= null;
        try {
            appender= new FileAppender(simple,"logs/LogGenerate");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        loggerStart.setLevel(Level.INFO);
        loggerStart.addAppender(appender);
        //untill here

    }

    private void CommunicateWithServer_SolveSearchProblem() {
        if (!viwe_soltion) {
            IServerStrategy server = new ServerStrategySolveSearchProblem();
            // Connect_Servers(server);
            try {
                Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                    @Override
                    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                        try {
                            ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                            ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                            toServer.flush();
                            toServer.writeObject(maze);
                            toServer.flush();
                            mazeSolution = (Solution) fromServer.readObject();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                client.communicateWithServer();
                ArrayList<AState> SolPath = mazeSolution.getSolutionPath();
                for (int i = 0; i < SolPath.size()-1; i++) {
                    MazeState tmp = (MazeState) SolPath.get(i);
                    Position tmpPos = tmp.getmyPosition();
                    maze.getMaze()[tmpPos.getRowIndex()][tmpPos.getColumnIndex()] = 5;
                }
                viwe_soltion = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();

            }
        } else {
            ArrayList<AState> SolPath = mazeSolution.getSolutionPath();
            for (int i = 0; i < SolPath.size()-1; i++) {
                MazeState tmp = (MazeState) SolPath.get(i);
                Position tmpPos = tmp.getmyPosition();
                maze.getMaze()[tmpPos.getRowIndex()][tmpPos.getColumnIndex()] = 0;
            }
            viwe_soltion = false;
            if (solver_server != null)
                solver_server.stop();
        }

    }



    public void pushedSave(File file) throws IOException {
        byte [] ByteMaze = maze.toByteArray();
        byte [] saveMaze = new byte[maze.toByteArray().length + 2];
        for(int i=0;i<ByteMaze.length;i++){
            saveMaze[i]=ByteMaze[i];
        }
        saveMaze[ByteMaze.length]= (byte) my_Position.getRowIndex();
        saveMaze[ByteMaze.length+1]= (byte) my_Position.getColumnIndex();
        try{
            file.createNewFile();
            FileOutputStream SaveMe = new FileOutputStream(file);
            SaveMe.write(saveMaze);
        }
        catch (IOException e){}

    }

    public void pushedLoad(File file) throws IOException, ClassNotFoundException {
        byte[] LoadedBytes = Files.readAllBytes(file.toPath());
        byte[] ByteMaze = new byte[LoadedBytes.length-2];
        for (int i = 0; i < ByteMaze.length; i++) {
            ByteMaze[i] = LoadedBytes[i];
        }
        Maze Loadmaze = new Maze(ByteMaze);
        this.maze = Loadmaze;
        EndPosition = maze.getGoalPosition();
        maze.getMaze()[EndPosition.getRowIndex()][EndPosition.getColumnIndex()]=4;
        characterPositionRow = (int)LoadedBytes[ByteMaze.length];
        characterPositionColumn = (int)LoadedBytes[ByteMaze.length+1];
        setChanged();
        notifyObservers();
    }

    private void CommunicateWithServer_MazeGenerating(int row,int col) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[col*row +12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        my_Position = maze.getStartPosition();
                        StartPosition = maze.getStartPosition();
                        EndPosition = maze.getGoalPosition();
                        maze.getMaze()[EndPosition.getRowIndex()][EndPosition.getColumnIndex()] = 4;
                        characterPositionColumn = my_Position.getColumnIndex();
                        characterPositionRow = my_Position.getRowIndex();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int[][] getMaze() {
        return maze.getMaze();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public void close() {
        if (generator_server != null)
            generator_server.stop();
        if (solver_server != null)
            solver_server.stop();
        threadPool.shutdown();

    }



    @Override
    public void generateMaze(int width, int height) {
        viwe_soltion = false;
        CommunicateWithServer_MazeGenerating(width, height);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //added information about start time
        String ro= Integer.toString(width);
        String co= Integer.toString(height);
        Date date= new Date();
        String date1= date.toString();
        String mazeProp="maze with properties of"+" "+ ro+","+ co+ " been started "+"at:"+date1;
        loggerStart.info(mazeProp);
        //untill here

        setChanged();
        notifyObservers(maze);
    }

    public Solution SolveMaze() {
        CommunicateWithServer_SolveSearchProblem();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
        return mazeSolution;
    }

    public boolean missionaccomplished() {
        if (characterPositionRow == maze.getGoalPosition().getRowIndex() && characterPositionColumn == maze.getGoalPosition().getColumnIndex()){
            //added information, notice we have duplicated data *3
            Date date= new Date();
            loggerStart.info("  Mission accomplished at :"+date.toString());
            return true;
        }

        return false;
    }


    public void moveCharacter(KeyCode movement) {
        boolean rightKey = false;
        switch (movement) {
            case NUMPAD8://up
                if(checkStep(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case UP://up
                if(checkStep(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD2://down
                if(checkStep(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case DOWN://down
                if(checkStep(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD6://right
                if(checkStep(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case RIGHT://right
                if(checkStep(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD4://left
                if(checkStep(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    rightKey = true;
                    missionaccomplished();
                }
                break;

            case LEFT://left
                if(checkStep(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD9://diagonal - up right
                if(checkStep(characterPositionRow-1,characterPositionColumn+1)) {
                    characterPositionRow--;
                    characterPositionColumn++;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD7://diagonal - up left
                if(checkStep(characterPositionRow-1,characterPositionColumn-1)) {
                    characterPositionRow--;
                    characterPositionColumn--;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD3://diagonal - down right
                if(checkStep(characterPositionRow+1,characterPositionColumn+1)) {
                    characterPositionRow++;
                    characterPositionColumn++;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
            case NUMPAD1://diagonal - down left
                if(checkStep(characterPositionRow+1,characterPositionColumn-1)) {
                    characterPositionRow++;
                    characterPositionColumn--;
                    rightKey = true;
                    missionaccomplished();
                }
                break;
        }
        if(rightKey){
            Position newStart = new Position(characterPositionRow,characterPositionColumn);
            maze.setStart(newStart);
            setChanged();
            missionaccomplished();
            notifyObservers();
        }
    }
    private boolean checkStep(int row,int col) {
        if (row<this.maze.getRows() && row>=0 && col<this.maze.getColumns() && col>=0 &&( this.maze.getval(row, col) == 0|| this.maze.getval(row, col) == 4))
            return true;
        return false;
    }
}
