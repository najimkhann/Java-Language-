import java.util.Objects;

public class Book implements Comparable<Book> {
    private Integer bookId;
    private String title;
    private String author;
    private String category;
    private boolean isIssued;

    public Book(Integer bookId, String title, String author, String category, boolean isIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;
    }

    // getters & setters
    public Integer getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isIssued() { return isIssued; }

    public void markAsIssued() { this.isIssued = true; }
    public void markAsReturned() { this.isIssued = false; }

    public void displayBookDetails() {
        System.out.println("ID: " + bookId + " | Title: " + title + " | Author: " + author
                + " | Category: " + category + " | Issued: " + (isIssued ? "Yes" : "No"));
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        // CSV friendly: id|title|author|category|isIssued
        return bookId + "|" + escape(title) + "|" + escape(author) + "|" + escape(category) + "|" + isIssued;
    }

    public static Book fromString(String line) {
        // split by '|' expecting 5 parts
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        Integer id = Integer.parseInt(parts[0]);
        String title = unescape(parts[1]);
        String author = unescape(parts[2]);
        String category = unescape(parts[3]);
        boolean isIssued = Boolean.parseBoolean(parts[4]);
        return new Book(id, title, author, category, isIssued);
    }

    private static String escape(String s) {
        return s.replace("|", "/|"); // simple escape for pipe
    }

    private static String unescape(String s) {
        return s.replace("/|", "|");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book b = (Book) o;
        return Objects.equals(bookId, b.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }
}
import java.util.ArrayList;
import java.util.List;

public class Member {
    private Integer memberId;
    private String name;
    private String email;
    private List<Integer> issuedBooks; // store book IDs

    public Member(Integer memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.issuedBooks = new ArrayList<>();
    }

    // getters & setters
    public Integer getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Integer> getIssuedBooks() { return issuedBooks; }

    public void addIssuedBook(int bookId) {
        if (!issuedBooks.contains(bookId)) {
            issuedBooks.add(bookId);
        }
    }

