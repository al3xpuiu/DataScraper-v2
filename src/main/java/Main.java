import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by Loky on 20/04/2018.
 */
public class Main {

    private static ScheduledExecutorService scheduledExecutorService = null;
    private static List<String> finishedUrls = new ArrayList<>(  );

    public static void main(String[] args) {

        long start1 = System.currentTimeMillis();

        Queue<String> urls = new ConcurrentLinkedQueue<>(  );

        try (BufferedReader reader = new BufferedReader(
                                        new FileReader( "E:\\Java Stuff\\Projects\\Workspace\\DataScramblerOrto\\src\\main\\resources\\test\\urls_test" )
        );
             BufferedReader finishedUrlsReader = Files.newBufferedReader( Paths.get("E:\\Java Stuff\\Projects\\Workspace\\DataScramblerOrto\\src\\main\\resources\\finishedUrls") )){


            String line;

            while ((line = finishedUrlsReader.readLine()) != null) {
                finishedUrls.add( line );
            }

            while ((line = reader.readLine()) != null) {
                if (!finishedUrls.contains( line ))
                    urls.add( line.trim() );
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

        try {

            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay( () -> {
                while (!ToDataBaseWriter.getContactsQueue().isEmpty())
                    ToDataBaseWriter.write( ToDataBaseWriter.getContactsQueue().poll() );

            }, 10000, 100, TimeUnit.MILLISECONDS );

            while (!urls.isEmpty()) {

                long start2 = System.currentTimeMillis();

                new WebDataScraper( ).dataScramble( urls );

                long end2 = (System.currentTimeMillis() - start2) / 1000;
                System.out.println( "Stage finished in : " + end2 / 60 + " minutes" );

            }

            System.out.println("Waiting 10 seconds for the database writer to start");

            Thread.sleep( 10000 );

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Waiting 10 seconds for the database writer to start");
            try {
                Thread.sleep( 10000 );
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }

        } finally {
            if (scheduledExecutorService != null)
                scheduledExecutorService.shutdown();
        }

            long end1 = (System.currentTimeMillis() - start1) / 1000;

            System.out.println( "Program finished in : " + end1 / 60 + " minutes" );



    }

    public static List<String> getFinishedUrls() {
        return finishedUrls;
    }
}
