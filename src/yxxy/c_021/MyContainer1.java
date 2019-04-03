/**
 * 面试题：写一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支持2个生产者线程以及10个消费者线程的阻塞调用
 * 
 * 使用wait和notify/notifyAll来实现
 * 
 * @author mashibing
 */
package yxxy.c_021;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MyContainer1<T> {
	final private LinkedList<T> lists = new LinkedList<>();
	final private int MAX = 10; //最多10个元素
	private int count = 0;
	
	
	public synchronized void put(T t) {
		
		while(lists.size() == MAX) { //想想为什么用while而不是用if？
			this.notifyAll();//通知消费者线程进行消费
			try {
				this.wait(); //effective java
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		lists.add(t);
		++count;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void get() {
		T t = null;
		
		while(lists.size() == 0) {
			this.notifyAll();//通知生产者进行生产
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		t = lists.removeFirst();
		count --;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MyContainer1<String> c = new MyContainer1<>();
		//启动消费者线程
		for(int i=0; i<10; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for(int j=0;j<2;j++) {
						c.get();
						System.out.println("消费后库存"+c.lists.size());
					}
				}
			}, "c" + i).start();
		}
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//启动生产者线程
		for(int i=0; i<2; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for(int j=0; j<25; j++){
						c.put("c"+j);
						System.out.println("生产后库存"+c.lists.size());
					}
					
				}
			}, "p" + i).start();
		}
	}
}
