package edu.uoc.pac1.model;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class BookContent {

    /**
     * An array of sample book items.
     */
    public static final List<BookItem> ITEMS = new ArrayList<>();

    static {
        BookItem book1 = new BookItem("Title1", "Author1", "12/07/2016", "Description", null);
        BookItem book2 = new BookItem("Title2", "Author2", "12/07/2016", "Description 2", null);
        ITEMS.add(book1);
        ITEMS.add(book2);
    }

    public static List<BookItem> getBooks(){
        // ============ INICI CODI A COMPLETAR ===============
        return BookItem.listAll(BookItem.class);
        // ============ FI CODI A COMPLETAR ===============
    }

    public static boolean exists(BookItem bookItem) {
        // ============ INICI CODI A COMPLETAR ===============
        List<BookItem> bookItem1 = BookItem.find(BookItem.class, "title = ?", bookItem.title);
        if (bookItem1 == null || bookItem1.size() == 0) {
            return false;
        } else {
            return true;
        }
        // ============ FI CODI A COMPLETAR ===============
    }

    /**
     * A book item representing a piece of content.
     */
    public static class BookItem extends SugarRecord {
        public String title;
        public String author;
        public String publicationDate;
        public String description;
        public String urlImage;

        public BookItem() {

        }

        public BookItem(String title, String author, String publicationDate,
                        String description, String urlImage) {
            this.title = title;
            this.author = author;
            this.publicationDate = publicationDate;
            this.description = description;
            this.urlImage = urlImage;
        }

        @Override
        public String toString() {
            return title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getPublication_date() {
            return publicationDate;
        }

        public void setPublication_date(String publicationDate) {
            this.publicationDate = publicationDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl_image() {
            return urlImage;
        }

        public void setUrl_image(String urlImage) {
            this.urlImage = urlImage;
        }
    }
}
