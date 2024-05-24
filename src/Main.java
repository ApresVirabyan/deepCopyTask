import java.util.Arrays;



public class Main {
    public static void main(String[] args) {

        Man man = new Man("John", 30, Arrays.asList("Book1", "Book2"));
        Man copiedMan = DeepCopy.deepCopy(man);
        man.setName("Doe");

        System.out.println("Copied Man Name: " + copiedMan.getName());
        System.out.println("Copied Man Books: " + copiedMan.getFavoriteBooks());
    }
}