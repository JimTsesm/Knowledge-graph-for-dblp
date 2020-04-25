import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Abox {

    public static void transformPaper() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read Papers from Edition csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.edition_paper_input));
        // skip the header line that includes the
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[1].replace("/","_");
            String title = row_data[4];
            String year = row_data[5];

            String paperUri = key.replace(" ","_");
            Resource paper = model.createResource(config.RESOURCE_URL+paperUri)
                    .addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Paper"))
                    .addProperty(model.createProperty(config.BASE_URL+"key"),key)
                    .addProperty(model.createProperty(config.BASE_URL+"title"),title)
                    .addProperty(model.createProperty(config.BASE_URL+"year"),year);
        }
        csvReader.close();

        // read Papers from Volume csv
        csvReader = new BufferedReader(new FileReader(config.volume_paper_input));
        // skip the header line that includes the
        csvReader.readLine();
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[0].replace("/","_");;
            String title = row_data[1];
            String year = row_data[2];

            String paperUri = key.replace(" ","_");
            Resource paper = model.createResource(config.RESOURCE_URL+paperUri)
                    .addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Paper"))
                    .addProperty(model.createProperty(config.BASE_URL+"key"),key)
                    .addProperty(model.createProperty(config.BASE_URL+"title"),title)
                    .addProperty(model.createProperty(config.BASE_URL+"year"),year);
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"paper.nt")), true), "NT");
    }

    public static void transformAuthor() throws IOException, URISyntaxException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.author_input));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String author_name = row_data[0];
            String affiliated_to = row_data[1].replace(" ","_");

            String authorUri = author_name.replace(" ","_");

            URI uri = null;
            try {
            URL url = new URL(config.RESOURCE_URL+authorUri);
            String nullFragment = null;
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            } catch (MalformedURLException e) {
                System.out.println("URL " + authorUri + " is a malformed URL");
            } catch (URISyntaxException e) {
                System.out.println("URI " + authorUri + " is a malformed URL");
            }

            Resource author = model.createResource(config.RESOURCE_URL+uri.toString())
                    .addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Author"))
                    .addProperty(model.createProperty(config.DBO_URL+"birthName"),author_name)
                    .addProperty(model.createProperty(config.BASE_URL+"affiliated_to"),model.createResource(config.RESOURCE_URL+affiliated_to));
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"author.nt")), true), "NT");
    }

    public static void transformUniversity() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.university_input));
        // skip the header line that includes the
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String country = row_data[0];
            String university_name = row_data[1];

            String universityUri = university_name.replace(" ","_").replace("\"","");
            Resource author = model.createResource(config.RESOURCE_URL+universityUri)
                    .addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.DBO_URL+"University"))
                    .addProperty(model.createProperty(config.DBO_URL+"foundationPlace"),country)
                    .addProperty(model.createProperty(config.BASE_URL+"university_name"),university_name);
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"university.nt")), true), "NT");
    }

    public static void link_authors_paper() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.writes_input));
        // skip the header line that includes the
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[1].replace("/","_");
            String author = row_data[2];

            String writesUri = author.replace(" ","_");
            URI uri = null;
            try {
                URL url = new URL(config.RESOURCE_URL+writesUri);
                String nullFragment = null;
                uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            } catch (MalformedURLException e) {
                System.out.println("URL " + writesUri + " is a malformed URL");
            } catch (URISyntaxException e) {
                System.out.println("URI " + writesUri + " is a malformed URL");
            }

            Resource writes = model.createResource(config.RESOURCE_URL+uri)
                    .addProperty(model.createProperty(config.BASE_URL+"writes"),model.createResource(config.RESOURCE_URL+key));
        }
        csvReader.close();

        // read csv
        csvReader = new BufferedReader(new FileReader(config.writes_input2));
        // skip the header line that includes the
        csvReader.readLine();
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[0].replace("/","_");
            String author = row_data[1];

            String writesUri = author.replace(" ","_");
            URI uri = null;
            try {
                URL url = new URL(config.RESOURCE_URL+writesUri);
                String nullFragment = null;
                uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            } catch (MalformedURLException e) {
                System.out.println("URL " + writesUri + " is a malformed URL");
            } catch (URISyntaxException e) {
                System.out.println("URI " + writesUri + " is a malformed URL");
            }
            Resource writes = model.createResource(config.RESOURCE_URL+uri)
                    .addProperty(model.createProperty(config.BASE_URL+"writes"),model.createResource(config.RESOURCE_URL+key));
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"link_author_paper.nt")), true), "NT");
    }

    public static void transformKeyword() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.keywords_input));
        // skip the header line that includes the
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {

            String keywordUri = row.replace(" ","_");
            Resource keyword = model.createResource(config.RESOURCE_URL+keywordUri)
                    .addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Keyword"))
                    .addProperty(model.createProperty(config.BASE_URL+"keyword_name"),row);
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"keyword.nt")), true), "NT");
    }

    public static void link_paper_keyword() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.link_paper_keyword_input));
        // skip the header line that includes the
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[0].replace("/","_");
            String keyword1 = row_data[1].replace(" ","_");
            String keyword2 = row_data[2].replace(" ","_");
            String keyword3 = row_data[3].replace(" ","_");

            String containsUri = key.replace(" ","_");
            Resource contains = model.createResource(config.RESOURCE_URL+containsUri)
                    .addProperty(model.createProperty(config.BASE_URL+"contains"),model.createResource(config.RESOURCE_URL+keyword1))
                    .addProperty(model.createProperty(config.BASE_URL+"contains"),model.createResource(config.RESOURCE_URL+keyword2))
                    .addProperty(model.createProperty(config.BASE_URL+"contains"),model.createResource(config.RESOURCE_URL+keyword3));
        }
        csvReader.close();

        csvReader = new BufferedReader(new FileReader(config.link_paper_keyword_input2));
        // skip the header line that includes the
        csvReader.readLine();
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[0].replace("/","_");
            String keyword1 = row_data[1].replace(" ","_");
            String keyword2 = row_data[2].replace(" ","_");
            String keyword3 = row_data[3].replace(" ","_");

            String containsUri = key.replace(" ","_");
            Resource contains = model.createResource(config.RESOURCE_URL+containsUri)
                    .addProperty(model.createProperty(config.BASE_URL+"contains"),model.createResource(config.RESOURCE_URL+keyword1))
                    .addProperty(model.createProperty(config.BASE_URL+"contains"),model.createResource(config.RESOURCE_URL+keyword2))
                    .addProperty(model.createProperty(config.BASE_URL+"contains"),model.createResource(config.RESOURCE_URL+keyword3));
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"link_paper_keyword.nt")), true), "NT");
    }

    //Ali's Part

}
