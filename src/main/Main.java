package main;

/**
 * Created by tzachs on 24/10/2017.
 *
 * @author tzachs
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SimpleService.INSTANCE.setStatus(true);
            SimpleService.INSTANCE.setStatus(false);

        }).start();

        new Thread(() -> {
            SimpleService.INSTANCE.setStatus(true);
            SimpleService.INSTANCE.setStatus(false);
        }).start();

        SimpleService.INSTANCE.setStatus(true);
        SimpleService.INSTANCE.setStatus(false);
    }
}
