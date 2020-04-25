public class Main {

    public static void main(String[] args) throws Exception {
        Abox abox = new Abox();
        System.out.println("starting");

        abox.transformPaper();
        abox.transformUniversity();
        abox.transformAuthor();
        abox.link_authors_paper();
        abox.transformKeyword();
        abox.link_paper_keyword();
        System.out.println("ending");
    }
}
