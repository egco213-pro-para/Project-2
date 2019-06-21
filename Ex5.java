
 
/* ------------------------------------------ Mink's with stackoverflow handle ------------------------------------------ */
 
package ex5;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
 
class BankingThread extends Thread implements Comparable<BankingThread> {
 
    private int totalDeposit;
    private Account account;
    private CyclicBarrier barrier;
 
    public BankingThread(String n, Account ac, CyclicBarrier br) {
        super(n);
        account = ac;
        barrier = br;
    }
 
    private void deposit(int count, int money) throws StackOverflowError {
        try {
        if (account.deposit(count, money)) totalDeposit += money;
        else deposit(count, money);
        } catch (StackOverflowError e) {
           
        }
    }
 
    public void printTotal() {
        System.out.println(getName() + " total deposit = " + totalDeposit);
    }
 
    public void run() {
        /* Add a loop to process 10 transactions. For each transaction processing
        1. Random the amount of money to deposit. You may set the range of
        randomed values, e.g. 1-10
        2. Call account.deposit(…) to deposit the money
        3. If the transaction is successful, sum this amount to totalDeposit
        4. Stop once 10 transactions are successfully processed
        */
        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
            deposit(i+1, rand.nextInt(10) + 1);
        }
        try { barrier.await(); }
        catch (Exception e) { }
    }
 
    @Override
    public int compareTo(BankingThread o) { return Integer.compare(o.totalDeposit, totalDeposit); }
 
}
 
class Account {
 
    private int balance = 0;
    private Thread previous;
 
    synchronized public boolean deposit(int count, int money) {
        /* Each thread is not allowed to update the account twice in a row
        1. If current & previous threads are the same thread, the transaction is
        considered failure. Return false, i.e. no account update
        2. If they are different threads, the current one can update the account
        - Print its name, its (successful) transaction count, money to deposit,
        and account balance to the screen
        - Memorize it as previous thread
        - Return true
        */
        if (previous == Thread.currentThread()) return false;
        balance += money;
        previous = Thread.currentThread();
        System.out.printf("%s (transaction %2d) +%2d  account balance = %3d\n"
                , previous.getName(), count, money, balance);
        return true;
    }
}
 
 
 
 
public class Ex5 {
 
    public static void main(String[] args) {
 
        Account account = new Account();
        ArrayList<BankingThread> BT = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
 
        System.out.print("Number of Threads = ");
        int numThread = scan.nextInt();
 
        CyclicBarrier barrier = new CyclicBarrier(numThread);
 
        for (int i = 0; i < numThread; i++)
            BT.add(new BankingThread("T" + (i + 1), account, barrier));
 
        for (BankingThread T : BT) T.start();
 
        for (BankingThread T : BT)
            try { T.join(); }
            catch (InterruptedException e) { }
 
        Collections.sort(BT);
        System.out.println("\n=== Summary ===");
        for (BankingThread T : BT) T.printTotal();
 
    }
 
}