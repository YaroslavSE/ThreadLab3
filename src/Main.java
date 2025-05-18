import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private Semaphore access;
    private Semaphore full;
    private Semaphore empty;

    private ArrayList<String> storage = new ArrayList<>();


    public static void main(String[] args) {
        Main program = new Main();
        program.starter(3,15,2,4);
    }

    private void starter(int storageSize, int totalItems, int producerCount, int consumerCount){
        access = new Semaphore(1);
        full = new Semaphore(storageSize);
        empty = new Semaphore(0);

        int itemsPerProducer = totalItems / producerCount;
        int itemsPerConsumer = totalItems / consumerCount;

        int remainingProducerItems = totalItems % producerCount;
        int remainingConsumerItems = totalItems % consumerCount;

        ArrayList<Thread> threads = new ArrayList<Thread>();

        for(int i = 0; i < producerCount; i++){
            int itemsToProduce = itemsPerProducer + (i < remainingProducerItems ? 1 : 0);
            int producerId = i + 1;
            Thread t = new Thread(() -> producer(itemsToProduce, producerId));
            t.start();
            threads.add(t);
        }

        for (int i = 0; i < consumerCount; i++)
        {
            int itemsToConsume = itemsPerConsumer + (i < remainingConsumerItems ? 1 : 0);
            int consumerId = i + 1;
            Thread t = new Thread(() -> consumer(itemsToConsume, consumerId));
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted.");
            }
        }
    }

    private void producer(int count, int producerId) {
        for (int i = 0; i < count; i++) {
            try {
                full.acquire();    // чекаємо вільне місце
                access.acquire();  // вхід до сховища

                String item = "item P" + producerId + "-" + (i + 1);
                storage.add(item);
                System.out.println("[Producer " + producerId + "] Produced: " + item + "storage: " + storage.size()) ;

                access.release();  // вихід зі сховища
                empty.release();   // новий елемент доступний
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    private void consumer(int count, int consumerId) {
        for (int i = 0; i < count; i++) {
            try {
                empty.acquire();    // чекаємо елемент
                access.acquire();   // вхід до сховища

                String item = storage.remove(0);
                System.out.println("[Consumer " + consumerId + "] Consumed: " + item + "storage: " + storage.size());

                access.release();   // вихід зі сховища
                full.release();     // звільнили місце

                Thread.sleep(3000); // імітація обробки
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}