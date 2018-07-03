import contact.ContactFactory;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.*;


/**
 * Created by Loky on 19/04/2018.
 */
public class WebDataScrambler {

    private List<Element> elements;

    private String contactMainBlockCSSQuery = "example";
    private String contactNameCSSQuery = "example";
    private String contactAddressCSSQuery = "example";
    private String contactPhoneNumberCSSQuery = "example";
    private String contactWebAddressCSSQuery = "example";


    public WebDataScrambler() {
        this.elements = new ArrayList<>();
    }

    public void dataScramble(Queue<String> urls) throws IOException {

        IOException exception = null;
        int i = 1;

        //We read no more then 20 URLs at a time
        while (!urls.isEmpty() && i <= 20) {
            String url = urls.poll();
            try {
                extractMainElements( url );
            } catch (IOException e) {
                exception = e;
                break;
            }
            i++;
            try (BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get( "E:\\Java Stuff\\Projects\\Workspace\\DataScramblerOrto\\src\\main\\resources\\finishedUrls" ), Charset.defaultCharset(), StandardOpenOption.APPEND )) {
                writer.write( url );
                writer.newLine();
                writer.flush();
            }
            Main.getFinishedUrls().add( url );
        }

        System.out.println( "Total number of elements collected = " + elements.size() );


        ForkJoinTask<?> task = new DataProcessingAction( 0, elements.size(), elements );
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke( task );

        if (exception != null) throw new IOException( exception );

    }

    private void extractMainElements(String url) throws IOException {

        Document doc;
        try {
            doc = Jsoup.connect( url )
                    .userAgent( "Mozilla/5.0" )
                    .referrer( "https://google.com/" )
                    .timeout( 15000 )
                    .followRedirects( true )
                    .get();

            System.out.println( "==============================================" );
            System.out.println( "Starting url: " + url );
            Random random = new Random();
            int i = 0;

            //We wait 15 to 25 seconds between each connection
            while (i < 15) {
                i = random.nextInt( 20 );
            }
            int sleepTime = i * 1000;
            System.out.println( "Sleeping for " + i + " seconds" );
            Thread.sleep( sleepTime );

            //In case the program throws an IOException we want to stop, but controlled
            //to make sure that what was extracted until now is written to the database.
            //The exception is rethrown in this block and after the task that is sent to the ForkJoinPool is invoked.
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println( "Was a problem with the url: " + url );

            try (BufferedWriter writer = Files.newBufferedWriter( Paths.get( "E:\\Java Stuff\\Projects\\Workspace\\DataScramblerOrto\\src\\main\\resources\\urlsWithProblems" ), Charset.defaultCharset(), StandardOpenOption.APPEND )) {
                writer.write( url );
                writer.newLine();
                writer.flush();

                throw new IOException( e );
            }
        }

        Elements temp = null;
        if (doc != null) temp = doc.select( contactMainBlockCSSQuery );
        if (temp != null && temp.size() > 0) elements.addAll( temp );

        System.out.println( "Url: " + url + " = finished." );
    }

    private class DataProcessingAction extends RecursiveAction {

        private int start;
        private int end;
        private List<Element> webElements;

        private DataProcessingAction(int start, int end, List<Element> webElements) {
            this.start = start;
            this.end = end;
            this.webElements = webElements;
        }

        @Override
        protected void compute() {
            if (end - start <= 50) {
                findDataAndAddToQueue( webElements );
            } else {
                int middle = (start + end) / 2;
                invokeAll( new DataProcessingAction( start, middle, webElements ),
                        new DataProcessingAction( middle, end, webElements ) );
            }
        }

        private void findDataAndAddToQueue(List<Element> elements) {

            elements.stream()
                    .filter( w -> elements.indexOf( w ) >= start && elements.indexOf( w ) < end )
                    .map( element -> {

                        //Collect the contact data from the elements
                        Queue<String> clientData = element.select( contactNameCSSQuery + ", " + contactAddressCSSQuery + ", " + contactPhoneNumberCSSQuery )
                                .stream()
                                .map( e -> e.text().trim() )
                                .limit( 4 )
                                .collect( Collectors.toCollection( ArrayDeque::new ) );

                        element.select( contactWebAddressCSSQuery )
                                .forEach( e -> {
                                    String s = e.attr( "href" );
                                    if (s != null) {
                                        clientData.add( s );
                                    }
                                } );
                        return ContactFactory.getContact( clientData );
                    } )
                    .forEach( c -> {
                        ContactsQueue.getInstance().getContactsQueue().offer( c );
                    } );
        }
    }
}
