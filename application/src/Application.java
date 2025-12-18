import nl.saxion.app.SaxionApp;

public class Application implements Runnable{
    public static void main(String[] args){
        SaxionApp.start(new Application());
    }

    public void run(){
        SaxionApp.printLine("Hello World!");
    }
}

