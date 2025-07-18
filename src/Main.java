import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private Semaphore access = new Semaphore(1);
    private Semaphore full;
    private Semaphore empty = new Semaphore(0);

    private ArrayList<String> storage = new ArrayList<>();


    public static void main(String[] args) {
        Main program = new Main();
        //program.starter(3,15,2,4);
        program.fixedStarter(3);
    }

    private void starter(int storageSize, int totalItems, int producerCount, int consumerCount){
        full = new Semaphore(storageSize);

        int itemsPerProducer = totalItems / producerCount;
        int itemsPerConsumer = totalItems / consumerCount;

        int remainingProducerItems = totalItems % producerCount;
        int remainingConsumerItems = totalItems % consumerCount;


        for(int i = 0; i < producerCount; i++){
            int itemsToProduce = itemsPerProducer + (i < remainingProducerItems ? 1 : 0);
            int producerId = i + 1;
            Thread t = new Thread(() -> producer(itemsToProduce, producerId));
            t.start();
        }

        for (int i = 0; i < consumerCount; i++)
        {
            int itemsToConsume = itemsPerConsumer + (i < remainingConsumerItems ? 1 : 0);
            int consumerId = i + 1;
            Thread t = new Thread(() -> consumer(itemsToConsume, consumerId));
            t.start();
        }


    }
    private void fixedStarter(int storageSize){
        full = new Semaphore(storageSize);

        int[] producersLoad = {5,10,15};
        int[] consumersLoad = {4, 4, 8, 10, 2, 2};

        for(int i = 0; i < producersLoad.length; i++){
            int itemsToProduce = producersLoad[i];
            int producerId = i + 1;
            Thread t = new Thread(() -> producer(itemsToProduce, producerId));
            t.start();
        }

        for (int i = 0; i < consumersLoad.length; i++)
        {
            int itemsToConsume = consumersLoad[i];
            int consumerId = i + 1;
            Thread t = new Thread(() -> consumer(itemsToConsume, consumerId));
            t.start();
        }
    }

    private void producer(int count, int producerId) {
        for (int i = 0; i < count; i++) {
            try {
                full.acquire();    // чекаємо вільне місце
                access.acquire();  // вхід до сховища

                String item = "item P" + producerId + "-" + (i + 1);
                storage.add(item);
                System.out.println("[Producer " + producerId + "] Produced: " + item + " storage: " + storage.size()) ;

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
                System.out.println("[Consumer " + consumerId + "] Consumed: " + item + " storage: " + storage.size());

                access.release();   // вихід зі сховища
                full.release();     // звільнили місце

                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}