    public boolean returnIssuedBook(int bookId) {
        return issuedBooks.remove((Integer) bookId);
    }

    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId + " | Name: " + name + " | Email: " + email);
        System.out.print("Issued Books: ");
        if (issuedBooks.isEmpty()) {
            System.out.println("None");
        } else {
            for (int id : issuedBooks) {
                System.out.print(id + " ");
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {
        // CSV style: id|name|email|id,id,...
        StringBuilder sb = new StringBuilder();
        sb.append(memberId).append("|").append(escape(name)).append("|").append(escape(email)).append("|");
        for (int i = 0; i < issuedBooks.size(); i++) {
            sb.append(issuedBooks.get(i));
            if (i < issuedBooks.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    public static Member fromString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        Integer id = Integer.parseInt(parts[0]);
        String name = unescape(parts[1]);
        String email = unescape(parts[2]);
        Member m = new Member(id, name, email);
        if (!parts[3].isEmpty()) {
            String[] ids = parts[3].split(",");
            for (String s : ids) {
                try {
                    m.addIssuedBook(Integer.parseInt(s));
                } catch (NumberFormatException ignored) {}
            }
        }
        return m;
    }

    private static String escape(String s) {
        return s.replace("|", "/|");
    }

    private static String unescape(String s) {
        return s.replace("/|", "|");
    }
}
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class LibraryManager {

    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, Member> members = new HashMap<>();
    private Set<String> categories = new HashSet<>();

    private final String BOOKS_FILE = "books.txt";
    private final String MEMBERS_FILE = "members.txt";

    private Scanner scanner = new Scanner(System.in);
    private int nextBookId = 100;   // starting id (can be adjusted)
    private int nextMemberId = 200; // starting id

    // email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static void main(String[] args) {
        LibraryManager lm = new LibraryManager();
        lm.loadFromFile();
        lm.mainMenu();
    }

    private void mainMenu() {
        try {
            int choice;
            do {
                System.out.println("\n===== City Library Digital Management System =====");
                System.out.println("1. Add Book");
                System.out.println("2. Add Member");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Search Books");
                System.out.println("6. Sort Books");
                System.out.println("7. Show All Books");
                System.out.println("8. Show All Members");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                choice = safeIntInput();

                switch (choice) {
                    case 1: addBook(); break;
                    case 2: addMember(); break;
                    case 3: issueBook(); break;
                    case 4: returnBook(); break;
                    case 5: searchBooks(); break;
                    case 6: sortBooksMenu(); break;
                    case 7: showAllBooks(); break;
                    case 8: showAllMembers(); break;
                    case 9:
                        System.out.println("Saving data and exiting...");
                        saveToFile();
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } while (choice != 9);
        } finally {
            scanner.close();
        }
    }

    // 1. Add Book
    private void addBook() {
        try {
            System.out.print("Enter Book Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Enter Author: ");
            String author = scanner.nextLine().trim();
            System.out.print("Enter Category: ");
            String category = scanner.nextLine().trim();

            if (title.isEmpty() || author.isEmpty() || category.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            int id = generateBookId();
            Book b = new Book(id, title, author, category, false);
            books.put(id, b);
            categories.add(category.toLowerCase());
            System.out.println("Book added successfully with ID: " + id);

            saveToFile(); // persist immediately
        } catch (Exception e) {
            System.out.println("Error while adding book: " + e.getMessage());
        }
    }

    // 2. Add Member
    private void addMember() {
        try {
            System.out.print("Enter Member Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();

            if (name.isEmpty() || email.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format.");
                return;
            }

            int id = generateMemberId();
            Member m = new Member(id, name, email);
            members.put(id, m);
            System.out.println("Member added successfully with ID: " + id);

            saveToFile();
        } catch (Exception e) {
            System.out.println("Error while adding member: " + e.getMessage());
        }
    }

    // 3. Issue Book
    private void issueBook() {
        try {
            System.out.print("Enter Book ID to issue: ");
            int bookId = safeIntInput();
            Book b = books.get(bookId);
            if (b == null) {
                System.out.println("Book not found.");
                return;
            }
            if (b.isIssued()) {
                System.out.println("Book is already issued to someone else.");
                return;
            }
            System.out.print("Enter Member ID: ");
            int memberId = safeIntInput();
            Member m = members.get(memberId);
            if (m == null) {
                System.out.println("Member not found.");
                return;
            }

            b.markAsIssued();
            m.addIssuedBook(bookId);
            System.out.println("Book ID " + bookId + " issued to Member ID " + memberId);

            saveToFile();
        } catch (Exception e) {
            System.out.println("Error while issuing: " + e.getMessage());
        }
    }

    // 4. Return Book
    private void returnBook() {
        try {
            System.out.print("Enter Book ID to return: ");
            int bookId = safeIntInput();
            Book b = books.get(bookId);
            if (b == null) {
                System.out.println("Book not found.");
                return;
            }
            System.out.print("Enter Member ID: ");
            int memberId = safeIntInput();
            Member m = members.get(memberId);
            if (m == null) {
                System.out.println("Member not found.");
                return;
            }

            boolean removed = m.returnIssuedBook(bookId);
            if (removed) {
                b.markAsReturned();
                System.out.println("Book returned successfully.");
                saveToFile();
            } else {
                System.out.println("This member does not have this book issued.");
            }
        } catch (Exception e) {
            System.out.println("Error while returning book: " + e.getMessage());
        }
    }

    // 5. Search Books (title, author, category)
    private void searchBooks() {
        System.out.println("Search by: 1.Title 2.Author 3.Category");
        int ch = safeIntInput();
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        List<Book> results = new ArrayList<>();
        for (Book b : books.values()) {
            switch (ch) {
                case 1:
                    if (b.getTitle().toLowerCase().contains(keyword)) results.add(b);
                    break;
                case 2:
                    if (b.getAuthor().toLowerCase().contains(keyword)) results.add(b);
                    break;
                case 3:
                    if (b.getCategory().toLowerCase().contains(keyword)) results.add(b);
                    break;
                default:
                    System.out.println("Invalid option.");
                    return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            for (Book r : results) r.displayBookDetails();
        }
    }

    // 6. Sort Books menu
    private void sortBooksMenu() {
        System.out.println("Sort by: 1.Title (Comparable) 2.Author 3.Category");
        int ch = safeIntInput();
        List<Book> list = new ArrayList<>(books.values());

        switch (ch) {
            case 1:
                Collections.sort(list); // uses Comparable (title)
                break;
            case 2:
                list.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
                break;
            case 3:
                list.sort(Comparator.comparing(Book::getCategory, String.CASE_INSENSITIVE_ORDER));
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        for (Book b : list) b.displayBookDetails();
    }

    // helper: show all books
    private void showAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        for (Book b : books.values()) b.displayBookDetails();
    }

    // helper: show all members
    private void showAllMembers() {
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        for (Member m : members.values()) m.displayMemberDetails();
    }

    // safe int input handling
    private int safeIntInput() {
        while (true) {
            try {
                String line = scanner.nextLine();
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private synchronized int generateBookId() {
        while (books.containsKey(nextBookId)) nextBookId++;
        return nextBookId++;
    }

    private synchronized int generateMemberId() {
        while (members.containsKey(nextMemberId)) nextMemberId++;
        return nextMemberId++;
    }

    // ========== File I/O ==========
    public void loadFromFile() {
        // create files if not exist
        try {
            File bf = new File(BOOKS_FILE);
            if (!bf.exists()) bf.createNewFile();

            File mf = new File(MEMBERS_FILE);
            if (!mf.exists()) mf.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating data files: " + e.getMessage());
        }

        // load books
        try (BufferedReader br = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Book b = Book.fromString(line);
                if (b != null) {
                    books.put(b.getBookId(), b);
                    categories.add(b.getCategory().toLowerCase());
                    nextBookId = Math.max(nextBookId, b.getBookId() + 1);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
        // load members
        try (BufferedReader br = new BufferedReader(new FileReader(MEMBERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Member m = Member.fromString(line);
                if (m != null) {
                    members.put(m.getMemberId(), m);
                    nextMemberId = Math.max(nextMemberId, m.getMemberId() + 1);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    public void saveToFile() {
        // write books
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books.values()) {
                bw.write(b.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }

        // write members
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members.values()) {
                bw.write(m.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }
}
