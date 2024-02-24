import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

class Labyrinth {
    private int numGuests;
    private Semaphore mutex;
    private int guestsEntered;
    private Random random;

    public Labyrinth(int numGuests) {
        this.numGuests = numGuests;
        mutex = new Semaphore(1);
        guestsEntered = 0;
        random = new Random();
    }

    public void enterLabyrinth(int guestId) throws InterruptedException {
        mutex.acquire();
        if (random.nextDouble() < 0.1) { // 10% chance of being called again
            System.out.println("Guest " + guestId + " is called again to enter the labyrinth.");
        } else {
            guestsEntered++;
            System.out.println("Guest " + guestId + " entered the labyrinth.");
            if (guestsEntered == numGuests) {
                System.out.println("All guests have entered the labyrinth.");
                System.out.println("////////////////");
            }
        }
        mutex.release();
    }
}

class Showroom {
    private Semaphore mutex;
    private Queue<Integer> queue;

    public Showroom() {
        mutex = new Semaphore(1);
        queue = new LinkedList<>();
    }

    public void enterShowroom(int guestId) throws InterruptedException {
        mutex.acquire();
        queue.add(guestId);
        System.out.println("Guest " + guestId + " entered the showroom.");
        mutex.release();
    }

    public void exitShowroom() throws InterruptedException {
        mutex.acquire();
        int guestId = queue.poll();
        System.out.println("Guest " + guestId + " exited the showroom.");
        notifyNextGuest();
        mutex.release();
    }

    private void notifyNextGuest() {
        if (!queue.isEmpty()) {
            int nextGuestId = queue.peek();
            System.out.println("Showroom is available for guest " + nextGuestId + ".");
        }
    }
}

class Guest extends Thread {
    private int id;
    private Labyrinth labyrinth;
    private Showroom showroom;

    public Guest(int id, Labyrinth labyrinth, Showroom showroom) {
        this.id = id;
        this.labyrinth = labyrinth;
        this.showroom = showroom;
    }

    @Override
    public void run() {
        try {
            labyrinth.enterLabyrinth(id);
            Thread.sleep(1000); // Simulating time spent in the labyrinth
            showroom.enterShowroom(id);
            Thread.sleep(1000); // Simulating time spent viewing the vase
            showroom.exitShowroom();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Assignment2 {
    public static void main(String[] args) {
        int numGuests = 5; // You can change the number of guests as needed
        Labyrinth labyrinth = new Labyrinth(numGuests);
        Showroom showroom = new Showroom();

        // Randomize the order of guests
        int[] guestIds = new int[numGuests];
        for (int i = 0; i < numGuests; i++) {
            guestIds[i] = i + 1;
        }
        shuffleArray(guestIds);

        // Create and start guest threads
        for (int guestId : guestIds) {
            Guest guest = new Guest(guestId, labyrinth, showroom);
            guest.start();
        }
    }

    // Method to shuffle an array
    private static void shuffleArray(int[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
