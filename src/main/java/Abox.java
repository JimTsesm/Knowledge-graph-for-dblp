import org.apache.jena.rdf.model.Literal;
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
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Paper"))
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
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Paper"))
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
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Author"))
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
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.DBO_URL+"University"))
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
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Keyword"))
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

    public static void transformCitation() throws IOException, URISyntaxException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.citation_input));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String paper = row_data[0].replace("/","_");
            String citation_id = row_data[1];

            String citationUri = "citation_"+citation_id;

            Resource author = model.createResource(config.RESOURCE_URL+citationUri.toString())
                    .addProperty(model.createProperty(config.BASE_URL+"citation_id"),citation_id)
                    .addProperty(model.createProperty(config.BASE_URL+"cites"),model.createResource(config.RESOURCE_URL+paper));
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"citations.nt")), true), "NT");
    }

    public static void transformReviews() throws IOException, URISyntaxException {

        Model model = ModelFactory.createDefaultModel();
        // read csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.review_input));
        String row;
        int row_count = 0;
        while ((row = csvReader.readLine()) != null) {
            row_count++;
            String[] row_data = row.split(",");
            String paper = row_data[0].replace("/","_");
            String reviewer = row_data[1].replace(" ","_");;

            URI reviewerUri = null;
            try {
                URL url = new URL(config.RESOURCE_URL+reviewer);
                String nullFragment = null;
                reviewerUri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            } catch (MalformedURLException e) {
                System.out.println("URL " + reviewer + " is a malformed URL");
            } catch (URISyntaxException e) {
                System.out.println("URI " + reviewer + " is a malformed URL");
            }

            //create reviewer resource
            Resource rev = model.createResource(reviewerUri.toString())
                    .addProperty(model.createProperty(config.DBO_URL+"birthName"),reviewer)
                    .addProperty(model.createProperty(config.BASE_URL+"sends"),model.createResource(config.RESOURCE_URL+"review_"+row_count));
            //create review resource
            Resource rev2 = model.createResource(config.RESOURCE_URL+"review_"+row_count)
                    .addProperty(model.createProperty(config.BASE_URL+"review_id"),String.valueOf(row_count))
                    .addProperty(model.createProperty(config.BASE_URL+"for"),model.createResource(config.RESOURCE_URL+paper));

        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"reviews.nt")), true), "NT");
    }

    //Ali's Part
    public static void transformJournal() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read Journals from journal csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.journal_input));

        String row;
        while ((row = csvReader.readLine()) != null) {
            String name = row;

            String journalUri = name.replace(" ","_");
            Resource journal = model.createResource(config.RESOURCE_URL+journalUri)
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Journal"))
                    .addProperty(model.createProperty(config.BASE_URL+"name"),name);
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"journal.nt")), true), "NT");
    }

    public static void transformVolume_linkToJournal() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read Volumes from volume(journal) csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.volume_input));
        // skip the header line
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[0];//.replace('/','_');
            Literal number = model.createTypedLiteral(Integer.parseInt(row_data[1]));
            Literal year = model.createTypedLiteral(Integer.parseInt(row_data[2]));

            String journalUri = row_data[3].replace(' ','_');


            String volumeUri = key.replace(" ","_").replace('/','_');
            Resource volume = model.createResource(config.RESOURCE_URL+volumeUri)
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Volume"))
                    .addProperty(model.createProperty(config.BASE_URL+"key"),key)
                    .addProperty(model.createProperty(config.BASE_URL+"number"),number)
                    .addProperty(model.createProperty(config.BASE_URL+"year"),year);

            Resource has_volume = model.createResource(config.RESOURCE_URL+journalUri)
                    .addProperty(model.createProperty(config.BASE_URL+"has_volume"), model.createResource(config.RESOURCE_URL+volumeUri));

        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"volume_link_journal.nt")), true), "NT");
    }

    public static void transformConference() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read Conferences from Conferences csv
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(config.conference_input), "Cp1252"));

        String row;
        while ((row = csvReader.readLine()) != null) {
            String name = row;
            String conferenceUri = name.replace(' ','_').replace('/','_');

            Resource conference = model.createResource(config.RESOURCE_URL+conferenceUri)
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Conference"))
                    .addProperty(model.createProperty(config.BASE_URL+"name"),name);
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"conference.nt")), true), "NT");
    }

    public static void transformEdition_linkToConference() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read Editions from Edition csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.edition_input));
        // skip the header line
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            if(row_data.length>4){
                String str = row.substring(row.indexOf("\"")+1, row.lastIndexOf("\""));
                row = row.replace(str,"");
                row_data = row.split(",");
                row_data[2] = str;
            }

            String key = row_data[1];//.replace('/','_');

            String _year = row_data[3];
            if (_year.length()>4)
                _year = _year.substring(0,4);
            Literal year;
            try {
                year = model.createTypedLiteral(Integer.parseInt(_year));
            }catch(Exception ex){
                year = model.createTypedLiteral(0);
            }
            String editionUri = key.replace(' ','_').replace('/','_');
            String conferenceUri = row_data[2].replace(' ','_').replace('/','_');

            Resource edition = model.createResource(config.RESOURCE_URL+editionUri)
                    //.addProperty(model.createProperty(RDF.type.toString()), model.createResource(config.BASE_URL+"Edition"))
                    .addProperty(model.createProperty(config.BASE_URL+"key"),key)
                    .addProperty(model.createProperty(config.BASE_URL+"year"),year);

            Resource has_edition = model.createResource(config.RESOURCE_URL+conferenceUri)
                    .addProperty(model.createProperty(config.BASE_URL+"has_edition"), model.createResource(config.RESOURCE_URL+editionUri));

        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"edition_link_conference.nt")), true), "NT");
    }

    public static void link_paper_edition() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read papers & editions from paper(edition) csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.link_paper_edition_input));
        // skip the header line
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[1];
            String crossref = row_data[3];

            String paperUri = key.replace(' ','_').replace('/','_');
            String editionUri = crossref.replace(' ','_').replace('/','_');

            Resource published_in = model.createResource(config.RESOURCE_URL+paperUri)
                    .addProperty(model.createProperty(config.BASE_URL+"published_in"), model.createResource(config.RESOURCE_URL+editionUri));

        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"link_paper_edition.nt")), true), "NT");
    }

    public static void link_paper_volume() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        // read papers & volumes from paper(volume) csv
        BufferedReader csvReader = new BufferedReader(new FileReader(config.link_paper_volume_input));
        // skip the header line
        csvReader.readLine();
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");
            String key = row_data[0];
            String crossref = row_data[3];

            String paperUri = key.replace(' ','_').replace('/','_');
            String volumeUri = crossref.replace(' ','_').replace('/','_');

            Resource published_in = model.createResource(config.RESOURCE_URL+paperUri)
                    .addProperty(model.createProperty(config.BASE_URL+"published_in"), model.createResource(config.RESOURCE_URL+volumeUri));
        }
        csvReader.close();

        // write the mode to file
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(config.OUTPUT_FILE_PATH+"link_paper_volume.nt")), true), "NT");
    }

}
